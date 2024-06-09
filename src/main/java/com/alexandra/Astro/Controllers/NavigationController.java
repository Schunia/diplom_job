package com.alexandra.Astro.Controllers;

import com.alexandra.Astro.Models.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavigationController {

    private static Logger logger = LoggerFactory.getLogger(ErrorController.class);


    @GetMapping("/")
    public String getHome(Model model){

        model.addAttribute("activePage", "home");
        model.addAttribute("subscription", new Subscription());
        return "index";
    }

//    @ExceptionHandler(Throwable.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public String exception(final Throwable throwable, final Model model) {
//        logger.error("Exception during execution of SpringSecurity application", throwable);
//        String errorMessage = (throwable != null ? throwable.getMessage() : "Unknown error");
//        model.addAttribute("errorMessage", errorMessage);
//        return "error";
//    }


//
//    @GetMapping("/routes")
//    public String getRoutes(Model model){
//        model.addAttribute("activePage", "routes");
//        return "routes/routes";
//    }
}
