package io.riddles.hackman2.game.enemy;

/**
 * io.riddles.hackman2.game.enemy.EnemyDeath - Created on 10-7-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public enum EnemyDeath {
    ATTACK,
    BOMBED;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
