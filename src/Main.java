import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        double randomMin = 0.7;
        {
            Histogram hist = new Histogram();
            System.out.printf("%3d, ", 0);
            System.out.println(hist.getX());
        }
        int end;
        int size = 6;
        end = size * size;
        for (int start = 1; start <= end; start++)
        {
            System.out.printf("%3d, ", start);
            Histogram hist = new Histogram();
            String filename = String.format("%03d-%dx%d-min=%.2f.txt", start, size, size, randomMin);
            if (new File(filename).exists())
            {
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                String output;
                while ((output = reader.readLine()) != null)
                    hist.add(Double.parseDouble(output));
                reader.close();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            for (int iter = 0; iter < 100000; iter++)
            {
                Board board = new Board(size);
                board.magic = Math.random() * (1 - randomMin) + randomMin;
                Stack<Cell> stack = new Stack<>();
//            stack.push(board.grid[(int) (Math.random() * board.size)][(int) (Math.random() * board.size)]);
                stack.push(board.grid[(start - 1) % board.size][(start - 1) / board.size]);
                while (stack.size() < board.size * board.size)
                {
                    Cell move = stack.peek().move();
                    if (move != null) stack.push(move);
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
                if (stack.size() == size * size)
                {
                    hist.add(board.magic);
                    writer.write(String.format("%.5f\n", board.magic));
                }
//            System.out.printf("%.4f, %d\n", board.magic, stack.size());
//            System.out.printf("%.4f, %d, %d, %d\n", board.magic, stack.size(), stack.firstElement().x, stack.firstElement().y);
//            int o = 1;
//            for (Cell c : stack)
//                board.grid[c.y][c.x].order = o++;
//            System.out.println(board);
            }
            System.out.println(hist);
            writer.close();
//            System.out.printf("%f\t%d\t%f\n", sum, count, count > 0 ? sum / count : 0);
        }
    }
}

class Histogram
{
    double yMin = 0.7;
    double yMax = 1;
    int steps = 25;
    double[] x = new double[steps];
    int[] y = new int[steps];

    public Histogram()
    {
        for (int i = 0; i < steps; i++)
            x[i] = yMin + ((double) i * (yMax - yMin)) / steps;
    }

    public void add(double value)
    {
        int i;
        for (i = 0; i < steps && value > x[i]; i++) ;
        i--;
        if (i < 0)
            return;
        y[i]++;
    }

    public String getX()
    {
        String o = "";
        for (double i : x)
            o += String.format("%.5f, ", i);
        return o;
    }

    @Override
    public String toString()
    {

        String o = "";
        int max = 1;

        for (int i : y)
            max = Math.max(i, max);

        for (int i : y)
            if (i == max)
                o += String.format("\u001B[96m%7d,\u001B[0m", i);
            else
                o += String.format("%7d, ", i);

        return o;
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
        if (moves != -1) return moves;
        moves = 0;
        for (int i = 0; i < 8; i++)
        {
            int[] xOffsets = {-2, -2, -1, -1, 1, 1, 2, 2};
            int[] yOffsets = {-1, 1, 2, -2, 2, -2, 1, -1};
            int nextX = x + xOffsets[i];
            int nextY = y + yOffsets[i];
            if (nextX >= 0 && nextX < board.size) if (nextY >= 0 && nextY < board.size) moves++;
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
                if (nextY >= 0 && nextY < board.size) if (board.grid[nextY][nextX].value == 0)
                {
                    move = board.grid[nextY][nextX];
                    min = Math.min(move.moves, min);
                    moveList.add(move);
                }
        }
        board.grid[y][x].moves--;
        if (moveList.size() == 0) return null;
        if (Math.random() < board.magic)
        {
            ArrayList<Cell> removeList = new ArrayList<>();
            for (Cell m : moveList)
                if (m.moves > min) removeList.add(m);
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
    public int size;
    public Cell[][] grid;
    public double magic = 1;

    public Board(int size)
    {
        grid = new Cell[size][size];
        this.size = size;
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