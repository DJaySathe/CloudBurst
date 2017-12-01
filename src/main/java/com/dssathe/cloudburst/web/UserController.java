package com.dssathe.cloudburst.web;

import com.dssathe.cloudburst.aws.AWS;
import com.dssathe.cloudburst.model.Reservation;
import com.dssathe.cloudburst.service.UserService;
import com.dssathe.cloudburst.validator.UserValidator;
import com.dssathe.cloudburst.model.User;
import com.dssathe.cloudburst.repository.ReservationRepository;
import com.dssathe.cloudburst.service.SecurityService;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;
    
    @Autowired
    private ReservationRepository reservationRepository;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);

        //securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/adminDashboard";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    @RequestMapping(value = {"/adminDashboard"}, method = RequestMethod.GET)
    public String adminDashBoard(Model model) {
        return "adminDashboard";
    }

    @RequestMapping(value = {"/", "/welcome"}, method = RequestMethod.GET)
    public String welcome(Model model) {
        return "welcome";
    }

    @RequestMapping(value = "/reservation", method = RequestMethod.GET)
    public String reservation(Model model) {
        return "reservation";
    }

    @RequestMapping(value = "/reservation", method = RequestMethod.POST)
    public String reservation(@ModelAttribute("reserveForm") Reservation reservationForm, Model model) {

        reservationForm.setPassword("YxAnsK");

        AWS aws = new AWS();
        String ip = aws.createInstance(reservationForm.getUsername(), reservationForm.getPassword());
        if(ip == null) {
            System.out.println("Unable to create Instance");
        }
        else {

        }
        System.out.println("Coming out from aws thing");
        return "redirect:/welcome";
    }
    
    @RequestMapping(value = "/deleteReservation", method = RequestMethod.POST)
    public String deleteReservation(@RequestParam(value="deleteReservation", required=true) Long id) {
    	Reservation reservation = reservationRepository.findOne(id); // fetch reservation to check private/public
    	if(reservation.getSource() == 1) { // Call Script to delete VM on private cloud
    		
    	}
    	
    	else { // Call Script to delete VM on public cloud
    		
    	}
    	
    	reservationRepository.delete(id); // delete reservation from database
    	return "redirect:/welcome";
    }
}
