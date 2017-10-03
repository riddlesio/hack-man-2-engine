package io.riddles.hackman2.engine

import io.riddles.hackman2.game.player.HackMan2Player
import io.riddles.hackman2.game.state.HackMan2PlayerState
import io.riddles.hackman2.game.state.HackMan2State
import io.riddles.javainterface.game.player.PlayerProvider
import io.riddles.javainterface.io.FileIOHandler
import spock.lang.Specification

/**
 * io.riddles.hackman2.engine.HackMan2EngineSpec - Created on 3-10-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
class HackMan2EngineSpec extends Specification {

    class TestEngine extends HackMan2Engine {

        TestEngine(String wrapperInput, String[] botFiles) {
            super(new PlayerProvider<>(), new FileIOHandler(wrapperInput))

            for (int i = 0; i < botFiles.length; i++) {
                HackMan2Player player = new HackMan2Player(i)
                player.setIoHandler(new FileIOHandler(botFiles[i]))
                this.playerProvider.add(player)
            }

            this.processor = createProcessor()
        }
    }

    def "test gates"() {

        setup:
        String[] botInputs = new String[2]

        String wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot1_test_gates_input.txt"
        botInputs[1] = "./test/resources/bot2_test_gates_input.txt"

        TestEngine engine = new TestEngine(wrapperInput, botInputs)
        TestEngine.configuration = engine.getDefaultConfiguration()
        TestEngine.configuration.put("maxRounds", 13)

        HackMan2State initialState = engine.getInitialState()

        when:
        HackMan2State finalState = engine.run(initialState)
        HackMan2PlayerState playerState1 = finalState.getPlayerStateById(0)
        HackMan2PlayerState playerState2 = finalState.getPlayerStateById(1)

        then:
        playerState1.getCoordinate().x == 17
        playerState1.getCoordinate().y == 6
        playerState2.getCoordinate().x == 1
        playerState2.getCoordinate().y == 7
    }
}
