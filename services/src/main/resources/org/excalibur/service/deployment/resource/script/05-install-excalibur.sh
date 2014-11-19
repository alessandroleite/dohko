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

#configures rabbitmq
sudo rabbitmqctl add_user excalibur Par1Zone_eX
sudo rabbitmqctl set_user_tags excalibur administration
sudo rabbitmqctl set_permissions -p / excalibur ".*" ".*" ".*"

echo "export NODE_REGION_NAME=${[type.region.name]}" >> ~/.profile
echo "export NODE_REGION_ENDPOINT=${[location.region.endpoint]}" >> ~/.profile
echo "export NODE_ZONE_NAME=${[location.name]}" >> ~/.profile
echo "export NODE_ADDRESS=${[configuration.publicIpAddress]}" >> ~/.profile
echo "export INTERNAL_NODE_ADDRESS=${[configuration.privateIpAddress]}" >> ~/.profile
echo "export NODE_NAME=${[name]}" >> ~/.profile
echo "export NODE_KEY_NAME=${[configuration.keyName]}" >> ~/.profile
echo "export NODE_PROVIDER_NAME=${[type.provider.name]}" >> ~/.profile
echo "export NODE_PROVIDER_CLASS_NAME=${[type.provider.serviceClass]}" >> ~/.profile
echo "export NODE_PROJECT_NAME=${[configuration.credentials.project]}" >> ~/.profile
echo "export NODE_ACCESS_CREDENTIAL=${[configuration.credentials.loginCredentials.credential]}" >> ~/.profile
echo "export NODE_ACCESS_IDENTITY=${[configuration.credentials.loginCredentials.identity]}" >> ~/.profile

# configure java
echo "export _JAVA_OPTIONS=\"-Djava.net.preferIPv4Stack=true -Dorg.excalibur.provider.name=${[type.provider.name]} -Dorg.excalibur.provider.region.name=${[type.region.name]} -Dorg.excalibur.provider.region.zone.name=${[location.name]} -Dorg.excalibur.user.name=${[owner.username]} -Dorg.excalibur.user.keyname=${[configuration.keyName]} -Dorg.excalibur.instance.hostname=${[name]}\"">> ~/.profile


cd $APP_DATA_DIR
wget https://<an url>/keys.tar.gz
tar xzvf keys.tar.gz
rm -rf keys.tar.gz __MACOSX/

mkdir -p $APP_DATA_DIR/database
cd $APP_DATA_DIR/database
wget https://<an url>/database.tar.gz
tar xzvf database.tar.gz
rm -rf __MACOSX/ database.tar.gz

cd $APP_HOME_DIR
wget https://<an url>/excalibur.tar.gz
tar xzvf excalibur.tar.gz
rm -rf __MACOSX excalibur.tar.gz
chmod +x *.sh

