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

package io.riddles.hackman2.game.state;

import java.awt.*;

import io.riddles.hackman2.game.move.HackMan2Move;
import io.riddles.hackman2.game.move.MoveType;
import io.riddles.javainterface.game.state.AbstractPlayerState;

/**
 * io.riddles.hackman2.game.state.HackMan2PlayerState - Created on 8-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class HackMan2PlayerState extends AbstractPlayerState<HackMan2Move> {

    private int snippets;
    private int bombs;
    private Point coordinate;
    private boolean isAlive;
    private MoveType direction;

    public HackMan2PlayerState(int playerId, Point startCoordinate) {
        super(playerId);
        this.snippets = 0;
        this.bombs = 0;
        this.coordinate = startCoordinate;
        this.isAlive = true;
        this.direction = null;
    }

    public HackMan2PlayerState(HackMan2PlayerState playerState) {
        super(playerState.getPlayerId());
        this.snippets = playerState.getSnippets();
        this.bombs = playerState.getBombs();
        this.coordinate = new Point(playerState.getCoordinate());
        this.isAlive = playerState.isAlive();
        this.direction = playerState.getDirection();
    }

    public String toString() {
        return String.format("P%d", this.playerId);
    }

    public void updateSnippets(int delta) {
        this.snippets += delta;

        if (this.snippets < 0) {
            this.snippets = 0;
            this.isAlive = false;
        }
    }

    public int getSnippets() {
        return this.snippets;
    }

    public void updateBombs(int delta) {
        this.bombs = this.bombs + delta >= 0 ? this.bombs + delta : 0;
    }

    public int getBombs() {
        return this.bombs;
    }

    public void setCoordinate(Point coordinate) {
        this.coordinate = coordinate;
    }

    public Point getCoordinate() {
        return this.coordinate;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public void setDirection(MoveType direction) {
        this.direction = direction;
    }

    public MoveType getDirection() {
        return this.direction;
    }
}
