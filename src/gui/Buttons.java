package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import level.BoardLevel;

public class Buttons extends GridPane {

    private MinesBtn[][] access;

    /**
     * Constructor. Creates buttons for a given BoardLevel.
     *
     * @param BoardLevel minesweeper BoardLevel.
     */
    public Buttons(BoardLevel BoardLevel) {
        setPadding(new Insets(5, 5, 5, 5));
        this.setAlignment(Pos.CENTER);
        this.access = new MinesBtn[BoardLevel.getWidth()][BoardLevel.getHeight()];
        for (int i = 0; i < BoardLevel.getWidth(); i++) {
            for (int j = 0; j < BoardLevel.getHeight(); j++) {
                MinesBtn button = new MinesBtn(i,j);
                this.add(button, i, j);
                this.access[i][j] = button;
            }
        }
    }

    /**
     * Getter for data structure.
     *
     * @param x coordinate
     * @param y coordinate
     * @return A button at (x,y).
     */
    public MinesBtn get(int x, int y) {
        return this.access[x][y];
    }
}
