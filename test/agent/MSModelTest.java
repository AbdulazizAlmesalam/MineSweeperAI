package agent;

import org.chocosolver.solver.exception.ContradictionException;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class MSModelTest {

    private Grid grid;

    @Before
    public void setUp() {
        this.grid = new Grid(24, 24);
    }

    @Test
    public void solveTest() throws ContradictionException {
        //###
        //232
        Set<ConstraintInfo> constraints = new HashSet<>();
        Set<Position> s1 = new HashSet<>(), s2 = new HashSet<>(), s3 = new HashSet<>(), s = new HashSet<>();
        s.addAll(Arrays.asList(this.grid.getVar(0,0), this.grid.getVar(1,0), this.grid.getVar(2,0)));
        s1.addAll(Arrays.asList(this.grid.getVar(0,0), this.grid.getVar(1,0)));
        s2.addAll(Arrays.asList(this.grid.getVar(0,0), this.grid.getVar(1,0), this.grid.getVar(2,0)));
        s3.addAll(Arrays.asList(this.grid.getVar(1,0), this.grid.getVar(2,0)));
        constraints.addAll(Arrays.asList(
                new ConstraintInfo(s1, 2),
                new ConstraintInfo(s2, 3),
                new ConstraintInfo(s3, 2)
        ));
        Model_ model = new Model_(constraints, s);
        for (Position p : s) {
            assertTrue(model.hBomb(p));
            assertFalse(model.hNoBombs(p));
        }
    }

    @Test
    public void solveTest2() throws ContradictionException {
        //###
        //12
        Set<ConstraintInfo> constraints = new HashSet<>();
        Set<Position> s1 = new HashSet<>(), s2 = new HashSet<>(), s = new HashSet<>();
        s.addAll(Arrays.asList(this.grid.getVar(0,0), this.grid.getVar(1,0), this.grid.getVar(2,0)));
        s1.addAll(Arrays.asList(this.grid.getVar(0,0), this.grid.getVar(1,0)));
        s2.addAll(Arrays.asList(this.grid.getVar(0,0), this.grid.getVar(1,0), this.grid.getVar(2,0)));
        constraints.addAll(Arrays.asList(
                new ConstraintInfo(s1, 1),
                new ConstraintInfo(s2, 2)
        ));
        Model_ model = new Model_(constraints, s);
        assertTrue(model.hBomb(this.grid.getVar(2,0)));
        assertFalse(model.hNoBombs(this.grid.getVar(2,0)));
        assertFalse(model.hNoBombs(this.grid.getVar(1,0)));
        assertFalse(model.hNoBombs(this.grid.getVar(0,0)));
        assertFalse(model.hBomb(this.grid.getVar(1,0)));
        assertFalse(model.hBomb(this.grid.getVar(0,0)));
    }

    @Test
    public void solveTest3() throws ContradictionException {
        
        Set<ConstraintInfo> constraints = new HashSet<>();
        Set<Position> s1 = new HashSet<>(), s2 = new HashSet<>(), s3 = new HashSet<>(), s = new HashSet<>();
        s.addAll(Arrays.asList(
                this.grid.getVar(0,0),
                this.grid.getVar(1,0),
                this.grid.getVar(2,0),
                this.grid.getVar(0,1),
                this.grid.getVar(0,2)
        ));
        s1.addAll(Arrays.asList(this.grid.getVar(0,1), this.grid.getVar(0,2)));
        s2.addAll(Arrays.asList(
                this.grid.getVar(0,0),
                this.grid.getVar(1,0),
                this.grid.getVar(2,0),
                this.grid.getVar(0,1),
                this.grid.getVar(0,2)
        ));
        s3.addAll(Arrays.asList(this.grid.getVar(1,0), this.grid.getVar(2,0)));
        constraints.addAll(Arrays.asList(
                new ConstraintInfo(s1, 1),
                new ConstraintInfo(s2, 3),
                new ConstraintInfo(s3, 1)
        ));
        Model_ model = new Model_(constraints, s);
        assertTrue(model.hBomb(this.grid.getVar(0,0)));
        for (Position pos : s) {
            assertFalse(model.hNoBombs(pos));
            if (!pos.equals(this.grid.getVar(0,0))) assertFalse(model.hBomb(pos));
        }
    }

    @Test
    public void solveTest4() throws ContradictionException {
   

        Set<ConstraintInfo> constraints = new HashSet<>();
        Set<Position> vars = new HashSet<>();
        for (int i = 0; i < 14; i++) vars.add(this.grid.getVar(i, 0));

        Set<Position> temp = new HashSet<>();
        temp.add(new Position(0,0)); temp.add(new Position(1,0));
        constraints.add(new ConstraintInfo(temp, 1));

        for (int i = 1; i < 13; i++) {
            temp = new HashSet<>();
            temp.add(new Position(i-1, 0));
            temp.add(new Position(i,0));
            temp.add(new Position(i+1,0));
            constraints.add(new ConstraintInfo(temp, (i & 1) == 1 ? 2 : 1));
        }
        temp = new HashSet<>();
        temp.add(new Position(12,0)); temp.add(new Position(13,0));
        constraints.add(new ConstraintInfo(temp, 1));

        Model_ model = new Model_(constraints, vars);

        for (int i = 0; i < 14; i++) {
            if ((i & 1) == 1) {
                assertFalse(model.hBomb(this.grid.getVar(i ,0)));
                assertTrue(model.hNoBombs(this.grid.getVar(i, 0)));
            } else {
                assertTrue(model.hBomb(this.grid.getVar(i, 0)));
                assertFalse(model.hNoBombs(this.grid.getVar(i, 0)));
            }
        }
    }

    @Test
    public void solveTest5() throws ContradictionException {
      

        Set<ConstraintInfo> constraints = new HashSet<>();
        Set<Position> vars = new HashSet<>();
        Position[] varArr = new Position[]{
                this.grid.getVar(0, 6),
                this.grid.getVar(1, 6),
                this.grid.getVar(2, 6),
                this.grid.getVar(3, 6),
                this.grid.getVar(4, 6),
                this.grid.getVar(5, 6),
                this.grid.getVar(6, 6),
                this.grid.getVar(7, 6),
                this.grid.getVar(7, 5),
                this.grid.getVar(7, 4),
                this.grid.getVar(7, 3),
                this.grid.getVar(7, 2),
                this.grid.getVar(6, 2),
                this.grid.getVar(5, 2),
                this.grid.getVar(5, 1),
                this.grid.getVar(5, 0)
        };
        vars.addAll(Arrays.asList(varArr));
        ArrayList<Set<Position>> cArr = new ArrayList<>();
        for (int i = 0; i < 14; i++) cArr.add(new HashSet<>());
        add(cArr.get(0), varArr[0], varArr[1]);
        add(cArr.get(1), varArr[0], varArr[1], varArr[2]);
        add(cArr.get(2), varArr[3], varArr[1], varArr[2]);
        add(cArr.get(3), varArr[3], varArr[4], varArr[2]);
        add(cArr.get(4), varArr[3], varArr[4], varArr[5]);
        add(cArr.get(5), varArr[6], varArr[4], varArr[5]);
        add(cArr.get(6), varArr[6], varArr[7], varArr[5], varArr[8], varArr[9]);
        add(cArr.get(7), varArr[8], varArr[9], varArr[10]);
        add(cArr.get(8), varArr[9], varArr[10], varArr[11], varArr[12], varArr[13]);
        add(cArr.get(9), varArr[12], varArr[13]);
        add(cArr.get(10), varArr[13]);
        add(cArr.get(11), varArr[13], varArr[14]);
        add(cArr.get(12), varArr[13], varArr[14], varArr[15]);
        add(cArr.get(13), varArr[14], varArr[15]);

        int[] cVal = new int[] { 1,2,2,1,1,1,2,1,2,1,1,2,3,2 };
        for (int i = 0; i < 14; i++) {
            constraints.add(new ConstraintInfo(cArr.get(i), cVal[i]));
        }

        Model_ model = new Model_(constraints, vars);

        Set<Position> expectedBombs = new HashSet<>();
        expectedBombs.addAll(Arrays.asList(
                this.grid.getVar(1, 6),
                this.grid.getVar(2, 6),
                this.grid.getVar(5, 6),
                this.grid.getVar(5, 0),
                this.grid.getVar(5, 1),
                this.grid.getVar(5, 2)
        ));
        Set<Position> expectedNoBombs = new HashSet<>();
        expectedNoBombs.addAll(Arrays.asList(
                this.grid.getVar(6,2),
                this.grid.getVar(0,6),
                this.grid.getVar(3,6),
                this.grid.getVar(4,6),
                this.grid.getVar(6,6)
        ));

        for (Position pos : vars) {
            if (expectedBombs.contains(pos)) {
                assertTrue(model.hBomb(pos));
            } else {
                assertFalse(model.hBomb(pos));
            }

            if (expectedNoBombs.contains(pos)) {
                assertTrue(model.hNoBombs(pos));
            } else {
                assertFalse(model.hNoBombs(pos));
            }
        }

       
    }

    private void add(Set<Position> set, Position... positions) {
        Collections.addAll(set, positions);
    }
}