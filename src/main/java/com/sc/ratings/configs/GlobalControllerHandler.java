package com.sc.ratings.configs;

import com.sc.ratings.exceptions.NotAdminException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@ControllerAdvice(annotations = RestController.class)
public class GlobalControllerHandler {


//    @ExceptionHandler(value=Exception.class)
//    @ResponseBody
//    public Object authExceptionHandler(Exception e) {
//        if (e instanceof NotAdminException) {
//
//        }
//    }
}
