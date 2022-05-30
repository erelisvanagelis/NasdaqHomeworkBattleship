package tama.antanas.battleship.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("battleship/")
class BattleshipController {
    @RequestMapping("/")
    fun index() : String {
        return "index"
    }
}