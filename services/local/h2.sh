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


declare w_pid=0
declare s_pid=0

#USER_HOME=$(env |grep USER)
LIB_DIR=$HOME/.m2/repository/com/h2database/h2/1.3.173/
echo "$HOME"

function start_web_interface()
{
  eval "(java -cp .:$HOME/.m2/repository/com/h2database/h2/1.3.173/h2-1.3.173.jar org.h2.tools.Server -web -webAllowOthers -webPort 6082) &"
  w_pid=$! 
  echo "Web PID -> $w_pid"
}


function start_server
{
   eval "(java -cp .:$HOME/.m2/repository/com/h2database/h2/1.3.173/h2-1.3.173.jar org.h2.tools.Server -tcp -tcpAllowOthers -tcpPort 6083) &"
   s_pid=$!
   echo "Database server PID -> $s_pid"
}

start_web_interface 

start_server
