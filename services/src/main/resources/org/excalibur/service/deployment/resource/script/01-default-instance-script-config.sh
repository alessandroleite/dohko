#!/bin/sh
#
#     Copyright (C) 2013-2014  the original author or authors.
#
#     This program is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License,
#     any later version.
#
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
#
#     You should have received a copy of the GNU General Public License
#     along with this program.  If not, see <http://www.gnu.org/licenses/>
#

# prepares the application environment
APP_HOME_DIR=$HOME/excalibur
APP_DATA_DIR=$HOME/.excalibur
SCRIPT_DIR=$APP_DATA_DIR/scripts
HOST_NAME=${[name]}

mkdir -p $APP_HOME_DIR
mkdir -p $SCRIPT_DIR

#[ -d ~/excalibur ] || mkdir ~/excalibur
#[ -d ~/.excalibur ] || mkdir ~/.excalibur

echo "echo \"${[name]}\" > /etc/hostname" >> $SCRIPT_DIR/conf_hostname.sh
echo "echo \"127.0.0.1 ${[name]}\" >> /etc/hosts" >> $SCRIPT_DIR/conf_hostname.sh
echo "hostname -F /etc/hostname" >> $SCRIPT_DIR/conf_hostname.sh

chmod +x $SCRIPT_DIR/conf_hostname.sh
sudo sh $SCRIPT_DIR/conf_hostname.sh

echo "cat <<EOF > /etc/apt/sources.list.d/rabbitmq.list" >> $SCRIPT_DIR/conf_source_list.sh
echo "deb http://www.rabbitmq.com/debian/ testing main" >> $SCRIPT_DIR/conf_source_list.sh
echo "EOF" >> $SCRIPT_DIR/conf_source_list.sh

chmod +x $SCRIPT_DIR/conf_source_list.sh
sudo sh $SCRIPT_DIR/conf_source_list.sh 

curl http://www.rabbitmq.com/rabbitmq-signing-key-public.asc -o /tmp/rabbitmq-signing-key-public.asc
sudo apt-key add /tmp/rabbitmq-signing-key-public.asc
rm /tmp/rabbitmq-signing-key-public.asc

sudo apt-get -qy update
sudo aptitude -y update
#sudo apt-get -qy install rabbitmq-server
#sudo apt-get -qy install openjdk-7-jdk 
#sudo apt-get -qy install htop iperf vim
sudo aptitude -y install rabbitmq-server
sudo aptitude -y install openjdk-7-jdk 
sudo aptitude -y install lshw 
sudo aptitude -y install htop
sudo aptitude -y install vim
sudo aptitude -y install iperf

sudo aptitude -y update
sudo aptitude -y install build-essential
#sudo aptitude -y install unzip
#sudo apt-get -qy install make gcc unzip htop iperf vim 
#zookeeper zookeeper-bin zookeeperd

#sudo aptitude -y install unzip
#sudo apt-get -qy update
#sudo apt-get -qy clean

#sudo rabbitmqctl stop_app
#sudo rabbitmqctl reset
#sudo rabbitmqctl start_app

echo "#`date`" >> $APP_DATA_DIR/config
echo "done `date`"