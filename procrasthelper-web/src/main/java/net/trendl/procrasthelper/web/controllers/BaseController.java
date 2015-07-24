package net.trendl.procrasthelper.web.controllers;

import net.trendl.procrasthelper.web.domain.UIMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Tomas.Rendl on 22.7.2015.
 */
public abstract class BaseController {
    static Logger LOG = LoggerFactory.getLogger(BaseController.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UIMessage> exceptionHandler(Exception ex, HttpServletRequest request) {
        LOG.warn(ex.getMessage(), ex);
        return new ResponseEntity<UIMessage>(new UIMessage(ex.getMessage()), HttpStatus.ACCEPTED);
    }

}
