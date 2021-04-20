import java.util.Set;

/*
 * @lc app=leetcode id=381 lang=java
 *
 * [381] Insert Delete GetRandom O(1) - Duplicates allowed
 */

// @lc code=start
class RandomizedCollection {
    // 用Set来代表同一数字出现过的所有坐标
    Map<Integer, Set<Integer>> hashIdx;
    List<Integer> valList;
    Random random;
    /** Initialize your data structure here. */
    public RandomizedCollection() {
        hashIdx = new HashMap<>();
        valList = new ArrayList<>();
        random = new Random();
    }
    
    /** Inserts a value to the collection. Returns true if the collection did not already contain the specified element. */
    public boolean insert(int val) {
        Set<Integer> currSet = hashIdx.getOrDefault(val, new HashSet<>());
        valList.add(val);
        currSet.add(valList.size() - 1);
        hashIdx.put(val, currSet);
        return currSet.size() == 1;
    }
    
    /** Removes a value from the collection. Returns true if the collection contained the specified element. */
    public boolean remove(int val) {
        Set<Integer> currSet = hashIdx.getOrDefault(val, new HashSet<>());
        if (currSet.isEmpty()) {
            return false;
        }
        // 把要删除的数字和数组最后一位调换位置再删掉
        int lastNum = valList.get(valList.size() - 1);
        int removeIdx = currSet.iterator().next();
        currSet.remove(removeIdx);
        // 记得把数组最后一位的数字的位置也在hashtable里更新
        Set<Integer> lastNumSet = hashIdx.getOrDefault(lastNum, new HashSet<>());
        lastNumSet.add(removeIdx);
        lastNumSet.remove(valList.size() - 1);
        hashIdx.put(lastNum, lastNumSet);
        valList.set(removeIdx, lastNum);
        valList.remove(valList.size() - 1);
        return true;
    }
    
    /** Get a random element from the collection. */
    public int getRandom() {
        return valList.get(random.nextInt(valList.size()));
    }
}

/**
 * Your RandomizedCollection object will be instantiated and called as such:
 * RandomizedCollection obj = new RandomizedCollection();
 * boolean param_1 = obj.insert(val);
 * boolean param_2 = obj.remove(val);
 * int param_3 = obj.getRandom();
 */
// @lc code=end

