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

package io.riddles.hackman2.game.enemy;

import java.awt.*;
import java.util.ArrayList;

import io.riddles.hackman2.game.board.HackMan2Board;
import io.riddles.hackman2.game.move.MoveType;
import io.riddles.hackman2.game.state.HackMan2PlayerState;
import io.riddles.hackman2.game.state.HackMan2State;

/**
 * io.riddles.hackman2.game.enemy.FarChaseAI - Created on 8-6-17
 *
 * This enemy AI will always move to the current location
 * of the farthest player.
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class FarChaseAI extends AbstractEnemyAI {

    @Override
    public Point transform(Enemy enemy, HackMan2State state) {
        HackMan2Board board = state.getBoard();
        Point coordinate = enemy.getCoordinate();
        ArrayList<MoveType> availableDirections = getAvailableDirections(enemy, board);

        Point mandatoryTransform = mandatoryTransform(enemy, availableDirections);
        if (mandatoryTransform != null) {  // Mandatory directions are always taken
            return mandatoryTransform;
        }

        // Get farthest player
        ArrayList<HackMan2PlayerState> players = getSortedPlayers(coordinate, state);
        HackMan2PlayerState farthestPlayer = players.get(players.size() - 1);

        return transformToGoal(coordinate, farthestPlayer.getCoordinate(), availableDirections);
    }
}
