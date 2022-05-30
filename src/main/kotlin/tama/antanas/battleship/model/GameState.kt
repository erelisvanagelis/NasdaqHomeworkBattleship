package tama.antanas.battleship.model

import tama.antanas.battleship.utility.Action
import tama.antanas.battleship.utility.Player

data class GameState(
    val id: String,
    val turn: Int = 0,
    val playerTurn: Player = Player.ONE,
    val action: Action = Action.GENERATED,
    val gameOver: Boolean = false,
    val fpGrid: List<Tile>,
    val spGrid: List<Tile>,
    val attackCoordinates: Coordinates?,
)
