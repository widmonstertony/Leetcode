import java.util.Map;

/*
 * @lc app=leetcode id=288 lang=java
 *
 * [288] Unique Word Abbreviation
 */

// @lc code=start
class ValidWordAbbr {
    Map<String, String> dicMap;
    public ValidWordAbbr(String[] dictionary) {
        dicMap = new HashMap<>();
        for (String word: dictionary) {
            String currAbbr = getAbbreviation(word);
            // 如果当前缩略词已经在Map里并且不是当前词，说明当前缩略词一定不会是unique
            // 用空字符来表示不是unique
            if (dicMap.containsKey(currAbbr) && !dicMap.get(currAbbr).equals(word)) {
                dicMap.put(currAbbr, "");
            }
            else {
                dicMap.put(currAbbr, word);
            }
        }
    }
    private String getAbbreviation(String word) {
        if (word == null) {
            return new String(" ");
        }
        if (word.length() <= 2) {
            return word;
        }
        return word.charAt(0) + "" + (word.length() - 2) + "" + word.charAt(word.length() - 1);
    }
    
    public boolean isUnique(String word) {
        String currAbbr = getAbbreviation(word);
        if (!dicMap.containsKey(currAbbr)) {
            return true;
        }
        if (dicMap.get(currAbbr).equals(word)) {
            return true;
        }
        else {
            return false;
        }
    }
    // Map<String, Set<String>> dicMap;
    // public ValidWordAbbr(String[] dictionary) {
    //     dicMap = new HashMap<>();
    //     for (String word: dictionary) {
    //         String currAbbr = getAbbreviation(word);
    //         Set<String> currSet = dicMap.getOrDefault(currAbbr, new HashSet<String>());
    //         currSet.add(word);
    //         dicMap.put(currAbbr, currSet);
    //     }
    // }
    // private String getAbbreviation(String word) {
    //     if (word == null) {
    //         return new String(" ");
    //     }
    //     if (word.length() <= 2) {
    //         return word;
    //     }
    //     return word.charAt(0) + "" + (word.length() - 2) + "" + word.charAt(word.length() - 1);
    // }
    
    // public boolean isUnique(String word) {
    //     String currAbbr = getAbbreviation(word);
    //     if (!dicMap.containsKey(currAbbr)) {
    //         return true;
    //     }
    //     Set<String> currSet = dicMap.get(currAbbr);
    //     if (currSet.contains(word) && currSet.size() == 1) {
    //         return true;
    //     }
    //     else {
    //         return false;
    //     }
    // }
}

/**
 * Your ValidWordAbbr object will be instantiated and called as such:
 * ValidWordAbbr obj = new ValidWordAbbr(dictionary);
 * boolean param_1 = obj.isUnique(word);
 */
// @lc code=end

