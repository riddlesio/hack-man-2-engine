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

package io.riddles.hackman2.game.board

import io.riddles.hackman2.game.item.Bomb
import spock.lang.Specification

import java.awt.Point

/**
 * io.riddles.hackman2.game.board.HackMan2BoardSpec - Created on 13-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
class HackMan2BoardSpec extends Specification {

    String layout = ".,.,.,x,.,.,.,.,.,.,.,.,.,.,.,x,.,.,.," +
                    ".,x,.,x,.,x,x,x,x,.,x,x,x,x,.,x,.,x,.," +
                    ".,x,.,.,.,x,.,.,.,.,.,.,.,x,.,.,.,x,.," +
                    ".,x,x,x,.,x,.,x,x,x,x,x,.,x,.,x,x,x,.," +
                    ".,x,.,.,.,x,.,.,.,.,.,.,.,x,.,.,.,x,.," +
                    ".,.,.,x,.,x,.,x,x,.,x,x,.,x,.,x,.,.,.," +
                    "x,.,x,x,.,.,.,x,x,.,x,x,.,.,.,x,x,.,x," +
                    ".,.,x,x,.,x,x,x,x,.,x,x,x,x,.,x,x,.,.," +
                    "x,.,x,x,.,.,.,.,.,.,.,.,.,.,.,x,x,.,x," +
                    ".,.,.,x,.,x,x,x,x,x,x,x,x,x,.,x,.,.,.," +
                    ".,x,.,.,.,.,.,.,x,x,x,.,.,.,.,.,.,x,.," +
                    ".,x,.,x,x,.,x,.,.,.,.,.,x,.,x,x,.,x,.," +
                    ".,x,.,x,x,.,x,x,x,x,x,x,x,.,x,x,.,x,.," +
                    ".,x,.,x,x,.,x,.,.,.,.,.,x,.,x,x,.,x,.," +
                    ".,.,.,.,.,.,.,.,x,x,x,.,.,.,.,.,.,.,."

    def "test explode bombs"() {

        setup:
        HackMan2Board board = new HackMan2Board(19, 15, layout, null, new ArrayList<>())
        board.dropBomb(new Point(0, 0), 0)
        board.dropBomb(new Point(2, 0), 5)
        board.dropBomb(new Point(2, 1), 2)
        board.dropBomb(new Point(2, 2), 0)
        board.dropBomb(new Point(18, 0), 0)
        Point coordinate = new Point(18, 5)
        board.bombs.put(coordinate.toString(), new Bomb(coordinate, null))

        when:
        ArrayList<String> explosions = board.explodeBombs()

        then:
        explosions.toString() == "[java.awt.Point[x=0,y=0], java.awt.Point[x=1,y=0], " +
                "java.awt.Point[x=2,y=0], java.awt.Point[x=2,y=1], java.awt.Point[x=2,y=2], " +
                "java.awt.Point[x=3,y=2], java.awt.Point[x=4,y=2], java.awt.Point[x=0,y=1], " +
                "java.awt.Point[x=0,y=2], java.awt.Point[x=0,y=3], java.awt.Point[x=0,y=4], " +
                "java.awt.Point[x=0,y=5], java.awt.Point[x=18,y=0], java.awt.Point[x=18,y=1], " +
                "java.awt.Point[x=18,y=2], java.awt.Point[x=18,y=3], java.awt.Point[x=18,y=4], " +
                "java.awt.Point[x=18,y=5], java.awt.Point[x=17,y=0], java.awt.Point[x=16,y=0]]"
        board.getBombs().size() == 1
    }
}
