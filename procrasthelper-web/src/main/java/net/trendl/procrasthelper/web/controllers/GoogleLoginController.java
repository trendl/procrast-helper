package net.trendl.procrasthelper.web.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Tomas.Rendl on 7.8.2015.
 */
@RestController
@RequestMapping("/auth/google")
public class GoogleLoginController {

    @RequestMapping(value="/", method= RequestMethod.GET)
    public void loginGet() {
        System.out.println("login get");
    }

    @RequestMapping(value="/", method= RequestMethod.POST)
    public void loginPost() {
        System.out.println("login post");
    }
}
