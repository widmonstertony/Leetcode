/*
 * @lc app=leetcode id=328 lang=java
 *
 * [328] Odd Even Linked List
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
    public ListNode oddEvenList(ListNode head) {
        if (head == null) {
            return head;
        }
        // 把odd和even分成两条分别添加
        ListNode oddCurr = head, evenCurr = head.next, evenHead = evenCurr;
        while (evenCurr != null && evenCurr.next != null) {
            // 先把odd连好，把odd指针移过去
            oddCurr.next = evenCurr.next;
            oddCurr = oddCurr.next;
            // 再把even指针连好，再移到下一个
            evenCurr.next = oddCurr.next;
            evenCurr = evenCurr.next;
        }
        oddCurr.next = evenHead;
        return head;
    }
}
// @lc code=end

