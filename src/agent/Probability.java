package agent;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Probability {

    private Model model;
    private Map<Position, IntVar> mapV;

    /**
     * Set up Choco model for the constraint group.
     *
     * @param info constraint group
     * @param variables all variables in the constraint group
     * @param varCollection a collection to store any variable for outside use
     * @throws ContradictionException should never happen
     */
    public Probability(Set<ConstraintInfo> info, Set<Position> variables, Set<Position> varCollection) throws ContradictionException {
        this.model = new Model();
        this.mapV = new HashMap<>();

        for (Position pos : variables) {
            this.mapV.put(pos, this.model.intVar(pos.toString(), 0, 1));
            varCollection.add(pos);
        }
        for (ConstraintInfo c : info) {
            IntVar[] con = new IntVar[c.getnotKnownNeighbours().size()];
            int index = 0;
            for (Position pos : c.getnotKnownNeighbours()) {
                con[index] = this.mapV.get(pos);
                index++;
            }
            this.model.sum(con, "=", c.getadjBombs()).post();
        }
        this.model.getSolver().propagate();
    }

    /**
     * Updates a map for probabilities for all variables in a constraint group.
     *
     * @param probMap a map to update
     * @return minimum number of bombs for the constraint group
     */
    public int getProb(Map<Position, Double> probMap) {
        int minimumB = Integer.MAX_VALUE;

        // Initialize all probabilities as 0.0
        for (Position position : this.mapV.keySet()) probMap.put(position, 0.0);

        // For a solution in the group of all solutions for the constraint group
        for (Solution solution : this.model.getSolver().findAllSolutions()) {
            int bSolutions = 0;
            for (Position position : this.mapV.keySet()) {
                int value = solution.getIntVal(this.mapV.get(position));
                // If the position contains a bomb in this solution,
                // we add one to the probability map
                // The map works as a counter at this point.
                if (value == 1) {
                    probMap.put(position, probMap.get(position) + 1);
                    bSolutions++;
                }
            }
            if (minimumB > bSolutions) minimumB = bSolutions;
        }
        // Convert counter to probabilities.
        long totalSolutions = this.model.getSolver().getSolutionCount();
        for (Position position : this.mapV.keySet()) {
            probMap.put(position, 100.0 * probMap.get(position) / totalSolutions);
        }

        return minimumB;
    }
}
