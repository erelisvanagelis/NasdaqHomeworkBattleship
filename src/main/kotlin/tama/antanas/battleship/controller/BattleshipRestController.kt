package tama.antanas.battleship.controller

import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tama.antanas.battleship.model.GameState

@RestController
@RequestMapping("battleship/api")
class BattleshipRestController {
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
}