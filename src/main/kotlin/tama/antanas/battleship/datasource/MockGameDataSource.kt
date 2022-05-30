package tama.antanas.battleship.datasource

import org.springframework.stereotype.Repository
import tama.antanas.battleship.model.GameState
import tama.antanas.battleship.model.Tile
import tama.antanas.battleship.utility.Action
import tama.antanas.battleship.utility.Player

@Repository
class MockGameDataSource : GameDataSource {
    private val gameStates = mutableListOf<GameState>(
        GameState("test1", 0, Player.ONE, Action.GENERATED, false, listOf<Tile>(), listOf<Tile>(), null),
        GameState("test2", 0, Player.ONE, Action.GENERATED, false, listOf<Tile>(), listOf<Tile>(), null),
        GameState("test2", 0, Player.ONE, Action.GENERATED, false, listOf<Tile>(), listOf<Tile>(), null),
    )

    override fun addGameState(gameState: GameState): GameState {
        gameStates.add(gameState)
        return gameState
    }

    override fun updateGameState(gameState: GameState): GameState {
        val current = gameStates.first { it.id == gameState.id && it.turn == gameState.turn }
        gameStates.remove(current)
        gameStates.add(gameState)
        return gameState
    }

    override fun retrieveGameStates(gameId: String): Collection<GameState> = gameStates.filter { it.id == gameId }

    override fun retrieveGameState(gameId: String, turn: Int): GameState? =
        gameStates.firstOrNull { it.id == gameId && it.turn == turn }

    override fun retrieveAllGameStates(): Collection<GameState> = gameStates
    override fun deleteGameStates(gameId: String) {
        gameStates.removeAll { it.id == gameId }
    }
}