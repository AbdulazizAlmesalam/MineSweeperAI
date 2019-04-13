package agent;

import level.BoardLevel;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;


public class PerspectiveBoardTest {

    private Board pBoard;
    private Grid grid;

    @Before
    public void setUp() {
        this.pBoard = new Board(5, 5);
        this.grid = new Grid(5, 5);
    }

    @Test
    public void constructorTest() {
        for (byte[] arr : this.pBoard.getBoard()) {
            for (byte b : arr) {
                assertEquals(Board.UNKNOWN, b);
            }
        }
    }

    @Test
    public void clickZeroAtFirstTest() {
        Set<Position> pendingMoves = new HashSet<>();
        this.pBoard.setAdjacent(2,3,0, this.grid, pendingMoves, new HashSet<>());
        assertTrue(this.pBoard.getconstraintP().isEmpty());
        assertEquals(8, pendingMoves.size());
        assertEquals(0, this.pBoard.getBoard()[2][3]);
        for (Position pos : pendingMoves) {
            assertTrue(Math.sqrt((2 - pos.getX()) * (2 - pos.getX()) + (3 - pos.getY()) * (3 - pos.getY())) < 1.5);
            assertEquals(-1, this.pBoard.getBoard()[pos.getX()][pos.getY()]);
        }
    }

    @Test
    public void addBombTest() {
        Set<Position> pendingMoves = new HashSet<>();

        System.out.println("Step 1");
       
        this.pBoard.setAdjacent(1,0, 1, this.grid, pendingMoves, new HashSet<>());
        Set<Position> expectedVariables1 = new HashSet<>();
        expectedVariables1.addAll(Arrays.asList(
                this.grid.getVar(0,0),
                this.grid.getVar(0,1),
                this.grid.getVar(1,1),
                this.grid.getVar(2,0),
                this.grid.getVar(2,1)
        ));
        assertEquals(expectedVariables1, this.pBoard.getconstraintP().get(this.grid.getVar(1,0)).getnotKnownNeighbours());
        assertTrue(pendingMoves.isEmpty());

        System.out.println("Step 2");
        
        this.pBoard.setAdjacent(0,1, 1, this.grid, pendingMoves, new HashSet<>());
        Set<Position> expectedVariables2 = new HashSet<>();
        expectedVariables2.addAll(Arrays.asList(
                this.grid.getVar(0,0),
                this.grid.getVar(1,1),
                this.grid.getVar(0,2),
                this.grid.getVar(1,2)
        ));
        assertEquals(expectedVariables2, this.pBoard.getconstraintP().get(this.grid.getVar(0,1)).getnotKnownNeighbours());
        assertNotEquals(expectedVariables1, this.pBoard.getconstraintP().get(this.grid.getVar(1,0)).getnotKnownNeighbours());
        expectedVariables1.remove(this.grid.getVar(0, 1));
        assertEquals(expectedVariables1, this.pBoard.getconstraintP().get(this.grid.getVar(1,0)).getnotKnownNeighbours());
        assertTrue(pendingMoves.isEmpty());

        System.out.println("Step 3");
        
        this.pBoard.setAdjacent(1,1, 1, this.grid, pendingMoves, new HashSet<>());
        Set<Position> expectedVariables3 = new HashSet<>();
        expectedVariables3.addAll(Arrays.asList(
                this.grid.getVar(2,0),
                this.grid.getVar(0,2),
                this.grid.getVar(2,1),
                this.grid.getVar(1,2),
                this.grid.getVar(2,2),
                this.grid.getVar(0,0)
        ));
        assertEquals(expectedVariables3, this.pBoard.getconstraintP().get(this.grid.getVar(1,1)).getnotKnownNeighbours());
        assertNotEquals(expectedVariables2, this.pBoard.getconstraintP().get(this.grid.getVar(0,1)).getnotKnownNeighbours());
        assertNotEquals(expectedVariables1, this.pBoard.getconstraintP().get(this.grid.getVar(1,0)).getnotKnownNeighbours());
        expectedVariables1.remove(this.grid.getVar(1,1));
        expectedVariables2.remove(this.grid.getVar(1,1));
        assertEquals(expectedVariables2, this.pBoard.getconstraintP().get(this.grid.getVar(0,1)).getnotKnownNeighbours());
        assertEquals(expectedVariables1, this.pBoard.getconstraintP().get(this.grid.getVar(1,0)).getnotKnownNeighbours());
        assertTrue(pendingMoves.isEmpty());

        System.out.println("Step 4");
      
        this.pBoard.setAdjacent(2,0, 0, this.grid, pendingMoves, new HashSet<>());
        assertFalse(pendingMoves.isEmpty());
        assertEquals(3, pendingMoves.size());
        assertTrue(pendingMoves.containsAll(Arrays.asList(this.grid.getVar(3,0), this.grid.getVar(3,1), this.grid.getVar(2,1))));
        assertFalse(this.pBoard.getconstraintP().get(this.grid.getVar(1,0)).getnotKnownNeighbours().contains(this.grid.getVar(2,0)));

        assertNotEquals(expectedVariables3, this.pBoard.getconstraintP().get(this.grid.getVar(1,1)).getnotKnownNeighbours());
        assertNotEquals(expectedVariables1, this.pBoard.getconstraintP().get(this.grid.getVar(1,0)).getnotKnownNeighbours());
        expectedVariables1.remove(this.grid.getVar(2, 0));
        expectedVariables3.remove(this.grid.getVar(2, 0));
        assertEquals(expectedVariables3, this.pBoard.getconstraintP().get(this.grid.getVar(1,1)).getnotKnownNeighbours());
        assertEquals(expectedVariables2, this.pBoard.getconstraintP().get(this.grid.getVar(0,1)).getnotKnownNeighbours());
        assertEquals(expectedVariables1, this.pBoard.getconstraintP().get(this.grid.getVar(1,0)).getnotKnownNeighbours());

        System.out.println("Step 5");
       
        this.pBoard.setAdjacent(2, 1, 0, this.grid, pendingMoves, new HashSet<>());
        assertEquals(7, pendingMoves.size());
        assertTrue(pendingMoves.containsAll(Arrays.asList(
                this.grid.getVar(3,0),
                this.grid.getVar(3,1),
                this.grid.getVar(3,2),
                this.grid.getVar(2,2),
                this.grid.getVar(1,2),
                this.grid.getVar(0,2)
        )));
        assertEquals(10, this.pBoard.getBoard()[0][0]);
        assertEquals(1, this.pBoard.getBoard()[1][0]);
        assertEquals(1, this.pBoard.getBoard()[0][1]);
        assertEquals(1, this.pBoard.getBoard()[1][1]);
        assertEquals(0, this.pBoard.getBoard()[2][0]);
        assertEquals(0, this.pBoard.getBoard()[2][1]);
        pendingMoves.remove(this.grid.getVar(2,1));
        for (Position p : pendingMoves) assertEquals(-1, this.pBoard.getBoard()[p.getX()][p.getY()]);

    }

    @Test
    public void crowdedTest() {
        
       
        Set<Position> pendingMoves = new HashSet<>();
        this.pBoard.setAdjacent(2,2, 8, this.grid, pendingMoves, new HashSet<>());
        assertTrue(pendingMoves.isEmpty());
        assertTrue(this.pBoard.getconstraintP().isEmpty());

        Set<Position> bombs = new HashSet<>();
        bombs.addAll(Arrays.asList(
                this.grid.getVar(1,1),
                this.grid.getVar(1,2),
                this.grid.getVar(1,3),
                this.grid.getVar(2,1),
                this.grid.getVar(2,3),
                this.grid.getVar(3,1),
                this.grid.getVar(3,2),
                this.grid.getVar(3,3)
        ));
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (bombs.contains(this.grid.getVar(i, j))) {
                    assertEquals(10, this.pBoard.getBoard()[i][j]);
                } else if (i == 2 && j == 2) {
                    assertEquals(8, this.pBoard.getBoard()[i][j]);
                } else {
                    assertEquals(-1, this.pBoard.getBoard()[i][j]);
                }
            }
        }
    }

    @Test
    public void nextToBombTest() {
        Set<Position> pendingMoves = new HashSet<>();
        this.pBoard.setBomb(1,1, this.grid, pendingMoves, new HashSet<>());
        this.pBoard.setBomb(3,3, this.grid, pendingMoves, new HashSet<>());
        assertTrue(pendingMoves.isEmpty());
        assertTrue(this.pBoard.getconstraintP().isEmpty());

        this.pBoard.setAdjacent(2,2,3, this.grid, pendingMoves, new HashSet<>());
        assertEquals(1, this.pBoard.getconstraintP().size());
        assertTrue(pendingMoves.isEmpty());
        assertFalse(this.pBoard
                        .getconstraintP()
                        .get(this.grid.getVar(2,2))
                        .getnotKnownNeighbours()
                        .contains(this.grid.getVar(1,1))
        );
        assertEquals(1, this.pBoard.getconstraintP().get(this.grid.getVar(2,2)).getadjBombs());
        System.out.println(this.pBoard.getconstraintP().get(this.grid.getVar(2,2)).getnotKnownNeighbours());
        assertTrue(this.pBoard.getconstraintP()
                .get(this.grid.getVar(2,2))
                .getnotKnownNeighbours()
                .containsAll(Arrays.asList(
                        this.grid.getVar(2,3),
                        this.grid.getVar(3,2),
                        this.grid.getVar(1,2),
                        this.grid.getVar(2,1),
                        this.grid.getVar(1,3),
                        this.grid.getVar(3,1)
                ))
        );
    }

    @Test
    public void gameTest1() {
        Set<Position> pending = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            this.pBoard.setAdjacent(i,2, 0, this.grid, pending, new HashSet<>());
            if (i != 2) this.pBoard.setAdjacent(2,i, 0, this.grid, pending, new HashSet<>());
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if ((i == 0 && j == 0) || (i == 4 && j == 4)) assertFalse(pending.contains(this.grid.getVar(i,j)));
            }
        }
        this.pBoard.setAdjacent(1,1,1,this.grid,pending, new HashSet<>());
        this.pBoard.setAdjacent(0,1,1,this.grid,pending, new HashSet<>());
        this.pBoard.setAdjacent(1,0,1,this.grid,pending, new HashSet<>());
        this.pBoard.setAdjacent(4,3,1,this.grid,pending, new HashSet<>());
        this.pBoard.setAdjacent(3,3,1,this.grid,pending, new HashSet<>());
        this.pBoard.setAdjacent(3,4,1,this.grid,pending, new HashSet<>());
        assertTrue(this.pBoard.getconstraintP().isEmpty());
        assertEquals(10, this.pBoard.getBoard()[0][0]);
        assertEquals(10, this.pBoard.getBoard()[4][4]);

       
    }

    @Test
    public void gameTest2() {
       
        BoardLevel board = new BoardLevel(8, 8);
        board.addBomb(2,2);
        board.addBomb(3,2);
        board.addBomb(5,3);
        board.addBomb(6,3);
        board.addBomb(7,3);
        board.addBomb(6,4);
        board.addBomb(0,5);
        board.addBomb(7,5);
        board.addBomb(7,6);
        board.addBomb(1,7);

        Set<Position> pending = new HashSet<>();
        Grid grid = new Grid(8, 8);

        Board pBoard = new Board(8, 8);
        String[] tmp = new String[]{"(4,0)", "(5,0)", "(6,0)", "(7,0)", "(7,1)", "(6,1)", "(7,2)", "(5,1)", "(6,2)"};
        Queue<Position> q = new LinkedList<>();
        for (String t : tmp) q.add(Position.fromString(t));
        while (!q.isEmpty()) {
            Position next = q.poll();
            pBoard.setAdjacent(next.getX(), next.getY(), board.adjBombs(next.getX(), next.getY()), grid, pending, new HashSet<>());
        }

        

        assertTrue(pending.contains(grid.getVar(3,0)));
        assertTrue(pending.contains(grid.getVar(3,1)));
        assertTrue(pending.contains(grid.getVar(4,1)));
        assertTrue(pending.contains(grid.getVar(4,2)));
        assertTrue(pending.contains(grid.getVar(5,2)));

        assertFalse(pending.contains(grid.getVar(7,3)));
        assertFalse(pending.contains(grid.getVar(6,3)));
        assertFalse(pending.contains(grid.getVar(5,3)));

        assertEquals(Board.BOMB, pBoard.getBoard()[7][3]);
        assertEquals(Board.BOMB, pBoard.getBoard()[6][3]);

        assertEquals(2, pBoard.getBoard()[7][2]);
        assertEquals(3, pBoard.getBoard()[6][2]);

        int counterZero = 0;
        int counterUnknown = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pBoard.getBoard()[i][j] == Board.UNKNOWN) counterUnknown++;
                if (pBoard.getBoard()[i][j] == 0) counterZero++;
            }
        }
        assertEquals(4 * 8 + 6 + 6 + 5 + 4, counterUnknown);
        assertEquals(7, counterZero);

        assertEquals(1, pBoard.getconstraintP().size());
        assertEquals(grid.getVar(6,2), pBoard.getconstraintP().entrySet().iterator().next().getKey());
        ConstraintInfo i = pBoard.getconstraintP().entrySet().iterator().next().getValue();
        Set<Position> expectedVars = new HashSet<>();
        expectedVars.addAll(Arrays.asList(grid.getVar(5,2), grid.getVar(5,3)));
        assertEquals(expectedVars, i.getnotKnownNeighbours());
        assertEquals(1, i.getadjBombs());
    }
}