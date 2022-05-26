package tama.antanas.battleship.datasource.memory

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import tama.antanas.battleship.model.Coordinates
import tama.antanas.battleship.model.Game
import tama.antanas.battleship.model.GameState
import tama.antanas.battleship.model.Tile
import tama.antanas.battleship.utility.Environment

internal class MemoryGameDataSourceTest {
    private val dataSource: MemoryGameDataSource = MemoryGameDataSource()

    @Test
    fun `retrieveAllGames() should retrieve all games` () {
        //when
        val games = dataSource.retrieveAllGames()

        //then
        assertThat(games.size).isEqualTo(3)
    }

@Test
fun `retrieveGame(id) - should retrieve a game by id` () {
    //given
    val id = "test1"

    //when
    val game = dataSource.retrieveGame(id)

    //then
    assertThat(game).isNotNull
}

    @Test
    fun `addGame(game) - should add a game to the dataSource`() {
        //given
        val game = Game("testId", mutableListOf())

        //when
        val oldSize = dataSource.retrieveAllGames().size
        dataSource.addGame(game)
        val newSize = dataSource.retrieveAllGames().size

        //then
        assertThat(oldSize + 1).isEqualTo(newSize)
        assertThat(dataSource.retrieveAllGames().contains(game))
    }

    @Test
    fun `updateGame(game) - should update an existing item`() {
        //given
        val id = "test1"
        val game = Game(
            id, mutableListOf(
                GameState(
                    fpGrid = listOf(Tile(Environment.WATER.name, Coordinates("A", 2), false)),
                    spGrid = listOf(Tile(Environment.WATER.name, Coordinates("A", 2), false))
                )
            )
        )

        //when
        val oldGame = dataSource.retrieveGame(id)
        dataSource.updateGame(game)
        val newGame = dataSource.retrieveGame(id)

        //then
        assertThat(oldGame).isNotEqualTo(newGame)
    }

}