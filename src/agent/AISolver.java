package agent;

import org.chocosolver.solver.exception.ContradictionException;

import java.util.*;

public class AISolver {

    private static final int EOG = 15; // At the end Markings

    private Set<Position> checkedBomb;
    private Set<Position> uncheckedBomb;
    private Set<Position> history;
    private Set<Position> movesLeft;

    private Random randomGen;
    private Grid grid;
    private Board board;

    private int wd; //width
    private int ht;  //height
    private int bombs;
    private int initialCount; //initial Bomb count variable
    private int movesRemainingToWin;
    private boolean eg; //boolean variable for endgame

    /**
     * Contructor for first random move
     *
     * @param wd size of rows
     * @param ht size of columns
     * @param bombs Bomb number
     */
    public AISolver(int wd, int ht, int bombs) {
        this.init(wd, ht, bombs);
        this.firstMove();
    }

    /**
     * Constructor for first move in parameters
     *
     *
     * @param wd size of rows
     * @param ht size of columns
     * @param bombs Bomb number
     * @param first player's first move
     */
    public AISolver(int wd, int ht, int bombs, Position first) {
        this.init(wd, ht, bombs);
        this.movesLeft.add(first);
    }

    /**
     * Refreshes the GUI until the end of the game
     * Keeps track of the makred position as well.
     *
     * @return Marked Position of GUI and returns null if no position is marked yet.
     */

    public Position bombMarker() {
        Position returnValue = null;
        while (!this.uncheckedBomb.isEmpty()) {
            Position bomb = nextBomb();
            if (!this.checkedBomb.contains(bomb)) {
                this.checkedBomb.add(bomb);
                returnValue = bomb;
                this.bombs--;
                break;
            }
        }
        return returnValue;
    }

    /**
     * Called in each turn
     *
     * @return next move
     */
    public Position nextMove() {
        //stateCheck();

        Position next = null;

        // Any bending moves?
        while (!this.movesLeft.isEmpty()) {
            Position nextMove = nextLeft();
            if (!this.history.contains(nextMove)) {
                next = nextMove;
                this.history.add(nextMove);
                break;
            }
        }

        // If not, look for one
        if (next == null) {
            findMove();
            next = nextLeft();
            this.history.add(next);
        }

        this.movesRemainingToWin--;
        return next;
    }

    /**
     * Called to pass adjacent
     *
     * @param position adjacent position
     * @param adjacent adjacent number
     */
    public void sendBackResult(Position position, int adjacent) {
        this.board.setAdjacent(
                position.getX(),
                position.getY(),
                adjacent,
                this.grid,
                this.movesLeft,
                this.uncheckedBomb
        );

    }

    /**
     * Initializes variable and counters
     *
     * @param wd size of row
     * @param ht size of column
     * @param bombs bomb counter
     */
    private void init(int wd, int ht, int bombs) {
        this.checkedBomb = new HashSet<>();
        this.uncheckedBomb = new HashSet<>();
        this.history = new HashSet<>();
        this.randomGen = new Random();
        this.movesLeft = new HashSet<>();
        this.board = new Board(wd, ht);
        this.grid = new Grid(wd, ht);
        this.wd = wd;
        this.ht = ht;
        this.bombs = bombs;
        this.initialCount = bombs;
        this.eg = false;
        this.movesRemainingToWin = this.wd * this.ht - this.bombs;
    }

    /**
     * Do the first move from pending moves. It is mainly there for avoiding edges of the board.
     */

    private void firstMove() {
        this.movesLeft.add(
                this.grid.getVar(
                        1 + this.randomGen.nextInt(this.wd - 2),
                        1 + this.randomGen.nextInt(this.ht - 2)
                )
        );
    }

    /**
     * Non pending moves searching
     */
    private void findMove() {
        if (!search()) {
            if (this.movesRemainingToWin <= AISolver.EOG) {
                this.eg = true;
            }
            if (this.eg) {
                if (egSearch()) {
                    return;
                }
            }
            guess();
        }
    }


    // @return boolean value for the edge search
    private boolean egSearch() {
        boolean found = false;
        Stack<Position> bombs = new Stack<>();

        Endgame constraints = new Endgame(this.board, this.initialCount, this.wd, this.ht, this.grid);
        try {
            Model_ model = new Model_(constraints);
            for (Position pos : constraints.getvars()) {
                if (model.hBomb(pos)) bombs.add(pos);
                else if (model.hNoBombs(pos)) {
                    found = true;
                    this.movesLeft.add(pos);
                }
            }
        } catch (ContradictionException ex) {
            System.out.println("something wrong with model! debug!");
        }

        boolean searchAgain = !found && !bombs.isEmpty();
        while (!bombs.isEmpty()) {
            Position position = bombs.pop();
            this.board.setManual(position.getX(), position.getY(), this.grid, this.movesLeft, this.uncheckedBomb);
        }
        return searchAgain ? egSearch() : found;
    }

    /**
     * @return boolean value as true if move is available for example some moves might not contain a bomb in it.
     */
    private boolean search() {
        boolean found = false;
        // Any found bombs are set after the search
        Stack<Position> bombs = new Stack<>();
        Groups cGroups = new Groups(this.board);
        for (Map.Entry<Set<ConstraintInfo>, Set<Position>> entry : cGroups.getGroups().entrySet()) {
            found = searchGroup(entry, bombs);
        }
        boolean searchAgain = !found && !bombs.isEmpty();
        while (!bombs.isEmpty()) {
            Position position = bombs.pop();
            this.board.setManual(position.getX(), position.getY(), this.grid, this.movesLeft, this.uncheckedBomb);
        }
        // For finding more bombs then the known bombs. It will re-search grid again.
        return searchAgain ? search() : found;
    }

    /**
     * Searching with taking constraint accordingly
     *
     * @param entry a constraint tuple with the variables.
     * @param bombs a collection which stores located bombs.
     * @return true if non bombed square is found.
     */
    private boolean searchGroup(Map.Entry<Set<ConstraintInfo>, Set<Position>> entry, Stack<Position> bombs) {
        boolean found = false;
        try {
            Model_ model = new Model_(entry.getKey(), entry.getValue());
            for (Position position : entry.getValue()) {
                if (model.hNoBombs(position)) {
                    this.movesLeft.add(position);
                    found = true;
                }
                else if (model.hBomb(position)) bombs.add(position);
            }
        } catch (ContradictionException e) {
            System.out.println("Something wrong with the csp model!");
        }
        return found;
    }

    /**
     * Finds and adds the most likely non bombed square to the pending moves.
     */

    private void guess() {
        if (!this.movesLeft.isEmpty()) return;
        Map<Position, Double> probabilities = new HashMap<>();                  // Map for variables in probability
        Set<Position> variables = new HashSet<>();                              // Collection of variables altogether
        Groups cGroups = new Groups(this.board);            // Group of constraints
        int bombsOutsideVariables = this.bombs - this.uncheckedBombCounter();   // Squares outside variable's Bomb Counter

        // Finds probability for each group of variables
        for (Map.Entry<Set<ConstraintInfo>, Set<Position>> entry : cGroups.getGroups().entrySet()) {
            try {
                Probability pModel = new Probability(entry.getKey(), entry.getValue(), variables);
                // Minus the minimum bombs from non variables' bomb counter
                bombsOutsideVariables -= pModel.getProb(probabilities);
            } catch (ContradictionException e) {
                System.out.println("Something wrong with the csp model!");
            }
        }

        // Sort
        PriorityQueue<Map.Entry<Position, Double>> pq = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));
        for (Map.Entry<Position, Double> entry : probabilities.entrySet()) {
            pq.add(entry);
        }


        // Not known variables
        ArrayList<Position> unknownNonVariables = getUnknown(variables);

        // Case:
        // 1: Select a random from not known if no variable is present
        // 2: Select a least likely bomb from probability if there are no not knonwn
        // 3: Select a least likely from variable and random not known if the variable is neither empty not known.

        if (pq.isEmpty()) {
            this.movesLeft.add(unknownNonVariables.get(this.randomGen.nextInt(unknownNonVariables.size())));
        } else if (unknownNonVariables.isEmpty()) {
            this.movesLeft.add(randomLowestProbability(pq));
        } else {
            double probabilityOfUnknowns = (100.0 * bombsOutsideVariables) / unknownNonVariables.size();
            if (probabilityOfUnknowns < pq.peek().getValue()) {
                this.movesLeft.add(unknownNonVariables.get(this.randomGen.nextInt(unknownNonVariables.size())));
            } else {
                this.movesLeft.add(randomLowestProbability(pq));
            }
        }
    }

    private Position randomLowestProbability(PriorityQueue<Map.Entry<Position, Double>> sortedProbabilities) {
        ArrayList<Map.Entry<Position, Double>> lowProb = new ArrayList<>();
        lowProb.add(sortedProbabilities.poll());
        double prob = lowProb.get(0).getValue();
        while (!sortedProbabilities.isEmpty() && sortedProbabilities.peek().getValue() == prob) {
            lowProb.add(sortedProbabilities.poll());
        }
        return lowProb.get(this.randomGen.nextInt(lowProb.size())).getKey();
    }

    /**
     *
     * @return Some amount of Unchecked Bombs
     */
    private Position nextBomb() {
        Iterator<Position> it = this.uncheckedBomb.iterator();
        Position bomb = it.next();
        it.remove();
        return bomb;
    }

    /**
     *
     * @return Some amount of Pending moves
     */

    private Position nextLeft() {
        Iterator<Position> it = movesLeft.iterator();
        Position pos = it.next();
        it.remove();
        return pos;
    }

    /**
     * @param variables all the variables that are known
     * @return all the unknown variables as an Array List
     */
    public ArrayList<Position> getUnknown(Set<Position> variables) {
        ArrayList<Position> nonVars = new ArrayList<>();
        for (int i = 0; i < this.wd; i++) {
            for (int j = 0; j < this.ht; j++) {
                if (
                        this.board.getBoard()[i][j] == Board.UNKNOWN &&
                        !variables.contains(this.grid.getVar(i, j)) &&
                        !this.uncheckedBomb.contains(this.grid.getVar(i, j))
                ) {
                    nonVars.add(this.grid.getVar(i, j));
                }
            }
        }
        return nonVars;
    }

    /**
     * Calculates no of unchecked bombs
     *
     * @return no of unchecked bombs
     */
    private int uncheckedBombCounter() {
        int counter = 0;
        for (Position position : this.uncheckedBomb) {
            if (!this.checkedBomb.contains(position)) counter++;
        }
        return counter;
    }
}
