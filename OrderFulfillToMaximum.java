/*
A technology company announced that a new supply of P monitors 
would soon be available at their store, 
There were N orders (numbered from 0 to N-1) placed by customers 
who wanted to buy those monitors. 
The K-th order has to be delivered to a location at distance DIK] 
from the store and is for exactly CK] monitors. 
Now the time has come for the monitors to be delivered. 
The orders will be fulfilled one by one. To minimize the shipping time, 
it has been decided that the deliveries will be made in order of increasing 
distance from the store. If there are many customers at the same distance, 
they can be processed in any order. 
Monitors to more distant customers will be delivered only once all orders 
to customers closer to the store have already been fulfilled. 
What is the maximum total number of orders that can be fulfilled?
*/ 


// given two arrays of integers D and C, and an integer P. 
// returns the maximum total number of orders that can be fulfilled. 
// Examples: 
// 1.GivenD =[5,11,1,3], C=[6, 1,3, 2] and P = 7, the function should return 2. 
// The customers at distances 1 and 3 will have 
// their orders fulfilled and 3 + 2 = 5 monitors will be delivered.        

import java.util.*;

class OrderFulfillToMaximum {
    // time complexity is O(nlogn) because of the sorting of the orders based on distance
    public int solution(int[] D, int[] C, int P) {
        try{
            // gettinhg the number of orders
            int n = D.length;
            // creating a 2D array to store the distance and count of monitors for each order
            int[][] orders = new int[n][2];
            // populating the 2D array with distance and count of monitors for each order
            for (int i = 0; i < n; i++) {
                orders[i][0] = D[i];
                orders[i][1] = C[i];
            }
            // sorting the orders based on distance in ascending order
            Arrays.sort(orders, (a, b) -> Integer.compare(a[0], b[0]));
            // fulfilling the orders one by one until the monitors run out
            int totalOrdersFulfilled = 0;
            // iterating through the sorted orders and fulfilling them if possible
            for (int i = 0; i < n; i++) {
                if (P >= orders[i][1]) {
                    P -= orders[i][1];
                    totalOrdersFulfilled++;
                } else {
                    break;
                }
            }
            // returning the total number of orders fulfilled
            return totalOrdersFulfilled;
        }
        catch (Exception e) {
            return 0;
        }
    }

    // Another implementation of the solution method with a different approach
    // time complexity is O(nlogn) because of the sorting of the orders based on distance
    public int solution2(int[] D, int[] C, int P) {
        // Pairing distances with corresponding monitor requests
        int n = D.length;
        int[][] orders = new int[n][2];
        for (int i = 0; i < n; i++) {
            orders[i][0] = D[i];
            orders[i][1] = C[i];
        }

        // Sorting the orders based on distance
        Arrays.sort(orders, Comparator.comparingInt(o -> o[0]));

        int totalMonitorsUsed = 0;
        int fulfilledOrders = 0;

        // Iterate through sorted orders and try to fulfill as many as possible
        for (int i = 0; i < n; i++) {
            int monitorsNeeded = orders[i][1];
            if (totalMonitorsUsed + monitorsNeeded <= P) {
                totalMonitorsUsed += monitorsNeeded;
                fulfilledOrders++;
            } else {
                break; // No more orders can be fulfilled without exceeding P
            }
        }

        return fulfilledOrders;
    }

    public static void main(String[] args) {   

        System.out.println(new OrderFulfillToMaximum().solution(new int[]{5,11,1,3}, new int[]{6, 1,3, 2}, 5)); // should return 2

        System.out.println(new OrderFulfillToMaximum().solution(new int[]{6,41,31,23}, new int[]{2, 4, 6, 6}, 3)); 

    }

}

/*

class OrderFulfillToMaximum {

Inner class to represent an order
private static class Order {
    int distance;
    int monitors;

    Order(int distance, int monitors) {
        this.distance = distance;
        this.monitors = monitors;
    }

    }

    /*
     * Calculates the maximum number of orders that can be fulfilled with P
     * monitors.
     * Orders are processed in increasing order of distance from the store.
     * 
     * @param D array of distances from store
     * @param C array of monitor counts for each order
     * @param P total monitors available
     * @return maximum number of orders that can be fulfilled
     * @throws IllegalArgumentException if inputs are invalid
     
    public int solution(int[] D, int[] C, int P) {
        // Input validation
        if (D == null || C == null || D.length != C.length) {
            throw new IllegalArgumentException("Arrays D and C must be non-null and have same length");
        }
        if (P < 0) {
            throw new IllegalArgumentException("Available monitors P must be non-negative");
        }

        int n = D.length;
        if (n == 0)
            return 0;

        // Create order list
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (D[i] < 0 || C[i] < 0) {
                throw new IllegalArgumentException("Distance and monitor count must be non-negative");
            }
            orders.add(new Order(D[i], C[i]));
        }

        // Sort by distance
        orders.sort(Comparator.comparingInt(o -> o.distance));

        // Fulfill orders greedily
        int fulfilledCount = 0;
        int monitorsUsed = 0;

        for (Order order : orders) {
            if (monitorsUsed + order.monitors <= P) {
                monitorsUsed += order.monitors;
                fulfilledCount++;
            } else {
                break;
            }
        }

        return fulfilledCount;
    }

    public static void main(String[] args) {
        OrderFulfillToMaximum solver = new OrderFulfillToMaximum();
        
        // Test case 1: should return 2
        System.out.println(solver.solution(
            new int[]{5, 11, 1, 3}, 
            new int[]{6, 1, 3, 2}, 
            5
        )); // Output: 2
        
        // Test case 2
        System.out.println(solver.solution(
            new int[]{6, 41, 31, 23}, 
            new int[]{2, 4, 6, 6}, 
            3
        ));
    }
}

*/