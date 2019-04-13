package level;

public class BoardLevel {
    private boolean[][] BoardLevel;
    private int countBomb;

    /**
     * @param width row length
     * @param height column length
     */
    public BoardLevel(int width, int height) {
        this.countBomb = 0;
        this.BoardLevel = new boolean[height][width];
    }

    /**
     * @return number of squares in a row
     */
    public int getWidth() {
        return this.BoardLevel[0].length;
    }

    /**
     * @return number of squares in a column
     */
    public int getHeight() {
        return this.BoardLevel.length;
    }

    /**
     * @return number of bombs
     */
    public int getcount() {
        return this.countBomb;
    }

    /**
     * @param x coordinate
     * @param y coordinate
     * @return true iff there is a bomb at (x,y)
     */
    public boolean hasBomb(int x, int y) {
        return BoardLevel[y][x];
    }

    /**
     * Checks if the given position is outside the BoardLevel.
     *
     * @param x coordinate
     * @param y coordinate
     * @return true iff (x,y) is outside the BoardLevel
     */
    public boolean outBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight();
    }

    /**
     * Adds a bomb to the given position.
     *
     * @param x coordinate
     * @param y coordinate
     */
    public void addBomb(int x, int y) {
        if (!BoardLevel[y][x]) countBomb++;
        BoardLevel[y][x] = true;
    }

    /**
     * Counts the number of adjacent bombs to a square.
     *
     * @param x coordinate
     * @param y coordinate
     * @return number of bombs next to (x,y)
     */
    public int adjBombs(int x, int y) {
        int counter = 0;
        for (int i = x-1; i < x+2; i++) {
            for (int j = y-1; j < y+2; j++) {
                if (outBounds(i, j) && hasBomb(i,j)) {
                    counter++;
                }
            }
        }
        return counter;
    }
}
