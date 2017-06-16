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

import org.json.JSONObject;

import io.riddles.javainterface.serialize.Serializer;

/**
 * io.riddles.hackman2.game.HackMan2ObjectSerializer - Created on 15-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class HackMan2ObjectSerializer implements Serializer<HackMan2Object> {

    @Override
    public String traverseToString(HackMan2Object object) {
        return visitObject(object).toString();
    }

    @Override
    public JSONObject traverseToJson(HackMan2Object object) {
        return visitObject(object);
    }

    private JSONObject visitObject(HackMan2Object object) {
        JSONObject objectObj = new JSONObject();

        objectObj.put("x", object.getCoordinate().x);
        objectObj.put("y", object.getCoordinate().y);

        return objectObj;
    }
}
