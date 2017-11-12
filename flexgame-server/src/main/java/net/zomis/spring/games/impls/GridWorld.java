package net.zomis.spring.games.impls;

public class GridWorld {

    public boolean canMove(int i) {
        Tile current = grid[posY][posX];
        if (current != Tile.EMPTY) {
            return false;
        }
        switch (i) {
            case 0:
                return posAvailable(posX - 1, posY);
            case 1:
                return posAvailable(posX + 1, posY);
            case 2:
                return posAvailable(posX, posY - 1);
            case 3:
                return posAvailable(posX, posY + 1);
            default: throw new IllegalArgumentException("Action not available: " + i);
        }
    }

    private boolean posAvailable(int x, int y) {
        return y >= 0 && y < grid.length && x >= 0 && x < grid[y].length && grid[y][x] != Tile.WALL;
    }

    public MyQLearning.Rewarded<GridWorld> performMove(int action) {
        GridWorld state = new GridWorld();
        state.posX = posX;
        state.posY = posY;
        switch (action) {
            case 0:
                state.posX -= 1;
                break;
            case 1:
                state.posX += 1;
                break;
            case 2:
                state.posY -= 1;
                break;
            case 3:
                state.posY += 1;
                break;
            default:
                throw new IllegalArgumentException("No such action: " + action);
        }
        Tile newPos = grid[state.posY][state.posX];
        double reward = rewardForTile(newPos);

        return new MyQLearning.Rewarded<>(state, reward);
    }

    private double rewardForTile(Tile newPos) {
        switch (newPos) {
            case BAD_GOAL:
                return -1;
            case EMPTY:
                return -0.01;
            case GOAL:
                return 1;
            default:
                throw new IllegalArgumentException("Invalid tile for reward: " + newPos);
        }
    }

    public boolean isFinished() {
        Tile current = grid[posY][posX];
        return current == Tile.BAD_GOAL || current == Tile.GOAL;
    }

    public void print() {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                Tile tile = grid[y][x];
                boolean currentPos = y == posY && x == posX;
                System.out.print(currentPos ? '*' : tile.toChar());
            }
            System.out.println();
        }
    }

    private enum Tile {
        EMPTY('_'), WALL('#'), GOAL('G'), BAD_GOAL('B');

        private final char ch;

        Tile(char ch) {
            this.ch = ch;
        }

        public char toChar() {
            return ch;
        }
    }

    private final Tile[][] grid;
    private int posX;
    private int posY;


    /*
    * EEEE
    * EWEG
    * EWEB
    * EWES
    * EEEE
    *
    * */
    public GridWorld() {
        this.posX = 3;
        this.posY = 3;
        this.grid = new Tile[5][4];
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                grid[y][x] = Tile.EMPTY;
            }
        }
        this.grid[1][1] = Tile.WALL;
        this.grid[2][1] = Tile.WALL;
        this.grid[3][1] = Tile.WALL;
        this.grid[1][3] = Tile.GOAL;
        this.grid[2][3] = Tile.BAD_GOAL;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

}
