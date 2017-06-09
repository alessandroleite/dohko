# Dohko-DevLab

1. Install [VirtualBox](https://www.virtualbox.org/wiki/Downloads)
   
   Known working version:

   ```
   $ VBoxManage --version
   5.1.6r110634
   ```

1. Install [VirtualBox Extension Pack](https://www.virtualbox.org/wiki/Downloads). **Please install the extension pack with the same version as your installed version of VirtualBox**. For instance, if your installed VirtualBox version is 5.1.6, its extension pack can be downloaded on [http://download.virtualbox.org/virtualbox/5.1.6/Oracle_VM_VirtualBox_Extension_Pack-5.1.6-110634a.vbox-extpack](http://download.virtualbox.org/virtualbox/5.1.6/Oracle_VM_VirtualBox_Extension_Pack-5.1.6-110634a.vbox-extpack)

1. Install [Vagrant](http://www.vagrantup.com/downloads.html)

   Known working version:

   ```
   $ vagrant --version
   Vagrant 1.9.5
   ```

### Booting a virtual machine

1. Start Vagrant from this repository

    ```
    $ vagrant up
    ```
2. Connect to the virtual machine via SSH

    ```
    $ vagrant ssh
    ```

### Shut down the virtual machine

  ```
  $ vagrant halt
  ```

### Check the status of the virtual machine

  ```
  $ vagrant status
  ```

### Restart the virtual machine

  ```
  $ vagrant reload
  ```

### Agent forwarding over SSH

By default, **agent forwarding** over SSH connections **is enabled**. It means that your SSH's key can be available in the **guest machine** (i.e., virtual machine). For this, you must execute the following command on your machine (i.e., host machine) before executing _vagrant ssh_.

```bash
  ssh-agent -t 3600
  ssh-add ~/.ssh/id_rsa
```
