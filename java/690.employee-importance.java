/*
 * @lc app=leetcode id=690 lang=java
 *
 * [690] Employee Importance
 */

// @lc code=start
/*
// Definition for Employee.
class Employee {
    public int id;
    public int importance;
    public List<Integer> subordinates;
};
*/

class Solution {
    public int getImportance(List<Employee> employees, int id) {
        Map<Integer, Employee> idMap = new HashMap();
        for (Employee employee : employees) {
            idMap.put(employee.id, employee);  
        }
        return dfs(id, idMap);
    }
    private int dfs(int id, Map<Integer, Employee> idMap) {
        Employee currEmployee = idMap.get(id);
        int currSum = currEmployee.importance;
        for (int  subordinate: currEmployee.subordinates) {
            currSum += dfs(subordinate, idMap);  
        }
        return currSum;
    }
}
// @lc code=end

