/*
 * @lc app=leetcode id=269 lang=java
 *
 * [269] Alien Dictionary
 */

// @lc code=start
class Solution {
    // Topological sorting
    public String alienOrder(String[] words) {
        // 如果只有一个词，直接返回
        if (words.length == 1) {
            return words[0];
        }
        // map里每一个字符串，对应着应该排在他后面的所有字符
        // 用set因为每次添加都要确认是否之前已经添加过，避免算错indegree
        Map<Character, Set<Character>> wordMap = new HashMap<>();
        int[] indegree = new int[26];
        for (int i = 0; i < words.length - 1; i++) {
            int j = 0;
            String currWord = words[i], nextWord = words[i + 1];
            // 把两个词前面相等的部分的每个字符都放进Map
            while (j < currWord.length() && j < nextWord.length() && currWord.charAt(j) == nextWord.charAt(j)) {
                char fromChar = currWord.charAt(j);
                Set<Character> charSet = wordMap.getOrDefault(fromChar, new HashSet<>());
                wordMap.put(fromChar, charSet);
                j++;
            }
            // 找到第一个不相等的两个字符
            if (j < currWord.length() && j < nextWord.length()) {
                char fromChar = currWord.charAt(j), toChar = nextWord.charAt(j);
                Set<Character> charSet = wordMap.getOrDefault(fromChar, new HashSet<>());
                // 把排在后面的字符放进前面字符的set里，并且计入后面的字符的indegree数量加一
                if (!charSet.contains(toChar)) {
                    charSet.add(toChar);
                    indegree[toChar - 'a']++;
                }
                wordMap.put(fromChar, charSet);
            }
            // 如果前一个字符串长度比后面的字符串长，并且后面字符串是前面字符串的子字符串
            // 说明这个无解
            else if (j == nextWord.length() && currWord.length() > nextWord.length()) {
                return "";
            }
            // 把剩下的字符串都放进去，创建空set
            for (int k = j; k < currWord.length(); k++) {
                char restChar = currWord.charAt(k);
                if (!wordMap.containsKey(restChar)) {
                    wordMap.put(restChar, new HashSet<>());
                }
            }
            for (int k = j; k < nextWord.length(); k++) {
                char restChar = nextWord.charAt(k);
                if (!wordMap.containsKey(restChar)) {
                    wordMap.put(restChar, new HashSet<>());
                }
            }
        }
        Queue<Character> charQueue = new LinkedList();
        // 把indegree为0的都拿出来放进queue，作为开始
        for (Character currKey : wordMap.keySet()) {
            if (indegree[currKey - 'a'] == 0) {
                charQueue.add(currKey);
            }
        }
        int beginSize = charQueue.size();
        StringBuilder resBuilder = new StringBuilder();
        while (!charQueue.isEmpty()) {
            char currChar = charQueue.poll();
            resBuilder.append(currChar);
            // 把排在当前char之后的字符串全部indegree减一，如果减为0就放进queue
            Set<Character> charSet = wordMap.get(currChar);
            if (charSet != null) {
                for (char eachChar : charSet) {
                    indegree[eachChar - 'a']--;
                    if (indegree[eachChar - 'a'] == 0) {
                        charQueue.add(eachChar);
                    }
                }
            }
        }
        // 如果结果的长度和所有字符的数量不同，说明无解
        if (resBuilder.length() != wordMap.keySet().size()) {
            return "";
        }
        return resBuilder.toString();
    }
}
// @lc code=end

