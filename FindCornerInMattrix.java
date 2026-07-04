
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class FindCornerInMattrix {

    /*
     * brute force method to iterate over all the elements one by
     * The time complexity will be O(row x column^2 )
     * 
     * space complexity will be O(row x column)
     * due to this Map<Integer, Integer> colPairCounts = new HashMap<>()
     * 
     * @params 2 D array int[][] InputArray
     * 
     * @returns integer
     * 
     */
    public int findingCornersInInputArraySolution_1(int[][] InputArray) {
        try {
            // declaration for imported variables for calculation
            int rowLength = InputArray.length;
            int colLength = InputArray[0].length;
            int result = 0;
            Map<Integer, Integer> colPairCounts = new HashMap<>();

            // adding the base conditions
            if (InputArray.length < 2 || InputArray[0].length < 2) {
                return 0;
            }
            // iterating over the input array for the finding of +ve corners

            for (int r = 0; r < rowLength; r++) {
                for (int c = 0; c < colLength; c++) {
                    if (InputArray[r][c] == 1) {

                        for (int countings = c + 1; countings < colLength; countings++) {
                            if (InputArray[r][countings] == 1) {
                                int pairKey = c * 200 + countings;
                                int exisitingPair = colPairCounts.getOrDefault(pairKey, 0);
                                result += exisitingPair;
                                colPairCounts.put(pairKey, exisitingPair + 1);
                            }
                        }
                    }
                }
            }
            // result will be returns
            return result;

        } catch (Exception e) {
            System.out.println("\nError : " + e + "\n");
            return -1;
        }
    }

    // Optimised solution to find the corners in the 2-D matrix
    /*
     * *** The Sparse Matrix Optimization ***
     * If the matrix has very few 1s, scanning every single 0 is a waste of time.
     * Instead, we can pre-process the matrix to record only the column indices
     * where 1 appears for each row.
     * 
     * Time Complexity: (O(Number of 1s^2)) in the worst case, but
     * significantly faster than (R times C^2) for sparse inputs.
     * 
     * Space Complexity: (O(Number of 1s)) to store the coordinates.
     * 
     * @params 2 D array int[][] InputArray
     * 
     * @returns integer
     */
    public int findingCornersInInputArraySolution_2(int[][] inputArray) {
        try {
            if (inputArray == null || inputArray.length < 2 || inputArray[0] == null || inputArray[0].length < 2) {
                return 0;
            }

            int result = 0;
            Map<Integer, Integer> pairCounts = new HashMap<>();

            for (int row = 0; row < inputArray.length; row++) {
                List<Integer> columnsWithOnes = new ArrayList<>();
                for (int col = 0; col < inputArray[row].length; col++) {
                    if (inputArray[row][col] == 1) {
                        columnsWithOnes.add(col);
                    }
                }

                for (int leftIndex = 0; leftIndex < columnsWithOnes.size(); leftIndex++) {
                    for (int rightIndex = leftIndex + 1; rightIndex < columnsWithOnes.size(); rightIndex++) {
                        int firstColumn = columnsWithOnes.get(leftIndex);
                        int secondColumn = columnsWithOnes.get(rightIndex);
                        int pairKey = firstColumn * 200 + secondColumn;
                        int existingPair = pairCounts.getOrDefault(pairKey, 0);
                        result += existingPair;
                        pairCounts.put(pairKey, existingPair + 1);
                    }
                }
            }

            return result;

        } catch (Exception e) {
            System.out.println("\nError : " + e + "\n");
            return -1;
        }
    }

    public static void main(String[] args) {
        // need to create a functionality to find the corners in the 2-D matrix
        FindCornerInMattrix instanceCornerInMattrix = new FindCornerInMattrix();

        int[][][] testCases = {
                // Case 1: Too small
                { { 1 }, { 1 } },
                // Case 2: 3x3 All Ones
                { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } },
                // Case 3: Sparse distributed
                { { 1, 0, 0, 1 }, { 0, 1, 1, 0 }, { 1, 0, 0, 1 }, { 0, 1, 1, 0 } },
                // Case 4: Asymmetric tall grid
                { { 1, 1 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }
        };

        System.out.println("\n\nTesting the finding of corners in the 2-D matrix, a brutforce solution.\n");
        // test cases for the finding of corners in the 2-D matrix, a brutforce
        // solution.
        for (int i = 0; i < testCases.length; i++) {
            System.out.println("\nInput Array : " + Arrays.deepToString(testCases[i]) + "\n");
            int result = instanceCornerInMattrix.findingCornersInInputArraySolution_1(testCases[i]);
            System.out.println("Test Case " + (i + 1) + "\n" + "Got : " + result);
        }

        System.out.println(
                "\n\nTesting the finding of corners in the 2-D matrix, an optimised solution using sparse matrices.\n");
        // testing the optimised solution
        for (int i = 0; i < testCases.length; i++) {
            System.out.println("\nInput Array : " + Arrays.deepToString(testCases[i]) + "\n");
            int result = instanceCornerInMattrix.findingCornersInInputArraySolution_2(testCases[i]);
            int expected = instanceCornerInMattrix.findingCornersInInputArraySolution_1(testCases[i]);
            if (result != expected) {
                throw new IllegalStateException(
                        "Mismatch for test case " + (i + 1) + ": expected " + expected + " but got " + result);
            }
            System.out.println("Test Case " + (i + 1) + "\n" + "Got : " + result);
        }

    }
}