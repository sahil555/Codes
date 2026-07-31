from typing import List


def threeSum(nums: List[int]) -> List[List[int]]:
    nums.sort()
    result = []

    n = len(nums)

    for i in range(n - 2):
        # Early stop: once the fixed number is positive,
        # the remaining numbers are also positive.
        if nums[i] > 0:
            break

        # Skip duplicate fixed elements
        if i > 0 and nums[i] == nums[i - 1]:
            continue

        left, right = i + 1, n - 1

        while left < right:
            total = nums[i] + nums[left] + nums[right]

            if total == 0:
                result.append([nums[i], nums[left], nums[right]])

                left += 1
                right -= 1

                # Skip duplicate left values
                while left < right and nums[left] == nums[left - 1]:
                    left += 1

                # Skip duplicate right values
                while left < right and nums[right] == nums[right + 1]:
                    right -= 1

            elif total < 0:
                left += 1
            else:
                right -= 1

    return result


# Example usage
if __name__ == "__main__":
    nums = [-1, 0, 1, 2, -1, -4]
    print(threeSum(nums))
