import java.util.HashMap;
import java.util.Map;

/*
 * @lc app=leetcode id=170 lang=java
 *
 * [170] Two Sum III - Data structure design
 */

// @lc code=start
class TwoSum {
    Map<Integer, Integer> cntMap;
    /** Initialize your data structure here. */
    public TwoSum() {
        cntMap = new HashMap<>();
    }
    
    /** Add the number to an internal data structure.. */
    public void add(int number) {
        cntMap.put(number, cntMap.getOrDefault(number, 0) + 1);
    }
    
    /** Find if there exists any pair of numbers which sum is equal to the value. */
    public boolean find(int value) {
        for (int number: cntMap.keySet()) {
            if (cntMap.get(number) <= 0) {
                continue;
            }
            cntMap.put(number, cntMap.get(number) - 1);
            if (cntMap.getOrDefault(value - number, 0) > 0) {
                cntMap.put(number, cntMap.get(number) + 1);
                return true;
            }
            cntMap.put(number, cntMap.get(number) + 1);
        }
        return false;
    }
}

/**
 * Your TwoSum object will be instantiated and called as such:
 * TwoSum obj = new TwoSum();
 * obj.add(number);
 * boolean param_2 = obj.find(value);
 */
// @lc code=end

