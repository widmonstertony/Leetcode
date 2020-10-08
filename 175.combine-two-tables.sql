--
-- @lc app=leetcode id=175 lang=mysql
--
-- [175] Combine Two Tables
--

-- @lc code=start
# Write your MySQL query statement below
-- 用left join, 代表如果有地址就拿出地址，没有就只显示名字
SELECT FirstName, LastName, City, State from Person left join 
Address on Person.PersonId = Address.PersonId


-- @lc code=end

