#!/bin/bash

wd=`pwd`
cd ~/build/
rm I* -rf
cd $wd

#ant

cd ~/build/
dir=`ls -1 ~/build/ | grep "^I" | grep "liferay-ide$" | sort -r | head -1`
cd $dir
zip=`ls -1 | grep "zip$"`
unzipdir=`echo $dir | sed -e 's/-liferay-ide//'`
mkdir $unzipdir
cd $unzipdir
unzip ../$zip
cd ../
mv $unzipdir /mnt/builds
