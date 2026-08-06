import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/*

Problem Statement
Given an integer array nums, return True if any value appears at least twice, 
and return False if every element is distinct.

Example:
Input: nums = [1,2,3,1]
Output: True
Input: nums = [1,2,3,4]
Output: False

*/
public class FindDuplicateElements {
    /*
     * The brute-force solution compares every pair of elements, resulting in O(n²)
     * time. We can optimize this using a hash set. As we iterate through the array,
     * we check whether the current number has already been seen. If it has, we
     * immediately return True; otherwise, we add it to the set. This gives O(n)
     * average time complexity with O(n) additional space.
     */
    public static Boolean bruteforceMethod(int[] input){
        // complexity of this method is O(n^2)
        // space complexity of this method is O(1)

        int lengthOfInputArray = input.length;
        
        for (int i = 0; i < lengthOfInputArray - 1; i++) {
            for (int j = i + 1; j < lengthOfInputArray; j++) {
                if(input[i] == input[j]){
                    return true;
                }
            }    
        }
        return false;
    }

    public static Boolean optimiseUsingDPMethod(int[] input){
        // complexity of this method is O(n)
        // space complexity of this method is O(n)

        int lengthOfInputArray = input.length;

        Map<Integer, Integer> frequencyMap = new HashMap<>();
        
        for (int i = 0; i < lengthOfInputArray; i++) {
            int frequencyElement = input[i];
            frequencyMap.put(frequencyElement, frequencyMap.getOrDefault(frequencyElement, 0) + 1);

            if(frequencyMap.get(frequencyElement) == 2){
                return true;
            }
        }

        return false;
    }

    public static Boolean usingSortingMethod(int[] input ){
        
        Arrays.sort(input);

        for (int i = 0; i < input.length; i++) {
            if(input[i] == input[i+1]){
                return true;
            }
        }
        return false;

    }
    public static void main(String[] args) {
        System.out.println("Heelo ! This is the finding duplicate elements in array problem demostration");

        Map<Integer, int[]> testCases = new HashMap<>();

        testCases.put(1, new int[] { 1, 3, 3 });
        testCases.put(2, new int[] { 1, 5, 3, 7 });
        testCases.put(3, new int[] { 2, 5, 5, 11 });
        
        for(int i=0;i<testCases.size();i++){
            int target = (int) testCases.keySet().toArray()[i];
            int[] inputArray = testCases.get(target);
            System.out.println("Input Array : " + Arrays.toString(inputArray));

            try {
                System.out.println(
                        "results usingSortingMethod() : " + (usingSortingMethod(inputArray)).toString());
            } catch (Exception e) {
                System.err.println("Error While function usingSortingMethod() invoke : " + e);
            }
        }
    }
    
}
