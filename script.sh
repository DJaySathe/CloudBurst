#!/bin/sh
####
# This script automatically creates user accounts with random passwords.
#
# Author: Russ Sanderlin
# Date: 01/21/15
#
###
 
 
# Declare local variables, generate random password.
hostip=$1
username=$2
flag=$3
if [ "$3" = 1 ]
 then ssh root@$1 "bash -s" < add_script.sh $2 
else
 ssh root@$1 "bash -s" < delete_script.sh $2
fi
