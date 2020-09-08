import java.util.LinkedList;
import java.util.Queue;

/*
 * @lc app=leetcode id=225 lang=java
 *
 * [225] Implement Stack using Queues
 */

// @lc code=start
class MyStack {
    Queue<Integer> mQueue;
    /** Initialize your data structure here. */
    public MyStack() {
        mQueue = new LinkedList<>();
    }
    
    /** Push element x onto stack. */
    public void push(int x) {
        mQueue.add(x);
    }
    
    /** Removes the element on top of the stack and returns that element. */
    public int pop() {
        Queue<Integer> newQueue = new LinkedList<>();
        int currNum = -1;
        while (!mQueue.isEmpty()) {
            currNum = mQueue.poll();
            if (!mQueue.isEmpty()) {
                newQueue.add(currNum);
            }
        }
        mQueue = newQueue;
        return currNum;
    }
    
    /** Get the top element. */
    public int top() {
        Queue<Integer> newQueue = new LinkedList<>();
        int currNum = -1;
        while (!mQueue.isEmpty()) {
            currNum = mQueue.poll();
            newQueue.add(currNum);
        }
        mQueue = newQueue;
        return currNum;
    }
    
    /** Returns whether the stack is empty. */
    public boolean empty() {
        return mQueue.size() == 0;
    }
}

/**
 * Your MyStack object will be instantiated and called as such:
 * MyStack obj = new MyStack();
 * obj.push(x);
 * int param_2 = obj.pop();
 * int param_3 = obj.top();
 * boolean param_4 = obj.empty();
 */
// @lc code=end

