/*
 * @lc app=leetcode id=21 lang=java
 *
 * [21] Merge Two Sorted Lists
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
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode dummyNode = new ListNode(-1);
        ListNode currNode = dummyNode;
        while (l1 != null && l2 != null) {
            // 如果l1的值大于l2，则把l2放到当前node后面
            if (l1.val > l2.val) {
                currNode.next = l2;

                l2 = l2.next;
            }
            // 否则把l1放到当前node后面
            else {
                currNode.next = l1;
                l1 = l1.next;
            }
            currNode = currNode.next;
        }
        if (l1 != null) {
            currNode.next = l1;
        }
        if (l2 != null) {
            currNode.next = l2;
        }
        return dummyNode.next;

    }
    // 直接在l1上操作，转了个弯面试时这样写容易犯错
    // public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    //     // 默认主链是l1，当前node就是在遍历l1
    //     ListNode dummyNode = new ListNode(-1, l1);
    //     ListNode currNode = dummyNode;
    //     while (l1 != null && l2 != null) {
    //         // 如果l1的值大于l2，则把l2插入l1前面
    //         if (l1.val > l2.val) {
    //             // 把l2放到当前node的后面
    //             // 并把原本当前node的后面接到l2的后面

    //             ListNode nextNode = l2.next;

    //             l2.next = currNode.next;

    //             currNode.next = l2;

    //             l2 = nextNode;
    //         }
    //         // 否则不需要对当前l1的node做任何修改
    //         // l1移到下一个node
    //         else {
    //             l1 = l1.next;
    //         }

    //         currNode = currNode.next;
    //     }
    //     if (l2 != null) {
    //         currNode.next = l2;
    //     }
    //     return dummyNode.next;

    // }
}
// @lc code=end

