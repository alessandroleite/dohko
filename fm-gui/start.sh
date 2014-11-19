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

java -cp .:/Users/alessandro/java/jgoodies-looks-2.6.0/jgoodies-common-1.8.0/jgoodies-common-1.8.0.jar:/Users/alessandro/java/jgoodies-looks-2.6.0/jgoodies-looks-2.6.0.jar:target/fm-gui-1.0.0-SNAPSHOT.jar -Dcom.amazonaws.sdk.disableCertChecking=true -Dorg.excalibur.application.database.dir=/Users/alessandro/.excalibur/database -Dorg.excalibur.application.data.dir=/Users/alessandro/.excalibur  -Dh2.serverCachedObjects=10000 -Dorg.excalibur.provider.name=amazon -Dorg.excalibur.provider.region.name=us-east-1 -Dorg.excalibur.user.name=alessandro -Dorg.excalibur.user.keyname=alessandro -Dorg.excalibur.environment.local=false -Dorg.excalibur.provider.region.name=us-east-1 -Dorg.excalibur.instance.hostname=i-6625eb35 -Dorg.excalibur.server.host=localhost -Dorg.excalibur.overlay.port=9091 -Dorg.excalibur.overlay.is.bootstrap=true org.excalibur.fm.configuration.Main