package tama.antanas.battleship.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import tama.antanas.battleship.datasource.MockGameDataSource
import tama.antanas.battleship.model.Coordinates
import tama.antanas.battleship.model.GameState
import tama.antanas.battleship.utility.Action
import tama.antanas.battleship.utility.Environment
import tama.antanas.battleship.utility.Player
import tama.antanas.battleship.utility.Ship

internal class DefaultBattleshipServiceTest{
    private val service = DefaultBattleshipService(MockGameDataSource())

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
    fun `createGameState() - should create a game and add it to the dataSource` () {
        //given
        val id = "new1"

        //when
        val gameState = service.createPrimaryGameState(id)
        val retrieved = service.getGameState(id, 0)

        //then
        assertThat(gameState).isEqualTo(retrieved)
    }

    @Test
    fun `createGameState() - should throw an IllegalArgumentException because id already in use` () {
        //given
        val id = "test1"

        //when
        val exception = assertThrows<IllegalArgumentException> { service.createPrimaryGameState(id) }

        //then
        assertThat(exception::class).isEqualTo(IllegalArgumentException::class)
    }

    @Test
    fun `getGameState() - should return a game` () {
        //given
        val id = "test1"
        val turn = 0

        //when
        val game = service.getGameState(id, turn)

        //then
        assertThat(game).isNotNull
    }

    @Test
    fun `getGame() - should throw an NuSuchElementException because id is not in use` () {
        //given
        val id = "new2"
        val turn = 0

        //when
        val exception = assertThrows<NoSuchElementException> { service.getGameState(id, turn) }

        //then
        assertThat(exception::class).isEqualTo(NoSuchElementException::class)
    }
    
    @Nested
    @DisplayName("performAttack()")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PerformAttack {
        @Test
        fun `should throw NoSuchElementException because Id does not exist` () {
            //given
            val id = "new3"
            val player = Player.ONE
            val coordinates = Coordinates('A', 1)

            //when
            val exception = assertThrows<NoSuchElementException> { service.performAttack(id, player, coordinates) }

            //then
            assertThat(exception::class).isEqualTo(NoSuchElementException::class)
        }

        @Test
        fun `should throw UnsupportedOperationException because game is over` () {
            //given
            val id = "new4"
            val player = Player.ONE
            val coordinates = Coordinates('A', 1)
            val gameState = GameState(
                id = id,
                gameOver = true,
                fpGrid = listOf(),
                spGrid = listOf(),
                attackCoordinates = coordinates
            )

            //when
            service.addGameState(gameState)
            val exception = assertThrows<UnsupportedOperationException> { service.performAttack(id, player, coordinates)  }

            //then
            assertThat(exception::class).isEqualTo(UnsupportedOperationException::class)
        }

        @Test
        fun `should throw UnsupportedOperationException because not the requested players turn` () {
            //given
            val id = "new5"
            val player = Player.TWO
            val coordinates = Coordinates('A', 1)

            val gameState = GameState(
                id = id,
                gameOver = true,
                fpGrid = listOf(),
                spGrid = listOf(),
                attackCoordinates = coordinates
            )

            //when
            service.addGameState(gameState)
            val exception = assertThrows<UnsupportedOperationException> { service.performAttack(id, player, coordinates)  }

            //then
            assertThat(exception::class).isEqualTo(UnsupportedOperationException::class)
        }

        @Test
        fun `should create additional GameState and add it to states` () {
            //given
            val id = "new6"
            val player = Player.ONE
            val coordinates = Coordinates('A', 1)

            //when
            service.createPrimaryGameState(id)
            val currentTurnCount = service.getGameStates(id).size
            service.performAttack(id, player, coordinates)
            val newTurnCount = service.getGameStates(id).size

            //then
            assertThat(currentTurnCount + 1).isEqualTo(newTurnCount)
        }

        @Test
        fun `should create additional GameState and add its turn should be bigger` () {
            //given
            val id = "new8"
            val player = Player.ONE
            val coordinates = Coordinates('A', 1)
            val gameState = GameState(
                id = id,
                gameOver = true,
                fpGrid = listOf(),
                spGrid = listOf(),
                attackCoordinates = coordinates
            )

            //when
            service.createPrimaryGameState(id)
            val currentState = service.addGameState(gameState)
            service.performAttack(id, player, coordinates)
            val newState = service.getCurrentGameState(gameState.id)

            //then
            assertThat(currentState.turn).isLessThan(newState.turn)
        }

    }

    @Nested
    @DisplayName("attackTile()")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class AttackTile {
        @Test
        fun `should throw NoSuchElementException because Coordinates are wrong` () {
            //given
            val coordinates = Coordinates('z', 999)

            //when
            val grid = service.generateGrid('A'..'C', 1..3)

            val exception = assertThrows<NoSuchElementException> { service.attackTile(grid, coordinates) }

            //then
            assertThat(exception::class).isEqualTo(NoSuchElementException::class)
        }

        @Test
        fun `should throw UnsupportedOperationException because tile is already shot` () {
            //given
            val coordinates = Coordinates('A', 1)

            //when
            val grid = service.generateGrid('A'..'C', 1..3)
            grid.first().shot = true
            val exception = assertThrows<UnsupportedOperationException> { service.attackTile(grid, coordinates)  }

            //then
            assertThat(exception::class).isEqualTo(UnsupportedOperationException::class)
        }

        @Test
        fun `should change tile to shot` () {
            //given
            val coordinates = Coordinates('A', 1)

            //when
            val grid = service.generateGrid('A'..'C', 1..3)
            service.attackTile(grid, coordinates)

            //then
            assertThat(grid.first{it.coordinates == coordinates}.shot).isEqualTo(true)
        }

        @Test
        fun `should change tile to shot and return MISS` () {
            //given
            val coordinates = Coordinates('A', 1)
            val testTag = Environment.WATER.name

            //when
            val grid = service.generateGrid('A'..'C', 1..3)
            grid.first().tag = testTag
            val result = service.attackTile(grid, coordinates)

            //then
            assertThat(result).isEqualTo(Action.MISS)
        }

        @Test
        fun `should change tile to shot and return HIT` () {
            //given
            val coordinates = Coordinates('A', 1)
            val testTag = Ship.BATTLESHIP.name

            //when
            val grid = service.generateGrid('A'..'C', 1..3)
            grid.first().tag = testTag
            grid.last().tag = testTag
            val result = service.attackTile(grid, coordinates)

            //then
            assertThat(result).isEqualTo(Action.HIT)
        }

        @Test
        fun `should change tile to shot and return SANK` () {
            //given
            val coordinates = Coordinates('A', 1)
            val testTag = Ship.BATTLESHIP.name

            //when
            val grid = service.generateGrid('A'..'C', 1..3)
            grid.first().tag = testTag
            val result = service.attackTile(grid, coordinates)

            //then
            assertThat(result).isEqualTo(Action.SANK)
        }
    }


    @Nested
    @DisplayName("areAllShipsSunk()")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class AreAllShipsSunk {
        @Test
        fun `should return true because no ship tiles` () {
            //when
            val grid = service.generateGrid('A'..'C', 1..3)
            val result = service.areAllShipsSunk(grid, Ship.values().toList())

            //then
            assertThat(result).isEqualTo(true)
        }

        @Test
        fun `should return false because not all ship tiles shot` () {
            val testTag = Ship.BATTLESHIP.name
            //when
            val grid = service.generateGrid('A'..'C', 1..3)
            grid.first().tag = testTag
            grid.last().tag = testTag
            grid.first().shot = true
            val result = service.areAllShipsSunk(grid, Ship.values().toList())

            //then
            assertThat(result).isEqualTo(false)
        }

        @Test
        fun `should return true because all ships shot` () {
            //given
            val testTag = Ship.BATTLESHIP.name

            //when
            val grid = service.generateGrid('A'..'C', 1..3)
            grid.first().tag = testTag
            grid.last().tag = testTag
            grid.first().shot = true
            grid.last().shot = true
            val result = service.areAllShipsSunk(grid, Ship.values().toList())

            //then
            assertThat(result).isEqualTo(true)
        }
    }

    @Test
    fun `getCurrentGameState() - should return latestGameState` () {
        //given
        val gameId = "new9"
        val player = Player.ONE
        val coordinates = Coordinates('A', 1)

        //when
        service.createPrimaryGameState(gameId)
        val previousLatest = service.getCurrentGameState(gameId)
        service.performAttack(gameId, player, coordinates)
        val currentLatest = service.getCurrentGameState(gameId)

        //then
        assertThat(service.getCurrentGameState(gameId)).isNotEqualTo(previousLatest)
        assertThat(service.getCurrentGameState(gameId)).isEqualTo(currentLatest)
    }

    @Test
    fun `getGameState() - should get specified state of specified game` () {
        //given
        val gameId = "new10"
        val player = Player.ONE
        val coordinates = Coordinates('A', 1)
        val turn = 1

        //when
        val game = service.createPrimaryGameState(gameId)
        println(game.turn)
        service.performAttack(gameId, player, coordinates)
        val state = service.getGameState(gameId, turn)
        println(state.turn)

        //then
        assertThat(state.turn).isEqualTo(turn)
    }

    @Test
    fun `getGameState() - should throw NoSuchElementException because turn is not used` () {
        //given
        val gameId = "new11"
        val turn = 1

        //when
        service.createPrimaryGameState(gameId)
        val exception = assertThrows<NoSuchElementException> { service.getGameState(gameId, turn)  }

        //then
        assertThat(exception::class).isEqualTo(NoSuchElementException::class)
    }

    @Test
    fun `resetGame() - should set game drop all states except first` () {
        //given
        val id = "new11"
        val coordinates = Coordinates('A', 1)
        val gameState = GameState(
            id = id,
            gameOver = true,
            fpGrid = listOf(),
            spGrid = listOf(),
            attackCoordinates = coordinates
        )

        val gameState2 = GameState(
            id = id,
            turn = 2,
            gameOver = true,
            fpGrid = listOf(),
            spGrid = listOf(),
            attackCoordinates = coordinates
        )

        //when
        service.addGameState(gameState)
        service.addGameState(gameState2)
        val beforeReset = service.getGameStates(id)
        service.resetToPrimaryState(id)
        val afterReset = service.getGameStates(id)

        //then
        assertThat(afterReset.size).isLessThan(beforeReset.size)
        assertThat(afterReset.size).isEqualTo(1)
        assertThat(service.getCurrentGameState(id)).isEqualTo(gameState)
    }
}