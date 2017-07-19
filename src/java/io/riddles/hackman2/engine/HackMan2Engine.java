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

package io.riddles.hackman2.engine;

import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;

import io.riddles.hackman2.game.HackMan2Serializer;
import io.riddles.hackman2.game.board.Gate;
import io.riddles.hackman2.game.board.HackMan2Board;
import io.riddles.hackman2.game.enemy.EnemySpawnPoint;
import io.riddles.hackman2.game.move.ActionType;
import io.riddles.hackman2.game.move.MoveType;
import io.riddles.hackman2.game.player.HackMan2Player;
import io.riddles.hackman2.game.player.CharacterType;
import io.riddles.hackman2.game.processor.HackMan2Processor;
import io.riddles.hackman2.game.state.HackMan2PlayerState;
import io.riddles.hackman2.game.state.HackMan2State;
import io.riddles.javainterface.configuration.Configuration;
import io.riddles.javainterface.engine.AbstractEngine;
import io.riddles.javainterface.engine.GameLoopInterface;
import io.riddles.javainterface.engine.SimpleGameLoop;
import io.riddles.javainterface.exception.TerminalException;
import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.javainterface.io.IOInterface;

/**
 * io.riddles.hackman2.engine.HackMan2Engine - Created on 8-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class HackMan2Engine extends AbstractEngine<HackMan2Processor, HackMan2Player, HackMan2State> {

    public static SecureRandom RANDOM;

    public HackMan2Engine(PlayerProvider<HackMan2Player> playerProvider, IOInterface ioHandler) throws TerminalException {
        super(playerProvider, ioHandler);
    }

    @Override
    protected Configuration getDefaultConfiguration() {
        Configuration configuration = new Configuration();

        configuration.put("maxRounds", 250); // 250
        configuration.put("playerSnippetCount", 0);
        configuration.put("mapSnippetCount", 2);
        configuration.put("snippetSpawnRate", 8);
        configuration.put("snippetSpawnCount", 1);
        configuration.put("initialEnemyCount", 0);
        configuration.put("enemySpawnDelay", 0);
        configuration.put("enemySpawnRate", 4); // 4
        configuration.put("enemySnippetLoss", 4);
        configuration.put("enemySpawnTime", 3);
        configuration.put("mapBombCount", 0);  // 0
        configuration.put("bombSpawnDelay", 2);  // 2
        configuration.put("bombSpawnRate", 5);  // 5
        configuration.put("bombSnippetLoss", 4);
        configuration.put("bombMinTicks", 2);
        configuration.put("bombMaxTicks", 5);
        configuration.put("fieldWidth", 19);
        configuration.put("fieldHeight", 15);
        configuration.put("fieldLayout", getDefaultFieldLayout());
        configuration.put("seed", UUID.randomUUID().toString());

        return configuration;
    }

    @Override
    protected HackMan2Processor createProcessor() {
        return new HackMan2Processor(this.playerProvider);
    }

    @Override
    protected GameLoopInterface createGameLoop() {
        return new SimpleGameLoop();
    }

    @Override
    protected HackMan2Player createPlayer(int id) {
        return new HackMan2Player(id);
    }

    @Override
    protected void sendSettingsToPlayer(HackMan2Player player) {
        player.sendSetting("your_botid", player.getId());
        player.sendSetting("field_width", configuration.getInt("fieldWidth"));
        player.sendSetting("field_height", configuration.getInt("fieldHeight"));
        player.sendSetting("max_rounds", configuration.getInt("maxRounds"));
    }

    @Override
    protected HackMan2State getInitialState() {
        setRandomSeed();

        // Ask which character the bot wants to play as
        requestPlayerCharacters();

        int width = configuration.getInt("fieldWidth");
        int height = configuration.getInt("fieldHeight");
        String layout = getDefaultFieldLayout();
        ArrayList<EnemySpawnPoint> enemySpawnPoints = getEnemySpawnPoints();
        ArrayList<Gate> gates = getGates();

        // Create board
        HackMan2Board board = new HackMan2Board(width, height, layout, enemySpawnPoints, gates);

        // Create player states
        ArrayList<HackMan2PlayerState> playerStates = new ArrayList<>();
        for (HackMan2Player player : this.playerProvider.getPlayers()) {
            int id = player.getId();
            Point startingCoordinate = getStartingCoordinates(id);
            HackMan2PlayerState playerState = new HackMan2PlayerState(id, startingCoordinate);

            playerStates.add(playerState);
        }

        HackMan2State initialState = new HackMan2State(playerStates, board);

        // Spawn initial items
        board.spawnInitialObjects(initialState);

        return initialState;
    }

    @Override
    protected String getPlayedGame(HackMan2State initialState) {
        HackMan2Serializer serializer = new HackMan2Serializer();
        return serializer.traverseToString(this.processor, initialState);
    }

    private Point getStartingCoordinates(int id) {
        switch (id) {
            case 0:
                return new Point(4, 7);
            case 1:
                return new Point(14, 7);
        }

        return null;
    }

    private String getDefaultFieldLayout() {
        return  ".,.,.,x,.,.,.,.,.,.,.,.,.,.,.,x,.,.,.," +
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
                ".,.,.,.,.,.,.,.,x,x,x,.,.,.,.,.,.,.,.";
    }

    private ArrayList<EnemySpawnPoint> getEnemySpawnPoints() {
        ArrayList<EnemySpawnPoint> spawnPoints = new ArrayList<>();

        spawnPoints.add(new EnemySpawnPoint(new Point(0, 0), 0));
        spawnPoints.add(new EnemySpawnPoint(new Point(18, 0), 1));
        spawnPoints.add(new EnemySpawnPoint(new Point(18, 14), 2));
        spawnPoints.add(new EnemySpawnPoint(new Point(0, 14), 3));

        return spawnPoints;
    }

    private ArrayList<Gate> getGates() {
        ArrayList<Gate> gates = new ArrayList<>();

        Gate gate1 = new Gate(new Point(0, 7), MoveType.LEFT);
        Gate gate2 = new Gate(new Point(18, 7), MoveType.RIGHT);

        gate1.setLinkedGate(gate2);
        gate2.setLinkedGate(gate1);

        gates.add(gate1);
        gates.add(gate2);

        return gates;
    }

    private void requestPlayerCharacters() {
        for (HackMan2Player player : this.playerProvider.getPlayers()) {
            String response = player.requestMove(ActionType.CHARACTER);
            CharacterType character = CharacterType.fromString(response);

            if (character == null) {
                character = CharacterType.getRandomCharacter();
            }

            player.setCharacterType(character);
        }
    }

    private void setRandomSeed() {
        try {
            RANDOM = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.severe("Not able to use SHA1PRNG, using default algorithm");
            RANDOM = new SecureRandom();
        }
        String seed = configuration.getString("seed");
        LOGGER.info("RANDOM SEED IS: " + seed);
        RANDOM.setSeed(seed.getBytes());
    }
}
