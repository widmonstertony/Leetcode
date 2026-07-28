import java.util.LinkedHashMap;

/*
 * @lc app=leetcode id=146 lang=java
 *
 * [146] LRU Cache
 */

// @lc code=start
class LRUCache {
    private int mCapacity;
    private LinkedHashMap<Integer, Integer> mMap;
    public LRUCache(int capacity) {
        this.mCapacity = capacity;
        // Create a LinkedHashMap object with 
        // capacity(max elements to be stored), 
        // load factor(0.75 is default)
	    // and accessOrder(the ordering mode - true for access-order, false for insertion-order. We are using TRUE)
        mMap = new LinkedHashMap<>(capacity, 0.75f, true);
    }
    
    public int get(int key) {
        return mMap.getOrDefault(key, -1);
    }
    
    public void put(int key, int value) {
        // 如果当前key不在map里，并且capacity已经满了
        // 则删除最后那个element
        if (!mMap.containsKey(key) && mMap.size() >= this.mCapacity)  {
            mMap.remove(mMap.entrySet().iterator().next().getKey());
        }
        mMap.put(key, value);
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */
// @lc code=end

