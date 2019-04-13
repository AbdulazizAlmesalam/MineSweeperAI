package level;

import java.util.Random;

public class RandomGenerator {

    private static final int SPLIT_BOUNDARY = 5;
    private Random random;

    public RandomGenerator() {
        this.random = new Random();
    }

    /**
     * createBoards a random BoardLevel with given parameters. There are 4 different options,
     * set by the possible values of the boolean parameters.
     *
     * @param width row length of BoardLevel
     * @param height column length of BoardLevel
     * @param bombs number of bombs to be included in the BoardLevel
     * @param noSur true => no bomb is sur only by bombs
     * @param evenSpread true => recursively: split BoardLevel and generate in parts
     * @return BoardLevel that is generated
     */
    public BoardLevel createBoard(int width, int height, int bombs, boolean noSur, boolean evenSpread) {
        return (noSur && evenSpread) ?
                createBoardEvenSpreadnoSur(width, height, bombs) :
                (noSur ?
                        createBoardnoSur(width, height, bombs) :
                        (evenSpread ?
                                createBoardEvenSpread(width, height, bombs) :
                                createBoard(width, height, bombs)
                        )
                );
    }


    /**
     * No restrictions.
     *
     * @param width row length
     * @param height column length
     * @param bombs number of bombs
     * @return generated BoardLevel
     */
    private BoardLevel createBoard(int width, int height, int bombs) {
        BoardLevel BoardLevel = new BoardLevel(width, height);
        while (BoardLevel.getcount() < bombs) {
            int x = this.random.nextInt(width);
            int y = this.random.nextInt(height);
            BoardLevel.addBomb(x, y);
        }
        return BoardLevel;
    }

    /**
     *
     * @param width row length
     * @param height column length
     * @param bombs number of bombs
     * @return generated BoardLevel
     */
    private BoardLevel createBoardnoSur(int width, int height, int bombs) {
        BoardLevel BoardLevel = new BoardLevel(width, height);
        while (BoardLevel.getcount() < bombs) {
            int x = this.random.nextInt(width);
            int y = this.random.nextInt(height);
            if (sur(BoardLevel, x, y)) continue;
            BoardLevel.addBomb(x, y);
        }
        return BoardLevel;
    }

    /**
     *
     * @param width row length
     * @param height column length
     * @param bombs number of bombs
     * @return generated BoardLevel
     */
    private BoardLevel createBoardEvenSpread(int width, int height, int bombs) {
        BoardLevel BoardLevel = new BoardLevel(width, height);
        evenSpreadHelper(BoardLevel, 0, width - 1, 0, height - 1, bombs, false);
        return BoardLevel;
    }

    /**
     *
     * @param width row length
     * @param height column length
     * @param bombs number of bombs
     * @return generated BoardLevel
     */
    private BoardLevel createBoardEvenSpreadnoSur(int width, int height, int bombs) {
        BoardLevel BoardLevel = new BoardLevel(width, height);
        evenSpreadHelper(BoardLevel, 0, width - 1, 0, height - 1, bombs, true);
        return BoardLevel;
    }

    /**
     * A helper function for recursion that takes boundaries as parameter which it
     * changes and calls self with.
     *
     * @param BoardLevel the BoardLevel which is added to
     * @param minX lower horizontal bound
     * @param maxX upper horizontal bound
     * @param minY lower vertical bound
     * @param maxY upper vertical bound
     * @param bombs number of bombs
     * @param noSur true => no bomb is sur only by bombs
     */
    private void evenSpreadHelper(BoardLevel BoardLevel, int minX, int maxX, int minY, int maxY, int bombs, boolean noSur) {
        if ((maxX - minY > SPLIT_BOUNDARY) && (maxY - minY > SPLIT_BOUNDARY)) {
            // Split both horizontally and vertically

            int halfX = (maxX - minX) >> 1;
            int halfY = (maxY - minY) >> 1;
            int quartBombs = bombs >> 2;
            int bombReminder = bombs % 4; // reminder, if any, is spread to first, second and/or third

            // Each quadrant
            evenSpreadHelper(BoardLevel, minX, minX + halfX, minY, minY + halfY,
                    bombReminder > 0 ? quartBombs + 1: quartBombs, noSur);
            evenSpreadHelper(BoardLevel, minX, minX + halfX , minY + halfY + 1, maxY,
                    bombReminder > 1 ? quartBombs + 1 : quartBombs, noSur);
            evenSpreadHelper(BoardLevel, minX + halfX + 1, maxX, minY, minY + halfY,
                    bombReminder > 2 ? quartBombs + 1 : quartBombs, noSur);
            evenSpreadHelper(BoardLevel, minX + halfX + 1, maxX, minY + halfY + 1, maxY,
                    quartBombs, noSur);

        } else if (maxX - minX > SPLIT_BOUNDARY) {
            // Split horizontally

            int half = (maxX - minX) >> 1;
            int halfBombs = bombs >> 1;
            evenSpreadHelper(BoardLevel, minX, minX + half, minY, maxY, bombs - halfBombs, noSur);
            evenSpreadHelper(BoardLevel, minX + half + 1, maxX, minY, maxY, halfBombs, noSur);

        } else if (maxY - minY > SPLIT_BOUNDARY) {
            // Split vertically

            int half = (maxY - minY) >> 1;
            int halfBombs = bombs >> 1;
            evenSpreadHelper(BoardLevel, minX, maxX, minY, minY + half, bombs - halfBombs, noSur);
            evenSpreadHelper(BoardLevel, minX, maxX, minY + half + 1, maxY, halfBombs, noSur);

        } else {
            // Split neither horizontally and vertically

            int bombGoals = BoardLevel.getcount() + bombs;
            if (noSur) {
                while (BoardLevel.getcount() < bombGoals) {
                    int x = randInterval(minX, maxX);
                    int y = randInterval(minY, maxY);
                    if (sur(BoardLevel, x, y)) continue;
                    BoardLevel.addBomb(x, y);
                }
            } else {
                while (BoardLevel.getcount() < bombGoals) {
                    BoardLevel.addBomb(randInterval(minX, maxX), randInterval(minY, maxY));
                }
            }
        }
    }

    /**
     * @param BoardLevel the BoardLevel to check
     * @param x coordinate
     * @param y coordinate
     * @return false iff at least one of (x,y)'s neighbours is not a bomb
     */
    private boolean sur(BoardLevel BoardLevel, int x, int y) {
        for (int i = x - 1; i < x + 2; i++) {
            for (int j = y - 1; j < y + 2; j++) {
                if (BoardLevel.outBounds(i,j) && !BoardLevel.hasBomb(i,j)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a random integer value within boundaries.
     *
     * @param a lower bound
     * @param b upper bound
     * @return a random int from [a,b]
     */
    private int randInterval(int a, int b) {
        return this.random.nextInt(b-a+1)+a;
    }
}
