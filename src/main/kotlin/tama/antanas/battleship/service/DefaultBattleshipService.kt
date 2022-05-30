package tama.antanas.battleship.service

import org.springframework.stereotype.Service
import tama.antanas.battleship.datasource.GameDataSource
import tama.antanas.battleship.model.Coordinates
import tama.antanas.battleship.model.GameState
import tama.antanas.battleship.model.Tile
import tama.antanas.battleship.utility.Action
import tama.antanas.battleship.utility.Environment
import tama.antanas.battleship.utility.Player
import tama.antanas.battleship.utility.Ship
import kotlin.random.Random

@Service
class DefaultBattleshipService(private val gameDataSource: GameDataSource) : BattleshipService {
    override fun generateGrid(letters: CharRange, numbers: IntRange): List<Tile> {
        val tiles = mutableListOf<Tile>()
        for (letter in letters) {
            for (number in numbers) {
                tiles.add(Tile(Environment.WATER.name, Coordinates(letter, number), false))
            }
        }
        return tiles
    }

    override fun populateGrid(grid: List<Tile>, ships: List<Ship>): List<Tile> {
        val copiedGrid = grid.map { it.copy() }
        val possibleCoordinates = findDistinctCoordinates(copiedGrid)
        for (ship in ships) {
            val possiblePlacements = findSequences(copiedGrid, Environment.WATER.name, ship.length, possibleCoordinates)
            val index = Random.nextInt(0, possiblePlacements.size)

            for (tile in possiblePlacements[index]) {
                tile.tag = ship.name
            }
        }

        return copiedGrid
    }

    override fun findDistinctCoordinates(grid: List<Tile>): List<String> {
        val possibleCoordinates: MutableList<String> = mutableListOf()
        possibleCoordinates.addAll(
            grid.distinctBy { it.coordinates.letter }
                .map { it.coordinates.letter.toString() }
        )
        possibleCoordinates.addAll(
            grid.distinctBy { it.coordinates.number }
                .map { it.coordinates.number.toString() }
        )

        return possibleCoordinates
    }

    override fun findSequences(grid: List<Tile>, tag: String, length: Int, range: List<String>): List<List<Tile>> {
        val possibleCoordinates = findDistinctCoordinates(grid)
        val validSequences = mutableListOf<List<Tile>>()

        for (symbol in possibleCoordinates) {
            val line = mutableListOf<Tile>()
            if (symbol.toIntOrNull() is Int) {
                line.addAll(grid.filter { it.coordinates.number == symbol.toInt() })
            } else if (symbol.length == 1) {
                line.addAll(grid.filter { it.coordinates.letter == symbol.first() })
            }

            val possibleSequence = mutableListOf<Tile>()
            var i = 0
            while (i < line.count()) {
                if (line[i].tag == tag) {
                    possibleSequence.add(line[i])
                }
                if (possibleSequence.size == length) {
                    validSequences.add(possibleSequence.toList())
                    possibleSequence.clear()
                    i = i - length + 1
                }

                i++
            }
        }

        return validSequences
    }

    override fun createPrimaryGameState(id: String): GameState {
        gameDataSource.retrieveGameState(id, 0)?.let { throw IllegalArgumentException("Id: $id already in use") }

        val grid = generateGrid('A'..'J', 1..10)

        val gameState = GameState(
            id = id,
            fpGrid = populateGrid(grid, Ship.values().toList()),
            spGrid = populateGrid(grid, Ship.values().toList()),
            attackCoordinates = null
        )

        gameDataSource.addGameState(gameState)

        return gameState
    }

    override fun addGameState(gameState: GameState): GameState {
        gameDataSource.addGameState(gameState)
        return gameState
    }

    override fun changeGameState(gameState: GameState): GameState {
        gameDataSource.retrieveGameState(gameState.id, gameState.turn)
            ?: throw NoSuchElementException("This game (Id: ${gameState.id}, Turn: ${gameState.turn}) does not exist")

        gameDataSource.updateGameState(gameState)

        return gameState
    }

    override fun performAttack(gameId: String, player: Player, coordinates: Coordinates): String {
        val gameState = getCurrentGameState(gameId)
        when {
            gameState.gameOver -> throw UnsupportedOperationException("Game ($gameId) is already over")

            Player.values().firstOrNull { it == player } == null ->
                throw UnsupportedOperationException("Player ($player) does not exist. Supported names: ${Player.values()}")

            gameState.playerTurn != player -> throw UnsupportedOperationException("Currently player (${gameState.playerTurn}) turn")
        }

        lateinit var opponentGrid: List<Tile>
        lateinit var fpGrid: List<Tile>
        lateinit var spGrid: List<Tile>

        when (player) {
            Player.ONE -> {
                fpGrid = gameState.fpGrid.map { it.copy() }
                spGrid = gameState.spGrid.map { it.copy() }
                opponentGrid = spGrid
            }
            Player.TWO -> {
                fpGrid = gameState.fpGrid.map { it.copy() }
                spGrid = gameState.spGrid.map { it.copy() }
                opponentGrid = fpGrid
            }
        }

        val newState = GameState(
            id = gameId,
            turn = gameState.turn + 1,
            playerTurn = if (player == Player.ONE) Player.TWO else Player.ONE,
            action = attackTile(opponentGrid, coordinates),
            fpGrid = fpGrid,
            spGrid = spGrid,
            attackCoordinates = coordinates,
            gameOver = areAllShipsSunk(opponentGrid, Ship.values().toList())
        )
        gameDataSource.addGameState(newState)

        return newState.action.lowName
    }

    override fun attackTile(grid: List<Tile>, coordinates: Coordinates): Action {
        val target = grid.firstOrNull { it.coordinates == coordinates }

        when {
            target == null -> throw NoSuchElementException(
                "Invalid tile coordinates $coordinates, supported coordinates (${findDistinctCoordinates(grid)})"
            )

            target.shot -> throw UnsupportedOperationException(
                "Tile ($coordinates) was already shot"
            )
        }

        target?.shot = true
        lateinit var result: Action
        when (target?.tag) {
            in Environment.values().map { it.name } -> result = Action.MISS

            in Ship.values().map { it.name } -> result =
                if (grid.any { it.tag == target?.tag && !it.shot }) Action.HIT else Action.SANK
        }

        return result
    }

    override fun areAllShipsSunk(grid: List<Tile>, ships: List<Ship>): Boolean {
        for (ship in ships) {
            if (grid.any { it.tag == ship.name && !it.shot }) {
                return false
            }
        }
        return true
    }

    override fun getCurrentGameState(gameId: String): GameState {
        return getGameStates(gameId).maxByOrNull { it.turn }
            ?: throw NoSuchElementException("No latest state")
    }

    override fun getGameState(gameId: String, turn: Int): GameState = gameDataSource.retrieveGameState(gameId, turn)
        ?: throw NoSuchElementException("No state with with requested turn ($turn)")

    override fun getGameStates(gameId: String): Collection<GameState> {
        val states = gameDataSource.retrieveGameStates(gameId)
        if (states.isEmpty()) {
            throw NoSuchElementException("No states with requested Id: $gameId")
        }

        return states
    }

    override fun resetToPrimaryState(gameId: String): Unit {
        val primaryState = gameDataSource.retrieveGameStates(gameId).first { it.turn == 0 }
        gameDataSource.deleteGameStates(gameId)
        gameDataSource.addGameState(primaryState.copy(action = Action.RESET))
    }
}