package tama.antanas.battleship.utility

enum class Players {
    ONE,
    TWO,
    NONE,
}

enum class Ships(val length: Int) {
    CARRIER(5),
    BATTLESHIP(4),
    CRUISER(3),
    SUBMARINE(3),
    DESTROYER(2)
}

enum class Environment{
    WATER
}