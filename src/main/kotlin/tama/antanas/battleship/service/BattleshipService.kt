package tama.antanas.battleship.service

import tama.antanas.battleship.model.Coordinates
import tama.antanas.battleship.model.GameState
import tama.antanas.battleship.model.Tile
import tama.antanas.battleship.utility.Action
import tama.antanas.battleship.utility.Player
import tama.antanas.battleship.utility.Ship

interface BattleshipService {
    fun generateGrid(letters: CharRange, numbers: IntRange): List<Tile>
    fun populateGrid(grid: List<Tile>, ships: List<Ship>): List<Tile>
    fun findDistinctCoordinates(grid: List<Tile>): List<String>
    fun findSequences(grid: List<Tile>, tag: String, length: Int, range: List<String>): List<List<Tile>>
    fun createPrimaryGameState(id: String): GameState
    fun addGameState(gameState: GameState): GameState
    fun changeGameState(gameState: GameState): GameState
    fun performAttack(gameId: String, player: Player, coordinates: Coordinates): String
    fun attackTile(grid: List<Tile>, coordinates: Coordinates): Action
    fun areAllShipsSunk(grid: List<Tile>, ships: List<Ship>): Boolean
    fun getCurrentGameState(gameId: String): GameState
    fun getGameState(gameId: String, turn: Int): GameState
    fun getGameStates(gameId: String): Collection<GameState>
    fun resetToPrimaryState(gameId: String): Unit
}