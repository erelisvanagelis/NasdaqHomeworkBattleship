package tama.antanas.battleship.service

import org.springframework.stereotype.Service
import tama.antanas.battleship.datasource.GameDataSource
import tama.antanas.battleship.model.Coordinates
import tama.antanas.battleship.model.Game
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

    override fun createGame(id: String): Game {
        gameDataSource.retrieveGame(id)?.let { throw IllegalArgumentException("Id: $id already in use") }

        val grid = generateGrid('A'..'J', 1..10)
        val game = Game(
            id,
            mutableListOf(
                GameState(
                    fpGrid = populateGrid(grid, Ship.values().toList()),
                    spGrid = populateGrid(grid, Ship.values().toList())
                )
            )
        )
        gameDataSource.addGame(game)

        return game
    }

    override fun getGame(id: String): Game =
        gameDataSource.retrieveGame(id) ?: throw NoSuchElementException("This game (Id: $id) does not exist")

    override fun changeGame(game: Game): Game {
        gameDataSource.retrieveGame(game.id)
            ?: throw NoSuchElementException("This game (Id: ${game.id}) does not exist")
        gameDataSource.updateGame(game)
        return game
    }

    override fun performAttack(gameId: String, player: Player, coordinates: Coordinates): String {
        val game = getGame(gameId)
        val currentState = game.states.last()

        when {
            !game.active -> throw UnsupportedOperationException("Game ($gameId) is already over")

            Player.values().firstOrNull { it == player } == null ->
                throw UnsupportedOperationException("Player ($player) does not exist. Supported names: ${Player.values()}")

            currentState.playerTurn != player -> throw UnsupportedOperationException("Game ($gameId) is already over")
        }

        lateinit var nextPlayerTurn: Player
        lateinit var selectedGrid: List<Tile>
        lateinit var fpGrid: List<Tile>
        lateinit var spGrid: List<Tile>

        if (player == Player.ONE) {
            selectedGrid = currentState.spGrid.map { it }
            fpGrid = currentState.fpGrid
            spGrid = selectedGrid
            nextPlayerTurn = Player.TWO

        } else if (player == Player.TWO) {
            selectedGrid = currentState.fpGrid.map { it }
            fpGrid = currentState.spGrid
            spGrid = selectedGrid
            nextPlayerTurn = Player.ONE
        }

        val actionResult = attackTile(selectedGrid, coordinates)
        var actionMessage = "${player.name} ${actionResult.description}"
        val allShipsSunk = areAllShipsSunk(selectedGrid, Ship.values().toList())
        if (allShipsSunk) actionMessage += ", GAME OVER PLAYER ${player.name} WON"

        val newState = GameState(
            turn = currentState.turn + 1,
            playerTurn = nextPlayerTurn,
            action = actionMessage,
            fpGrid = fpGrid,
            spGrid = spGrid
        )

        game.states.add(newState)
        gameDataSource.updateGame(
            Game(
                id = gameId,
                active = !allShipsSunk,
                states = game.states
            )
        )

        return actionResult.lowName
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
        TODO("Not yet implemented")
    }

    override fun getGameState(gameId: String, turn: Int): GameState {
        TODO("Not yet implemented")
    }

    override fun resetGame(gameId: String): GameState {
        TODO("Not yet implemented")
    }
}