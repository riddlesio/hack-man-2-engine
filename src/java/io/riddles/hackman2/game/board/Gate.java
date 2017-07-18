package io.riddles.hackman2.game.board;

import java.awt.*;

import io.riddles.hackman2.game.HackMan2Object;
import io.riddles.hackman2.game.move.MoveType;

/**
 * io.riddles.hackman2.game.board.Gate - Created on 18-7-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class Gate extends HackMan2Object {

    private MoveType entry;
    private Gate linkedGate;

    public Gate(Point coordinate, MoveType entry) {
        super(coordinate);
        this.entry = entry;
    }

    @Override
    public String toString() {
        return String.format("G%s", this.entry.toString().charAt(0));
    }

    public MoveType getEntry() {
        return this.entry;
    }

    public Gate getLinkedGate() {
        return this.linkedGate;
    }

    public void setLinkedGate(Gate linkedGate) {
        this.linkedGate = linkedGate;
    }
}
