package agent;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Model_ {

    private Model model;
    private Map<Position, IntVar> mapV;

    /**
     * Creates a Choco Model using the constraint in a given constraint group.
     *
     * @param constraints Constraints in a constraint group
     * @param variables Variables in a constraint group
     * @throws ContradictionException should never happen
     */
    public Model_(Set<ConstraintInfo> constraints, Set<Position> variables) throws ContradictionException {
        this.model = new Model();
        this.mapV = new HashMap<>();

        // Map each variable to a Choco variable
        for (Position pos : variables) this.mapV.put(pos, this.model.intVar(pos.toString(), 0, 1));

        // Create Choco constraints from our constraints
        for (ConstraintInfo c : constraints) {
            IntVar[] con = new IntVar[c.getnotKnownNeighbours().size()];
            int index = 0;
            for (Position pos : c.getnotKnownNeighbours()) {
                con[index] = this.mapV.get(pos);
                index++;
            }
            this.model.sum(con, "=", c.getadjBombs()).post();
        }

        // constraint propagation
        this.model.getSolver().propagate();
    }

    /**
     * Constructor that adds end game constraint, sum of all unknowns = remaining bombs
     *
     * @param constraint endgame constraint
     * @throws ContradictionException should never happen
     */
    public Model_(Endgame constraint) throws ContradictionException {
        this.model = new Model();
        this.mapV = new HashMap<>();

        // Map each variable to a Choco variable
        for (Position pos : constraint.getvars()) this.mapV.put(pos, this.model.intVar(pos.toString(), 0, 1));

        // Create Choco constraints from our constraints
        for (ConstraintInfo c : constraint.getconsts()) {
            IntVar[] con = new IntVar[c.getnotKnownNeighbours().size()];
            int index = 0;
            for (Position pos : c.getnotKnownNeighbours()) {
                con[index] = this.mapV.get(pos);
                index++;
            }
            this.model.sum(con, "=", c.getadjBombs()).post();
        }

        IntVar[] con = new IntVar[constraint.getvars().size()];
        int index = 0;
        for (Position pos : constraint.getvars()) {
            con[index] = mapV.get(pos);
            index++;
        }
        this.model.sum(con, "=", constraint.getbombsLeft());

        // constraint propagation
        this.model.getSolver().propagate();
    }

    /**
     * Assume that a position does not contain a bomb, check if it
     * leads to a contradiction. If so, it must contain a bomb.
     *
     * @param position Variable
     * @return true if we can guarantee that it contains a bomb.
     */
    public boolean hBomb(Position position) {
        return contradict(model.arithm(mapV.get(position), "=", 0));
    }

    /**
     * Assume that a position contains a bomb, check if it
     * leads to a contradiction. If so, it must not contain a bomb.
     *
     * @param position Variable
     * @return true if we can guarantee that it contains no bomb.
     */
    public boolean hNoBombs(Position position) {
        return contradict(model.arithm(mapV.get(position), "=", 1));
    }

    /**
     * Use of Choco solver to see if we can find a solution given an assumption.
     *
     * @param assumption Constraint (Choco)
     * @return true iff no solution is found
     */
    private boolean contradict(Constraint assumption) {
        model.getEnvironment().worldPush();
        model.post(assumption);
        Solution sol = model.getSolver().findSolution();
        model.getEnvironment().worldPop();
        model.unpost(assumption);
        model.getSolver().hardReset();
        return sol == null;
    }
}
