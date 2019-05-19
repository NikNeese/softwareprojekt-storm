package code;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.IAxis;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;





import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class NegativeBolt extends BaseRichBolt{


	/**
	 * 
	 */
	private static final long serialVersionUID = 83766579991859335L;
	private static final Logger LOG = LoggerFactory.getLogger(NegativeBolt.class);
	private static int id = 0;
	static Integer county_ny=0;
	static Integer count_ldn=0;
	static Integer count_sf=0;
	//private static final Logger LOG = LoggerFactory.getLogger(NegativeBolt.class);
	
	
	String FILE2;
	long time;
	long avg_execution_time=0;
	ITrace2D london;
	ITrace2D NY;
	ITrace2D SF;

	int realID;
	HashSet<String> negdict;
	private boolean graphics;
	File file2;
	
	
	NegativeBolt(long time, boolean graphics){
		this.time = time;
		this.graphics = graphics;
		//this.time=time;
		negdict=ndictbuilder("negativewords.txt");
		realID=id;
		++id;
    	file2 = new File(System.getProperty("user.home")+"/outfile"+".csv");
	}

	  OutputCollector _collector;
	private FileWriter writer;
	private FileWriter writer2;
	/**
	 * 
	 */
    public void prepare(@SuppressWarnings("rawtypes") Map conf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
        //graphics is on when run in local mode
        if(this.graphics) {
	        //the following code&comment is taken from jchart2d examples: http://jchart2d.sourceforge.net/usage.shtml and extended
	        Chart2D chart = new Chart2D();
	        // Create an ITrace:
	        // Note that dynamic charts need limited amount of values!!!
	        london = new Trace2DLtd(200);
	        london.setColor(Color.RED);
	        london.setName("London");
	        NY = new Trace2DLtd(200);
	        NY.setColor(Color.WHITE);
	        NY.setName("New York");
	        SF = new Trace2DLtd(200);
	        SF.setColor(Color.BLACK);
	        SF.setName("San Francisco");
	        // Add the trace to the chart:
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
	        JFrame frame = new JFrame("MinimalDynamicChart");
	        // add the chart to the frame:
	        frame.getContentPane().add(chart);
	        frame.setSize(600, 400);
	        frame.setLocation(200, 200);
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
	         
	            london.addPoint(((double) System.currentTimeMillis()-time),(double) count_ldn);
	            SF.addPoint(((double) System.currentTimeMillis()-time),(double) count_sf);
	            NY.addPoint(((double) System.currentTimeMillis()-time),(double) county_ny);
	          }
	          
	        };	

	        // Every 20 milliseconds a new value is collected.
	        timer.schedule(task, 0, 1000);

        }
        else {	        	 
        	//String x = new File("").getAbsolutePath();
	       
        	//System.out.println(x);
	        String x2 = System.getProperty("user.home");
	        
        	File file = new File(x2+"/Textual_Data_Analysis"+realID+".csv");

        	try {
        		if(file.exists()) {file.delete();}
				file.createNewFile();
				
				writer = new FileWriter(file, true);
				writer.write("Time in ms,LDN,SF,NY,AVG_LDN,AVG_SF,AVG_NY,tweetprocessingtime\n");

		        Timer timer = new Timer(false);
		        TimerTask task = new TimerTask(){
    		    	double avg_ldn=0;
    		    	double avg_sf=0;
    		    	double avg_ny=0;

    		    	int pre_ldn=0, pre_sf=0, pre_ny=0;
			          @Override
			          public void run() {
			    		    try {

			    		    	avg_ldn=(double) Math.round(((pre_ldn+avg_ldn)/2.0)*100d)/100d;
			    		    	avg_sf=(double) Math.round(((pre_sf+avg_ldn)/2.0)*100d)/100d;
			    		    	avg_ny=(double) Math.round(((pre_ny+avg_ldn)/2.0)*100d)/100d;
								writer.write((System.currentTimeMillis()-time)+","+count_ldn+","+count_sf+","+county_ny+","+avg_ldn+","+avg_sf+","+avg_ny+","+avg_execution_time+"\n");
								writer.flush();
								writer2 = new FileWriter(file2,false);
								writer2.write(""+count_ldn+","+count_sf+","+county_ny);
								writer2.close();
								pre_ldn=count_ldn;
								pre_sf=count_sf;
								pre_ny=county_ny;

							} catch (IOException e) {
								e.printStackTrace();
							} 
			          }
		          
		        };
		        timer.schedule(task, 0, 5000);
			} catch (IOException e) {
				e.printStackTrace();
			}

	        
        }
    }  
	    
	/**
	 * 
	 */
	public void execute(Tuple tuple) {
		@SuppressWarnings("unchecked")
		ArrayList<String> tokens=(ArrayList<String>)tuple.getValueByField("tokens");
		@SuppressWarnings("unchecked")
		ArrayList<String> hashtags=(ArrayList<String>)tuple.getValueByField("hashtags");
		int i=0;
	//	System.out.print(tuple.getInteger(0));
		int count=0;
		long start=tuple.getLongByField("start");
		long end=System.currentTimeMillis();
		this.avg_execution_time = end -start;
		//System.out.println(end-start);
		 while(i<tokens.size()){
			 if(negdict.contains(tokens.get(i))){

				 count--;}
			 	i++;
	     }

		 i=0;

	//	 System.out.println("size"+hashtags.size());
		 while(i<hashtags.size()){
			if( negdict.contains(hashtags.get(i))){
				 count--;
			}
			i++;
		 }

		String city=tuple.getString(3);
		//LOG.info("nnn "+count+" "+city+" "+tuple.getInteger(0));
		//LOG.info("counts: "+county_ny+" "+count_ldn+" "+count_sf);
		if(city.equals("New York")){
			//synchronized (county_ny) {
				//LOG.info("if::NY");
				if(-count>tuple.getInteger(0)){
					//LOG.info("if::"+city+"_-1");
					county_ny-=1;
				}
				else if(-count<tuple.getInteger(0)){
					//LOG.info("if::"+city+"_-1");
					county_ny+=1;
				}
				else {
					//LOG.info("whyamihere");
				}
			//}
		}
		if(city.equals("London")){
			//synchronized(count_ldn) {
				//LOG.info("if::LL");
			//	count_ldn+=(count+tuple.getInteger(0));
				if(-count>tuple.getInteger(0)){
					//LOG.info("if::London_-1");
					count_ldn-=1;
				}
				else if(-count<tuple.getInteger(0)){
					//LOG.info("if::London_+1");
					count_ldn+=1;
				}
				else {
					//LOG.info("whyamihere");
				}
			//}
		}
		if(city.equals("San Francisco")){
			//synchronized(count_sf) {
				//LOG.info("if::SF");
				if(-count>tuple.getInteger(0)){
					//LOG.info("if::"+city+"_-1");
					count_sf-=1;
				}
				else if(-count<tuple.getInteger(0)){
					//LOG.info("if::"+city+"_+1");
					count_sf+=1;
				}
				else {
					//LOG.info("whyamihere");
				}
			//}
		}

	 }

	  public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("count", "city"));
	  }
	  /**
	   * 
	   * @param FILE path with a text file containing negative words. 1 word per txt file line. method is configured for a dictionary with 4160 words
	   * @return returns a dictionary 
	   */
	   public HashSet<String> ndictbuilder(String FILE){
			
			String[] zeile=new String[4159];
			String x = new File("").getAbsolutePath();
			URL url = getClass().getResource("/"+FILE);
			File file2 = new File(url.getPath());
			File file=new File(x+"/"+FILE);
			try {@SuppressWarnings("resource")

				BufferedReader in = new BufferedReader(new FileReader(file));
				int i=0;
				while (i<4159) {zeile[i]=in.readLine();i++;}}
		    catch (IOException e) { e.printStackTrace(); }
			 
		HashSet<String> dict = new HashSet<String>();
		//int o=0;
		for(int o=0;o<4159;o++){
			
		dict.add(zeile[o]);}
		return dict;
		}
		
}
