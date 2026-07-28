/*
 * @lc app=leetcode id=735 lang=java
 *
 * [735] Asteroid Collision
 */

// @lc code=start
class Solution {
    public int[] asteroidCollision(int[] asteroids) {
        List<Integer> asSt = new ArrayList<>();
        for (int i = 0; i < asteroids.length; i++) {
            int currAsteroid = asteroids[i];
            if (currAsteroid > 0) {
                asSt.add(currAsteroid);
            }
            // 如果当前stack里最后的是负的，或者为空，则直接把当前的放进去
            else if (asSt.isEmpty() || asSt.get(asSt.size() - 1) < 0) {
                asSt.add(currAsteroid);
            }
            // 如果当前stack里最后的比当前的数字的绝对值小于或者等于
            else if (asSt.get(asSt.size() - 1) <= Math.abs(currAsteroid)) {
                // 因为当前数字一定是负值，意味着最后那个在碰撞后消失了
                // 如果小的话，意味着当前行星在碰撞后还会存在
                // 所以i往前移一个确保当前行星能再次被遍历到
                if (asSt.get(asSt.size() - 1) < -currAsteroid) {
                    i--;
                }
                // 把最后那个碰撞消失的行星移走
                asSt.remove(asSt.size() - 1);
            }
        }
        int[] res = new int[asSt.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = asSt.get(i);
        }
        return res;
    }
}
// @lc code=end

