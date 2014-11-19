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
package org.excalibur.fm.configuration;

import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import net.vidageek.mirror.dsl.Mirror;

import org.excalibur.core.services.ProviderService;
import org.excalibur.fm.configuration.ui.InstanceSelectionPanel;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main
{
    @SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException
    {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/applicationContext.xml");
        
        final ProviderService providerService = context.getBean(ProviderService.class);

        configureLookAndFeel();
        
        final JFrame frame = new JFrame();
        frame.getContentPane().add(new InstanceSelectionPanel(providerService));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
        frame.setSize(1118,470);
        frame.setLocationRelativeTo(null);
        
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                frame.setVisible(true);
            }
        });
        
//        Thread.currentThread().join();
//       context.close();
    }
    
	private static void configureLookAndFeel() {
		try 
		{
			UIManager.setLookAndFeel((LookAndFeel) new Mirror().on("com.jgoodies.looks.plastic.Plastic3DLookAndFeel").invoke().constructor().withoutArgs());
		} 
		catch (Throwable e) 
		{
		}
	}
}
