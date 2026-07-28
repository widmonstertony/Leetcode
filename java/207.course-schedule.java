import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/*
 * @lc app=leetcode id=207 lang=java
 *
 * [207] Course Schedule
 */

// @lc code=start
class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        if (numCourses <= 0) {
            return false;
        }
        if (prerequisites == null || prerequisites.length == 0) {
            return true;
        }
        int[] inDegree = new int[numCourses];
        Arrays.fill(inDegree, 0);
        for (int[] prerequisite : prerequisites) {
            inDegree[prerequisite[0]]++;
        }

        List<Integer> resList = new ArrayList<>();
        Queue<Integer> courseQueue = new LinkedList<>();

        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                courseQueue.add(i);
            }
        }

        while(!courseQueue.isEmpty()) {
            int source = courseQueue.poll();
            resList.add(source);

            for (int[] prerequisite : prerequisites) {
                int fromCourse = prerequisite[1], nextCourse = prerequisite[0];
                if (fromCourse == source) {
                    inDegree[nextCourse]--;
                    if (inDegree[nextCourse] == 0) {
                        courseQueue.add(nextCourse);
                    }
                }

            }
        }
        return resList.size() == numCourses;
    }
}
// @lc code=end

