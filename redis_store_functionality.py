Implement a subset of Redis in memory.
store = new Store()

store.set("key", "value")
store.set("key", "value", ttl=30)
store.get("key")              → value or null
store.delete("key")
store.expire("key", seconds)
store.ttl("key")              → seconds remaining or -1 if no TTL or -2 if missing
store.exists("key")           → bool

// Numeric operations
store.incr("key")             → new value
store.incrby("key", amount)   → new value

// Hash operations
store.hset("key", field, value)
store.hget("key", field)      → value or null
store.hgetall("key")          → { field: value } or null
store.hdel("key", field)

Requirements:

get on an expired key returns null — same as missing

ttl returns correct remaining seconds — not the original TTL

incr on a non-existent key starts from 0 then increments

incr on a non-numeric value must throw a typed error

Background cleanup must remove expired keys periodically

hset and set on the same key must not corrupt each other — they are different data types, calling get on a hash key must throw a WRONGTYPE error

import time

class Store:
    def __init__(self) -> None:
        self.expiry = {}
        self.data = {}
        """
        data = { key: value, expiry: timestamp }
        """

    def _isexpired(self,key):
        if key in self.expiry:
            if time.time() >= self.expiry[key]:
                self.data.pop(key, None)
                self.expiry.pop(key, None)
                return True
        return False
    
    def set(self, key, value, ttl=None):

        self.data[key] = value

        if ttl is not None:
            self.expiry[key] = time.time()
        else:
            self.expiry.pop(key, None)

    def get(self, key):
        if self._isexpired(key):
            return None
        
        return self.data.get(key)
    
    def delete(self, key):
        if self._isexpired(key):
            self.data.pop(key, None)
            self.expiry.pop(key, None)
            return True
        
        return False
    
    def incr(self, key, amount=1):
        if self._isexpired(key):
            self.data[key] = 0

        value = self.
