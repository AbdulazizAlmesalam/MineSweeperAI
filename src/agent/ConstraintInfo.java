package agent;

import java.util.Set;

public class ConstraintInfo {

    private Set<Position> notKnownNeighbours;
    private int adjBombs;

    /**
     * Constructs the constraint sum(notKnownNeighbours) = adjBombs
     *
     * @param notKnownNeighbours set of variables
     * @param adjBombs sum of variables
     */
    public ConstraintInfo(Set<Position> notKnownNeighbours, int adjBombs) {
        this.notKnownNeighbours = notKnownNeighbours;
        this.adjBombs = adjBombs;
    }

    /**
     * @return sum of variables.
     */
    public int getadjBombs() {
        return this.adjBombs;
    }

    /**
     * decreases constraint sum by one.
     */
    public void decadjBombs() {
        this.adjBombs--;
    }

    /**
     * @return variables
     */
    public Set<Position> getnotKnownNeighbours() {
        return this.notKnownNeighbours;
    }

    /**
     * Removes a variable from the constraint.
     *
     * @param position variable
     */
    public void deleteVar(Position position) {
        this.notKnownNeighbours.remove(position);
    }

    /**
     * Check if sum(variables) = count(variables) which means all must be bombs.
     *
     * @return true iff sum(variables) = count(variables)
     */
    public boolean allBombs() {
        return this.adjBombs == notKnownNeighbours.size();
    }

    /**
     * Check if sum(variables) = 0 which means all must be clear (no bombs).
     *
     * @return true iff sum(variables) = 0
     */
    public boolean noBombs() {
        return this.adjBombs == 0;
    }

    /**
     * Check if constraint has variables.
     *
     * @return true if no variables remain in the constraint.
     */
    public boolean isEmpty() {
        return this.notKnownNeighbours.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        return this.notKnownNeighbours.equals(((ConstraintInfo)o).notKnownNeighbours);
    }

    @Override
    public int hashCode() {
        return this.notKnownNeighbours.hashCode();
    }

    @Override
    public String toString() {
        return "[" + adjBombs + ", " + notKnownNeighbours.toString() + "]";
    }
}
