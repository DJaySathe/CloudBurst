package com.dssathe.cloudburst.aws;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import com.jcraft.jsch.*;

public class AWS {
    private String username;
    private String password;

    private String publicIP;
    private String instanceId;

    private String imageId;
    private String securityGrp;
    private String key;


    public AWS() {
        username = null;
        password = null;

        publicIP = null;
        instanceId = null;

        imageId = "ami-da05a4a0";
        securityGrp = "sg-5a9e4c2f";
        key = "lab_machine_key";
    }

    public String createInstance(String user, String pass) {
        instanceId = launch();
        if(instanceId == null) {
            return null;
        }
        System.out.println("Instance Launched");

        setPublicIP();
        System.out.println("Public IP set");

        System.out.println("Waiting for status checks");
        if(!checkStatus()) {
            terminateInstance();
            return null;
        };

        createUser(user, pass);

        return publicIP;
    }

    private void createUser(String user, String pass) {
        username = user;
        password = pass;

        runSSH();
        System.out.println("New user created");
    }

    public void terminateInstance() {
        Process p;
        try {
            String command = "aws ec2 terminate-instances --instance-ids " + instanceId;

            p = Runtime.getRuntime().exec(command);

            p.waitFor();
            p.destroy();

        } catch (Exception e) {
            System.out.println("Unable to Terminate: " + e.getMessage());
        }
    }

    public static void terminateInstance(String instance) {
        Process p;
        try {
            String command = "aws ec2 terminate-instances --instance-ids " + instance;

            p = Runtime.getRuntime().exec(command);

            p.waitFor();
            p.destroy();

        } catch (Exception e) {
            System.out.println("Unable to Terminate: " + e.getMessage());
        }
    }

    public String getPublicIP() {
        return publicIP;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getImageId() {
        return imageId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private String launch() {
        Process p;
        String instance = null;
        try {
            // Launching New Instance
            String command = "aws ec2 run-instances --image-id " + imageId +
                    " --security-group-ids " + securityGrp +
                    " --count 1 --instance-type t2.micro --key-name " + key +
                    " --query Instances[0].InstanceId";

            p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            instance = br.readLine();

            p.waitFor();
            p.destroy();

            String status = "stopped";
            int i=0;

            while(!status.equals("running")) {
                command = "aws ec2 describe-instance-status --instance-id " + instance + " --query InstanceStatuses[0].InstanceState.Name";
                p = Runtime.getRuntime().exec(command);

                br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                status = br.readLine();

                p.waitFor();
                p.destroy();

                TimeUnit.SECONDS.sleep(10);

                if(i++ > 15) {
                    return null;
                }
            }

            TimeUnit.SECONDS.sleep(10);

        } catch (Exception e) {
            System.out.println("Unable to launch: " + e.getStackTrace());
        }

        return instance;
    }

    private Boolean checkStatus() {
        Process p;
        BufferedReader br;
        try {
            String status = "checking";
            int i = 0;

            while (!status.equals("passed")) {
                System.out.println("System reachability check in progress");
                String command = "aws ec2 describe-instance-status --instance-id " + instanceId + " --query InstanceStatuses[0].SystemStatus.Details[0].Status";
                p = Runtime.getRuntime().exec(command);

                br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                status = br.readLine();

                p.waitFor();
                p.destroy();

                TimeUnit.SECONDS.sleep(10);

                if (i++ > 30) {
                    return false;
                }
            }
            System.out.println("System reachability check passed");

            status = "checking";
            while (!status.equals("passed")) {
                System.out.println("Instance reachability check in progress");
                String command = "aws ec2 describe-instance-status --instance-id " + instanceId + " --query InstanceStatuses[0].InstanceStatus.Details[0].Status";
                p = Runtime.getRuntime().exec(command);

                br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                status = br.readLine();

                p.waitFor();
                p.destroy();

                TimeUnit.SECONDS.sleep(10);

                if (i++ > 35) {
                    return false;
                }
            }
            System.out.println("Instance reachability check passed");

        } catch (Exception e) {
            System.out.println("Status Check failed: " + e.getStackTrace());
        }

        // Both command outputs should change to "passed"

        return true;
    }

    private void setPublicIP() {
        try {
            // Getting ans setting public IP
            String command = "aws ec2 describe-instances --instance-ids " + instanceId + " --query Reservations[0].Instances[0].PublicIpAddress";

            Process p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            publicIP = br.readLine();

            p.waitFor();
            p.destroy();

        } catch (Exception e) {
            System.out.println("Unable to get public IP: " + e.getStackTrace());
        }
    }

    private void runSSH() {
        String keyPath = "~/" + key + ".pem";
        String rootUser = "ubuntu";

        try {
            JSch jsch = new JSch();

            jsch.addIdentity(keyPath);
            jsch.setConfig("StrictHostKeyChecking", "no");

            Session session=jsch.getSession(rootUser, publicIP, 22);
            session.connect();

            // Creating new user and allowing password authentication via ssh
            String commands = "sudo useradd -s /bin/bash -m -d /home/" + username + " -g root " + username + "\n" +
                    "echo -e \"" + password + "\\n" + password + "\" | sudo passwd " + username + "\n" +
                    "echo '" + username + " ALL=(ALL:ALL) ALL' | sudo EDITOR='tee -a' visudo\n" +
                    "sudo cp -p /etc/ssh/sshd_config /etc/ssh/sshd_config.old\n" +
                    "sudo sed -i '/PasswordAuthentication no/c\\PasswordAuthentication yes' /etc/ssh/sshd_config\n" +
                    "sudo /etc/init.d/ssh restart\n";

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            BufferedReader br = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.setCommand(commands);
            channel.setErrStream(System.err);

            channel.connect();

            while(br.readLine() != null);

            channel.disconnect();
            session.disconnect();

        } catch (Exception e) {
            System.out.println("Unable to add user: " + e.getStackTrace());
        }
    }

}
