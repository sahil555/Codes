import java.util.Arrays;

public class BinarySearchImp {
    public static Boolean binarySearchImplimentation(int[] input, int target) {
        Arrays.sort(input);
        int lengthOfInput = input.length;

        int left = 0;
        int right = lengthOfInput - 1;

        while (left <= right) {
            int middle = Math.abs(left + (right - left) / 2);

            if (input[middle] == target)
                return true;
            else if (input[middle] < target)
                left = middle + 1;
            else
                right = middle - 1;
        }

        return false;
    }

    public static Boolean usingBuiltInBinarySearch(int[] input, int target) {
        Arrays.sort(input);
        return Arrays.binarySearch(input, target) >= 0;
    }

    public static void main(String[] args) {
        System.out.println("This is the Binary Search Algo demonstration !");

        int[] arrayInput = { 5, 3, 1, 2, 4, 7, 8, 9, 0, 12, 43, 13, 54, 17, 27, 24, 29 };
        int target = 2;

        System.out.println("Finding target : " + target + ", in Array " + Arrays.toString(arrayInput));
        System.out.println("Output Index : " + binarySearchImplimentation(arrayInput, target));
        System.out.println("Output Index (Built-in) : " + usingBuiltInBinarySearch(arrayInput, target));

    }
}
