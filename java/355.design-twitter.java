import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/*
 * @lc app=leetcode id=355 lang=java
 *
 * [355] Design Twitter
 */

// @lc code=start
class Twitter {
    private class TimeTweet {
        // Tweet该有的一些属性
        private int id;
        private int userId;
        // 这个题里不需要text tho
        private String tweetText;
        // TimeTweet需要知道它的order
        private int mOrderIdx;
        public TimeTweet(int userId, int id, int orderIdx) {
            this.userId = userId;
            this.id = id;
            this.mOrderIdx = orderIdx;
        }
        public int getOrder() {
            return this.mOrderIdx;
        }
        public int getId() {
            return this.id;
        }
    }
    
    private int mOrderCnt;
    // Pull的解法：key是用户A的userId，set里的是用户A点了关注的所有的人的userID
    private Map<Integer, Set<Integer>> mFollowerMap;
    // Pull的解法：key是用户A的userId，list是他发的所有的推特
    private Map<Integer, List<TimeTweet>> mUserTweets;
    private final int MAX_TWEET_EACH_TIME = 10;
    /** Initialize your data structure here. */
    public Twitter() {
        this.mFollowerMap = new HashMap<>();
        this.mUserTweets = new HashMap<>();
        this.mOrderCnt = 0;
    }
    
    /** Compose a new tweet. */
    public void postTweet(int userId, int tweetId) {
        TimeTweet newTweet = new TimeTweet(userId, tweetId, this.mOrderCnt++);
        if (!this.mUserTweets.containsKey(userId)) {
            this.mUserTweets.put(userId, new ArrayList<TimeTweet>());
        }
        this.mUserTweets.get(userId).add(newTweet);
    }
    
    /** Retrieve the 10 most recent tweet ids in the user's news feed. Each item in the news feed must be posted by users who the user followed or by the user herself. Tweets must be ordered from most recent to least recent. */
    public List<Integer> getNewsFeed(int userId) {
        List<TimeTweet> resTweetList = new ArrayList<TimeTweet>();
        // 首先从自己发的帖里拿出10个
        if (this.mUserTweets.containsKey(userId)) {
            List<TimeTweet> allTweetsList = this.mUserTweets.get(userId);
            int numTweetsNeeded = MAX_TWEET_EACH_TIME;
            for (int i = allTweetsList.size() - 1; i >= 0 && numTweetsNeeded > 0; i--, numTweetsNeeded--) {
                resTweetList.add(allTweetsList.get(i));
            }
        }
        // 再从自己follow的人里每个人拿出10个
        if (this.mFollowerMap.containsKey(userId)) {
            Set<Integer> followerSet = this.mFollowerMap.get(userId);
            Iterator setIterator = followerSet.iterator();
            while (setIterator.hasNext()) {
                Integer currFollowerUserId = (Integer) setIterator.next();
                if (!this.mUserTweets.containsKey(currFollowerUserId)) {
                    this.mUserTweets.put(currFollowerUserId, new ArrayList<>());
                }
                List<TimeTweet> allTweetsList = this.mUserTweets.get(currFollowerUserId);
                int numTweetsNeeded = MAX_TWEET_EACH_TIME;
                for (int i = allTweetsList.size() - 1; i >= 0 && numTweetsNeeded > 0; i--, numTweetsNeeded--) {
                    resTweetList.add(allTweetsList.get(i));
                }
            }
        }
        // 把所有这些帖子按时间sort后取最新的10个
        Collections.sort(resTweetList, (a, b) -> {
            return b.getOrder() - a.getOrder();
        });
        List<Integer> resList = new ArrayList<>();
        int numTweetsNeeded = MAX_TWEET_EACH_TIME;
        for (int i = 0; numTweetsNeeded > 0 && i < resTweetList.size() ; i++, numTweetsNeeded--) {
            resList.add(resTweetList.get(i).getId());
        }
        return resList;
    }
    
    /** Follower follows a followee. If the operation is invalid, it should be a no-op. */
    public void follow(int followerId, int followeeId) {
        if (!this.mFollowerMap.containsKey(followerId)) {
            this.mFollowerMap.put(followerId, new HashSet<Integer>());
        }
        // 有点恶心，确保自己不能follow自己，不然有一个case过不了
        if (followeeId != followerId) {
            this.mFollowerMap.get(followerId).add(followeeId);
        }
    }
    
    /** Follower unfollows a followee. If the operation is invalid, it should be a no-op. */
    public void unfollow(int followerId, int followeeId) {
        if (this.mFollowerMap.containsKey(followerId)) {
            this.mFollowerMap.get(followerId).remove(followeeId);
        }
    }
}

/**
 * Your Twitter object will be instantiated and called as such:
 * Twitter obj = new Twitter();
 * obj.postTweet(userId,tweetId);
 * List<Integer> param_2 = obj.getNewsFeed(userId);
 * obj.follow(followerId,followeeId);
 * obj.unfollow(followerId,followeeId);
 */
// @lc code=end

