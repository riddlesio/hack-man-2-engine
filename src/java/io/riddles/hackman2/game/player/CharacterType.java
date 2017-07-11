package io.riddles.hackman2.game.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.riddles.hackman2.engine.HackMan2Engine;

/**
 * io.riddles.hackman2.game.player.PlayerType - Created on 10-7-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public enum CharacterType {
    BIXIE,
    BIXIETTE;

    private static final Map<String, CharacterType> TYPE_MAP = new HashMap<>();

    static {
        for (CharacterType moveType : values()) {
            TYPE_MAP.put(moveType.toString(), moveType);
        }
    }

    public static CharacterType getRandomCharacter() {
        ArrayList<CharacterType> values = new ArrayList<>(TYPE_MAP.values());
        return values.get(HackMan2Engine.RANDOM.nextInt(values.size()));
    }

    public static CharacterType fromString(String string) {
        return TYPE_MAP.get(string);
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
