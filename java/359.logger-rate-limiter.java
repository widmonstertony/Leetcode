/*
 * @lc app=leetcode id=359 lang=java
 *
 * [359] Logger Rate Limiter
 */

// @lc code=start
class Logger {

    Map<String, Integer> messageMap;
    /** Initialize your data structure here. */
    public Logger() {
        messageMap = new HashMap<>();
    }
    
    /** Returns true if the message should be printed in the given timestamp, otherwise returns false.
        If this method returns false, the message will not be printed.
        The timestamp is in seconds granularity. */
    public boolean shouldPrintMessage(int timestamp, String message) {
        if (messageMap.containsKey(message) && messageMap.get(message) + 10 > timestamp) {
            return false;
        }
        else {
            messageMap.put(message, timestamp);
            return true;
        }
    }
}

/**
 * Your Logger object will be instantiated and called as such:
 * Logger obj = new Logger();
 * boolean param_1 = obj.shouldPrintMessage(timestamp,message);
 */
// @lc code=end

