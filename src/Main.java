
import java.util.ArrayList;
import java.util.Stack;

public class Main
{
    public static void main(String[] args)
    {
        for (int iter = 0; iter < 10000; iter++)
        {
            Board board = new Board();
            board.magic = Math.random();
            Stack<Cell> stack = new Stack<>();
            stack.push(board.grid[(int) (Math.random() * board.size)][(int) (Math.random() * board.size)]);
//        stack.push(board.grid[1][1]);
            while (stack.size() < board.size * board.size)
            {
                Cell move = stack.peek().move();
                if (move != null)
                    stack.push(move);
                else
                {
                    break;
//                    for (int i = 0; i < board.size; i++)
//                        for (int o = 0; o < board.size; o++)
//                            board.grid[i][o].order = 0;
//
//                    {
//                        System.out.printf("%.4f, %d, %d, %d\n", board.magic, stack.size(), stack.firstElement().x, stack.firstElement().y);
//                        board.magic = Math.random();
////                        int o = 1;
////                        for (Cell c : stack)
////                            board.grid[c.y][c.x].order = o++;
////                    System.out.println(board);
//                    }
//
//                    int loop = (int) ((stack.size() - 1));
//                    for (int i = 0; i < loop; i++)
//                    {
//                        Cell removed = stack.pop();
//                        removed.value = 0;
//                        removed.moves--;
//                        board.grid[stack.peek().y][stack.peek().x].moves--;
//                    }
                }
            }
            System.out.printf("%.4f, %d, %d, %d\n", board.magic, stack.size(), stack.firstElement().x, stack.firstElement().y);
//            int o = 1;
//            for (Cell c : stack)
//                board.grid[c.y][c.x].order = o++;
//            System.out.println(board);
        }
    }
}

class Cell
{
    public int x;
    public int y;
    public int value = 0;
    public Board board;
    public int moves = -1;
    public int order = 0;

    public Cell(int x, int y, Board board)
    {
        this.x = x;
        this.y = y;
        this.board = board;
        countMoves();
    }

    public int countMoves()
    {
        if (moves != -1)
            return moves;
        moves = 0;
        for (int i = 0; i < 8; i++)
        {
            int[] xOffsets = {-2, -2, -1, -1, 1, 1, 2, 2};
            int[] yOffsets = {-1, 1, 2, -2, 2, -2, 1, -1};
            int nextX = x + xOffsets[i];
            int nextY = y + yOffsets[i];
            if (nextX >= 0 && nextX < board.size)
                if (nextY >= 0 && nextY < board.size)
                    moves++;
        }
        return moves;
    }

    public Cell move()
    {
        int min = Integer.MAX_VALUE;
        Cell move = null;
        int nextX = -1;
        int nextY = -1;
        ArrayList<Cell> moveList = new ArrayList<>();
        for (int i = 0; i < 8; i++)
        {
            int[] xOffsets = {-2, -2, -1, -1, 1, 1, 2, 2};
            int[] yOffsets = {-1, 1, 2, -2, 2, -2, 1, -1};
            nextX = x + xOffsets[i];
            nextY = y + yOffsets[i];
            if (nextX >= 0 && nextX < board.size)
                if (nextY >= 0 && nextY < board.size)
                    if (board.grid[nextY][nextX].value == 0)
                    {
                        move = board.grid[nextY][nextX];
                        min = Math.min(move.moves, min);
                        moveList.add(move);
                    }
        }
        board.grid[y][x].moves--;
        if (moveList.size() == 0)
            return null;
        if (Math.random() < board.magic)
        {
            ArrayList<Cell> removeList = new ArrayList<>();
            for (Cell m : moveList)
                if (m.moves > min)
                    removeList.add(m);
            for (Cell m : removeList)
                moveList.remove(m);
        }
        move = moveList.get((int) (Math.random() * (moveList.size())));
        board.grid[move.y][move.x].moves--;
        move.value = 1;
        this.value = 1;
        return move;
    }
}

class Board
{
    public int size = 5;
    public Cell[][] grid = new Cell[size][size];
    public double magic = 1;

    public Board()
    {
        for (int i = 0; i < size; i++)
            for (int o = 0; o < size; o++)
                grid[i][o] = new Cell(o, i, this);
    }

    @Override
    public String toString()
    {
        String s = "";
        for (int i = 0; i < size; i++)
        {
            for (int o = 0; o < size; o++)
                s += String.format("%2d ", grid[i][o].order);
            s += "\n";
        }
        return s;
    }
}