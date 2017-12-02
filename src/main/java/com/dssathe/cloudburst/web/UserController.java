package com.dssathe.cloudburst.web;

import com.dssathe.cloudburst.aws.AWS;
import com.dssathe.cloudburst.model.Reservation;
import com.dssathe.cloudburst.service.UserService;
import com.dssathe.cloudburst.validator.UserValidator;
import com.dssathe.cloudburst.model.User;
import com.dssathe.cloudburst.model.VmInfo;
import com.dssathe.cloudburst.repository.ReservationRepository;
import com.dssathe.cloudburst.repository.VmInfoRepository;
import com.dssathe.cloudburst.service.SecurityService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    @Autowired
    private VmInfoRepository vmInfoRepository;

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
        long reservationID = -1;
        String vm_id = "";

        reservationForm.setImage_name("CentOS 7");
        reservationForm.setPublic_ip("");
        reservationForm.setPassword("");
        reservationForm.setStart_time(dateFormat.format(cal.getTime()));
        cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(reservationForm.getEnd_time()));
        //System.out.println("End time="+ dateFormat.format(cal.getTime()));
        reservationForm.setEnd_time(dateFormat.format(cal.getTime()));

        // decide to spin VM on private cloud or burst to public cloud
        int vm_count = Helper.getAvailable();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        if(vm_count > 0) { // reserve on private cloud
            vm_id = Helper.getAvailablePrivateCloudVM(); // this function marks the vm as unavailable
            System.out.println("First available vm id = " + vm_id);

            if(vm_id != null && vm_id != "") {

              final String ip = Helper.getIPofPrivateCloudVM(vm_id);
              System.out.println("Ip of available vm = " + ip);

              // create a reservation in the db
              reservationForm.setVm_id(vm_id);
              reservationForm.setSource(1);

              System.out.println(reservationForm.toString());

              reservationID = Helper.insertReservation(reservationForm);
              System.out.format("Created new reservation with ID= %d\n", reservationID);

              if(reservationID != -1) {
                final long reserveID = reservationID;
                final String vmID = vm_id;

                // create a thread to setup the private cloud VM
                Thread setupVM = new Thread() {
                  @Override
                  public void run() {
                      try {
                          System.out.println("Starting private cloud provisioning");
                          Thread.sleep(3 * 1000);

                          // get the password
                          System.out.println("Running script:" + System.getProperty("user.dir") + "/script2.sh");
                          Process p = new ProcessBuilder("/bin/sh", System.getProperty("user.dir") + "/script2.sh", ip, reservationForm.getUsername(), "1").start();
                          p.waitFor();

                          BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                          String pw = br.readLine();
                          System.out.println("random password for new reservation = " + pw);

                          // if password is null delete the reservation from db and set vm as available
                          if(pw == null || pw == "") {
                            reservationRepository.delete(reserveID);
                            Helper.markAvailability(vmID, 1);

                          } else {
                            // set the public ip and password for the reservation in the db
                            Helper.updateReservation(reserveID, ip, pw);
                          }

                          System.out.println("Coming out of private cloud provisioning");

                      } catch (Exception e) {
                          System.out.println("Unable to invoke private cloud reservation: " + e.getMessage() + "\n" + e.getStackTrace());
                          // delete the reservation from db and set vm as available
                          reservationRepository.delete(reserveID);
                          Helper.markAvailability(vmID, 1);
                      }
                  }
                };
                setupVM.start();

              } else { // reservation could not be created in the db. Set vm as available.
                  Helper.markAvailability(vm_id, 1);
              }
            }

        } else { // reserve on public cloud

          // create a reservation in the db
          reservationForm.setVm_id("Some AWS ID");
          reservationForm.setSource(0);

          System.out.println(reservationForm.toString());

          reservationID = Helper.insertReservation(reservationForm);
          System.out.format("Created new reservation with ID= %d\n", reservationID);

          if(reservationID != -1) {
            final long reserveID = reservationID;

            // create a thread to setup the AWS VM
            Thread setupAWS = new Thread() {
              @Override
              public void run() {
                try {
                  System.out.println("Starting AWS cloud provisioning");
                  Thread.sleep(3 * 1000);

                  // generate random password
                  String pw = "YxAnsK";

                  AWS aws = new AWS();
                  String ip = aws.createInstance(reservationForm.getUsername(), pw);

                  if(ip == null) {
                      System.out.println("Unable to create AWS Instance");
                      // delete reservation from db
                      reservationRepository.delete(reserveID);
                  }
                  else {
                    System.out.println("AWS instance ip: " + ip);
                    // update ip and password for reservation in database
                    Helper.updateReservation(reserveID, ip, pw);
                    // TODO: set aws instance id
                  }

                  System.out.println("Coming out of AWS cloud provisioning");

                } catch (Exception e) {
                    System.out.println("Unable to invoke AWS cloud reservation: " + e.getMessage() + "\n" + e.getStackTrace());
                    // delete the reservation from db
                    reservationRepository.delete(reserveID);
                }
              }
            };
            setupAWS.start();

          }

        }

        return "redirect:/welcome";
    }

    @RequestMapping(value = "/deleteReservation", method = RequestMethod.POST)
    public String deleteReservation(@RequestParam(value="deleteReservation", required=true) Long id) {
    	Reservation reservation = reservationRepository.findOne(id); // fetch reservation to check private/public

      System.out.println("Delete reservation with id=" + reservation.getId() + " ip=" + reservation.getPublic_ip() + " user=" + reservation.getUsername());

      try {
        if(reservation.getSource() == 1) { // Call Script to delete VM on private cloud

          Process p = new ProcessBuilder("/bin/sh", System.getProperty("user.dir") + "/script2.sh", reservation.getPublic_ip(), reservation.getUsername(), "0").start();
	        p.waitFor();

	        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String value = br.readLine();
	        System.out.println("Return code from delete bash script:" + value);

          // mark the vm as available
          String vm_id = reservation.getVm_id();
          Helper.markAvailability(vm_id, 1);
          /*
          VmInfo info = vmInfoRepository.findByVmId(reservation.getVm_id());
	    		info.setAvailability(1);
	    		vmInfoRepository.save(info);
          */

        } else { // Call Script to delete VM on public cloud
	    		AWS.terminateInstance(reservation.getVm_id());
	    	}
      } catch (Throwable t) {
          t.printStackTrace();
      }

    	reservationRepository.delete(id); // delete reservation from database

    	return "redirect:/welcome";
    }
}
