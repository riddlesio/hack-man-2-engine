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

import org.json.JSONArray;
import org.json.JSONObject;

import io.riddles.hackman2.game.player.HackMan2Player;
import io.riddles.hackman2.game.processor.HackMan2Processor;
import io.riddles.hackman2.game.state.HackMan2State;
import io.riddles.hackman2.game.state.HackMan2StateSerializer;
import io.riddles.javainterface.game.AbstractGameSerializer;

/**
 * io.riddles.hackman2.game.HackMan2Serializer - Created on 8-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class HackMan2Serializer extends AbstractGameSerializer<HackMan2Processor, HackMan2State> {

    @Override
    public String traverseToString(HackMan2Processor processor, HackMan2State initialState) {
        HackMan2StateSerializer stateSerializer = new HackMan2StateSerializer();
        JSONObject game = new JSONObject();

        game = addDefaultJSON(initialState, game, processor);

        JSONArray characters = new JSONArray();
        for (HackMan2Player player : processor.getPlayerProvider().getPlayers()) {
            JSONObject characterObj = new JSONObject();
            characterObj.put("id", player.getId());
            characterObj.put("type", player.getCharacterType());

            characters.put(characterObj);
        }

        JSONArray states = new JSONArray();
        states.put(stateSerializer.traverseToJson(initialState));

        HackMan2State state = initialState;
        while (state.hasNextState()) {
            state = (HackMan2State) state.getNextState();
            states.put(stateSerializer.traverseToJson(state));
        }

        game.put("characters", characters);
        game.put("states", states);
        game.put("engineVersion", HackMan2Serializer.class.getPackage().getImplementationVersion());

        return game.toString();
    }
}
