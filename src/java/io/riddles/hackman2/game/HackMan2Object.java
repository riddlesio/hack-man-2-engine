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

package io.riddles.hackman2.game;

import java.awt.Point;

/**
 * io.riddles.hackman2.game.board.Item - Created on 12-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public abstract class HackMan2Object {

    protected Point coordinate;

    public HackMan2Object(Point coordinate) {
        this.coordinate = coordinate;
    }

    public HackMan2Object(HackMan2Object object) {
        this.coordinate = new Point(object.coordinate);
    }

    public abstract String toString();

    public Point getCoordinate() {
        return this.coordinate;
    }
}
