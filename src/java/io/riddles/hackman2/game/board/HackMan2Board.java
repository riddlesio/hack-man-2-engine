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

package io.riddles.hackman2.game.board;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.riddles.hackman2.engine.HackMan2Engine;
import io.riddles.hackman2.game.HackMan2Object;
import io.riddles.hackman2.game.enemy.Enemy;
import io.riddles.hackman2.game.enemy.EnemyDeath;
import io.riddles.hackman2.game.enemy.EnemySpawnPoint;
import io.riddles.hackman2.game.item.Bomb;
import io.riddles.hackman2.game.item.Snippet;
import io.riddles.hackman2.game.move.HackMan2Move;
import io.riddles.hackman2.game.move.MoveType;
import io.riddles.hackman2.game.state.HackMan2PlayerState;
import io.riddles.hackman2.game.state.HackMan2State;
import io.riddles.javainterface.configuration.Configuration;
import io.riddles.javainterface.exception.InvalidMoveException;

/**
 * io.riddles.hackman2.game.board.HackMan2Board - Created on 8-6-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class HackMan2Board extends Board {

    private ArrayList<Enemy> enemies;
    private HashMap<String, Bomb> bombs;
    private HashMap<String, Snippet> snippets;
    private HashMap<String, Gate> gates;
    private ArrayList<EnemySpawnPoint> enemySpawnPoints;

    private int spawnedEnemies;
    private int spawnedBombs;

    public HackMan2Board(int width, int height, String layout,
                         ArrayList<EnemySpawnPoint> enemySpawnPoints, ArrayList<Gate> gates) {
        super(width, height, layout);

        this.enemySpawnPoints = enemySpawnPoints;

        this.enemies = new ArrayList<>();
        this.bombs = new HashMap<>();
        this.snippets = new HashMap<>();
        this.gates = new HashMap<>();
        this.spawnedEnemies = 0;
        this.spawnedBombs = 0;

        gates.forEach(gate -> this.gates.put(gate.getCoordinate().toString(), gate));
    }

    public HackMan2Board(HackMan2Board board) {
        super(board.getWidth(), board.getHeight(), board.getLayout());
        this.enemySpawnPoints = board.enemySpawnPoints;
        this.gates = board.gates;
        this.spawnedEnemies = board.spawnedEnemies;
        this.spawnedBombs = board.spawnedBombs;

        this.enemies = board.enemies.stream()
                .map(Enemy::new)
                .collect(Collectors.toCollection(ArrayList::new));
        this.bombs = board.bombs.entrySet().stream()
                .collect(
                        HashMap::new,
                        (m, e) -> m.put(e.getKey(), new Bomb(e.getValue())),
                        Map::putAll
                );
        this.snippets = board.snippets.entrySet().stream()
                .collect(
                        HashMap::new,
                        (m, e) -> m.put(e.getKey(), new Snippet(e.getValue())),
                        Map::putAll
                );
    }

    public String toString(HackMan2State state) {
        String[][] outFields = new String[this.width][this.height];

        // Create a new 2d array to store the actual contents of each field
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                if (this.fields[x][y].equals(BLOCKED_FIELD)) {
                    outFields[x][y] = BLOCKED_FIELD;
                } else {
                    outFields[x][y] = EMTPY_FIELD;
                }
            }
        }

        // Add the string representations of each object on the board
        for (HackMan2PlayerState playerState : state.getPlayerStates()) {
            Point coordinate = playerState.getCoordinate();
            outFields[coordinate.x][coordinate.y] += playerState.toString() + ";";
        }

        this.enemySpawnPoints.forEach(spawnPoint -> addObjectToOutFields(outFields, spawnPoint));
        this.gates.forEach((key, gate) -> addObjectToOutFields(outFields, gate));
        this.enemies.forEach(enemy -> addObjectToOutFields(outFields, enemy));
        this.bombs.forEach((key, bomb) -> addObjectToOutFields(outFields, bomb));
        this.snippets.forEach((key, snippet) -> addObjectToOutFields(outFields, snippet));

        // Create the string from the 2d array
        StringBuilder output = new StringBuilder();
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                String value = outFields[x][y];

                if (value.length() > 1) {
                    value = value.substring(1, value.length() - 1);
                }

                output.append(value).append(",");
            }
        }
        output.setLength(output.length() - 1);

        return output.toString();
    }

    public void spawnInitialObjects(HackMan2State state) {
        for (int i = 0; i < HackMan2Engine.configuration.getInt("mapSnippetCount"); i++) {
            spawnSnippet(state);
        }
        for (int i = 0; i < HackMan2Engine.configuration.getInt("mapBombCount"); i++) {
            spawnBomb(state);
        }
    }

    public void validateMove(HackMan2PlayerState playerState, HackMan2Move move) {
        if (move.isInvalid()) return;

        MoveType moveType = move.getMoveType();
        Point oldCoordinate = playerState.getCoordinate();
        Point newCoordinate = getCoordinateAfterMove(moveType, oldCoordinate);

        if (!isCoordinateValid(newCoordinate)) {
            move.setException(new InvalidMoveException("Can't move this direction"));
        }
    }

    public Point getCoordinateAfterMove(MoveType moveType, Point coordinate) {
        Gate gate = this.gates.get(coordinate.toString());

        // Move through gate
        if (gate != null && gate.getEntry() == moveType) {
            return gate.getLinkedGate().getCoordinate();
        }

        // Normal move
        return moveType.getCoordinateAfterMove(coordinate);
    }

    public void cleanUpEnemies() {
        this.enemies = this.enemies.stream()
                .filter(Enemy::isAlive)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void moveEnemies(HackMan2State state) {
        this.enemies.forEach(enemy -> enemy.getNewCoordinate(state));
        this.enemies.forEach(Enemy::performMovement);
    }

    public void performPickups(HackMan2State state) {
        pickupSnippets(state);
        pickupBombs(state);
    }

    public void performCollisions(HackMan2State previousState, HackMan2State state) {
        swapCollideWithEnemies(previousState, state);
        detonateBombs(state);
        positionCollideWithEnemies(state);
    }

    public void spawnObjects(HackMan2State state) {
        spawnEnemies();
        startEnemySpawn(state);
        spawnSnippets(state);
        spawnBombs(state);
    }

    public void dropBomb(Point coordinate, int ticks) {
        this.bombs.put(coordinate.toString(), new Bomb(coordinate, ticks));
    }

    public ArrayList<Enemy> getEnemies() {
        return this.enemies;
    }

    public HashMap<String, Bomb> getBombs() {
        return this.bombs;
    }

    public HashMap<String, Snippet> getSnippets() {
        return this.snippets;
    }

    private void addObjectToOutFields(String[][] outFields, HackMan2Object object) {
        Point coordinate = object.getCoordinate();
        outFields[coordinate.x][coordinate.y] += object.toString() + ";";
    }

    private void pickupSnippets(HackMan2State state) {
        ArrayList<HackMan2PlayerState> playersOnSnippet = getPlayersOnItem(state, this.snippets);

        for (HackMan2PlayerState playerState : playersOnSnippet) {
            playerState.updateSnippets(1);
            state.addSnippetCollected();
            this.snippets.remove(playerState.getCoordinate().toString());
        }
    }

    private void pickupBombs(HackMan2State state) {
        HashMap<String, Bomb> collectableBombs = this.bombs.entrySet().stream()
                .filter(e -> e.getValue().getTicks() == null)
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);

        ArrayList<HackMan2PlayerState> playersOnBomb = getPlayersOnItem(state, collectableBombs);

        for (HackMan2PlayerState playerState : playersOnBomb) {
            playerState.updateBombs(1);
            this.bombs.remove(playerState.getCoordinate().toString());
        }
    }

    private void detonateBombs(HackMan2State state) {
        this.bombs.forEach((key, bomb) -> bomb.tick());
        ArrayList<String> explodingCoordinates = explodeBombs();

        blastEnemies(explodingCoordinates);
        blastPlayers(state, explodingCoordinates);
    }

    private ArrayList<HackMan2PlayerState> getPlayersOnItem(HackMan2State state, Map itemMap) {
        ArrayList<HackMan2PlayerState> playersNotOnSameCoord = getPlayersNotOnSameCoordinate(state);

        // Return list of playerStates that have same coord as some item map
        return playersNotOnSameCoord.stream()
                .filter(ps -> itemMap.containsKey(ps.getCoordinate().toString()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<HackMan2PlayerState> getPlayersNotOnSameCoordinate(HackMan2State state) {
        ArrayList<HackMan2PlayerState> playerStates = new ArrayList<>(state.getPlayerStates());

        // Shuffle to get random player on same coordinate
        Collections.shuffle(playerStates, HackMan2Engine.RANDOM);

        // Remove players on same coordinate
        return new ArrayList<>(playerStates.stream()
                .<Map<String, HackMan2PlayerState>> collect(
                        HashMap::new,
                        (m, ps) -> m.put(ps.getCoordinate().toString(), ps),
                        Map::putAll
                )
                .values());
    }

    private void swapCollideWithEnemies(HackMan2State previousState, HackMan2State state) {

        for (HackMan2PlayerState playerState : state.getPlayerStates()) {
            Point coord = playerState.getCoordinate();
            Point previousCoord = previousState.getPlayerStateById(
                    playerState.getPlayerId()).getCoordinate();

            for (Enemy enemy : this.enemies) {
                Enemy previousEnemy = previousState.getBoard().getEnemyById(enemy.getId());

                if (previousEnemy == null) continue;

                Point enemyCoord = enemy.getCoordinate();
                Point previousEnemyCoord = previousEnemy.getCoordinate();

                if (coord.equals(previousEnemyCoord) && enemyCoord.equals(previousCoord)) {
                    hitPlayerWithEnemy(playerState);
                    enemy.kill(EnemyDeath.ATTACK);
                }
            }
        }
    }

    private void positionCollideWithEnemies(HackMan2State state) {
        state.getPlayerStates().forEach(playerState ->
                state.getBoard().getEnemies().stream()
                        .filter(enemy -> enemy.getCoordinate().equals(playerState.getCoordinate()))
                        .forEach(enemy -> {
                            hitPlayerWithEnemy(playerState);
                            enemy.kill(EnemyDeath.ATTACK);
                        }));
    }

    protected ArrayList<String> explodeBombs() {
        ArrayList<String> explodingCoordinates = new ArrayList<>();
        ArrayList<Bomb> explodedBombs = new ArrayList<>();

        this.bombs.entrySet().stream()
                .filter(e -> e.getValue().getTicks() != null && e.getValue().getTicks() == 0)
                .forEach(e -> explodeBomb(e.getValue(), explodingCoordinates, explodedBombs));

        // Remove all exploded bombs at the end
        for (Bomb exploded : explodedBombs) {
            this.bombs.remove(exploded.getCoordinate().toString());
        }

        return explodingCoordinates;
    }

    private void explodeBomb(Bomb bomb, ArrayList<String> explodingCoordinates, ArrayList<Bomb> explodingBombs) {
        Point coordinate = bomb.getCoordinate();

        explodingBombs.add(bomb);
        addExplodingCoordinate(coordinate, explodingCoordinates);

        explodeInDirection(coordinate, new Point(0, -1), explodingCoordinates, explodingBombs);
        explodeInDirection(coordinate, new Point(1, 0), explodingCoordinates, explodingBombs);
        explodeInDirection(coordinate, new Point(0, 1), explodingCoordinates, explodingBombs);
        explodeInDirection(coordinate, new Point(-1, 0), explodingCoordinates, explodingBombs);
    }

    private void explodeInDirection(Point coordinate, Point direction,
                                    ArrayList<String> explodingCoordinates, ArrayList<Bomb> explodingBombs) {
        Point newCoordinate = new Point(coordinate.x + direction.x, coordinate.y + direction.y);

        if (!isCoordinateValid(newCoordinate)) return;

        addExplodingCoordinate(newCoordinate, explodingCoordinates);
        explodeInDirection(newCoordinate, direction, explodingCoordinates, explodingBombs); // recursive call

        Bomb nextBomb = this.bombs.get(newCoordinate.toString());

        if (nextBomb == null || explodingBombs.contains(nextBomb) || nextBomb.getTicks() == null) {
            return;
        }

        // explode bombs that are hit in the blast
        explodeBomb(nextBomb, explodingCoordinates, explodingBombs);
    }

    private void blastEnemies(ArrayList<String> explodingCoordinates) {
        this.enemies.stream()
                .filter(enemy -> explodingCoordinates.contains(enemy.getCoordinate().toString()))
                .forEach(enemy -> enemy.kill(EnemyDeath.BOMBED));
    }

    private void blastPlayers(HackMan2State state, ArrayList<String> explodingCoordinates) {
        state.getPlayerStates().stream()
                .filter(ps -> explodingCoordinates.contains(ps.getCoordinate().toString()))
                .forEach(ps -> hitPlayerWithBomb(state, ps));
    }

    private void hitPlayerWithEnemy(HackMan2PlayerState playerState) {
        int snippetLoss = HackMan2Engine.configuration.getInt("enemySnippetLoss");
        playerState.updateSnippets(-snippetLoss);
    }

    private void hitPlayerWithBomb(HackMan2State state, HackMan2PlayerState playerState) {
        int snippetLoss = HackMan2Engine.configuration.getInt("bombSnippetLoss");
        int playerSnippets = playerState.getSnippets();
        int spawnCount = playerSnippets < snippetLoss ? playerSnippets : snippetLoss;

        playerState.updateSnippets(-snippetLoss);

        for (int n = 0; n < spawnCount; n++) {
            spawnSnippet(state);
        }
    }

    private void addExplodingCoordinate(Point coordinate, ArrayList<String> explodingCoordinates) {
        String key = coordinate.toString();

        if (explodingCoordinates.contains(key)) return;

        explodingCoordinates.add(key);
    }

    private Point getRandomSpawnPoint(HackMan2State state) {
        ArrayList<Point> spawnPoints = getItemSpawnPoints(state);
        int randomIndex = HackMan2Engine.RANDOM.nextInt(spawnPoints.size());

        return spawnPoints.get(randomIndex);
    }

    private ArrayList<Point> getItemSpawnPoints(HackMan2State state) {
        ArrayList<Point> spawnPoints = new ArrayList<>();

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Point coordinate = new Point(x, y);

                if (!isCoordinateValid(coordinate)) continue;

                // Items can't spawn closer than 5 to each player
                boolean distant = state.getPlayerStates().stream()
                        .allMatch(p -> p.getCoordinate().distance(coordinate) > 5);
                boolean onOtherItem = this.snippets.containsKey(coordinate.toString()) ||
                        this.bombs.containsKey(coordinate.toString());

                if (distant && !onOtherItem) {
                    spawnPoints.add(coordinate);
                }
            }
        }

        return spawnPoints;
    }

    private Enemy getEnemyById(int id) {
        return this.enemies.stream()
                .filter(enemy -> enemy.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private void spawnEnemies() {
        for (EnemySpawnPoint spawnPoint : this.enemySpawnPoints) {
            spawnPoint.reduceSpawnTime();

            Enemy newEnemy = spawnPoint.spawnEnemy(this.spawnedEnemies);

            if (newEnemy == null) continue;

            this.enemies.add(newEnemy);
            this.spawnedEnemies++;
        }
    }

    // Based on the amount of snippets
    private void startEnemySpawn(HackMan2State state) {
        Configuration config = HackMan2Engine.configuration;
        int snippetsCollected = state.getSnippetsCollected();
        int spawningSpawnPoints = (int) this.enemySpawnPoints.stream()
                .filter(sp -> sp.spawnTime != null)
                .count();
        int totalEnemies = spawningSpawnPoints + this.spawnedEnemies;
        int enemiesToSpawn = ((snippetsCollected - config.getInt("enemySpawnDelay")) /
                config.getInt("enemySpawnRate")) - totalEnemies;

        for (int id = this.spawnedEnemies; id < this.spawnedEnemies + enemiesToSpawn; id++) {
            int spawnType = totalEnemies  % 4;
            this.enemySpawnPoints.get(spawnType).setSpawnTime(config.getInt("enemySpawnTime"));
        }
    }

    // Based on rounds
    private void spawnSnippets(HackMan2State state) {
        Configuration config = HackMan2Engine.configuration;

        if (state.getRoundNumber() % config.getInt("snippetSpawnRate") == 0) {
            for (int n = 0; n < config.getInt("snippetSpawnCount"); n++) {
                spawnSnippet(state);
            }
        }
    }

    // Based on amount of snippets
    private void spawnBombs(HackMan2State state) {
        Configuration config = HackMan2Engine.configuration;
        int snippetsCollected = state.getSnippetsCollected();
        int bombsToSpawn = ((snippetsCollected - config.getInt("bombSpawnDelay")) /
                config.getInt("bombSpawnRate")) - this.spawnedBombs;

        for (int n = 0; n < bombsToSpawn; n++) {
            spawnBomb(state);
        }
    }

    private void spawnSnippet(HackMan2State state) {
        Point coordinate = getRandomSpawnPoint(state);
        this.snippets.put(coordinate.toString(), new Snippet(coordinate));
    }

    private void spawnBomb(HackMan2State state) {
        Point coordinate = getRandomSpawnPoint(state);
        this.bombs.put(coordinate.toString(), new Bomb(coordinate, null));
        this.spawnedBombs++;
    }
}
