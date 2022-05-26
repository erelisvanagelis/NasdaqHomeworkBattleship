package tama.antanas.battleship.model

data class Game(
    val id : String,
    val state: MutableList<GameState>
)
