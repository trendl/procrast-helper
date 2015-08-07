package net.trendl.procrasthelper.web.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Tomas.Rendl on 5.8.2015.
 */
@RestController
@RequestMapping("login")
public class LoginController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String loginPost() {
        return "login";
    }
}
