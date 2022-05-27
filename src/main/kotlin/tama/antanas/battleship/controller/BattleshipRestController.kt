package tama.antanas.battleship.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tama.antanas.battleship.model.Coordinates
import tama.antanas.battleship.model.Game
import tama.antanas.battleship.model.GameState
import tama.antanas.battleship.model.Tile
import tama.antanas.battleship.service.BattleshipService
import tama.antanas.battleship.utility.Environment
import tama.antanas.battleship.utility.Player

@RestController
@RequestMapping("battleship/api")
class BattleshipRestController(private val service: BattleshipService) {

    @PostMapping("{id}")
    fun postGame(@PathVariable id: String): Game = service.createGame(id)

//    @GetMapping("{id}")
//    fun getCurrentState (@PathVariable id : String) : GameState {
//        val state = service.getCurrentGameState(id)
//        println(Player.ONE)
//        printGrid(game.)
//
//        return game
//    }

    fun printGrid(grid: List<Tile>){
        var previousCoordinate = grid.first().coordinates
        for (tile in grid){
            if (previousCoordinate.letter == tile.coordinates.letter){
                if (tile.tag == Environment.WATER.name){
                    print("0 ")
                } else {
                    print("X ")
                }
            } else {
                println()
                if (tile.tag == Environment.WATER.name){
                    print("0 ")
                } else {
                    print("X ")
                }
            }
            previousCoordinate = tile.coordinates
        }
        println()
    }
}