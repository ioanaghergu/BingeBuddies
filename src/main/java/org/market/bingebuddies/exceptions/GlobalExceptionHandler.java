package org.market.bingebuddies.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFoundException(Exception e) {
        ModelAndView mav = new ModelAndView();
        mav.getModel().put("exception", e);
        mav.setViewName("UserNotFound");

        return mav;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserAlreadyExists.class)
    public ModelAndView handleUserAlreadyExistsException(Exception e) {
        ModelAndView mav = new ModelAndView();
        mav.getModel().put("exception", e);
        mav.setViewName("UserAlreadyExists");

        return mav;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MovieClubNotFoundException.class)
    public ModelAndView handleMovieClubNotFoundException(Exception e) {
        ModelAndView mav = new ModelAndView();
        mav.getModel().put("exception", e);
        mav.setViewName("MovieClubNotFound");

        return mav;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ClubAlreadyExistsException.class)
    public ModelAndView handleClubAlreadyExistsException(Exception e) {
        ModelAndView mav = new ModelAndView();
        mav.getModel().put("exception", e);
        mav.setViewName("ClubAlreadyExists");

        return mav;
    }
}
