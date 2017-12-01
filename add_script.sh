#!/bin/sh
####
# This script automatically creates user accounts with random passwords.
#
# Author: Russ Sanderlin
# Date: 01/21/15
#
###
 
 
# Declare local variables, generate random password.
 
newuser=$1
randompw=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 8 | head -n 1)
 
# Create new user and assign random password.
 
useradd $newuser
echo $newuser:$randompw | chpasswd
echo "UserID:" $newuser "has been created with the following password:" $randompw

