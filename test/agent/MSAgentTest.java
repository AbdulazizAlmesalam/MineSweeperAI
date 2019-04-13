package agent;

import level.BoardLevel;
import level.RandomGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class MSAgentTest {

    private Grid grid;
    private RandomGenerator bGen;

    @Before
    public void setUp() throws Exception {
        this.grid = new Grid(24, 24);
        this.bGen = new RandomGenerator();
    }

    @Test
    public void simpleGameTest() {
        BoardLevel board = new BoardLevel(15, 15);
        board.addBomb(0, 14);

        AISolver agent = new AISolver(15, 15,1, this.grid.getVar(14, 0));

        boolean won = true;

        Set<Position> moves = new HashSet<>();

        Position next;
        while (moves.size() < 15 * 15 - 1) {
            next = agent.nextMove();
            if (board.hasBomb(next.getX(), next.getY())) {
                won = false;
                break;
            }
            agent.sendBackResult(next, board.adjBombs(next.getX(), next.getY()));
            moves.add(next);
        }

        assertTrue(won);

        assertTrue(agent.bombMarker().equals(this.grid.getVar(0, 14)));
        assertEquals(null, agent.bombMarker());

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (i != 0 || j != 14) assertTrue(moves.contains(this.grid.getVar(i, j)));
            }
        }
        assertEquals(15 * 15 - 1, moves.size());
    }

    @Test
    public void preferRandomUnknownTest() {
        BoardLevel board = new BoardLevel(8,8);
        board.addBomb(3,3);
        board.addBomb(1,1);
        // 8 more bombs that I won't add but tell the agent it has

        // Probability of bombs outside the constraint is
        // (10 - 2) / (8*8 - 9) which is ~14.5%
        // while the probability for a given square around the 2 is
        // 2 / 8 = 1 / 4 which is 25%
        // Thus we should always prefer to choose random unknowns here

        

        for (int i = 0; i < 50; i++) {
            AISolver agent = new AISolver(8, 8, 10, this.grid.getVar(2, 2));
            agent.sendBackResult(agent.nextMove(), 2);

            Set<Position> badProbability = new HashSet<>(
                    Arrays.asList(
                            this.grid.getVar(1, 1),
                            this.grid.getVar(2, 1),
                            this.grid.getVar(3, 1),
                            this.grid.getVar(1, 2),
                            this.grid.getVar(3, 2),
                            this.grid.getVar(1, 3),
                            this.grid.getVar(2, 3),
                            this.grid.getVar(3, 3)
                    )
            );

            assertFalse(badProbability.contains(agent.nextMove()));
        }
    }

    @Test
    public void simpleGuessTest() {
        
        BoardLevel b = new BoardLevel(5,3);
        b.addBomb(0,2);
        b.addBomb(0,1);
        b.addBomb(3,1);

        int total = 0, bombs = 0;
        for (int i = 0; i < 1000; i++) {

            AISolver agent = new AISolver(5, 3, 3, new Position(1, 1));
            agent.nextMove();
            agent.sendBackResult(this.grid.getVar(2, 1), 1);

            if (agent.nextMove().equals(this.grid.getVar(1, 1))) {
                total++;

                agent.sendBackResult(this.grid.getVar(1, 1), 2);

                Position next = agent.nextMove();
                assertFalse(new HashSet<>(Arrays.asList(
                        this.grid.getVar(0, 2),
                        this.grid.getVar(0, 2),
                        this.grid.getVar(0, 2))).contains(next));

                if (b.hasBomb(next.getX(), next.getY())) bombs++;
            }

        }

        // Probability is about 14.3%
        assertTrue("Probability is ~14, if fails, run several times and check if its fails again, if so something is probably wrong!",
                100.0 * bombs / total < 20);
    }

    @Test
    public void noGuessGameWin() {
      
        BoardLevel b = new BoardLevel(8,8);
        b.addBomb(7,0);
        b.addBomb(7,3);
        b.addBomb(7,5);
        Random random = new Random();

        ArrayList<Position> start = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                start.add(this.grid.getVar(i, j));
            }
        }
        start.add(this.grid.getVar(7,7));
        start.add(this.grid.getVar(6,7));


        for (int experiment = 0; experiment < 1500; experiment++) {
            Position pos = start.get(random.nextInt(start.size()));
            AISolver agent = new AISolver(8, 8, 3, pos);

            int movesToWin = 8 * 8 - 3;
            boolean won = true;

            Position next;
            while (movesToWin-- > 0) {
                next = agent.nextMove();
                if (b.hasBomb(next.getX(), next.getY())) {
                    won = false;
                    break;
                }
                agent.sendBackResult(next, b.adjBombs(next.getX(), next.getY()));
            }

            if (!won) System.out.println(pos);
            assertTrue(won);

            Set<Position> expectedBombs = new HashSet<>(
                    Arrays.asList(
                            this.grid.getVar(7, 0),
                            this.grid.getVar(7, 3),
                            this.grid.getVar(7, 5)
                    )
            );

            assertTrue(expectedBombs.contains(agent.bombMarker()));
            assertTrue(expectedBombs.contains(agent.bombMarker()));
            assertTrue(expectedBombs.contains(agent.bombMarker()));
            assertEquals(null, agent.bombMarker());
        }
    }

    @Test
    public void disjointConstraintTest() {
        

        BoardLevel b = new BoardLevel(8,8);
        b.addBomb(0,0); b.addBomb(0,1);
        b.addBomb(7,0); b.addBomb(7,1);
        b.addBomb(0,6); b.addBomb(0,7);
        b.addBomb(7,6); b.addBomb(7,7);

        Set<Position> uniqueMoveCounter = new HashSet<>();
        uniqueMoveCounter.add(this.grid.getVar(4,4));

        AISolver a = new AISolver(8,8,8, this.grid.getVar(4,4));
        a.sendBackResult(a.nextMove(), 0);
        int movesToWin = 8 * 8 - 8 - 1;
        boolean win = true;
        while (movesToWin-- > 0) {
            Position p = a.nextMove();
            uniqueMoveCounter.add(p);
            if (b.hasBomb(p.getX(), p.getY())) {
                win = false;
                break;
            }
            a.sendBackResult(p, b.adjBombs(p.getX(), p.getY()));
        }

        assertTrue(win);
        Set<Position> expectedBombSet = new HashSet<>(
                Arrays.asList(
                        this.grid.getVar(0,0),
                        this.grid.getVar(0,1),
                        this.grid.getVar(7,0),
                        this.grid.getVar(7,1),
                        this.grid.getVar(0,6),
                        this.grid.getVar(0,7),
                        this.grid.getVar(7,6),
                        this.grid.getVar(7,7)
                )
        );
        for (int i = 0; i < 8; i++) assertTrue(expectedBombSet.contains(a.bombMarker()));
        assertEquals(null, a.bombMarker());

        assertEquals(8*8-8, uniqueMoveCounter.size());

    }

    @Test
    public void noGuessNeededGame() {
        
       

        BoardLevel b = new BoardLevel(8,8);
        Set<Position> bombs = new HashSet<>(Arrays.asList(
                this.grid.getVar(2,0),
                this.grid.getVar(6,0),
                this.grid.getVar(5,1),
                this.grid.getVar(7,2),
                this.grid.getVar(3,3),
                this.grid.getVar(0,4),
                this.grid.getVar(7,4),
                this.grid.getVar(0,6),
                this.grid.getVar(4,6),
                this.grid.getVar(5,6)
        ));
        for (Position pos : bombs){
           b.addBomb(pos.getX(), pos.getY());
        }

        for (int n = 0; n < 50; n++) {
            AISolver agent = new AISolver(8, 8, 10, this.grid.getVar(0, 0));
            int toWin = 8 * 8 - 10;
            boolean win = true;
            while (toWin-- > 0) {
                Position next = agent.nextMove();
                if (b.hasBomb(next.getX(), next.getY())) {
                    win = false;
                    break;
                }
                agent.sendBackResult(next, b.adjBombs(next.getX(), next.getY()));
            }

            assertTrue(win);
            for (int i = 0; i < 10; i++) {
                assertTrue(bombs.contains(agent.bombMarker()));
            }
            assertEquals(null, agent.bombMarker());
        }
    }

    @Test
    public void noGuessNeededGame2() {
        
        BoardLevel b = new BoardLevel(16, 16);
        Set<Position> bombs = new HashSet<>(Arrays.asList(
                this.grid.getVar(5,0),
                this.grid.getVar(7,0),
                this.grid.getVar(3,1),
                this.grid.getVar(5,1),
                this.grid.getVar(7,1),
                this.grid.getVar(11,1),
                this.grid.getVar(12,1),
                this.grid.getVar(13,1),
                this.grid.getVar(3,3),
                this.grid.getVar(10,3),
                this.grid.getVar(12,3),
                this.grid.getVar(14,3),
                this.grid.getVar(3,4),
                this.grid.getVar(6,4),
                this.grid.getVar(7,4),
                this.grid.getVar(13,4),
                this.grid.getVar(15,4),
                this.grid.getVar(2,5),
                this.grid.getVar(7,5),
                this.grid.getVar(10,5),
                this.grid.getVar(5,6),
                this.grid.getVar(11,7),
                this.grid.getVar(15,7),
                this.grid.getVar(3,8),
                this.grid.getVar(8,9),
                this.grid.getVar(13,9),
                this.grid.getVar(0,10),
                this.grid.getVar(3,10),
                this.grid.getVar(11,10),
                this.grid.getVar(12,10),
                this.grid.getVar(13,10),
                this.grid.getVar(1,11),
                this.grid.getVar(3,11),
                this.grid.getVar(13,11),
                this.grid.getVar(13,12),
                this.grid.getVar(14,12),
                this.grid.getVar(3,13),
                this.grid.getVar(12,14),
                this.grid.getVar(5,15),
                this.grid.getVar(11,15)
        ));
        for (Position pos : bombs){
            b.addBomb(pos.getX(), pos.getY());
        }

        for (int n = 0; n < 50; n++) {
            AISolver agent = new AISolver(16, 16, 40, this.grid.getVar(0, 0));
            int toWin = 16 * 16 - 40;
            boolean win = true;
            while (toWin-- > 0) {
                Position next = agent.nextMove();
                if (b.hasBomb(next.getX(), next.getY())) {
                    win = false;
                    break;
                }
                agent.sendBackResult(next, b.adjBombs(next.getX(), next.getY()));
            }

            assertTrue(win);
            for (int i = 0; i < 40; i++) {
                assertTrue(bombs.contains(agent.bombMarker()));
            }
            assertEquals(null, agent.bombMarker());
        }
    }
}