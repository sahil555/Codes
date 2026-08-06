import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*

Problem:
Given an integer array nums and an integer target, 
return the indices of the two numbers such that they add up to the target.

Example:
nums = [2,7,11,15]
target = 9

Output:
[0,1]

*/

public class Array2Sum {
    /*
     * n = len(nums)
     * 
     * for i in range(n):
     * for j in range(i + 1, n):
     * if nums[i] + nums[j] == target:
     * return [i, j]
     */
    public static int[] brutforceMethod(int[] input, int targetInteger) {
        int lengthOfInputArray = input.length;
        int[] results = new int[2];
        // iterate over all of the input array elements
        for (int i = 0; i < lengthOfInputArray; i++) {
            for (int j = i + 1; j < lengthOfInputArray; j++) {
                if (input[i] + input[j] == targetInteger) {
                    results[0] = i;
                    results[1] = j;
                    break;
                }
            }
        }

        return results;
    }

    public static int[] usingVisitedMethod(int[] input, int targetInteger) {
        /*
         * seen = {}
         * 
         * for index, num in enumerate(nums):
         * complement = target - num
         * 
         * if complement in seen:
         * return [seen[complement], index]
         * 
         * seen[num] = index
         */
        int lengthOfInputArray = input.length;
        int[] results = new int[2];

        Map<Integer, Integer> dp = new HashMap<>();

        for (int i = 0; i < lengthOfInputArray; i++) {
            int complement = targetInteger - input[i];

            if (dp.containsKey(complement)) {
                results[0] = Arrays.binarySearch(input, complement);
                results[1] = i;
                break;
            }
            dp.put(input[i], i);
        }

        return results;
    }

    public static void main(String[] args) {
        System.out.println("Heelo ! This is the Aray of 2 sum problem demostration");

        Map<Integer, int[]> testCases = new HashMap<>();

        testCases.put(6, new int[] { 1, 3, 3 });
        testCases.put(8, new int[] { 1, 5, 3, 7 });
        testCases.put(10, new int[] { 2, 5, 5, 11 });
        
        for(int i=0;i<testCases.size();i++){
            int target = (int) testCases.keySet().toArray()[i];
            int[] inputArray = testCases.get(target);
            System.out.println("Input Array : " + Arrays.toString(inputArray));
            System.out.println("Target : " + target);

            try {
                System.out.println(
                        "results usingVisitedMethod() : " + Arrays.toString(brutforceMethod(inputArray, target)));
            } catch (Exception e) {
                System.err.println("Error While function usingVisitedMethod() invoke : " + e);
            }
        }
    }
}
