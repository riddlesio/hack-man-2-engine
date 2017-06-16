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

import io.riddles.hackman2.engine.HackMan2Engine;
import io.riddles.javainterface.exception.InvalidInputException;
import io.riddles.javainterface.serialize.Deserializer;

/**
 * io.riddles.hackman2.game.move.HackMan2MoveDeserializer - Created on 8-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class HackMan2MoveDeserializer implements Deserializer<HackMan2Move> {

    @Override
    public HackMan2Move traverse(String string) {
        try {
            return visitMove(string);
        } catch (InvalidInputException ex) {
            return new HackMan2Move(ex);
        } catch (Exception ex) {
            return new HackMan2Move(new InvalidInputException("Failed to parse action"));
        }
    }

    private HackMan2Move visitMove(String input) throws InvalidInputException {
        String[] split = input.split(";");

        MoveType moveType = visitMoveType(split[0]);

        if (split.length < 2) {
            return new HackMan2Move(moveType);
        }

        int bombTicks = visitDropBomb(split[1]);
        return new HackMan2Move(moveType, bombTicks);
    }

    private MoveType visitMoveType(String input) throws InvalidInputException {
        MoveType moveType = MoveType.fromString(input);

        if (moveType == null) {
            throw new InvalidInputException("Move action isn't valid");
        }

        return moveType;
    }

    private int visitDropBomb(String input) throws InvalidInputException {
        String[] split = input.split(" ");

        if (!split[0].equals("drop_bomb")) {
            throw new InvalidInputException("Secondary move can only be 'drop_bomb'");
        }

        int bombTicks;

        try {
            bombTicks = Integer.parseInt(split[1]);
        } catch (Exception e) {
            throw new InvalidInputException("Can't parse amount of bomb ticks");
        }

        int minTicks = HackMan2Engine.configuration.getInt("bombMinTicks");
        int maxTicks = HackMan2Engine.configuration.getInt("bombMaxTicks");
        if (bombTicks < minTicks || bombTicks > maxTicks) {
            throw new InvalidInputException(String.format(
                    "Bomb ticks must be between %d and %d (inclusive)", minTicks, maxTicks));
        }

        return bombTicks;
    }
}
