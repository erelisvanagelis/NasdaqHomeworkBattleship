package tama.antanas.battleship.model

import tama.antanas.battleship.utility.Players

data class GameState(
    val active: Boolean = true,
    val turn: Int = 0,
    val playerTurn: String = Players.ONE.name,
    val action: String = "Game initialized",
    val fpGrid: List<Tile>,
    val spGrid: List<Tile>,
)
