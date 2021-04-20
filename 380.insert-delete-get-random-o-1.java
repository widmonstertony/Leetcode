/*
 * @lc app=leetcode id=380 lang=java
 *
 * [380] Insert Delete GetRandom O(1)
 */

// @lc code=start
class RandomizedSet {
    Map<Integer, Integer> hashIdx;
    List<Integer> valList;
    Random random;

    /** Initialize your data structure here. */
    public RandomizedSet() {
        hashIdx = new HashMap<>();
        valList = new ArrayList<>();
        random = new Random();
    }
    
    /** Inserts a value to the set. Returns true if the set did not already contain the specified element. */
    public boolean insert(int val) {
        if (hashIdx.containsKey(val)) {
            return false;
        }
        valList.add(val);
        hashIdx.put(val, valList.size() - 1);
        return true;
    }
    
    /** Removes a value from the set. Returns true if the set contained the specified element. */
    public boolean remove(int val) {
        if (!hashIdx.containsKey(val)) {
            return false;
        }
        // 把要删除的数字和数组最后一位调换位置再删掉
        int lastNum = valList.get(valList.size() - 1);
        hashIdx.put(lastNum, hashIdx.get(val));
        valList.set(hashIdx.get(val), lastNum);
        valList.remove(valList.size() - 1);
        hashIdx.remove(val);
        return true;
    }
    
    /** Get a random element from the set. */
    public int getRandom() {
        return valList.get(random.nextInt(valList.size()));
    }
}

/**
 * Your RandomizedSet object will be instantiated and called as such:
 * RandomizedSet obj = new RandomizedSet();
 * boolean param_1 = obj.insert(val);
 * boolean param_2 = obj.remove(val);
 * int param_3 = obj.getRandom();
 */
// @lc code=end

