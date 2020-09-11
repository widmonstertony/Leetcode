/*
 * @lc app=leetcode id=160 lang=java
 *
 * [160] Intersection of Two Linked Lists
 */

// @lc code=start
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */
public class Solution {
    // public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
    //     if (headA == null || headB == null) return null;
    //     ListNode a = headA, b = headB;
    //     while (a != b) {
    //         a = (a != null) ? a.next : headB;
    //         b = (b != null) ? b.next : headA;
    //     }
    //     return a;
    // }
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) return null;
        ListNode ptrA = headA, ptrB = headB;
        while (true) {
            // 如果两个node相等，则尝试能否一直走到底，不能的话就不是答案
            if (ptrA == ptrB) {
                ListNode ansNode = ptrA;
                while (ptrA.next != null && ptrB.next != null) {
                    ptrA = ptrA.next;
                    ptrB = ptrB.next;
                    if (ptrA != ptrB) {
                        break;
                    }
                }
                if (ptrA != ptrB) {
                    ansNode = null;
                }
                return ansNode;
            }
            // 如果不是的话，就往后移一位
            else {
                if (ptrA.next != null && ptrB.next != null) {
                    ptrA = ptrA.next;
                    ptrB = ptrB.next;
                }
                // 如果两个中有一个已经到末尾了，则移去另一个列表的开头
                else if (ptrA.next == null && ptrB.next != null) {
                    ptrA = headB;
                    ptrB = ptrB.next;
                } 
                else if (ptrB.next == null && ptrA.next != null){
                    ptrB = headA;
                    ptrA = ptrA.next;
                }
                // 如果两个都到末尾了，说明没有答案
                else {
                    break;
                }
            }
        }
        return null;
    }
}
// @lc code=end

