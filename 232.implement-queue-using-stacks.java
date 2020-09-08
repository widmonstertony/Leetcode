/*
 * @lc app=leetcode id=232 lang=java
 *
 * [232] Implement Queue using Stacks
 */

// @lc code=start
class MyQueue {
    Stack<Integer> mStack;
    /** Initialize your data structure here. */
    public MyQueue() {
        mStack = new Stack<Integer>();
    }
    
    /** Push element x to the back of queue. */
    public void push(int x) {
        mStack.add(x);
    }
    
    /** Removes the element from in front of queue and returns that element. */
    public int pop() {
        Stack<Integer> newStack = new Stack<Integer>();
        int currTop = mStack.peek();
        while (!mStack.isEmpty()) {
            currTop = mStack.pop();
            if (!mStack.isEmpty()) {
                newStack.add(currTop);
            }
        }
        while (!newStack.isEmpty()) {
            mStack.add(newStack.pop());
        }
        return currTop;
    }
    
    /** Get the front element. */
    public int peek() {
        Stack<Integer> newStack = new Stack<Integer>();
        int currTop = mStack.peek();
        while (!mStack.isEmpty()) {
            currTop = mStack.pop();
            newStack.add(currTop);
        }
        while (!newStack.isEmpty()) {
            mStack.add(newStack.pop());
        }
        return currTop;
    }
    
    /** Returns whether the queue is empty. */
    public boolean empty() {
        return mStack.size() == 0;
    }
}

/**
 * Your MyQueue object will be instantiated and called as such:
 * MyQueue obj = new MyQueue();
 * obj.push(x);
 * int param_2 = obj.pop();
 * int param_3 = obj.peek();
 * boolean param_4 = obj.empty();
 */
// @lc code=end

