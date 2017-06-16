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

package io.riddles.hackman2.game.state;

import org.json.JSONArray;
import org.json.JSONObject;

import io.riddles.hackman2.game.HackMan2ObjectSerializer;
import io.riddles.hackman2.game.item.Snippet;
import io.riddles.javainterface.serialize.Serializer;

/**
 * io.riddles.hackman2.game.state.HackMan2StateSerializer - Created on 8-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class HackMan2StateSerializer implements Serializer<HackMan2State> {

    @Override
    public String traverseToString(HackMan2State state) {
        return visitState(state).toString();
    }

    @Override
    public JSONObject traverseToJson(HackMan2State state) {
        return visitState(state);
    }

    private JSONObject visitState(HackMan2State state) {
        HackMan2PlayerStateSerializer playerStateSerializer = new HackMan2PlayerStateSerializer();
        HackMan2ObjectSerializer objectSerializer = new HackMan2ObjectSerializer();

        JSONObject stateObj = new JSONObject();

        JSONArray playerArray = new JSONArray();
        JSONArray collectibleArray = new JSONArray();
        JSONArray bombArray = new JSONArray();
        JSONArray enemyArray = new JSONArray();

        state.getPlayerStates().forEach(
                playerState -> playerArray.put(playerStateSerializer.traverseToJson(playerState))
        );
        state.getBoard().getSnippets().forEach(
                (key, snippet) -> collectibleArray.put(objectSerializer.traverseToJson(snippet))
        );
        state.getBoard().getBombs().forEach(
                (key, bomb) -> bombArray.put(objectSerializer.traverseToJson(bomb))
        );
        state.getBoard().getEnemies().forEach(
                enemy -> enemyArray.put(objectSerializer.traverseToJson(enemy))
        );

        stateObj.put("round", state.getRoundNumber());
        stateObj.put("players", playerArray);
        stateObj.put("collectibles", collectibleArray);
        stateObj.put("bombs", bombArray);
        stateObj.put("enemies", enemyArray);

        return stateObj;
    }
}
