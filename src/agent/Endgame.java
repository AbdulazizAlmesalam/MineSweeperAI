package agent;

import java.util.HashSet;
import java.util.Set;

public class Endgame {

    private Set<ConstraintInfo> consts;
    private Set<Position> vars;
    private int bombsLeft;

    public Endgame(Board board, int tBombs, int w, int h, Grid grid) {
        this.consts = new HashSet<>();
        this.vars = new HashSet<>();
        for (ConstraintInfo constraint : board.getconstraintP().values()) {
            this.consts.add(constraint);
            this.vars.addAll(constraint.getnotKnownNeighbours());
        }
        this.bombsLeft = tBombs;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (board.getBoard()[i][j] == Board.BOMB) {
                    this.bombsLeft--;
                } else if (board.getBoard()[i][j] == Board.UNKNOWN) {
                    this.vars.add(grid.getVar(i, j));
                }
            }
        }
    }

    public Set<Position> getvars() {
        return this.vars;
    }

    public Set<ConstraintInfo> getconsts() {
        return this.consts;
    }

    public int getbombsLeft() {
        return this.bombsLeft;
    }
}
