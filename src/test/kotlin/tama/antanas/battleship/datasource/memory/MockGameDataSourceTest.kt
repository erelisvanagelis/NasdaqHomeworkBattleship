package tama.antanas.battleship.datasource.memory

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tama.antanas.battleship.datasource.MockGameDataSource
import tama.antanas.battleship.model.Coordinates
import tama.antanas.battleship.model.GameState
import tama.antanas.battleship.model.Tile
import tama.antanas.battleship.utility.Environment

internal class MockGameDataSourceTest {
    private val dataSource: MockGameDataSource = MockGameDataSource()

    @Test
    fun `retrieveAllGameStates() should retrieve all games`() {
        //when
        val games = dataSource.retrieveAllGameStates()

        //then
        assertThat(games.size).isEqualTo(3)
    }

    @Test
    fun `retrieveGameState(id) - should retrieve a game by id`() {
        //given
        val id = "test1"
        val turn = 0

        //when
        val gameState = dataSource.retrieveGameState(id, turn)

        //then
        assertThat(gameState).isNotNull
    }

    @Test
    fun `addGameState(game) - should add a game to the dataSource`() {
        //given
        val gameState = GameState(id = "passed1", spGrid = listOf(), fpGrid = listOf(), attackCoordinates = null)

        //when
        val oldSize = dataSource.retrieveAllGameStates().size
        dataSource.addGameState(gameState)
        val newSize = dataSource.retrieveAllGameStates().size

        //then
        assertThat(oldSize + 1).isEqualTo(newSize)
        assertThat(dataSource.retrieveAllGameStates().contains(gameState))
    }

    @Test
    fun `updateGameState(game) - should update an existing item`() {
        //given
        val id = "test1"
        val turn = 0
        val gameState =
            GameState(
                id = id,
                fpGrid = listOf(Tile(Environment.WATER.name, Coordinates('A', 2), false)),
                spGrid = listOf(Tile(Environment.WATER.name, Coordinates('A', 2), false)),
                attackCoordinates = null
            )

        //when
        val oldGameState = dataSource.retrieveGameState(id, turn)
        dataSource.updateGameState(gameState)
        val newGameState = dataSource.retrieveGameState(id, turn)

        //then
        assertThat(oldGameState).isNotEqualTo(newGameState)
    }

}