package tama.antanas.battleship.utility

enum class Player {
    ONE,
    TWO,
    NONE,
}

enum class Ship(val length: Int) {
    CARRIER(5),
    BATTLESHIP(4),
    CRUISER(3),
    SUBMARINE(3),
    DESTROYER(2)
}

enum class Environment{
    WATER
}

enum class Action(val lowName: String, val description: String){
    MISS("miss", "missed ships"),
    HIT("hit", "hit ship"),
    SANK("sank", "sank ship")
}