package com.sc.ratings.configs;

import org.springframework.web.bind.annotation.ControllerAdvice;
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
