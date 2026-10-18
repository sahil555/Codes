def find_rotation(arr: list) -> int:
    """Finds the rotation count (index of the minimum element) safely."""
    if not arr:
        raise ValueError("Invalid Input: Array cannot be empty")
    
    l, r = 0, len(arr) - 1
    
    # If the array is already sorted, the rotation count is 0
    if arr[l] <= arr[r]:
        return 0

    while l <= r:
        # Base case: if pointers converge or cross
        if l == r:
            return l
            
        mid = (l + r) // 2
        
        # Check if mid + 1 is the minimum
        if mid < r and arr[mid] > arr[mid + 1]:
            return mid + 1
        # Check if mid is the minimum
        if mid > l and arr[mid - 1] > arr[mid]:
            return mid
            
        # Decide which half to search
        if arr[mid] >= arr[l]:
            l = mid + 1
        else:
            r = mid - 1
            
    return 0

print(find_rotation([1, 2, 3, 4, 6, 8, -78, 5, -3]))
