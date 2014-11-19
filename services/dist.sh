#!/bin/bash
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

#mvn clean package -f services/pom.xml -Dorg.excalibur.service.standalone=true


PROJECT_HOME=`pwd`

rm -rf $PROJECT_HOME/target/dist
rm -rf $PROJECT_HOME/dist
mkdir $PROJECT_HOME/target/dist

cp $PROJECT_HOME/scripts/*.sh $PROJECT_HOME/target/dist
cp -R $PROJECT_HOME/target/lib $PROJECT_HOME/target/dist
cp excalibur.jks $PROJECT_HOME/target/dist
cp $PROJECT_HOME/target/excalibur-services-1.0.0-SNAPSHOT.jar $PROJECT_HOME/target/dist/

cd $PROJECT_HOME/target/dist/

tar czvf excalibur.tar.gz *

mkdir $PROJECT_HOME/dist
mv excalibur.tar.gz $PROJECT_HOME/dist/

cp $PROJECT_HOME/dist/excalibur.tar.gz $HOME/Dropbox/excalibur/