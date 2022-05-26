package tama.antanas.battleship.datasource

import tama.antanas.battleship.model.Game

interface GameDataSource {
    fun addGame (game: Game) : Game
    fun updateGame (game: Game) : Game
    fun retrieveGame (id: String) : Game
}