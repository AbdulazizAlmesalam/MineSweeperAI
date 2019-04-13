package agent;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;


public class ConstraintInfoTest {

    private Grid grid;

    @Before
    public void setUp() {
        this.grid = new Grid(10,10);
    }

    @Test
    public void equalsTest() {
        Random r = new Random();
        Set<Position> a = new HashSet<>(), b = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Position pos = this.grid.getVar(r.nextInt(10), r.nextInt(10));
            a.add(pos);
            b.add(pos);
        }
        assertEquals(new ConstraintInfo(a, 1), new ConstraintInfo(b, 2));
        while (a.size() < 12) a.add(this.grid.getVar(r.nextInt(10), r.nextInt(10)));
        assertNotEquals(new ConstraintInfo(a, 1), new ConstraintInfo(b, 2));
    }

    @Test
    public void gettersAndSettersTest() {
        Set<Position> set = new HashSet<>();
        set.add(this.grid.getVar(0,0));
        set.add(this.grid.getVar(0,1));
        ConstraintInfo info = new ConstraintInfo(set, 2);
        System.out.println(info);
        assertEquals(2, info.getadjBombs());
        info.decadjBombs();
        assertEquals(1, info.getadjBombs());
        Set<Position> set2 = new HashSet<>();
        set2.addAll(set);
        assertEquals(set2, info.getnotKnownNeighbours());
        info.deleteVar(this.grid.getVar(0,1));
        assertNotEquals(set2, info.getnotKnownNeighbours());
        Set<Position> set3 = new HashSet<>();
        set3.add(this.grid.getVar(0,0));
        assertEquals(set3, info.getnotKnownNeighbours());
    }

    @Test
    public void allBombsTest() {
        Set<Position> set = new HashSet<>();
        set.add(grid.getVar(3,3));
        set.add(grid.getVar(5,5));
        set.add(grid.getVar(4,5));
        assertTrue(new ConstraintInfo(set, 3).allBombs());
        assertFalse(new ConstraintInfo(set, 2).allBombs());
    }

    @Test
    public void noBombsTest() {
        Set<Position> set = new HashSet<>();
        set.add(grid.getVar(3,3));
        set.add(grid.getVar(5,5));
        set.add(grid.getVar(4,5));
        assertTrue(new ConstraintInfo(set, 0).noBombs());
        assertFalse(new ConstraintInfo(set, 1).noBombs());
    }

    @Test
    public void emptyTest() {
        Set<Position> set = new HashSet<>();
        set.add(grid.getVar(3,3));
        set.add(grid.getVar(5,5));
        set.add(grid.getVar(4,5));
        ConstraintInfo info = new ConstraintInfo(set, 0);
        assertFalse(info.isEmpty());
        info.deleteVar(grid.getVar(3,3));
        info.deleteVar(grid.getVar(5,5));
        info.deleteVar(grid.getVar(4,5));
        assertTrue(info.isEmpty());
    }

}