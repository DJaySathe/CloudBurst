#!/bin/sh
####
#This script delets the user
###
 
 
newuser=$1

rm -rf /tmp/* 
pkill -u $newuser 
userdel -r $newuser
echo "UserID:" $newuser "has been deleted successfully"

