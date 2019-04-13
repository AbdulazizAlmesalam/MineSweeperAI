package agent;

import java.util.*;

public class Groups {

    // A map to all variables for the group is used to simplify usage of the Choco framework.
    private Map<Set<ConstraintInfo>, Set<Position>> groups;

    /**
     * Constructs constraints group for a given agent knowledge.
     * @param board The board as the agent sees it.
     */
    public Groups(Board board) {
        this.groups = new HashMap<>();
        for (ConstraintInfo info : board.getconstraintP().values()) add(info);
    }

    /**
     * @return A map with constraint groups as keys, all variables of each group as key.
     */
    public Map<Set<ConstraintInfo>, Set<Position>> getGroups() {
        return this.groups;
    }

    /**
     * @return true if agent's current knowledge do not result in any constraints.
     */
    public boolean isEmpty() {
        return this.groups.isEmpty();
    }

    /**
     * adds a new constraint to the groups of constraint group.
     * Any group that contains any of its variable is added to
     * a stack and passed to another method to merge with all
     * groups in the stack, into which we add the new constraint.
     * If no such group exist, we create a new group.
     *
     * @param info Constraint
     */
    private void add(ConstraintInfo info) {
        Stack<Set<ConstraintInfo>> merge = new Stack<>();
        for (Map.Entry<Set<ConstraintInfo>, Set<Position>> entry : groups.entrySet()) {
            for (Position variable : info.getnotKnownNeighbours()) {
                if (entry.getValue().contains(variable)) {
                    merge.push(entry.getKey());
                    break;
                }
            }
        }
        if (merge.isEmpty()) {
            Set<ConstraintInfo> set = new HashSet<>();
            set.add(info);
            groups.put(set, info.getnotKnownNeighbours());
        } else {
            merge(merge, info);
        }
    }

    /**
     * Removes all sets in the stack from the map, merges them, adds the
     * new constraint to them and adds the merged set into the map.
     *
     * @param merge Stack of constraint groups
     * @param info constraint
     */
    private void merge(Stack<Set<ConstraintInfo>> merge, ConstraintInfo info) {
        Set<ConstraintInfo> key = new HashSet<>();
        Set<Position> value = new HashSet<>();
        while (!merge.isEmpty()) {
            Set<ConstraintInfo> temp = merge.pop();
            value.addAll(groups.get(temp));
            key.addAll(temp);
            groups.remove(temp);
        }
        key.add(info);
        value.addAll(info.getnotKnownNeighbours());
        groups.put(key, value);
    }
}
