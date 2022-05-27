package tama.antanas.battleship.model

import tama.antanas.battleship.utility.Player

data class AttackRequest(
    val gameId: String,
    val player: Player,
    val coordinates: Coordinates
)
