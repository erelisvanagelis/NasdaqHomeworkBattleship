package tama.antanas.battleship.controller

import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tama.antanas.battleship.model.Coordinates
import tama.antanas.battleship.model.Game
import tama.antanas.battleship.model.GameState
import tama.antanas.battleship.model.Tile
import tama.antanas.battleship.service.BattleshipService
import tama.antanas.battleship.utility.Environment

@RestController
@RequestMapping("battleship/api")
class BattleshipRestController(private val service: BattleshipService) {
    @GetMapping
    fun hello() : String{
        var text = " "
        for (i in 'A'..'J')
            text += "$i, "
        text += "\r\n"
        for (i in 1..10)
            text += "$i \r\n"
        return text
    }

    @GetMapping("{id}")
    fun getGame (@PathVariable id : String) : Game {

        val list1 = listOf(
            Tile("gg", Coordinates('A', 1), false),
            Tile("bb", Coordinates('b', 2), false),
        )

        val list2 = list1.toList()

        println(list1)
        println(list2)

        list2.first().tag = "BIG SHIP"

        println(list1)
        println(list2)

        val game = service.createGame(id)
        var previousCoordinate = game.states.first().fpGrid.first().coordinates
        println()
        for (tile in game.states.first().fpGrid){
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


        return game
    }
}