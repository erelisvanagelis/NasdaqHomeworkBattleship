package tama.antanas.battleship.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tama.antanas.battleship.datasource.MemoryGameDataSource
import tama.antanas.battleship.utility.Environment
import tama.antanas.battleship.utility.Ship

internal class DefaultBattleshipServiceTest{
    private val service = DefaultBattleshipService(MemoryGameDataSource())

    @Test
    fun `generateGrid() - should generate a grid within limits` () {
        //given
        val chars = 'A'..'J'
        val integers = 1..10

        //when
        val grid = service.generateGrid(chars, integers)

        //then
        assertThat(grid.size == integers.count() * chars.count())
        assertThat(grid.count { it.coordinates.letter == 'A' }).isEqualTo(10)
        assertThat(grid.count { it.coordinates.letter == 'J' }).isEqualTo(10)
        assertThat(grid.count { it.coordinates.number == 1 }).isEqualTo(10)
        assertThat(grid.count { it.coordinates.number == 10 }).isEqualTo(10)
    }

    @Test
    fun `populateGrid() - should place all the ships` () {
        //given
        val chars = 'A'..'J'
        val integers = 1..10


        //when
        val grid = service.generateGrid(chars, integers)
        val populatedGrid = service.populateGrid(grid.toList(), Ship.values().toList())


        //then
        assertThat(Ship.CARRIER.length).isEqualTo(populatedGrid.count { it.tag == Ship.CARRIER.name })
        assertThat(Ship.BATTLESHIP.length).isEqualTo(populatedGrid.count { it.tag == Ship.BATTLESHIP.name })
        assertThat(Ship.DESTROYER.length).isEqualTo(populatedGrid.count { it.tag == Ship.DESTROYER.name })
        assertThat(Ship.SUBMARINE.length).isEqualTo(populatedGrid.count { it.tag == Ship.SUBMARINE.name })
        assertThat(Ship.CRUISER.length).isEqualTo(populatedGrid.count { it.tag == Ship.CRUISER.name })
    }

    @Test
    fun `findSequences() - should find find a valid sequence` () {
        //given
        val sequenceCount = 12

        //when
        val grid = service.generateGrid('A'..'C', 1..3)
        val distinctCoordinates = service.findDistinctCoordinates(grid.toList())
        val validSequences = service.findSequences(grid.toList(), Environment.WATER.name, 2, distinctCoordinates)
        //then
        assertThat(validSequences.size).isEqualTo(sequenceCount)
    }

    @Test
    fun `findDistinctCoordinates() - should return a list of all letters and numbers used in given grid` () {
        //given
        val distinctCount = 6

        //when
        val grid = service.generateGrid('A'..'C', 1..3)
        val distinctCoordinates = service.findDistinctCoordinates(grid.toList())

        //then
        assertThat(distinctCoordinates.size).isEqualTo(distinctCount)
        assertThat(distinctCoordinates.count{it == "A"}).isEqualTo(1)
        assertThat(distinctCoordinates.count{it == "3"}).isEqualTo(1)
    }

    @Test
    fun `createGame() - should create a game and add it to the dataSource` () {
        //given
        val id = "new1"

        //when
        val game = service.createGame(id)
        val retrieved = service.getGame(id)

        //then
        assertThat(game).isEqualTo(retrieved)
    }

    @Test
    fun `createGame() - should throw an IllegalArgumentException because id already in use` () {
        //given
        val id = "test1"

        //when
        val exception = assertThrows<IllegalArgumentException> { service.createGame(id) }

        //then
        assertThat(exception::class).isEqualTo(IllegalArgumentException::class)
    }

    @Test
    fun `getGame() - should return a game` () {
        //given
        val id = "test1"

        //when
        val game = service.getGame(id)

        //then
        assertThat(game).isNotNull
    }

    @Test
    fun `getGame() - should throw an NuSuchElementException because id is not in use` () {
        //given
        val id = "new2"

        //when
        val exception = assertThrows<NoSuchElementException> { service.getGame(id) }

        //then
        assertThat(exception::class).isEqualTo(NoSuchElementException::class)
    }
}