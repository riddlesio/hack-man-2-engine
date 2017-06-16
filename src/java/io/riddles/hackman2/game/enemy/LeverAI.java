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
import java.util.Comparator;

import io.riddles.hackman2.game.board.HackMan2Board;
import io.riddles.hackman2.game.move.MoveType;
import io.riddles.hackman2.game.state.HackMan2PlayerState;
import io.riddles.hackman2.game.state.HackMan2State;

/**
 * io.riddles.hackman2.game.enemy.LeverAI - Created on 8-6-17
 *
 * This enemy AI will always move to a point that is the vector
 * from the closest player and the closest other enemy to that player
 * times two.
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class LeverAI extends AbstractEnemyAI {

    @Override
    public Point transform(Enemy enemy, HackMan2State state) {
        HackMan2Board board = state.getBoard();
        Point coordinate = enemy.getCoordinate();
        ArrayList<MoveType> availableDirections = getAvailableDirections(enemy, board);

        Point mandatoryTransform = mandatoryTransform(enemy, availableDirections);
        if (mandatoryTransform != null) {  // Mandatory directions are always taken
            return mandatoryTransform;
        }

        HackMan2PlayerState closestPlayer = getSortedPlayers(coordinate, state).get(0);
        ArrayList<Enemy> enemies = state.getBoard().getEnemies();
        Enemy closestEnemy = enemies.stream()
                .filter(e -> e.getId() != enemy.getId())
                .min(Comparator.comparingDouble(
                        e -> e.getCoordinate().distance(closestPlayer.getCoordinate())))
                .orElse(null);

        if (closestEnemy == null) {  // if no other enemy, take self
            closestEnemy = enemy;
        }

        Point playerCoord = closestPlayer.getCoordinate();
        Point enemyCoord = closestEnemy.getCoordinate();
        Point leverVector = new Point(
                (playerCoord.x - enemyCoord.x) * 2,
                (playerCoord.y - enemyCoord.y) * 2
        );
        Point goalPosition = new Point(coordinate.x + leverVector.x, coordinate.y + leverVector.y);

        return transformToGoal(coordinate, goalPosition, availableDirections);
    }
}
