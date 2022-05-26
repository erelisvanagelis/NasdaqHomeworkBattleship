package tama.antanas.battleship.datasource.memory

import org.springframework.stereotype.Repository
import tama.antanas.battleship.datasource.GameDataSource
import tama.antanas.battleship.model.Game

@Repository
class MemoryGameDataSource : GameDataSource {
    override fun addGame(game: Game): Game {
        TODO("Not yet implemented")
    }

    override fun updateGame(game: Game): Game {
        TODO("Not yet implemented")
    }

    override fun retrieveGame(id: String): Game {
        TODO("Not yet implemented")
    }
}