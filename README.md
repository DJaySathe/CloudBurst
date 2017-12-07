Cloud Bursting to AWS from VCL
1. Implement Virtual computing infrastructure which should include below mentioned points
• Compute Node (Physical or virtual).
• Management Node (you can run as virtual machine on Compute machine or separate
physical machine)
• Host a Webserver and User privileges related configuration in Database on Management
Node.
2. Implement the dash board or user interface which should provide the below mentioned
functionalities
• User should be able to reserve the virtual machine.
• User should be see the list of reserved virtual machine.
• Admin/User should be able to see the number of available images and other resources
like virtual machines.
3. If the user requirement is going beyond than availability or threshold, then provide the requested
resources on the AWS. For Ex: you can provide 5 VM’s. Currently, all 5 are reserved. If user request for new virtual machine, then send the REST API query to the AWS and reserve a machine on AWS and return to the user with Machine IP & login credentials.
 

Provisioning Engine Setup 
-------------

Install git mysql, maven, MySQL, Jetty9, awscli

1. sudo apt-get update
2. sudo apt-get install git
3. sudo apt-get install  mysql-server
4. sudo apt-get install maven
5. sudo apt-get install jetty9
6. sudo apt-get install awscli

Clone the Git Repository

git clone https://github.com/DJaySathe/CloudBurst
or
git clone https://github.ncsu.edu/engr-csc-547/2017-fall-team-05

Use file at [db.sql](/src/main/resources/db.sql) to load the database schema.

Configure Aws
---------------

Follow the instruction given in [aws_details](aws_details) file.

Private cloud setup
---------------

Follow the instruction given in [Networking_conf](Networking_conf) file.

Starting the Project
------------

Go to the project home folder.

mvn jetty:run
