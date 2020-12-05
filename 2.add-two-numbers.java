/*
 * @lc app=leetcode id=2 lang=java
 *
 * [2] Add Two Numbers
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
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        return addTwoNumbers(l1, l2, 0);
    }
    private ListNode addTwoNumbers(ListNode l1, ListNode l2, int carry) {
        ListNode head = new ListNode();
        if (l1 == null && l2 == null) {
            if (carry == 0) {
                return null;
            }
            else {
                head.val = carry;
                return head;
            }
        }
        ListNode l1Next = null, l2Next = null;
        int sum = 0;
        if(l1 != null) {
            l1Next = l1.next;
            sum += l1.val;
        }
        if (l2 != null) {
            l2Next = l2.next;
            sum += l2.val;
        }
        sum += carry;
        if (sum > 9) {
            carry = 1;
            sum -= 10;
        }
        else {
            carry = 0;
        }
        head.val = sum;
        ListNode nextNode = addTwoNumbers(l1Next, l2Next, carry);
        head.next = nextNode;
        return head;
    }
}
// @lc code=end

