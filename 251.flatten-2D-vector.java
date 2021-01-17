/*
 * @lc app=leetcode id=251 lang=java
 *
 * [251] Flatten 2D Vector
 */

// @lc code=start
class Vector2D {
    private int currX, currY;
    private int[][] mArr;
    public Vector2D(int[][] v) {
        currX = 0;
        currY = 0;
        mArr = v;
    }
    public int next() {
        if (this.hasNext()) {
            // 把当前数字返回后，把Y指向下一个
            // 下一次再call hasNext的时候会自动检测Y是否到头了
            return mArr[currX][currY++];
        }
        else {
            throw new java.util.NoSuchElementException();
        }
    }
    
    // check是否有next的同时，把x和y移动到next的坐标上
    public boolean hasNext() {
        // 只要当前x是在数组内，并且y已经走到头了
        // 就把x移到下一个，并把y归零
        while (currX < mArr.length && currY == mArr[currX].length) {
            currX++;
            currY = 0;
        }
        // 确认x还是在数组内
        return currX < mArr.length;
    }
}
// 自己写的两个变量解法，乱七八糟的，我菜爆了
// class Vector2D {
//     private int currX, currY;
//     private int[][] mArr;
//     public Vector2D(int[][] v) {
//         currX = 0;
//         currY = -1;
//         mArr = v;
//     }
    
//     public int next() {
//         if (this.hasNext()) {
//             if (this.currY >= this.mArr[currX].length - 1) {
//                 currX += 1;
//                 currY = 0;
//             }
//             else {
//                 currY += 1;
//             }
//             return mArr[currX][currY];
//         }
//         else {
//             return -1;
//         }
//     }
    
//     public boolean hasNext() {
//         if (currX < 0 && mArr.length <= 0) {
//             return false;
//         }
//         if (currX > this.mArr.length - 1) {
//             return false;
//         }
//         if (currX == this.mArr.length - 1 && currY >= this.mArr[currX].length - 1) {
//             return false;
//         }
//         int tmpCurrX = this.currX, tmpCurrY = this.currY;
//         if (tmpCurrY >= this.mArr[tmpCurrX].length - 1) {
//             tmpCurrX += 1;
//             tmpCurrY = 0;
//         }
//         else {
//             tmpCurrY += 1;
//         }
//         if (tmpCurrX > this.mArr.length - 1) {
//             return false;
//         }
//         if (tmpCurrY >= this.mArr[tmpCurrX].length) {
//             this.currX = tmpCurrX;
//             this.currY = tmpCurrY;
//             return this.hasNext();
//         }
//         return true;
//     }
// }

/**
 * Your Vector2D object will be instantiated and called as such:
 * Vector2D obj = new Vector2D(v);
 * int param_1 = obj.next();
 * boolean param_2 = obj.hasNext();
 */
// @lc code=end

