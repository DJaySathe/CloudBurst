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

import com.dssathe.cloudburst.utility.Helper;
import java.io.*;


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

        // decide to spin VM on private cloud or burst to public cloud
        int vm_count = Helper.getAvailable();

        if(vm_count > 0) { // reserve on private cloud
            String vm_id = Helper.getAvailablePrivateCloudVM();
            System.out.println("First available vm id = " + vm_id);

            String ip = Helper.getIPofPrivateCloudVM(vm_id);
            System.out.println("Ip of available vm = " + ip);

            String username = "dummy";

            try {
              // get the password
              System.out.println("Running script:" + System.getProperty("user.dir") + "/script2.sh");

              Process p = new ProcessBuilder("/bin/sh", System.getProperty("user.dir") + "/script2.sh", ip, username, "1").start();
              p.waitFor();

              BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
              String pw = br.readLine();
              System.out.println("Private VM id = " + vm_id + " random password = " + pw);

              // mark the vm as unavailable
              int update_count = Helper.markUnavailable(vm_id);

              // if password is null or vm wasn't marked unavailable, don't create reservation
              if(pw == null || pw == "" || update_count == 0) {



              }

              reservationForm.setVm_id(vm_id);
              reservationForm.setImage_name("CentOS 7");
              reservationForm.setSource(1);
              reservationForm.setPublic_ip(ip);
              reservationForm.setPassword(pw);
              reservationForm.setStart_time("2017-11-30 01:01:01");
              reservationForm.setEnd_time("2017-11-30 01:01:01");

              System.out.println(reservationForm.toString());

              Helper.insertReservation(reservationForm);

            } catch (Exception e) {
                System.out.println("Unable to invoke private cloud reservation: " + e.getMessage() + "\n" + e.getStackTrace());
            }

        } else { // reserve on public cloud
          reservationForm.setPassword("YxAnsK");

          AWS aws = new AWS();
          String ip = aws.createInstance(reservationForm.getUsername(), reservationForm.getPassword());
          if(ip == null) {
              System.out.println("Unable to create Instance");
          }
          else {
            System.out.println(ip);
          }
          System.out.println("Coming out from aws thing");
        }

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
