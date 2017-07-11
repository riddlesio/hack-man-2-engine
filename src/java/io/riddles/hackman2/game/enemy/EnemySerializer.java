package io.riddles.hackman2.game.enemy;

import org.json.JSONObject;

import io.riddles.hackman2.game.HackMan2ObjectSerializer;
import io.riddles.javainterface.serialize.Serializer;

/**
 * io.riddles.hackman2.game.enemy.EnemySerializer - Created on 11-7-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class EnemySerializer implements Serializer<Enemy> {

    @Override
    public String traverseToString(Enemy enemy) {
        return visitEnemy(enemy).toString();
    }

    @Override
    public JSONObject traverseToJson(Enemy enemy) {
        return visitEnemy(enemy);
    }

    private JSONObject visitEnemy(Enemy enemy) {
        HackMan2ObjectSerializer objectSerializer = new HackMan2ObjectSerializer();

        JSONObject enemyObj = objectSerializer.traverseToJson(enemy);

        enemyObj.put("id", enemy.getId());
        enemyObj.put("type", enemy.getType());
        enemyObj.put("move", enemy.getDirection());

        if (!enemy.isAlive()) {
            enemyObj.put("death", enemy.getDeathType());
        }

        return enemyObj;
    }
}
