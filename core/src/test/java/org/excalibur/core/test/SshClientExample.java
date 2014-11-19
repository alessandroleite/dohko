/**
 *     Copyright (C) 2013-2014  the original author or authors.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License,
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.excalibur.core.test;

import java.io.IOException;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.exec.ExecutableResponse;
import org.excalibur.core.ssh.SshClient;
import org.excalibur.core.ssh.SshClientFactory;

import com.google.common.net.HostAndPort;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.agentproxy.AgentProxyException;

public class SshClientExample
{
    public static void main(String[] args) throws AgentProxyException, IOException, JSchException
    {
        HostAndPort hostAndPort = HostAndPort.fromParts("ec2-54-83-158-5.compute-1.amazonaws.com", 22);
        LoginCredentials loginCredentials = LoginCredentials.builder().privateKey(System.getProperty("user.home") + "/.ec2/leite.pem").user("ubuntu")
                .build();

        SshClient client = SshClientFactory.defaultSshClientFactory().create(hostAndPort, loginCredentials);
        client.connect();

        ExecutableResponse response = client.execute("uname -a && date && uptime && who");
        System.out.println(response);

//        final OnlineChannel shell = client.shell();
//        shell.getInput().write("sudo apt-get install maven -y\n".getBytes());
//        shell.getInput().flush();
//
//        Thread t = new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                while (true)
//                {
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(shell.getOutput(), Charsets.US_ASCII));
//                    String line;
//                    try
//                    {
//                        while ((line = reader.readLine()) != null)
//                        {
//                            System.out.println(line);
//                        }
//                    }
//                    catch (IOException e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        t.setDaemon(true);
//        t.start();
        
        client.put("/home/ubuntu/hi.txt", "Hi node!");

        client.disconnect();

        // JSch jsch = new JSch();
        // jsch.addIdentity(loginCredentials.getPrivateKey());
        //
        // Session session = jsch.getSession(loginCredentials.getUser(), hostAndPort.getHostText());
        //
        // Properties config = new Properties();
        // config.put("StrictHostKeyChecking", "no");
        //
        // session.setConfig(config);
        // session.connect();
        //
        // Channel shell = session.openChannel("shell");
        // shell.setInputStream(System.in);
        // shell.setOutputStream(System.out);
        // shell.connect(0);

    }
}
