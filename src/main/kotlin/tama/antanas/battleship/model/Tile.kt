package tama.antanas.battleship.model

data class Tile(
    var tag: String,
    val coordinates: Coordinates,
    var shot: Boolean,
)