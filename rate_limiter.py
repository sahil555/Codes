import redis

# Connection to Redis
redis_client = redis.Redis(host='localhost', port=6379, db=0)

# Tenant subscription mapping
subscription_map = {
    'Free': {'requests': 100, 'burst': 10, 'ttl': 60},
    'Pro': {'requests': 500, 'burst': 50, 'ttl': 60},
    'Enterprise': {'requests': 1000, 'burst': 100, 'ttl': 60}
}

# Middleware/Interceptor pattern
def rate_limiter(request, response):
    # Get the tenant ID and subscription type
    tenant_id = request.headers.get('tenant-id')
    subscription_type = request.headers.get('subscription-type')

    # Check if the tenant exists in the subscription map
    if subscription_type not in subscription_map:
        response.status_code = 400
        response.headers['X-RateLimit-Limit'] = '0'
        response.headers['X-RateLimit-Remaining'] = '0'
        response.headers['X-RateLimit-Reset'] = '0'
        return

    # Get the current timestamp
    current_timestamp = int(time.time())

    # Check if the request counter has been reset
    if redis_client.exists(f"{tenant_id}:{subscription_type}:reset") > 0:
        redis_client.expire(f"{tenant_id}:{subscription_type}:reset", subscription_map[subscription_type]['ttl'])
        redis_client.delete(f"{tenant_id}:{subscription_type}:reset")

    # Increment the request counter
    redis_client.incr(f"{tenant_id}:{subscription_type}:requests")
    if redis_client.exists(f"{tenant_id}:{subscription_type}:requests") == 0:
        redis_client.set(f"{tenant_id}:{subscription_type}:reset", current_timestamp, ex=subscription_map[subscription_type]['ttl'])

    # Check if the tenant has exceeded their request limit
    if redis_client.exists(f"{tenant_id}:{subscription_type}:remaining") > 0:
        remaining_requests = redis_client.get(f"{tenant_id}:{subscription_type}:remaining")
        if int(remaining_requests) - 1 <= 0:
            response.status_code = 429
            response.headers['X-RateLimit-Limit'] = '0'
            response.headers['X-RateLimit-Remaining'] = '0'
            response.headers['X-RateLimit-Reset'] = str(current_timestamp + subscription_map[subscription_type]['burst'] * 60)
            response.headers['Retry-After'] = str(current_timestamp + subscription_map[subscription_type]['burst'] * 60)
            return

    # Get the remaining requests
    remaining_requests = redis_client.get(f"{tenant_id}:{subscription_type}:remaining")
    if remaining_requests is not None:
        redis_client.expire(f"{tenant_id}:{subscription_type}:remaining", 60)
        redis_client.set(f"{tenant_id}:{subscription_type}:remaining", int(remaining_requests) - 1, ex=60)

    # Set the remaining requests
    redis_client.set(f"{tenant_id}:{subscription_type}:remaining", int(remaining_requests) - 1, ex=60)

    # Set the X-RateLimit-* headers
    response.headers['X-RateLimit-Limit'] = str(subscription_map[subscription_type]['requests'])
    response.headers['X-RateLimit-Remaining'] = str(int(remaining_requests))
    response.headers['X-RateLimit-Reset'] = str(current_timestamp + subscription_map[subscription_type]['ttl'])
    response.headers['Retry-After'] = str(current_timestamp + subscription_map[subscription_type]['ttl'])

# Example configuration mapping subscription tiers to limits
subscription_configuration = {
    'Free': {'requests': 100, 'burst': 10, 'ttl': 60},
    'Pro': {'requests': 500, 'burst': 50, 'ttl': 60},
    'Enterprise': {'requests': 1000, 'burst': 100, 'ttl': 60}
}

# Concurrency & Race Condition Handling
def atomic_increment(key, value):
    pipeline = redis_client.pipeline()
    pipeline.incr(key)
    pipeline.exec_()

def atomic_decrement(key, value):
    pipeline = redis_client.pipeline()
    pipeline.decr(key)
    pipeline.exec_()

def atomic_reset(key):
    pipeline = redis_client.pipeline()
    pipeline.delete(key)
    pipeline.exec_()
