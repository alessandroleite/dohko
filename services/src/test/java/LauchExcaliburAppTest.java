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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.exec.OnlineChannel;
import org.excalibur.core.ssh.SshClient;
import org.excalibur.core.ssh.SshClientFactory;
import org.excalibur.core.util.Strings2;
import org.excalibur.core.util.SystemUtils2;

import com.google.common.net.HostAndPort;

public class LauchExcaliburAppTest
{
    static final String GCE = "chmod +x /home/alessandro/excalibur/*.sh && cd /home/alessandro/excalibur && nohup /home/alessandro/excalibur/excalibur.sh &";

    public static void main(String[] args) throws IOException
    {
        System.err.println(TimeUnit.MINUTES.toMillis(1));
        
        File credential = new File(SystemUtils2.getUserDirectory(), "/ec2/ssh/key3.key");
        LoginCredentials loginCredentials = LoginCredentials.builder().authenticateAsSudo(false).privateKey(credential).user("alessandro").build();

        SshClient client = SshClientFactory.defaultSshClientFactory().create(HostAndPort.fromParts("146.148.51.170", 22), loginCredentials);

        client.connect();

        OnlineChannel shell = client.shell();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(shell.getOutput()));
        final StringBuilder sb = new StringBuilder();

        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    try
                    {
                        String line = reader.readLine();
                        if (line == null)
                        {
                            break;
                        }

                        sb.append(line).append(Strings2.NEW_LINE);
                    }
                    catch (IOException exception)
                    {
                    }
                }
            }
        });
        
        t.start();

        shell.write("cd ~/excalibur\n");
        shell.write("pwd\n");
        shell.write("nohup ./excalibur.sh &\n");
        shell.write("tail -f nohup.out\n");

        shell.close();
        client.disconnect();

        reader.close();
    }
}
