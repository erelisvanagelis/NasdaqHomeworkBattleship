package tama.antanas.battleship.service

import tama.antanas.battleship.model.Coordinates
import tama.antanas.battleship.model.Game
import tama.antanas.battleship.model.GameState
import tama.antanas.battleship.model.Tile
import tama.antanas.battleship.utility.Environment
import tama.antanas.battleship.utility.Ship

interface BattleshipService {
    fun generateGrid (letters: CharRange, numbers: IntRange) : List<Tile>
    fun populateGrid (grid: List<Tile>, ships: List<Ship>) : List<Tile>
    fun findSequences(grid: List<Tile>, tag: String, length: Int, range: List<String>): List<List<Tile>>
    fun createGame(id: String) : Game
    fun getGame(id: String) : Game
    fun attackTile (gameId: String, player: String, coordinates: Coordinates) : String
    fun getCurrentGameState(gameId: String) : GameState
    fun getGameState(gameId: String, turn: Int) : GameState
    fun resetGame(gameId: String): GameState
}