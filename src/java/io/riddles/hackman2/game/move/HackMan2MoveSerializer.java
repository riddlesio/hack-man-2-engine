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

package io.riddles.hackman2.game.move;

import org.json.JSONObject;

import io.riddles.javainterface.serialize.Serializer;

/**
 * io.riddles.hackman2.game.move.HackMan2MoveSerializer - Created on 15-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class HackMan2MoveSerializer implements Serializer<HackMan2Move> {

    @Override
    public String traverseToString(HackMan2Move move) {
        return visitMove(move).toString();
    }

    @Override
    public JSONObject traverseToJson(HackMan2Move move) {
        return visitMove(move);
    }

    private JSONObject visitMove(HackMan2Move move) {
        JSONObject moveObj = new JSONObject();

        moveObj.put("move", move.getMoveType());

        // Only add exception if present, saves filesize
        if (move.isInvalid()) {
            moveObj.put("exception", move.getException().getMessage());
        }

        // Bomb drop not needed for frontend for now

        return moveObj;
    }
}
