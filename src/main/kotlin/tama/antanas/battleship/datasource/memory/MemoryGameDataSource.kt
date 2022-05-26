package tama.antanas.battleship.datasource.memory

import org.springframework.stereotype.Repository
import tama.antanas.battleship.datasource.GameDataSource
import tama.antanas.battleship.model.Game
import tama.antanas.battleship.model.GameState

@Repository
class MemoryGameDataSource : GameDataSource {
    private val games = mutableListOf<Game>(
        Game("test1", mutableListOf<GameState>()),
        Game("test2", mutableListOf<GameState>()),
        Game("test3", mutableListOf<GameState>()),
    )

    override fun addGame(game: Game): Game {
        games.add(game)
        return game
    }

    override fun updateGame(game: Game): Game {
        val foundGame = games.find { x -> x.id == game.id  }
        games.remove(foundGame)
        games.add(game)
        return game
    }

    override fun retrieveGame(id: String): Game = games.first() { x -> x.id == id }

    override fun retrieveAllGames(): Collection<Game> = games
}