package tama.antanas.battleship.model

data class Game(
    val id : String,
    val states: MutableList<GameState>
)
