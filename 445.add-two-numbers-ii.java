import java.util.Stack;

/*
 * @lc app=leetcode id=445 lang=java
 *
 * [445] Add Two Numbers II
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
        Stack<ListNode> l1Stack = new Stack<>();
        Stack<ListNode> l2Stack = new Stack<>();
        while (l1 != null) {
            l1Stack.push(l1);
            l1 = l1.next;
        }
        while (l2 != null) {
            l2Stack.push(l2);
            l2 = l2.next;
        }
        ListNode currNode = null;
        int carry = 0;
        while (!l1Stack.isEmpty() || !l2Stack.isEmpty()) {
            int currSum = 0;
            if (!l1Stack.isEmpty()) {
                currSum += l1Stack.pop().val;
            }
            if (!l2Stack.isEmpty()) {
                currSum += l2Stack.pop().val;
            }
            currSum += carry;
            if (currSum > 9) {
                currSum -= 10;
                carry = 1;
            }
            else {
                carry = 0;
            }
            currNode = new ListNode(currSum, currNode);
        }
        if (carry != 0) {
            currNode = new ListNode(carry, currNode);
        }
        return currNode;
    }
}
// @lc code=end

