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

import io.riddles.hackman2.game.HackMan2Object;
import io.riddles.hackman2.game.move.MoveType;
import io.riddles.hackman2.game.state.HackMan2State;

/**
 * io.riddles.hackman2.game.enemy.Enemy - Created on 8-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class Enemy extends HackMan2Object {

    private int id;
    private int type;
    private boolean isAlive;
    private EnemyDeath deathType;
    private MoveType direction;
    private AbstractEnemyAI enemyAI;

    public Enemy(int id, Point coordinate, int type) {
        super(coordinate);
        this.id = id;
        this.type = type;
        this.isAlive = true;

        switch (type) {
            case 0:
                this.enemyAI = new ChaseAI();
                break;
            case 1:
                this.enemyAI = new PredictAI();
                break;
            case 2:
                this.enemyAI = new LeverAI();
                break;
            case 3:
                this.enemyAI = new FarChaseAI();
                break;
        }
    }

    public Enemy(Enemy enemy) {
        super(enemy);
        this.id = enemy.id;
        this.type = enemy.type;
        this.direction = enemy.direction;
        this.enemyAI = enemy.enemyAI;
        this.isAlive = enemy.isAlive;
        this.deathType = enemy.deathType;
    }

    public String toString() {
        return String.format("E%d", this.type);
    }

    public void performMovement(HackMan2State state) {
        Point c = this.enemyAI.transform(this, state);
        setCoordinate(c);
    }

    public void kill(HackMan2State state, EnemyDeath deathType) {
        this.isAlive = false;
        this.deathType = deathType;
    }

    public int getId() {
        return this.id;
    }

    public int getType() {
        return this.type;
    }

    public MoveType getDirection() {
        return this.direction;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public EnemyDeath getDeathType() {
        return this.deathType;
    }

    private void setCoordinate(Point coordinate) {
        this.direction = getNewDirection(this.coordinate, coordinate);
        this.coordinate = coordinate;
    }

    private MoveType getNewDirection(Point oldCoordinate, Point newCoordinate) {
        if (newCoordinate.x > oldCoordinate.x) return MoveType.RIGHT;
        if (newCoordinate.x < oldCoordinate.x) return MoveType.LEFT;
        if (newCoordinate.y > oldCoordinate.y) return MoveType.DOWN;
        if (newCoordinate.y < oldCoordinate.y) return MoveType.DOWN;

        return null;
    }
}
