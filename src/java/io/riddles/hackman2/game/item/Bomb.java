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

package io.riddles.hackman2.game.item;

import java.awt.Point;

import io.riddles.hackman2.game.HackMan2Object;

/**
 * io.riddles.hackman2.game.item.Bomb - Created on 9-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class Bomb extends HackMan2Object {

    private Integer ticks;

    public Bomb(Point coordinate, Integer ticks) {
        super(coordinate);
        this.ticks = ticks;
    }

    public Bomb(Bomb bomb) {
        super(bomb);
        this.ticks = bomb.ticks;
    }

    @Override
    public String toString() {
        if (this.ticks == null) {
            return "B";
        }

        return String.format("B%d", this.ticks);
    }

    public void tick() {
        if (this.ticks == null) return;

        this.ticks--;
    }

    public Integer getTicks() {
        return this.ticks;
    }
}
