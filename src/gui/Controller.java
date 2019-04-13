package gui;

import agent.AISolver;
import agent.Position;
import javafx.application.Platform;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import level.BoardLevel;
import level.RandomGenerator;

/**
 * The event handler and brain for the entire GUI.
 */
public class Controller {

    /**
     * Constructor is private and only this single instance can be used.
     */
    public static Controller controller = new Controller();

    private Player player = Player.HUMAN;
    private Size size = Size.SMALL;
    private BoardLevel BoardLevel;
    private GameState state = GameState.IDLE;
    private RandomGenerator BoardLevelGenerator;
    private Buttons BoardLevelButtons;
    private BorderPane root;
    private MinesMenu menu;
    private Footer footer;
    private Stage stage;
    private int winClicks;
    private AISolver agent;

    /**
     * Constructor. Initially, a human playing 8x8 game is created.
     */
    private Controller() {
        this.player = Player.HUMAN;
        this.size = Size.SMALL;
        this.state = GameState.IDLE;
        this.BoardLevelGenerator = new RandomGenerator();
        this.BoardLevel = null;
        this.BoardLevelButtons = null;
        this.root = new BorderPane();
        this.menu = new MinesMenu();
        this.footer = new Footer();
        this.root.setTop(this.menu);
        this.BoardLevel = this.BoardLevelGenerator.createBoard(8, 8, 10, false, false);
        this.winClicks = 8 * 8 - 10;
        this.BoardLevelButtons = new Buttons(this.BoardLevel);
        this.root.setCenter(this.BoardLevelButtons);
        this.root.setBottom(this.footer);
        this.footer.getPlay().setDisable(true);
        this.agent = null;
    }

    /**
     * Quit program.
     */
    public void exit() {
        Platform.exit();
    }

    /**
     * Create a new game.
     */
    public void newGame() {
        this.state = GameState.IDLE;
        switch (size) {
            case SMALL:
                this.BoardLevel = this.BoardLevelGenerator.createBoard(8, 8, 10, false, false);
                this.footer.getBombsLeft().setamntLeft(10);
                this.winClicks = 8 * 8 - 10;
                if (this.player == Player.Computer) this.agent = new AISolver(8,8,10);
                break;
            case MEDIUM:
                this.BoardLevel = this.BoardLevelGenerator.createBoard(16, 16, 40, false, false);
                this.footer.getBombsLeft().setamntLeft(40);
                this.winClicks = 16 * 16 - 40;
                if (this.player == Player.Computer) this.agent = new AISolver(16,16,40);
                break;
            case LARGE:
                this.BoardLevel = this.BoardLevelGenerator.createBoard(24, 24, 99, false, false);
                this.footer.getBombsLeft().setamntLeft(99);
                this.winClicks = 24 * 24 - 99;
                if (this.player == Player.Computer) this.agent = new AISolver(24,24,99);
        }
        this.footer.getTimer().restartPlayClock();
        this.footer.setStatus(this.state);
        if (this.player == Player.HUMAN) footer.getPlay().setDisable(true);
        else footer.getPlay().setDisable(false);
        this.BoardLevelButtons = new Buttons(this.BoardLevel);
        this.root.setCenter(this.BoardLevelButtons);
    }

    /**
     * Changes player
     *
     * @param player Player enum
     */
    public void setPlayer(Player player) {
        this.player = player;
        newGame();
    }

    /**
     * Changes size.
     *
     * @param size Size enum
     */
    public void setSize(Size size) {
        this.size = size;
        newGame();
        this.resizeStage();
    }

    /**
     * @return UI root.
     */
    public BorderPane getRoot() {
        return this.root;
    }

    /**
     * Open square as a human player.
     *
     * @param button Button to click
     * @param x coordinate
     * @param y coordinate
     */
    public void buttonUpdate(MinesBtn button, int x, int y) {
        if (this.player == Player.Computer || this.state == GameState.LOST || this.state == GameState.WON || button.isDown() || button.getText().equals("#")) return;
        if (this.state == GameState.IDLE) {
            this.state = GameState.PLAYING;
            this.footer.setStatus(this.state);
            this.footer.getTimer().startPlayClock();
        }
        button.click();

        if (this.BoardLevel.hasBomb(x,y)) {
            button.setText("X");
            button.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
            this.state = GameState.LOST;
            this.footer.setStatus(this.state);
            this.footer.getTimer().stopPlayClock();
        } else {
            this.winClicks--;
            int adj = this.BoardLevel.adjBombs(x, y);
            if (adj == 0) {
                // Recursive auto update for 0-squares
                if (BoardLevel.outBounds(x - 1, y - 1)) this.BoardLevelButtons.get(x - 1, y - 1).fire();
                if (BoardLevel.outBounds(x, y - 1)) this.BoardLevelButtons.get(x, y - 1).fire();
                if (BoardLevel.outBounds(x + 1, y - 1)) this.BoardLevelButtons.get(x + 1, y - 1).fire();
                if (BoardLevel.outBounds(x - 1, y)) this.BoardLevelButtons.get(x - 1, y).fire();
                if (BoardLevel.outBounds(x + 1, y)) this.BoardLevelButtons.get(x + 1, y).fire();
                if (BoardLevel.outBounds(x - 1, y + 1)) this.BoardLevelButtons.get(x - 1, y + 1).fire();
                if (BoardLevel.outBounds(x, y + 1)) this.BoardLevelButtons.get(x, y + 1).fire();
                if (BoardLevel.outBounds(x + 1, y + 1)) this.BoardLevelButtons.get(x + 1, y + 1).fire();
            } else {
                button.setText(Integer.toString(adj));
            }
            button.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            if (winClicks == 0) {
                this.state = GameState.WON;
                this.footer.setStatus(this.state);
                this.footer.getTimer().stopPlayClock();
            }
        }
    }

    /**
     * Run solver.
     */
    public void playAsComputer() {
        if (this.player == Player.HUMAN) return;
        newGame();
        this.state = GameState.PLAYING;
        this.footer.setStatus(this.state);
        this.footer.getTimer().startPlayClock();
        while (this.state != GameState.LOST && this.state != GameState.WON) {
            // Mark all bombs the agent knows of that have not already been marked.
            Position bomb;
            while ((bomb = this.agent.bombMarker()) != null) {
                this.BoardLevelButtons.get(bomb.getX(), bomb.getY()).setText("#");
                this.footer.getBombsLeft().decBombLeft();
            }
            // Get next move from agent.
            Position pos = this.agent.nextMove();
            this.agent.sendBackResult(pos, computerClick(pos.getX(), pos.getY()));
        }
        if (this.state == GameState.WON) {
            Position bomb;
            while ((bomb = this.agent.bombMarker()) != null) {
                this.BoardLevelButtons.get(bomb.getX(), bomb.getY()).setText("#");
                this.footer.getBombsLeft().decBombLeft();
            }
        }
    }

    /**
     * Click for computer. Not a UI event.
     *
     * @param x coordinate
     * @param y coordinate
     * @return adjacent number in actual BoardLevel for (x,y)
     */
    private int computerClick(int x, int y) {
        MinesBtn button = this.BoardLevelButtons.get(x, y);
        button.click();
        if (this.BoardLevel.hasBomb(x,y)) {
            // If lost, we just return any number since they game won't continue.
            button.setText("X");
            button.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
            this.state = GameState.LOST;
            this.footer.setStatus(this.state);
            this.footer.getTimer().stopPlayClock();
            return 0;
        } else {
            // If the square does not contain a bomb, we return the number it contains.
            // This is the only communication with the agent.
            this.winClicks--;
            int adj = this.BoardLevel.adjBombs(x, y);
            if (adj != 0) button.setText(Integer.toString(adj));
            button.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            if (winClicks == 0) {
                this.state = GameState.WON;
                this.footer.setStatus(this.state);
                this.footer.getTimer().stopPlayClock();
            }
            return adj;
        }
    }

    /**
     * Mark square as bombs for human player.
     *
     * @param button Button to mark
     */
    public void markBomb(MinesBtn button) {
        if (this.player == Player.Computer || this.state == GameState.LOST || this.state == GameState.WON) return;
        if (button.getText().equals("#")) {
            button.setText("");
            this.footer.getBombsLeft().incBombLeft();
        } else {
            button.setText("#");
            this.footer.getBombsLeft().decBombLeft();
        }
    }

    /**
     * @param stage Stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Resizing for level changing.
     */
    public void resizeStage() {
        this.stage.sizeToScene();
        this.stage.centerOnScreen();
        this.stage.setWidth(this.stage.getWidth() + 10);
        this.stage.setHeight(this.stage.getHeight() + 10);
    }

}
