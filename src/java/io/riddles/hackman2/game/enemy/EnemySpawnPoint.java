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

/**
 * io.riddles.hackman2.game.enemy.SpawnPoint - Created on 14-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class EnemySpawnPoint extends HackMan2Object {

    private int type;
    public Integer spawnTime;

    public EnemySpawnPoint(Point coordinate, int type) {
        super(coordinate);
        this.type = type;
        this.spawnTime = null;
    }

    @Override
    public String toString() {
        if (this.spawnTime == null || this.spawnTime == 0) {
            return "e";
        }

        return String.format("e%d", this.spawnTime);
    }

    public void setSpawnTime(Integer spawnTime) {
        this.spawnTime = spawnTime;
    }

    public void reduceSpawnTime() {
        if (this.spawnTime == null) return;

        this.spawnTime--;
    }

    public Enemy spawnEnemy(int id) {
        if (this.spawnTime == null || this.spawnTime != 0) return null;

        this.spawnTime = null;

        return new Enemy(id, new Point(getCoordinate()), this.type);
    }
}
