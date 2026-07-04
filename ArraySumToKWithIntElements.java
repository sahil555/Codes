import java.util.HashMap;
import java.util.Map;

// This class provides a method to find the maximum number of elements from an integer array that can sum up to a given target K.
public class ArraySumToKWithIntElements {
    /**
     * This method calculates the maximum number of elements from the input array that can sum up to the target K.
     * It uses a dynamic programming approach to keep track of the maximum count of elements used to make each possible sum.
     *
     * @param arr The input array of integers.
     * @param K   The target sum.
     * @return The maximum number of elements that can sum up to K, or -1 if it's not possible.
     */
    public static int maxElements(int[] arr, int K) {
        // sum -> maximum count of elements used to make this sum
        System.out.println("Target sum: " + K);
        System.out.println("Input array: " + java.util.Arrays.toString(arr));
        // Initialize a map to store the maximum count of elements for each possible sum.
        // what if i use hashset instead of hashmap?
        // The key is the current sum, and the value is the maximum count of elements used to achieve that sum.
        // The initial state is that a sum of 0 can be achieved with 0 elements.
        // The time complexity of this approach is O(n * K), where n is the number of elements in the array and K is the target sum.
        // The space complexity is O(K) due to the storage of sums in the map.
        // The algorithm iterates through each number in the input array and updates the map of sums accordingly.
        // The final result is obtained by checking the maximum count of elements that can achieve the target sum K in the map.
        // If K is not present in the map, it means it's not possible to achieve that sum with the given elements, and the method returns -1.
        Map<Integer, Integer> dp = new HashMap<>();
        dp.put(0, 0);
        // Iterate through each number in the input array.
        for (int num : arr) {
            // Create a new map to store the updated counts for the current iteration.
            Map<Integer, Integer> next = new HashMap<>(dp);
            // Iterate through the current sums in the dp map.
            for (Map.Entry<Integer, Integer> entry : dp.entrySet()) {
                // Calculate the new sum and count if the current number is included.
                int currentSum = entry.getKey();
                int currentCount = entry.getValue();

                int newSum = currentSum + num;
                int newCount = currentCount + 1;
                // Update the next map with the new sum and count, ensuring we keep the maximum count for each sum.
                next.put(
                        newSum,
                        Math.max(
                                next.getOrDefault(newSum, -1),
                                newCount));
            }
            System.out.println("Current number: " + num);
            System.out.println("Next DP: " + next);

            // Update the dp map for the next iteration.
            dp = next;
        }

        return dp.getOrDefault(K, -1);
    }

    public static void main(String[] args) {

        int[] arr1 = { 1, -1, 2, 3, -2 };
        int k1 = 3;

        System.out.println("Answer: " + maxElements(arr1, k1));

        int[] arr2 = { 1, 2, 3, 4, 5 };
        int k2 = 6;

        System.out.println("Answer: " + maxElements(arr2, k2));

        int[] arr3 = { -5, -2, 1, 4, 7 };
        int k3 = 0;

        System.out.println("Answer: " + maxElements(arr3, k3));
    }
}