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
ping -q -c 1 $1 > /dev/null

if [ ! $? -eq 0 ]
then
        echo "Network Not reachable"
	exit
fi

if [ $3 -eq 1 ]
then ssh root@$1 "bash -s" < add_script.sh $2 
elif [ $3 -eq 0 ]
then 
  ssh root@$1 "bash -s" < delete_script.sh $2
else
echo "Wrong Input"
fi
