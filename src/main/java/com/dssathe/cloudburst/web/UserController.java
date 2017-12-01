package com.dssathe.cloudburst.web;

import com.dssathe.cloudburst.aws.AWS;
import com.dssathe.cloudburst.model.Reservation;
import com.dssathe.cloudburst.service.UserService;
import com.dssathe.cloudburst.validator.UserValidator;
import com.dssathe.cloudburst.model.User;
import com.dssathe.cloudburst.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

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
            String username = "dummy";

            try {
              String command = "sh ../../../../../../../script2.sh";

              System.out.println(command);

              p = Runtime.getRuntime().exec(command);
              BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

              String pw = br.readLine();
              System.out.println("Private VM id = " + vm_id + "random password = " + password);

              reservationForm.setVm_id(vm_id);
              reservationForm.setSource(1);
              reservationForm.setPublic_ip(ip);
              reservationForm.setPassword(pw);

              Helper.insertReservation(reservationForm);

              p.waitFor();
              p.destroy();

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

          }
          System.out.println("Coming out from aws thing");
          return "redirect:/welcome";
        }

    }
}
