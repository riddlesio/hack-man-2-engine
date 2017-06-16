/*
 * Copyright 2017 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

package io.riddles.hackman2.game.move;

import java.awt.Point;
import java.util.ArrayList;

import io.riddles.hackman2.engine.HackMan2Engine;

/**
 * io.riddles.hackman2.game.move.MoveType - Created on 8-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public enum MoveType {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    PASS;

    public static ArrayList<MoveType> getMovingMoveTypes() {
        ArrayList<MoveType> movingMoveTypes = new ArrayList<>();

        movingMoveTypes.add(UP);
        movingMoveTypes.add(DOWN);
        movingMoveTypes.add(LEFT);
        movingMoveTypes.add(RIGHT);

        return movingMoveTypes;
    }

    public MoveType getOppositeMoveType() {
        switch (this) {
            case LEFT:
                return MoveType.RIGHT;
            case RIGHT:
                return MoveType.LEFT;
            case UP:
                return MoveType.DOWN;
            case DOWN:
                return MoveType.UP;
            default:
                return this;
        }
    }

    public Point getDirectionVector() {
        switch (this) {
            case LEFT:
                return new Point(-1, 0);
            case RIGHT:
                return new Point(1, 0);
            case UP:
                return new Point(0, -1);
            case DOWN:
                return new Point(0, 1);
            default:
                return new Point(0, 0);
        }
    }

    public Point getCoordinateAfterMove(Point coordinate) {
        Point direction = getDirectionVector();

        return new Point(coordinate.x + direction.x, coordinate.y + direction.y);
    }

    public static MoveType fromString(String input) {
        for (MoveType moveType : MoveType.values()) {
            if (moveType.toString().equalsIgnoreCase(input)) {
                return moveType;
            }
        }

        return null;
    }

    public String toString() {
        return this.name().toLowerCase();
    }
}
