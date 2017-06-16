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

import java.util.ArrayList;
import java.util.stream.Collectors;

import io.riddles.hackman2.game.board.HackMan2Board;
import io.riddles.javainterface.game.state.AbstractPlayerState;
import io.riddles.javainterface.game.state.AbstractState;

/**
 * io.riddles.hackman2.game.state.HackMan2State - Created on 8-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class HackMan2State extends AbstractState<HackMan2PlayerState> {

    private HackMan2Board board;
    private int snippetsCollected;

    // For initital state only
    public HackMan2State(ArrayList<HackMan2PlayerState> playerStates, HackMan2Board board) {
        super(null, playerStates, 0);
        this.board = board;
    }

    public HackMan2State(HackMan2State previousState, ArrayList<HackMan2PlayerState> playerStates, int roundNumber) {
        super(previousState, playerStates, roundNumber);
        this.board = new HackMan2Board(previousState.getBoard());
    }

    public HackMan2State createNextState(int roundNumber) {
        // Create new player states from current player states
        ArrayList<HackMan2PlayerState> playerStates = new ArrayList<>();
        for (HackMan2PlayerState playerState : this.getPlayerStates()) {
            playerStates.add(new HackMan2PlayerState(playerState));
        }

        // Create new state from current state
        return new HackMan2State(this, playerStates, roundNumber);
    }

    public ArrayList<HackMan2PlayerState> getAlivePlayers() {
        return this.playerStates.stream()
                .filter(HackMan2PlayerState::isAlive)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void addSnippetCollected() {
        this.snippetsCollected++;
    }

    public int getSnippetsCollected() {
        return this.snippetsCollected;
    }

    public HackMan2Board getBoard() {
        return this.board;
    }
}
