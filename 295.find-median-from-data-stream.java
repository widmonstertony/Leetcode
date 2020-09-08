import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;

/*
 * @lc app=leetcode id=295 lang=java
 *
 * [295] Find Median from Data Stream
 */

// @lc code=start
class MedianFinder {
    List<Integer> mOrderList;
    /** initialize your data structure here. */
    // 大小堆
    // 大堆保存右半段较大的数字，小堆保存左半段较小的数组
    PriorityQueue<Integer> maxHeap;
    PriorityQueue<Integer> minHeap;
    public MedianFinder() {
        // 大堆从小到大排列
        maxHeap = new PriorityQueue<>();
        // 小堆从大到小排列
        minHeap = new PriorityQueue<>(Collections.reverseOrder());
    }
    
    public void addNum(int num) {
        // 把num加进两个堆里
        maxHeap.add(num);
        minHeap.add(maxHeap.poll());
        // 确保大堆元素的数量大于等于小堆的数量
        if (maxHeap.size() < minHeap.size()) {
            maxHeap.add(minHeap.poll());
        }
    }
    
    public double findMedian() {
        // 当大堆元素多时，取大堆首元素为中位数
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        }
        else {
            // 取出大堆小堆的首元素求平均值
            return 0.5 * (maxHeap.peek() + minHeap.peek());
        }
    }
}
// class MedianFinder {
//     List<Integer> mOrderList;
//     /** initialize your data structure here. */
//     public MedianFinder() {
//         // 用linkedlist的话，addNum会快一点，Olog(n),但是findMedian会变成O(n)
//         // mOrderList = new LinkedList<>();
//         // 用ArrayList, addNum变成Ologn + O(n), 但是findMedian就是O(1)了
//         mOrderList = new ArrayList<>();
//     }
    
//     public void addNum(int num) {
//         if (mOrderList.size() == 0) {
//             mOrderList.add(num);
//             return;
//         }
//         // 二分法找到小于等于当前数的那个数，然后把当前的数
//         int left = 0, right = mOrderList.size() - 1;
//         while (left <= right) {
//             int mid = left + (right - left) / 2;
//             if (mOrderList.get(mid) > num) {
//                 right = mid - 1;
//             }
//             else{
//                 left = mid + 1;
//             }
//         }
//         if (right >= mOrderList.size()) {
//             mOrderList.add(num);
//         }
//         else {
//             mOrderList.add(right + 1, num);
//         }
//     }
    
//     public double findMedian() {
//         if (mOrderList.size() % 2 == 0) {
//             return (mOrderList.get(mOrderList.size() / 2 - 1) + mOrderList.get(mOrderList.size() / 2)) / (double)2;
//         }
//         return mOrderList.get(mOrderList.size() / 2);
//     }
// }

/**
 * Your MedianFinder object will be instantiated and called as such:
 * MedianFinder obj = new MedianFinder();
 * obj.addNum(num);
 * double param_2 = obj.findMedian();
 */
// @lc code=end

