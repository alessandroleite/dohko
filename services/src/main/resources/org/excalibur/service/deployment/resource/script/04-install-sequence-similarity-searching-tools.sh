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



cd $HOME
SSEARCH_HOME=$HOME/fasta-36.3.6d

echo "Compiles and installs the SSEARCH tools in $SSEARCH_HOME"

sudo apt-get install zlib1g-dev -qy
sudo apt-get install libz-dev -qy
#sudo apt-get install unzip -qy
sudo apt-get update -qy
sudo apt-get clean

wget http://faculty.virginia.edu/wrpearson/fasta/fasta3.tar.gz
tar -xvf fasta3.tar.gz

cd $SSEARCH_HOME/src

make -f ../make/Makefile.linux64 all

echo "SSEARCH_HOME=$SSEARCH_HOME/bin" >> $HOME/.profile
echo 'PATH=$SSEARCH_HOME:$PATH' >> $HOME/.profile

cd $HOME
wget https://www.dropbox.com/s/k6qviz9rgsxxijf/uniprot_sprot.fasta.gz
gunzip -f uniprot_sprot.fasta.gz
rm -rf __MACOSX/

#
mkdir $HOME/sequences
cd $HOME/sequences
wget https://www.dropbox.com/s/ugm0y9pppimg6pl/sequences.tar.gz
tar -xzvf sequences.tar.gz
rm -rf __MACOSX

mkdir $HOME/scores

echo "done"