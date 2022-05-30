package tama.antanas.battleship.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tama.antanas.battleship.model.*
import tama.antanas.battleship.service.BattleshipService
import tama.antanas.battleship.utility.Environment
import tama.antanas.battleship.utility.Player

@RestController
@RequestMapping("battleship/api")
class BattleshipRestController(private val service: BattleshipService) {

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    fun postGame(@PathVariable id: String): Game = service.createGame(id)

    @GetMapping("/{id}")
    fun getCurrentState (@PathVariable id : String) : GameState {
        val state = service.getCurrentGameState(id)
        println(Player.ONE)
        printGrid(state.fpGrid)
        println(Player.TWO)
        printGrid(state.spGrid)

        return state
    }

    @GetMapping("/{id}/{turn}")
    fun getSpecifiedState(@PathVariable id: String, @PathVariable turn:Int) = service.getGameState(id, turn)

    @DeleteMapping("/{id}/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun resetSpecifiedGame(@PathVariable id: String) = service.resetGame(id)

    @PostMapping
    fun postAction (@RequestBody attackRequest: AttackRequest) : String {
        val result = service.performAttack(attackRequest.gameId, attackRequest.player, attackRequest.coordinates)
        println(result)
        return result
    }

    @GetMapping("/test")
    fun test() : AttackRequest = AttackRequest("gg", Player.ONE, Coordinates('A', 1))

    fun printGrid(grid: List<Tile>){
        var previousCoordinate = grid.first().coordinates
        for (tile in grid){
            if (previousCoordinate.letter != tile.coordinates.letter){
                println()
            }

            if (!tile.shot){
                if (tile.tag == Environment.WATER.name){
                    print("0 ")
                } else{
                    print("X ")
                }
            } else {
                if (tile.tag == Environment.WATER.name){
                    print("W ")
                } else{
                    print("S ")
                }
            }



            previousCoordinate = tile.coordinates
        }
        println()
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound (e: NoSuchElementException) = ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument (e: IllegalArgumentException) = ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(UnsupportedOperationException::class)
    fun handleUnsupportedOperation (e: UnsupportedOperationException) = ResponseEntity(e.message, HttpStatus.NOT_ACCEPTABLE)
}