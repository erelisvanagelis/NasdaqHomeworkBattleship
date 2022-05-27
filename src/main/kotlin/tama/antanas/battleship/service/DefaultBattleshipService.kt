package tama.antanas.battleship.service

import org.springframework.stereotype.Service
import tama.antanas.battleship.datasource.GameDataSource
import tama.antanas.battleship.model.Coordinates
import tama.antanas.battleship.model.Game
import tama.antanas.battleship.model.GameState
import tama.antanas.battleship.model.Tile
import tama.antanas.battleship.utility.Environment
import tama.antanas.battleship.utility.Ship
import kotlin.random.Random

@Service
class DefaultBattleshipService(private val gameDataSource: GameDataSource) : BattleshipService {
    override fun generateGrid(letters: CharRange, numbers: IntRange): List<Tile> {
        val tiles = mutableListOf<Tile>()
        for (letter in letters) {
            for (number in numbers) {
                tiles.add(Tile("${Environment.WATER.name}", Coordinates(letter, number), false))
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

    fun findDistinctCoordinates(grid: List<Tile>): List<String> {
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


    override fun attackTile(gameId: String, player: String, coordinates: Coordinates): String {
        TODO("Not yet implemented")
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