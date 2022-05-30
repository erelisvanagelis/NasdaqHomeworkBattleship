package tama.antanas.battleship.datasource

import tama.antanas.battleship.model.GameState

interface GameDataSource {
    fun addGameState (gameState: GameState) : GameState
    fun updateGameState (gameState: GameState) : GameState
    fun retrieveGameStates (gameId: String) : Collection<GameState>
    fun retrieveGameState (gameId: String, turn: Int) : GameState?
    fun retrieveAllGameStates() : Collection<GameState>
    fun deleteGameStates(gameId: String) : Unit
}