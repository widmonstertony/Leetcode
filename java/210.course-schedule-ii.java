import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
 * @lc app=leetcode id=210 lang=java
 *
 * [210] Course Schedule II
 */

// @lc code=start
class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        if (numCourses <= 0) {
            return null;
        }

        int[] inDegree = new int[numCourses];
        Arrays.fill(inDegree, 0);

        for (int[] prerequisite: prerequisites) {
            inDegree[prerequisite[0]]++;
        }

        List<Integer> resList = new ArrayList<>();
        Queue<Integer> nodeQueue = new LinkedList<>();

        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                nodeQueue.add(i);
            }
        }

        while (!nodeQueue.isEmpty()) {
            int source = nodeQueue.poll();
            resList.add(source);

            for (int[] prerequisite: prerequisites) {
                int nextCourse = prerequisite[0], fromCourse = prerequisite[1];

                if (fromCourse == source) {
                    inDegree[nextCourse]--;

                    if (inDegree[nextCourse] == 0) {
                        nodeQueue.add(nextCourse);
                    }
                    
                }

            }
        }

        if (resList.size() != numCourses) {
            return new int[0];
        }

        int[] resArr = new int[resList.size()];

        for (int i = 0; i < resList.size(); i++) {
            resArr[i] = resList.get(i);
        }

        return resArr;
    }
}
// @lc code=end

