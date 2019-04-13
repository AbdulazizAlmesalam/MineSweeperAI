package agent;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class Grid {
    private Position[][] board;

    /**
     * Constructs a 2d array of all possible positions.
     *
     * @param width width of board
     * @param height height of board
     */
    public Grid(int width, int height) {
        this.board = IntStream.range(0, width).mapToObj(x ->
                IntStream.range(0, height).mapToObj(y ->
                        new Position(x, y)).toArray(Position[]::new)
        ).toArray(Position[][]::new);
    }

    /**
     * Getter for allocated position, given coordinates.
     *
     * @param x coordinate
     * @param y coordinate
     * @return Position
     */
    public Position getVar(int x, int y) {
        return this.board[x][y];
    }

    /**
     * Returns a list of all neighbours for a given position.
     *
     * @param x coordinate
     * @param y coordinate
     * @return ArrayList of Position
     */
    public ArrayList<Position> getNeighbours(int x, int y) {
        ArrayList<Position> returnVal = new ArrayList<>();
        for (int i = x - 1; i < x + 2; i++) {
            for (int j = y - 1; j < y + 2; j++) {
                if ((i != x || j != y) && j >= 0 && i >= 0 && i < this.board.length && j < this.board[0].length) {
                    returnVal.add(this.board[i][j]);
                }
            }
        }
        return returnVal;
    }
}
