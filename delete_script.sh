#!/bin/sh
####
#This script delets the user
###
 
 
# Declare local variables, generate random password.
 
newuser=$1
#randompw=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 8 | head -n 1)
 
# Create new user and assign random password.
 
userdel -r $newuser
#echo $newuser:$randompw | chpasswd
echo "UserID:" $newuser "has been deleted successfully"

