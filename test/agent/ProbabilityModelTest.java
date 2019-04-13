package agent;

import org.chocosolver.solver.exception.ContradictionException;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;


public class ProbabilityModelTest {

    private static final double EPSILON = 1E-10;

    private Grid grid;


    @Before
    public void setUp() throws Exception {
        this.grid = new Grid(24, 24);
    }

    @Test
    public void foo()  throws ContradictionException {
       
        Set<ConstraintInfo> constraints = new HashSet<>();
        Set<Position> s1 = new HashSet<>(), s2 = new HashSet<>(), s = new HashSet<>();
        s.addAll(Arrays.asList(
                this.grid.getVar(1, 1),
                this.grid.getVar(2, 1),
                this.grid.getVar(3, 1),
                this.grid.getVar(1, 2),
                this.grid.getVar(3, 2),
                this.grid.getVar(1, 3),
                this.grid.getVar(3, 3),
                this.grid.getVar(1, 4),
                this.grid.getVar(2, 4),
                this.grid.getVar(3, 4)
        ));
        s1.addAll(Arrays.asList(
                this.grid.getVar(1, 1),
                this.grid.getVar(2, 1),
                this.grid.getVar(3, 1),
                this.grid.getVar(1, 2),
                this.grid.getVar(3, 2),
                this.grid.getVar(1, 3),
                this.grid.getVar(3, 3)
        ));
        s2.addAll(Arrays.asList(
                this.grid.getVar(1, 2),
                this.grid.getVar(3, 2),
                this.grid.getVar(3, 3),
                this.grid.getVar(1, 3),
                this.grid.getVar(1, 4),
                this.grid.getVar(2, 4),
                this.grid.getVar(3, 4)
        ));
        constraints.addAll(Arrays.asList(
                new ConstraintInfo(s1, 2),
                new ConstraintInfo(s2, 1)
        ));

        // Nothing to conduct
        Model_ model = new Model_(constraints, s);
        for (Position p : s) {
            assertFalse(model.hBomb(p));
            assertFalse(model.hNoBombs(p));
        }



        Probability pModel = new Probability(constraints, s, new HashSet<>() /* don't need that here */);
        Map<Position, Double> probabilities = new HashMap<>();
        assertEquals(2, pModel.getProb(probabilities)); // can be 2 or 3, 2 being the minimum

        PriorityQueue<Map.Entry<Position, Double>> sorter = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));
        for (Map.Entry<Position, Double> entry : probabilities.entrySet()) sorter.add(entry);

        

        

        Set<Position> low = new HashSet<>(Arrays.asList(
                this.grid.getVar(1,2),
                this.grid.getVar(3,2),
                this.grid.getVar(1,3),
                this.grid.getVar(3,3),
                this.grid.getVar(1,4),
                this.grid.getVar(2,4),
                this.grid.getVar(3,4)
        ));

        Set<Position> high = new HashSet<>(Arrays.asList(
                this.grid.getVar(1,1),
                this.grid.getVar(2,1),
                this.grid.getVar(3,1)
        ));

        for (Position lowPos : low) assertEquals(100.0 * 3.0 / 21.0, probabilities.get(lowPos), EPSILON);
        for (Position highPos : high) assertEquals(100.0 * 10.0 / 21.0, probabilities.get(highPos), EPSILON);


        for (int i = 0; i < low.size(); i++) {
            assertTrue(low.contains(sorter.poll().getKey()));
        }

        for (int i = 0; i < high.size(); i++) {
            assertTrue(high.contains(sorter.poll().getKey()));
        }
    }

}