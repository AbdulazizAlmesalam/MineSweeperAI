package agent;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ConstraintGroupsTest {

    private Board board;
    private Grid grid;

    @Before
    public void setUp() {
        this.board = new Board(10, 10);
        grid = new Grid(10, 10);
    }

    @Test
    public void duplicatesTest() {
        
        Set<Position> var1 = new HashSet<>(), var2 = new HashSet<>();
        var1.add(this.grid.getVar(0,0)); var2.add(this.grid.getVar(0,0));
        var1.add(this.grid.getVar(1,0)); var2.add(this.grid.getVar(1,0));
        getMap().put(new Position(0,1), new ConstraintInfo(var1, 1));
        getMap().put(new Position(1,1), new ConstraintInfo(var2, 1));
        assertEquals(1, new Groups(this.board).getGroups().keySet().size());
    }

    @Test
    public void singleJoinedGroup() {
       
        Set<Position> var1 = new HashSet<>(Arrays.asList(p(1,1),p(2,2)));
        Set<Position> var3 = new HashSet<>(Arrays.asList(p(2,4),p(3,4)));
        Set<Position> var2 = new HashSet<>(Arrays.asList(p(2,2),p(2,3),p(2,4)));
        getMap().put(new Position(2,1), new ConstraintInfo(var1, 1));
        getMap().put(new Position(2,5), new ConstraintInfo(var3, 1));
        getMap().put(new Position(1,3), new ConstraintInfo(var2, 2));

        Groups grp = new Groups(this.board);
        assertEquals(1, grp.getGroups().size());
        Set<ConstraintInfo> _set = grp.getGroups().keySet().iterator().next();
        assertEquals(3, _set.size());
        for (ConstraintInfo info : _set) {
            assertTrue(info.getnotKnownNeighbours().equals(var1) || info.getnotKnownNeighbours().equals(var2) || info.getnotKnownNeighbours().equals(var3));
            assertTrue(info.getnotKnownNeighbours().equals(var1) || info.getnotKnownNeighbours().equals(var2) || info.getnotKnownNeighbours().equals(var3));
            assertTrue(info.getnotKnownNeighbours().equals(var1) || info.getnotKnownNeighbours().equals(var2) || info.getnotKnownNeighbours().equals(var3));
        }
    }

    @Test
    public void empty() {
        this.board.setAdjacent(5, 5, 0, this.grid, new HashSet<>(), new HashSet<>());
        this.board.setBomb(3,3, this.grid, new HashSet<>(), new HashSet<>());
        assertTrue(new Groups(this.board).isEmpty());
    }

    @Test
    public void disjointGroups() {
        
        Set<Position> var1 = new HashSet<>(), var2 = new HashSet<>(), var3 = new HashSet<>();
        var1.add(this.grid.getVar(0,0));
        var1.add(this.grid.getVar(1,0));
        var1.add(this.grid.getVar(2,0));
        var2.add(this.grid.getVar(4,0));
        var2.add(this.grid.getVar(5,0));
        var3.add(this.grid.getVar(3,3));
        var3.add(this.grid.getVar(3,4));
        getMap().put(new Position(1,1), new ConstraintInfo(var1, 2));
        getMap().put(new Position(5,1), new ConstraintInfo(var2, 1));
        getMap().put(new Position(4,4), new ConstraintInfo(var3, 1));

        Groups grp = new Groups(this.board);
        assertEquals(3, grp.getGroups().size());
    }

    private Position p(int x, int y) {
        return this.grid.getVar(x, y);
    }

    private Map<Position, ConstraintInfo> getMap() {
        return this.board.getconstraintP();
    }
}