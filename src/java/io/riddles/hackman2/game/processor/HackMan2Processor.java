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

package io.riddles.hackman2.game.processor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;

import io.riddles.hackman2.engine.HackMan2Engine;
import io.riddles.hackman2.game.board.HackMan2Board;
import io.riddles.hackman2.game.move.ActionType;
import io.riddles.hackman2.game.move.HackMan2Move;
import io.riddles.hackman2.game.move.HackMan2MoveDeserializer;
import io.riddles.hackman2.game.player.HackMan2Player;
import io.riddles.hackman2.game.state.HackMan2PlayerState;
import io.riddles.hackman2.game.state.HackMan2State;
import io.riddles.javainterface.exception.InvalidMoveException;
import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.javainterface.game.processor.SimpleProcessor;
import io.riddles.javainterface.game.state.AbstractPlayerState;

/**
 * io.riddles.hackman2.game.processor.HackMan2Processor - Created on 8-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class HackMan2Processor extends SimpleProcessor<HackMan2State, HackMan2Player> {

    private HackMan2MoveDeserializer moveDeserializer;

    public HackMan2Processor(PlayerProvider<HackMan2Player> playerProvider) {
        super(playerProvider);
        this.moveDeserializer = new HackMan2MoveDeserializer();
    }

    @Override
    public boolean hasGameEnded(HackMan2State state) {
        int maxRounds = HackMan2Engine.configuration.getInt("maxRounds");
        ArrayList<HackMan2PlayerState> alivePlayers = state.getAlivePlayers();

        return state.getRoundNumber() >= maxRounds || alivePlayers.size() <= 1;
    }

    @Override
    public Integer getWinnerId(HackMan2State state) {
        ArrayList<HackMan2PlayerState> alivePlayers = state.getAlivePlayers();

        return alivePlayers.stream()
                .max(Comparator.comparingInt(HackMan2PlayerState::getSnippets))
                .map(AbstractPlayerState::getPlayerId)
                .orElse(null);
    }

    @Override
    public double getScore(HackMan2State state) {
        return state.getRoundNumber();
    }

    @Override
    public HackMan2State createNextState(HackMan2State inputState, int roundNumber) {
        HackMan2State nextState = inputState.createNextState(roundNumber);
        HackMan2Board board = nextState.getBoard();

        // Clean up enemies from previous round (i.e. remove dead ones)
        board.cleanUpEnemies();

        // Send updates and get all moves
        for (HackMan2PlayerState playerState : nextState.getPlayerStates()) {
            HackMan2Player player = getPlayer(playerState.getPlayerId());

            sendUpdatesToPlayer(inputState, player);
            HackMan2Move move = getPlayerMove(player);
            playerState.setMove(move);
        }

        // Move enemies (before they know where the players are going)
        board.moveEnemies(nextState);

        // Move the players
        executePlayerMoves(nextState);

        // Calculate changes due to collisions
        board.performPickups(nextState);
        board.performCollisions(inputState, nextState);

        // Spawn all new objects
        board.spawnObjects(nextState);

        return nextState;
    }

    private void sendUpdatesToPlayer(HackMan2State state, HackMan2Player player) {
        player.sendUpdate("round", state.getRoundNumber());
        player.sendUpdate("field", state.getBoard().toString(state));

        for (HackMan2PlayerState targetPlayerState : state.getPlayerStates()) {
            HackMan2Player target = getPlayer(targetPlayerState.getPlayerId());

            player.sendUpdate("snippets", target, targetPlayerState.getSnippets());
            player.sendUpdate("bombs", target, targetPlayerState.getBombs());
        }
    }

    private void executePlayerMoves(HackMan2State state) {
        HackMan2Board board = state.getBoard();

        for (HackMan2PlayerState playerState : state.getPlayerStates()) {
            HackMan2Move move = playerState.getMove();
            board.validateMove(playerState, move);

            if (!move.isInvalid()) {
                executePlayerMove(board, playerState, move);
            }

            if (move.isInvalid()) {
                HackMan2Player player = getPlayer(playerState.getPlayerId());
                player.sendWarning(move.getException().getMessage());
            }
        }
    }

    private void executePlayerMove(HackMan2Board board, HackMan2PlayerState playerState, HackMan2Move move) {
        // Move player
        Point oldCoordinate = playerState.getCoordinate();
        Point newCoordinate = move.getMoveType().getCoordinateAfterMove(oldCoordinate);
        playerState.setCoordinate(newCoordinate);
        playerState.setDirection(move.getMoveType());

        // Drop bomb
        if (move.getBombTicks() != null) {
            if (playerState.getBombs() > 0) {
                board.dropBomb(newCoordinate, move.getBombTicks());
                playerState.updateBombs(-1);
            } else {
                move.setException(new InvalidMoveException("No bombs available"));
            }
        }
    }

    private HackMan2Move getPlayerMove(HackMan2Player player) {
        String response = player.requestMove(ActionType.MOVE);
        return this.moveDeserializer.traverse(response);
    }

    private HackMan2Player getPlayer(int id) {
        return this.playerProvider.getPlayerById(id);
    }
}
