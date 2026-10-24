import java.util.Set;
import java.util.TreeSet;

/******************************************************************************
 * Find Second largest element in an array:
 * 
 * Given an array of integers, our task is to write a program that efficiently
 * finds the second-largest element present
 * in the array.
 * 
 * Examples:
 * 
 * Input: arr[] = {12, 35, 1, 10, 34, 1}
 * Output: The second largest element is 34.
 * Explanation: The largest element of the array is 35 and the second largest
 * element is 34
 * 
 * Input: arr[] = {10, 5, 10}
 * Output: The second largest element is 5.
 * Explanation: The largest element of the array is 10 and the second largest
 * element is 5
 * 
 * Select p.category AS product_category, Sum(od.quantity) AS total_quantity_sold 
 * From Products p 
 * Join OrderDetails od ON p. product_id = od. product_id 
 * GROUP BY p.category 
 * ORDER BY SUM(od. quantity) DESC FETCH FIRST 3 ROWS ONLY;
 * 
 *******************************************************************************/
public class FindSecondLargest {
	public static void main(String[] args) {
	    // int[] input = new int[]{12, 35, 1, 10, 34, 1};
	    // //Expected ans is 34.
	    // int  secondLargetValue = getSecondLarget(input);
	    
		// System.out.println(secondLargetValue);

		Set<String> treeSet = new TreeSet<>();
		treeSet.add("Indian");
		treeSet.add("Premier");
		treeSet.add("League");
		treeSet.add("Premier");
		treeSet.add("IndianPremierLeague");
		for (String temp : treeSet){
			System.out.println(temp + " ");
		}
	}
	
	static int getSecondLarget(int[] input){
	    int firstlargest = Integer.MIN_VALUE;
	    int secondlargest = Integer.MAX_VALUE;
	    
	    for(int i: input){
			System.out.println("Elements - "+i);
	        if(i > firstlargest){
				secondlargest = firstlargest;
	            firstlargest = i;
	        } else if (firstlargest > i && i > secondlargest){
	            secondlargest = i;
	        }
	    }
	    
	    return secondlargest;
	    
	}
	
}

