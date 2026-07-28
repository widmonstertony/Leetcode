/*
 * @lc app=leetcode id=929 lang=java
 *
 * [929] Unique Email Addresses
 */

// @lc code=start
class Solution {
    public int numUniqueEmails(String[] emails) {
        Set<String> emailSet = new HashSet<>();
        for (String email : emails) {
            String[] splitEmails = email.split("@");
            if (splitEmails.length != 2) {
                continue;
            }
            StringBuilder emailStrBuiler = new StringBuilder();
            for (char eachChar : splitEmails[0].toCharArray()) {
                if (eachChar == '.') {
                    continue;
                }
                if (eachChar == '+') {
                    break;
                }
                emailStrBuiler.append(eachChar);
            }
            emailStrBuiler.append('@' + splitEmails[1]);
            emailSet.add(emailStrBuiler.toString());
        }
        return emailSet.size();
    }
}
// @lc code=end

