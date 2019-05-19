package nn.storm.praktikum;

import com.jcraft.jsch.*;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.traces.Trace2DLtd;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.bouncycastle.*;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.userauth.keyprovider.PKCS8KeyFile;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.IOException;

/** This example demonstrates downloading of a file over SCP from the SSH server. */
public class SCP {

    public static void main(String[] args)
            throws IOException {
    	
    	Properties prop = new Properties();
    	InputStream input = null;

    	try {

    		input = new FileInputStream("config.properties");

    		// load a properties file
    		prop.load(input);

    		// get the property value and print it out
    		System.out.println(prop.getProperty("database"));


    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} finally {
    		if (input != null) {
    			try {
    				input.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	
    	////////////////////////////////////////////////////////////
    	
    	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SSHClient ssh = createClient("ec2-18-185-48-215.eu-central-1.compute.amazonaws.com");
        PKCS8KeyFile keyFile = new PKCS8KeyFile();
        keyFile.init(new File("C:\\Users\\Alibengali\\.ssh\\storm-18.pem"));
        Timer timer = new Timer(false);
        TimerTask task = new TimerTask(){
          @Override
          public void run() {
              try {
                  ssh.authPublickey("ec2-user", keyFile);
                  
                //  ssh.newSCPFileTransfer().download("Textual_Data_Analysis0.csv", new FileSystemFile("C:\\Users\\Alibengali\\Desktop\\"));
                  ssh.newSCPFileTransfer().download("outfile.csv", new FileSystemFile("C:\\Users\\Alibengali\\Desktop\\"));
              } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			} finally {
                  try {
					ssh.disconnect();
	                ssh.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

              }
          }
          
        };	

        // Every 20 milliseconds a new value is collected.
        timer.schedule(task, 0, 1000);
        



        
    }
    public static SSHClient createClient(String s) throws IOException {
        SSHClient ssh = new SSHClient();
    	ssh.addHostKeyVerifier(new PromiscuousVerifier());
    	ssh.connect(s);
    	return ssh;
    }
    


}




