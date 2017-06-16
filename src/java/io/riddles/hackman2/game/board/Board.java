package io.riddles.hackman2.game.board;

import java.awt.*;

import io.riddles.hackman2.game.move.MoveType;
import io.riddles.hackman2.game.state.HackMan2State;

/**
 * io.riddles.hackman2.game.board.Board - Created on 9-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public abstract class Board {

    protected final String EMTPY_FIELD = ".";
    protected final String BLOCKED_FIELD = "x";

    protected int width;
    protected int height;
    protected String layout;
    protected String[][] fields;

    Board(int width, int height, String layout) {
        this.width = width;
        this.height = height;
        this.layout = layout;
        this.fields = new String[width][height];

        setFieldsFromString(layout);
    }

    public boolean isDirectionValid(Point coordinate, MoveType direction) {
        if (direction == null) {
            return false;
        }

        Point newCoordinate = direction.getCoordinateAfterMove(coordinate);
        return isCoordinateValid(newCoordinate);
    }

    public boolean isCoordinateValid(Point coordinate) {
        int x = coordinate.x;
        int y = coordinate.y;

        return x >= 0 && y >= 0 && x < this.width && y < this.height &&
                !this.fields[x][y].equals(BLOCKED_FIELD);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String getLayout() {
        return this.layout;
    }

    private void setFieldsFromString(String input) {
        String[] split = input.split(",");
        int x = 0;
        int y = 0;

        for (String fieldString : split) {
            this.fields[x][y] = fieldString;

            if (++x == this.width) {
                x = 0;
                y++;
            }
        }
    }
}
