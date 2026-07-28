--
-- @lc app=leetcode id=577 lang=mysql
--
-- [577] Employee Bonus
--

-- @lc code=start
# Write your MySQL query statement below
# 这个不会show那些在employee表但是不在bonus表里的那些员工，所以需要left join
-- SELECT a.name, b.bonus from Employee a, Bonus b WHERE a.empId = b.empId AND b.bonus < 1000 OR b.bonus is null

-- 首先要确保name一定会显示出来，无论有没有bonus
-- 一定要确保bonus是低于1000的，所以用where
SELECT name, bonus from Employee left Join Bonus on Employee.empId = Bonus.empId WHERE Bonus.bonus < 1000 OR Bonus.bonus is null


-- @lc code=end

