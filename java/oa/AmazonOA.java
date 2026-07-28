import java.util.Arrays;
import java.util.PriorityQueue;

public class AmazonOA {
    // Optimizing Box Weights
    public static List<Integer> optimizingBoxWeights(List<Integer> arr) {
        // WRITE YOUR BRILLIANT CODE HERE
        int[] sum = new int[arr.size()];
        Collections.sort(arr);
        for (int i = 0; i < arr.size(); i++) {
            if (i == 0) {
                sum[i] = arr.get(i);
            }
            else {
                sum[i] = sum[i - 1] + arr.get(i);
            }
        }
        int divIdx = 0;
        List<Integer> resList = new ArrayList<>();
        for (int i = arr.size() - 1; i >= 0; i--) {
            resList.add(arr.get(i));
            if (sum[sum.length - 1] - sum[i - 1] > sum[i - 1]) {
                divIdx = i;
                break;
            }
        }
        return resList;
    }
    
    // Storage Optimization 找到连续长度最大的高度和宽度
    public static int storageOptimization(int n, int m, List<Integer> h, List<Integer> v) {
        // WRITE YOUR BRILLIANT CODE HERE
        return findCumulateLongest(h) * findCumulateLongest(v);
    }
    public static int findCumulateLongest(List<Integer> nums) {
        int prevSum = 1;
        int max = 1;
        for (int i = 1; i < nums.size(); i++) {
            int currNum = nums.get(i);
            int prevNum = nums.get(i - 1);
            if (currNum - prevNum == 1) {
                prevSum++;
                max = Math.max(max, prevSum);
            }
            else {
                prevSum = 1;
            }
        }
        return max + 1;
    }
    // number of items
    public static List<Integer> numberOfItems(String s, List<List<Integer>> ranges) {
        // WRITE YOUR BRILLIANT CODE HERE
        // Traverse the array two times, use dp[N][3] to 
        // store numbers of item up to now, nearest left/ right gate
        // leftClosestGate[i] 代表i最靠左的Gate
        // rightClosestGate[i] 代表i最靠右的Gate
        // sum[i] 代表到i为止的cummulate的物品的总数
        int[] sum = new int[s.length()];
        int[] leftClosestGate = new int[s.length()];
        int[] rightClosestGate = new int[s.length()];
        int cumSum = 0;
        int preGateIdx = -1;
        for (int i = 0; i < s.length(); i++) {
            char currChar = s.charAt(i);
            if (currChar == '|') {
                if (i == 0) {
                    sum[i] = 0;
                }
                else {
                    sum[i] = cumSum;
                }
                preGateIdx = i;
            }
            else {
                if (i == 0) {
                    sum[i] = 0;
                }
                else {
                    sum[i] = sum[i - 1];
                }
                cumSum++;
            }
            leftClosestGate[i] = preGateIdx;
        }
        preGateIdx = -1;
        for (int i = s.length() - 1; i >= 0; i--) {
            char currChar = s.charAt(i);
            if (currChar == '|') {
                preGateIdx = i;
            }
            rightClosestGate[i] = preGateIdx;
        }
        List<Integer> resList = new ArrayList<>();
        for (List<Integer> range: ranges) {
            int rightGateIdx = leftClosestGate[range.get(1)];
            int leftGateIdx = rightClosestGate[range.get(0)];
            if (leftGateIdx == -1 || rightGateIdx == -1) {
                resList.add(0);
            }
            else {
                resList.add(sum[rightGateIdx] - sum[leftGateIdx]);
            }
        }
        return resList;
    }
    // Amazon fresh delivery leetcode 973
    public static int[][] closestKLocations(int[][] allLocations, int k) {
        // 从大到小排列所有距离
        PriorityQueue<int[]> locationPQ = new PriorityQueue<>((a, b) -> {
            int distanceA = a[0] * a[0] + a[1] * a[1];
            int distanceB = b[0] * b[0] + b[1] * b[1];
            if (distanceB - distanceA == 0) {
                return b[0] - a[0];
            }
            return distanceB - distanceA;
        });
        for (int[] location: loactions) {
            locationPQ.offer(location);
            // 如果满了，就把head移出，也就是最大的那个
            if (locationPQ.size() > k) {
                locationPQ.poll();
            }
        }
        int[][] res = new int[k][2];
        for (int i = 0; i < res.length; i++) {
            res[i] = locationPQ.poll();
        }
        return res;
    }
    // CloudFront Caching 
    // Union-find: 先把每个edge上的两个nodes两两union起来.
    public static int connectedSum(int n, List<List<Integer>> edges) {
        int[] root = new int[n];
        Arrays.fill(root, -1);
        for (List<Integer> edge: edges) {
            union(edge.get(0), edge.get(1), root);
        }
        int res = 0;
        for (int i = 0; i < n; i++) {
            if (root[n] < 0) {
                int size = -root[n];
                res += Math.ceil(Math.sqrt(size));
            }
        }
        return res;
    }
    private boolean union(int x, int y, int[] root) {
        int rootOfX = find(x, root), rootOfY = find(y, root);
        if (rootOfX == rootOfY) {
            return false;
        }
        int sizeOfX = root[rootOfX], sizeOfY = root[rootOfY];
        if (sizeOfX > sizeOfY) {
            root[rootOfX] = rootOfY;
            root[rootOfY] += sizeOfX;
        }
        else {
            root[rootOfY] = rootOfX;
            root[rootOfX] += sizeOfY;
        }
        return true;
    }
    private int find(int x, int[] root) {
        int rootValue = root[x];
        if (rootValue < 0) {
            return x;
        }
        if (root[rootValue] >= 0) {
            rootValue = find(rootValue, root);
            root[x] = rootValue;
        }
        return rootValue;
    }

    // Demolition Robot, Move The Obstacle
    // BFS找最短路径
    public static int moveObstacle(List<List<Integer>> lot) {
        // WRITE YOUR BRILLIANT CODE HERE
        final int[][] directions = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        Queue<List<Integer>> moveQueue = new LinkedList<>();
        moveQueue.add(Arrays.asList(0, 0));
        int res = 0;
        int currLevelNum = 1;
        while (!moveQueue.isEmpty()) {
            if (currLevelNum == 0) {
                currLevelNum = moveQueue.size();
                res++;
            }
            List<Integer> currMove = moveQueue.poll();
            currLevelNum--;
            int currX = currMove.get(0), currY = currMove.get(1);
            if (lot.get(currX).get(currY) == 9) {
                break;
            }
            if (lot.get(currX).get(currY) == 0) {
                continue;
            }
            for (int[] direction: directions) {
                int nextX = currX + direction[0];
                int nextY = currY + direction[1];
                if (nextX < 0 || nextY < 0 || nextX >= lot.size() || nextY >= lot.get(nextX).size()) {
                    continue;
                }
                moveQueue.add(Arrays.asList(nextX, nextY));
            }
        }
        return res;
    }
    // Reorder log
    public String[] reorderLogFiles(String[] logs) {
        Arrays.sort(logs, (a, b) -> {
            String[] aLetter = a.split(" ");
            String[] bLetter = b.split(" ");
            boolean isADigitLog = aLetter[1].charAt(0) >= '0' && aLetter[1].charAt(0) <= '9';
            boolean isBDigitLog = bLetter[1].charAt(0) >= '0' && bLetter[1].charAt(0) <= '9';
            if (isADigitLog && !isBDigitLog) {
                return 1;
            }
            else if (!isADigitLog && isBDigitLog) {
                return -1;
            }
            else if (!isADigitLog && !isBDigitLog) {
                int aWordIdx = 1, bWordIdx = 1;
                while (true) {
                    if (aWordIdx == aLetter.length && bWordIdx == bLetter.length) {
                        break;
                    }
                    if (aWordIdx == aLetter.length) {
                        return -1;
                    }
                    if (bWordIdx == bLetter.length) {
                        return 1;
                    }
                    int cmp = aLetter[aWordIdx].compareTo(bLetter[bWordIdx]);
                    if (cmp != 0) {
                        return cmp;
                    }
                    aWordIdx++;
                    bWordIdx++;
                }
                return aLetter[0].compareTo(bLetter[0]);
            }
            else {
                return 0;
            }
        });
        return logs;
    }

    // Number of Swaps to Sort
    static int merge(int arr[], int temp[],
            int left, int mid, int right)
    {
        int inv_count = 0;

        /* i is index for left subarray*/
        int i = left;

        /* i is index for right subarray*/
        int j = mid;

        /* i is index for resultant merged subarray*/
        int k = left;

        while ((i <= mid - 1) && (j <= right))
        {
            if (arr[i] <= arr[j])
                temp[k++] = arr[i++];
            else
            {
                temp[k++] = arr[j++];

                /* this is tricky -- see above /
                 explanation diagram for merge()*/
                inv_count = inv_count + (mid - i);
            }
        }

        /* Copy the remaining elements of left
        subarray (if there are any) to temp*/
        while (i <= mid - 1)
            temp[k++] = arr[i++];

        /* Copy the remaining elements of right
        subarray (if there are any) to temp*/
        while (j <= right)
            temp[k++] = arr[j++];

        /*Copy back the merged elements
        to original array*/
        for (i=left; i <= right; i++)
            arr[i] = temp[i];

        return inv_count;
    }
 
    // An auxiliary recursive function that
    // sorts the input array and returns
    // the number of inversions in the array.
    static int _mergeSort(int arr[], int temp[],
                             int left, int right)
    {
        int mid, inv_count = 0;
        if (right > left)
        {
            // Divide the array into two parts and
            // call _mergeSortAndCountInv() for
            // each of the parts
            mid = (right + left)/2;

            /* Inversion count will be sum of
            inversions in left-part, right-part
            and number of inversions in merging */
            inv_count = _mergeSort(arr, temp,
                                    left, mid);

            inv_count += _mergeSort(arr, temp,
                                    mid+1, right);

            /*Merge the two parts*/
            inv_count += merge(arr, temp,
                            left, mid+1, right);
        }

        return inv_count;
    }
    public static int numberOfSwapsToSort(List<Integer> nums) {
       int n = nums.size();
        int temp[] = new int[n];
        int[] arr = nums.stream().mapToInt(i->i).toArray();
        return _mergeSort(arr, temp, 0, n - 1);
    }

    // Shopping Options 
    public static int numberOfOptions(List<Integer> a, List<Integer> b, List<Integer> c, List<Integer> d, int limit) {
        // WRITE YOUR BRILLIANT CODE HERE
        List<Integer> cdSumList = new ArrayList();
        // 先把 C和D的总和放进hashmap
        for (int i = 0; i < c.size(); i++) {
            for (int j = 0; j < d.size(); j++) {
                int currSum = c.get(i) + d.get(j);
                cdSumList.add(currSum);
            }
        }
        Collections.sort(cdSumList);
        int res = 0;
        // 然后计算A和B的和，在hashmap里找负的那个情况
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                int currSum = a.get(i) + b.get(j);
                int left = 0, right = cdSumList.size() - 1;
                while (left <= right) {
                    int mid = left + (right - left) / 2;
                    if (cdSumList.get(mid) > (limit - currSum)) {
                        right = mid - 1;
                    }
                    else {
                        left = mid + 1;
                    }
                }
                right++;
                res += right;
            }
        }
        return res;
    }
}
