# -*- mode: ruby -*-
# vi: set ft=ruby :

def install_plugins (plugins=[], restart=false)
  installed = false

  plugins.each do |plugin|
    unless Vagrant.has_plugin? plugin 
      system ("vagrant plugin install #{plugin}")
      puts "Plugin #{plugin} installed!"
      installed = true
    end
  end

  if installed and restart
    puts "Dependencies installed, restarting vagrant ..."
    exec "vagrant #{ARGV.join(' ')}"
  end
  
  return installed
end

plugins = ["vagrant-docker-compose",
  "vagrant-vbguest",
  "vagrant-proxyconf",
  "vagrant-proxyconf",
  "vagrant-env",
  "vagrant-persistent-storage",
  "vagrant-cachier"]

install_plugins(plugins, restart = true)

Vagrant.configure("2") do |config|

  config.vm.define "dohko-lab", primary: true do |dohkolab|
    dohkolab.vm.box      = "trusty-server-cloudimg-amd64"
    dohkolab.vm.hostname = "dohko-lab"
    dohkolab.vm.box_url = "https://cloud-images.ubuntu.com/vagrant/trusty/current/trusty-server-cloudimg-amd64-vagrant-disk1.box"
    dohkolab.vm.box_download_checksum = "4a39a1bf9736162e917f1eacf209349e9f5913d4190ee56a0826f9f8cf81625f"
    dohkolab.vm.box_download_checksum_type = "sha256"

    dohkolab.ssh.forward_agent = true
    dohkolab.ssh.keys_only = true
    dohkolab.ssh.forward_x11 = true

    dohkolab.vm.provider "virtualbox" do |vb|
      vb.customize ["modifyvm", :id, "--memory", 1024]
      vb.customize ["modifyvm", :id, "--ioapic", "on", "--cpus", 4]
      vb.name = "dohko-lab"
    end

    # Create a private network, which allows host-only access to the machine
    # using a specific IP.
    dohkolab.vm.network "private_network", ip: "10.10.3.10"

    dohkolab.vm.synced_folder ".", "/vagrant", disable: true
    dohkolab.vm.synced_folder ".", "/home/vagrant/devlab", disable: false
    dohkolab.vm.synced_folder "../", "/home/vagrant/workspace", disable: false

    dohkolab.vm.provision "shell", inline: <<-SHELL
    
      apt-get update  -y
      apt-get install -y build-essential
      apt-get install -y software-properties-common
      apt-get install -y byobu curl git htop man unzip vim wget

      echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
      add-apt-repository -y ppa:webupd8team/java
      apt-get update
      apt-get install -y oracle-java8-installer

      apt-get autoremove -y

      rm -rf /var/lib/apt/lists/*
      rm -rf /var/cache/oracle-jdk8-installer

      wget -qO- http://apache.mediamirrors.org/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz | tar xzvf - -C /opt/
      ln -s /opt/apache-maven-3.5.0 /opt/maven
      chown -R vagrant:vagrant /opt/maven

      { \
        echo '#!/bin/sh'; \
        echo 'set -e'; \
        echo ; \
        echo ; \
        echo 'export MAVEN_HOME=/opt/maven' ; \
        echo 'export PATH=$PATH:$MAVEN_HOME/bin' ; \
        echo ; \
      } >> /etc/mvn

    SHELL

    dohkolab.vm.provision :docker
    dohkolab.vm.provision :docker_compose

  end
end
