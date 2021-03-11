/*
 * @lc app=leetcode id=346 lang=java
 *
 * [346] Moving Average from Data Stream
 */

// @lc code=start
class MovingAverage {
    LinkedList<Integer> windowList;
    int mMaxSize;
    int mCurrSum;
    /** Initialize your data structure here. */
    public MovingAverage(int size) {
        windowList = new LinkedList<>();
        mMaxSize = size;
        mCurrSum = 0;
    }
    
    public double next(int val) {
        if (windowList.size() == mMaxSize) {
            mCurrSum -= windowList.removeFirst();
        }
        windowList.add(val);
        mCurrSum += val;
        return mCurrSum * 1.0 / windowList.size();
    }
}

/**
 * Your MovingAverage object will be instantiated and called as such:
 * MovingAverage obj = new MovingAverage(size);
 * double param_1 = obj.next(val);
 */
// @lc code=end

