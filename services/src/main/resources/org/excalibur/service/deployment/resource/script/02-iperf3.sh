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


sudo apt-get update
sudo aptitude install -y build-essential
sudo apt-get install iperf -y
wget -qO- http://downloads.es.net/pub/iperf/iperf-3.0.1.tar.gz | tar xzvf -
cd iperf-3.0.1

./configure

make && sudo make install
cd ../
rm -rf iperf-3.0.1