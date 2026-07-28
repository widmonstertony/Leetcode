/*
 * @lc app=leetcode id=19 lang=java
 *
 * [19] Remove Nth Node From End of List
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
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(-1, head);
        ListNode first = dummy, second = dummy;
        // 两个pointer，先让第一个走n步
        while (n > 0) {
            if (first.next == null) {
                return head;
            }
            first = first.next;
            n--;
        }
        // 然后再一起走 直到第一个走到终点
        while (first.next != null) {
            first = first.next;
            second = second.next;
        }
        // 把第二个后面那个删掉
        second.next = second.next.next;
        return dummy.next;
    }
}
// @lc code=end

