package gui;

import javafx.scene.control.Label;

public class BCounter extends Label {
    private int amntLeft;

    /**
     * Constructor.
     *
     * @param amntLeft bombs remaining.
     */
    public BCounter(int amntLeft) {
        this.setamntLeft(amntLeft);
    }

    /**
     * Set bombs remaining.
     *
     * @param amntLeft bombs left.
     */
    public void setamntLeft(int amntLeft) {
        this.amntLeft = amntLeft;
        this.setBText();
    }

    /**
     * Increase bombs by one.
     */
    public void incBombLeft() {
        this.amntLeft++;
        this.setBText();
    }

    /**
     * Decrease bombs by one.
     */
    public void decBombLeft() {
        this.amntLeft--;
        this.setBText();
    }

    /**
     * Change text.
     */
    private void setBText() {
        // TODO: generalize for more than 99 and handle negatives better
        if (this.amntLeft > 10) {
            this.setText(String.format(" %d", this.amntLeft));
        } else if (this.amntLeft >= 0) {
            this.setText(String.format("  %d", this.amntLeft));
        } else if (this.amntLeft > -10) {
            this.setText(String.format(" %d", this.amntLeft));
        } else {
            this.setText(String.format("%d", this.amntLeft));
        }
    }
}
