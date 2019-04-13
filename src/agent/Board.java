package agent;

import java.util.*;

public class Board {

    public static final byte UNKNOWN = -1;
    public static final byte BOMB = 10;

    private Map<Position, ConstraintInfo> constraintP;
    private byte[][] board;

    private Set<Position> bombSet;
    private Set<Position> deleteSet;

    /**
     * Read the board with all the squares as unchecked
     *
     * @param width length of row
     * @param height length of column
     */
    public Board(int width, int height) {
        this.bombSet = new HashSet<>();
        this.deleteSet = new HashSet<>();
        this.constraintP = new HashMap<>();
        board = new byte[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                board[i][j] = UNKNOWN;
            }
        }
    }

    /**
     * Allocate bomb at the x and y co-ordinates. Constraints in the position will
     * remove this variable and decrease the bomb counter by one. The adjacent neighbouring
     * constraint will be sent as a parameter to the helper method further simplification of the algorithm.
     *
     * @param x coordinate
     * @param y coordinate
     * @param grid memory allocated for all possible positions
     * @param moves no of moves
     * @param bombs position that will be marked as the bomb on board
     */
    public void setBomb(int x, int y, Grid grid, Set<Position> moves, Set<Position> bombs) {
        board[x][y] = BOMB;
        bombs.add(grid.getVar(x,y));
        for (Position position : grid.getNeighbours(x,y)) {
            ConstraintInfo info;
            if ((info = constraintP.get(position)) != null) {
                info.decadjBombs();
                info.deleteVar(grid.getVar(x, y));
                store(info, position, moves);
            }
        }
    }

    /**
     * Same as setBomb but empties temp sets when done.
     *
     * @param x coordinate
     * @param y coordinate
     * @param grid pre-allocated memory for all possible positions
     * @param moves the set of pending moves for the agent
     * @param bombs a set of position for the agent to mark on GUI
     */
    public void setManual(int x, int y, Grid grid, Set<Position> moves, Set<Position> bombs) {
        setBomb(x, y, grid, moves, bombs);
        emptySets(grid, moves, bombs);
    }

    /**
     * Select X and Y Co-ordinates to have no bomb. All the neighbours were explored and unknowns made into a variable
     * If there is a constraint in any of the neighbouring position, Co-ordinate of X and Y gets removed
     * from the variable and the constraints sent to the helper function. We add the constraints to the pendings if
     * if the formed constraint is trivial. Otherwise a new constraint is added to system
     *
     * @param x Co-Ordinate
     * @param y Co-Ordinate
     * @param adjacent No. of Adjacent bombs
     * @param grid Allocated memory for positions
     * @param moves Pending moves set
     * @param bombs Bombs to mark in GUI set
     */
    public void setAdjacent(int x, int y, int adjacent, Grid grid, Set<Position> moves, Set<Position> bombs) {
        board[x][y] = (byte)adjacent;
        Set<Position> newVar = new HashSet<>();
        for (Position position : grid.getNeighbours(x,y)) {
            if (board[position.getX()][position.getY()] == UNKNOWN) {
                newVar.add(position);
            } else if (board[position.getX()][position.getY()] == BOMB) {
                adjacent--;
            } else {
                ConstraintInfo info;
                if ((info = constraintP.get(position)) != null) {
                    info.deleteVar(grid.getVar(x, y));
                    store(info, position, moves);
                }
            }
        }
        if (newVar.size() == adjacent) {
            for (Position position : newVar) {
                bombSet.add(position);
            }
        }
        else if (adjacent == 0) moves.addAll(newVar);
        else this.constraintP.put(grid.getVar(x, y), new ConstraintInfo(newVar, adjacent));

        // Handling of temporary sets
        emptySets(grid, moves, bombs);
    }

    /**
     * @return Board mapping according to constraints
     */
    public Map<Position, ConstraintInfo> getconstraintP() {
        return this.constraintP;
    }

    /**
     * @return Board Perspective
     */
    public byte[][] getBoard() {
        return this.board;
    }

    /**
     * Updation of not found bomb grids. It'll get updated if we remove them from pending moves.
     *
     * @param grid Allocated memory for positions
     * @param moves Pending moves
     * @param bombs Unmarked Grid of Bombs
     */
    private void emptySets(Grid grid, Set<Position> moves, Set<Position> bombs) {
        while (!bombSet.isEmpty()) {
            Iterator<Position> it = bombSet.iterator();
            Position bomb = it.next();
            it.remove();
            if (this.board[bomb.getX()][bomb.getY()] != Board.BOMB) {
                setBomb(bomb.getX(), bomb.getY(), grid, moves, bombs);
            }
        }
        for (Position pos : this.deleteSet) {
            this.constraintP.remove(pos);
        }
    }

    /**
     * Constraints where variable is removed. It also searches if the variable is
     * trivial or empty. We will add all the variables to the pending moves if no bombs are present. If the variables contains all bomb sets,
     * we will add all the bomb list to the temporary list of variables. For the three cases mentioned above.
     * we select the position of removing temporary set, which will be used later for removing the constraints.
     *
     * @param info constraint
     * @param position coordinates
     * @param moves Pending moves set
     */
    private void store(ConstraintInfo info, Position position, Set<Position> moves) {
        if (info.isEmpty()) {
            this.deleteSet.add(position);
        } else if (info.noBombs()) {
            moves.addAll(info.getnotKnownNeighbours());
            this.deleteSet.add(position);
        } else if (info.allBombs()) {
            for (Position pos : info.getnotKnownNeighbours()) this.bombSet.add(pos);
            this.deleteSet.add(position);
        }
    }
}
