package tama.antanas.battleship.service

import tama.antanas.battleship.model.Coordinates
import tama.antanas.battleship.model.Game
import tama.antanas.battleship.model.GameState
import tama.antanas.battleship.model.Tile

interface BattleshipService {
    fun generateGrid (letters: CharRange, numbers: IntRange) : List<Tile>
    fun createGame(id: String) : Game
    fun attackTile (gameId: String, player: String, coordinates: Coordinates) : String
    fun getCurrentGameState(gameId: String) : GameState
    fun getGameState(gameId: String, turn: Int) : GameState
    fun resetGame(gameId: String): GameState
}