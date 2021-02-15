package pko.unity.time.tracker.ui.kernel

import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute

open class FreemarkerController {

    @ModelAttribute
    fun addTools(model: Model){
        model.addAttribute("timeFormat", TimeFormat())
    }
}
