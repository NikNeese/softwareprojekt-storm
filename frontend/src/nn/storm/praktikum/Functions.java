package nn.storm.praktikum;



import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
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


public class Functions {
	ITrace2D london;
	ITrace2D NY;
	ITrace2D SF;
	Chart2D chart = new Chart2D();
    JFrame frame = new JFrame("MinimalDynamicChart");
	ArrayList<SSHClient> cons = new ArrayList<>();
    int count_ldn = 0;
    int count_sf = 0;
    int count_ny = 0;
    double time;
	public Functions() {
        london = new Trace2DLtd(200);
        london.setColor(Color.RED);
        london.setName("London");
        NY = new Trace2DLtd(200);
        NY.setColor(Color.WHITE);
        NY.setName("New York");
        SF = new Trace2DLtd(200);
        SF.setColor(Color.BLACK);
        SF.setName("San Francisco");
        chart.addTrace(london);
        chart.addTrace(NY);
        chart.addTrace(SF);
        chart.setBackground(Color.LIGHT_GRAY);
        chart.setForeground(Color.BLUE);
        chart.setGridColor(Color.GREEN);
        

        @SuppressWarnings("rawtypes")
    	IAxis axisX = chart.getAxisX();
        axisX.setPaintGrid(true);
        @SuppressWarnings("rawtypes")
    	IAxis axisY = chart.getAxisY();
        axisY.setPaintGrid(true);

        // Create a frame.

        // add the chart to the frame:
        frame.getContentPane().add(chart);
        frame.setSize(600, 400);
        frame.setLocation(200, 200);
        
        
        time = (double) System.currentTimeMillis();
	}
	public void mergeCsvs() {};
	
	
	public static SSHClient createConnection(String hostname) throws IOException {
        SSHClient ssh = new SSHClient();
    	ssh.addHostKeyVerifier(new PromiscuousVerifier());
    	ssh.connect(hostname);
        PKCS8KeyFile keyFile = new PKCS8KeyFile();
        keyFile.init(new File("C:\\Users\\Alibengali\\.ssh\\storm-18.pem"));
        ssh.authPublickey("ec2-user", keyFile);
    	return ssh;
	};
	public void createConnectionsWithAllHostsFromAFile(String filename) throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	cons.add(createConnection(line));
		    }
		}
	}
	public void printGraph() {
        // the program is terminated if one of the windows is closed 
        frame.addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e){
                    System.exit(0);
                }
            }
        );
        // Make it visible
        frame.setVisible(true);
        Timer timer = new Timer(false);
        TimerTask task = new TimerTask(){
          @Override
          public void run() {
           int x = 0;

           for(SSHClient con: cons) {
               try {
				con.newSCPFileTransfer().download("outfile.csv", new FileSystemFile("C:\\Users\\Alibengali\\Desktop\\out"+x+".csv"));
            	BufferedReader brTest = new BufferedReader(new FileReader("C:\\Users\\Alibengali\\Desktop\\out"+x+".csv"));
			    String[] text = brTest.readLine().split(",");
			    count_ldn+=Integer.parseInt(text[0]);
			    count_sf+=Integer.parseInt(text[1]);
			    count_ny+=Integer.parseInt(text[2]);
				x++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           }
            london.addPoint(((double) System.currentTimeMillis()-time),(double) count_ldn);
            SF.addPoint(((double) System.currentTimeMillis()-time),(double) count_sf);
            NY.addPoint(((double) System.currentTimeMillis()-time),(double) count_ny);
            count_ldn= 0;
            count_ny= 0; 
            count_sf = 0;
          }
          
        };	

        timer.schedule(task, 0, 2000);

		
	};

	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Functions f = new Functions();
		f.createConnectionsWithAllHostsFromAFile("hosts.txt");
		f.printGraph();
	}
}
