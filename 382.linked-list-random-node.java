import java.util.Random;

/*
 * @lc app=leetcode id=382 lang=java
 *
 * [382] Linked List Random Node
 */

// @lc code=start
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    ListNode head;
    // One Pass 只遍历一遍，水滴法
    /** @param head The linked list's head.
    Note that the head is guaranteed to be not null, so it contains at least one node. */
    public Solution(ListNode head) {
        this.head = head;
    }
    
    /** Returns a random node's value. */
    public int getRandom() {
        int res = head.val;
        ListNode curr = head.next;
        int cnt = 2;
        Random rand = new Random();
        while (curr != null) {
            // 保证每个数字的概率相等，一定是在0到cnt之间随机选一个数
            // 只有选到0才会把res变成当前值
            if (rand.nextInt() % cnt == 0) {
                res = curr.val;
            }
            cnt++;
            curr = curr.next;
        }
        return res;
    }


    // Two Pass 遍历两次
    // ListNode head;
    // /** @param head The linked list's head.
    //     Note that the head is guaranteed to be not null, so it contains at least one node. */
    // public Solution(ListNode head) {
    //     this.head = head;
    // }
    
    // /** Returns a random node's value. */
    // public int getRandom() {
    //     int cnt = 0;
    //     ListNode curr = head;
    //     while (curr != null) {
    //         cnt++;
    //         curr = curr.next;
    //     }
    //     Random ran = new Random(); 
    //     cnt = ran.nextInt(cnt);
    //     curr = head;
    //     while (cnt > 0) {
    //         cnt--;
    //         curr = curr.next;
    //     }
    //     return curr.val;
    // }
}

/**
 * Your Solution object will be instantiated and called as such:
 * Solution obj = new Solution(head);
 * int param_1 = obj.getRandom();
 */
// @lc code=end

