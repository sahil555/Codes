import java.util.HashMap;
import java.util.Map;

public class FindLongestSubString {
    /*
     * This method is used to find the longest substring without repeating characters
     * The time complexity will be O(n)
     * space complexity will be O(n)
     * // due to this Map<Character, Integer> charIndexMap = new HashMap<>()
     * 
     * @params String s
     * @returns integer
     * 
     */
    public int lengthOfLongestSubstring(String s) {
        // adding base case checks
        if(s == null || s.length() == 0) return 0;

        int maxLength = 0;
        // initializing the map to store the subsequence length of non repeating sub string 
        Map<Character, Integer> charIndexMap = new HashMap<>();

        for(int leftpointer = 0, rightpointer=0; rightpointer < s.length(); rightpointer++){
            char currentCharacter = s.charAt(rightpointer);
            // check already in window
            if(charIndexMap.containsKey(currentCharacter)){
                leftpointer = Math.max(leftpointer, charIndexMap.get(currentCharacter)+1);

            }

            charIndexMap.put(currentCharacter, rightpointer);

            maxLength = Math.max(maxLength, (rightpointer - leftpointer + 1));
        }

        return maxLength;
    }

    public static void main(String[] args) {
        FindLongestSubString solution = new FindLongestSubString();
        
        Map<String, String> testCases = new HashMap<>();
        testCases.put("abcabcbb", "Expected: 3");
        testCases.put("bbbkjascabb", "Expected: 6");
        testCases.put("pwwkepllkosajaskcnw", "Expected: 9"); 
        testCases.put("pwjhakcalcwkaddafafew", "Expected: 3");
        testCases.put("pwwoimknasckew", "Expected: 9");
        testCases.put("sioniknabvchjjpwwkew", "Expected: 9");

        System.out.println("Testing lengthOfLongestSubstring method :");
        for (Map.Entry<String, String> testCase : testCases.entrySet()) {
            String input = testCase.getKey();
            String expected = testCase.getValue();
            int result = solution.lengthOfLongestSubstring(input);
            System.out.println("Input: \"" + input + "\" | " + expected + " | Actual: " + result);
        }
    }
}
