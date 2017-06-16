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
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import io.riddles.hackman2.engine.HackMan2Engine;
import io.riddles.hackman2.game.board.HackMan2Board;
import io.riddles.hackman2.game.move.MoveType;
import io.riddles.hackman2.game.state.HackMan2PlayerState;
import io.riddles.hackman2.game.state.HackMan2State;

/**
 * io.riddles.hackman2.game.enemy.AbstractEnemyAI - Created on 8-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public abstract class AbstractEnemyAI {

    public abstract Point transform(Enemy enemy, HackMan2State state);

    ArrayList<MoveType> getAvailableDirections(Enemy enemy, HackMan2Board board) {
        MoveType direction = enemy.getDirection() != null ? enemy.getDirection() : MoveType.PASS;

        return MoveType.getMovingMoveTypes().stream()
                .filter(moveType -> !moveType.equals(direction.getOppositeMoveType()) &&
                        board.isDirectionValid(enemy.getCoordinate(), moveType))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * If there are any transformations the enemy MUST make, that value is returned.
     */
    Point mandatoryTransform(Enemy enemy, ArrayList<MoveType> availableDirections) {
        Point coordinate = enemy.getCoordinate();

        switch (availableDirections.size()) {
            // No directions available, turn around
            case 0:
                MoveType oppositeDirection = enemy.getDirection().getOppositeMoveType();
                return oppositeDirection.getCoordinateAfterMove(coordinate);
            // Only one direction available, go that way
            case 1:
                return availableDirections.get(0).getCoordinateAfterMove(coordinate);
        }

        return null;
    }

    /**
     * Returns next point (1 step) the enemy must move to if it wants to get closer
     * to given goal.
     */
    Point transformToGoal(Point coordinate, Point goal, ArrayList<MoveType> availableDirections) {
        // Map to the coordinates after moving in each available direction
        ArrayList<Point> movedCoordinates = availableDirections.stream()
                .map(direction -> direction.getCoordinateAfterMove(coordinate))
                .collect(Collectors.toCollection(ArrayList::new));

        // Shuffle so min returns a random value on same distances
        Collections.shuffle(movedCoordinates, HackMan2Engine.RANDOM);

        // Return coordinate with smallest euclidean distance
        return movedCoordinates.stream()
                .min(Comparator.comparingDouble(goal::distance))
                .orElseThrow(() -> new RuntimeException("Failed to find shortest distance"));
    }

    /**
     * Returns the player states sorted from closest to farthest to enemy
     */
    ArrayList<HackMan2PlayerState> getSortedPlayers(Point coordinate, HackMan2State state) {
        ArrayList<HackMan2PlayerState> playerStates = new ArrayList<>(state.getPlayerStates());

        // Shuffle to get random player state on same distance values
        Collections.shuffle(playerStates, HackMan2Engine.RANDOM);

        // Sort from closest to farthest away from enemy coordinate
        playerStates.sort(
                Comparator.comparingDouble(ps -> coordinate.distance(ps.getCoordinate())));

        return playerStates;
    }
}
