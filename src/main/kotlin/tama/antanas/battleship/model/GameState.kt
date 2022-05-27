package tama.antanas.battleship.model

import tama.antanas.battleship.utility.Player

data class GameState(
    val turn: Int = 0,
    val playerTurn: Player = Player.ONE,
    val action: String = "Game initialized",
    val fpGrid: List<Tile>,
    val spGrid: List<Tile>,
)
