package tama.antanas.battleship.service

import tama.antanas.battleship.model.Coordinates
import tama.antanas.battleship.model.Game
import tama.antanas.battleship.model.GameState
import tama.antanas.battleship.model.Tile

class BattleshipServiceImpl : BattleshipService {
    override fun generateGrid(letters: CharRange, numbers: IntRange): List<Tile> {
        TODO("Not yet implemented")
    }

    override fun createGame(id: String): Game {
        TODO("Not yet implemented")
    }

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