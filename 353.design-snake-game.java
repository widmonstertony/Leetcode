/*
 * @lc app=leetcode id=353 lang=java
 *
 * [353] Design Snake Game
 */

// @lc code=start
class SnakeGame {
    /** Initialize your data structure here.
        @param width - screen width
        @param height - screen height 
        @param food - A list of food positions
        E.g food = [[1,1], [1,0]] means the first food is positioned at [1,1], the second is at [1,0]. */
    Queue<List<Integer>> foodQueue;
    LinkedList<SnakeBody> snake;
    int width, height;
    private class SnakeBody {
        int x, y;
        SnakeBody(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    public SnakeGame(int width, int height, int[][] food) {
        snake = new LinkedList<>();
        foodQueue = new LinkedList<>();
        for(int[] foodCoors: food) {
            List<Integer> coorList = new ArrayList<>();
            coorList.add(foodCoors[1]);
            coorList.add(foodCoors[0]);
            foodQueue.offer(coorList);
        }
        snake.offer(new SnakeBody(0, 0));
        this.width = width;
        this.height = height;
    }
    
    /** Moves the snake.
        @param direction - 'U' = Up, 'L' = Left, 'R' = Right, 'D' = Down 
        @return The game's score after the move. Return -1 if game over. 
        Game over when snake crosses the screen boundary or bites its body. */
    public int move(String direction) {
        if (direction.length() != 1) {
            return -1;
        }
        SnakeBody newHead = new SnakeBody(snake.peek().x, snake.peek().y);
        switch (direction.charAt(0)) {
            case 'U' :
                newHead.y -= 1;
                break;
            case 'L' :
                newHead.x -= 1;
                break;
            case 'R' :
                newHead.x += 1;
                break;
            case 'D' :
                newHead.y += 1;
                break;
            default:
                return -1;
        }
        if (newHead.x < 0 || newHead.y < 0 || newHead.x >= width || newHead.y >= height) {
            return -1;
        }
        // 如果新的位置有食物，则不要删掉之前的尾巴，那个就相当于是新增的body
        if (!foodQueue.isEmpty() && newHead.x == foodQueue.peek().get(0) && newHead.y == foodQueue.peek().get(1)) {
            foodQueue.poll();
        }
        else {
            snake.removeLast();
        }
        // 确保新的位置没有蛇的身体
        Iterator<SnakeBody> it = snake.iterator();
        while (it.hasNext()) {
            SnakeBody currBody = it.next();
            if (currBody.x == newHead.x && currBody.y == newHead.y) {
                return -1;
            }
        }
        // 然后把新的蛇头加进去
        snake.addFirst(newHead);
        return snake.size() - 1;
    }

    // 脑子有坑写出来的，不要看，会memory limit，虽然没bug
    // /** Initialize your data structure here.
    //     @param width - screen width
    //     @param height - screen height 
    //     @param food - A list of food positions
    //     E.g food = [[1,1], [1,0]] means the first food is positioned at [1,1], the second is at [1,0]. */
    // int[][] board;
    // SnakeBody snakeHead;
    // SnakeBody snakeTail;
    // Queue<List<Integer>> foodQueue;
    // int lengthOfSnake;
    // private class SnakeBody {
    //     int x, y;
    //     SnakeBody nextBody;
    //     SnakeBody(int x, int y) {
    //         this.x = x;
    //         this.y = y;
    //     }
    //     void setNextBody(SnakeBody nextBody) {
    //         this.nextBody = nextBody;
    //     }
    // }
    // public SnakeGame(int width, int height, int[][] food) {
    //     board = new int[width][height];
    //     foodQueue = new LinkedList<>();
    //     for(int[] foodCoors: food) {
    //         List<Integer> coorList = new ArrayList<>();
    //         coorList.add(foodCoors[1]);
    //         coorList.add(foodCoors[0]);
    //         foodQueue.offer(coorList);
    //     }
    //     snakeHead = new SnakeBody(0, 0);
    //     board[0][0] = 1;
    //     snakeTail = snakeHead;
    //     lengthOfSnake = 1;
    // }
    
    // /** Moves the snake.
    //     @param direction - 'U' = Up, 'L' = Left, 'R' = Right, 'D' = Down 
    //     @return The game's score after the move. Return -1 if game over. 
    //     Game over when snake crosses the screen boundary or bites its body. */
    // public int move(String direction) {
    //     if (direction.length() != 1) {
    //         return -1;
    //     }
    //     int[] directions = new int[2];
    //     switch (direction.charAt(0)) {
    //         case 'U' :
    //             directions[0] = 0;
    //             directions[1] = -1;
    //             break;
    //         case 'L' :
    //             directions[0] = -1;
    //             directions[1] = 0;
    //             break;
    //         case 'R' :
    //             directions[0] = 1;
    //             directions[1] = 0;
    //             break;
    //         case 'D' :
    //             directions[0] = 0;
    //             directions[1] = 1;
    //             break;
    //         default:
    //             return -1;
    //     }

    //     if (moveTo(directions[0] + snakeHead.x, directions[1] + snakeHead.y, snakeHead)) {
    //         return lengthOfSnake - 1;
    //     }
    //     else {
    //         return -1;
    //     }
    // }
    // private boolean moveTo(int x, int y, SnakeBody currBody) {
    //     if (x < 0 || y < 0 || x >= board.length || y >= board[x].length) {
    //         return false;
    //     }

    //     SnakeBody prevTail = new SnakeBody(snakeTail.x, snakeTail.y);
    //     if (currBody.nextBody != null) {
    //         if (!moveTo(currBody.x, currBody.y, currBody.nextBody)) {
    //             return false;
    //         }
    //     }
    //     if (!foodQueue.isEmpty() && x == foodQueue.peek().get(0) && y == foodQueue.peek().get(1)) {
    //         foodQueue.poll();
    //         lengthOfSnake++;
    //         SnakeBody newSnakeTail = new SnakeBody(prevTail.x, prevTail.y);
    //         snakeTail.setNextBody(newSnakeTail);
    //         snakeTail = newSnakeTail;
    //         board[snakeTail.x][snakeTail.y] = 1;

    //     }
    //     if (board[x][y] == 1 && currBody == snakeHead) {
    //         return false;
    //     }

    //     if (currBody == snakeTail) {
    //         board[currBody.x][currBody.y] = 0;
    //     }
    //     currBody.x = x;
    //     currBody.y = y;
    //     board[x][y] = 1;
    //     return true;
    // }
}

/**
 * Your SnakeGame object will be instantiated and called as such:
 * SnakeGame obj = new SnakeGame(width, height, food);
 * int param_1 = obj.move(direction);
 */
// @lc code=end

