package code;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

//import com.jidesoft.utils.Base64.InputStream;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class PositiveBolt extends BaseRichBolt{
	
	private static final long serialVersionUID = 8980572301916739261L;
	String FILE3;	HashSet<String> posdict;
	PositiveBolt(){
		posdict = pdictbuilder("positivewords.txt");
	}



	OutputCollector _collector;

	public void prepare(@SuppressWarnings("rawtypes") Map conf, TopologyContext context, OutputCollector collector) {
	        _collector = collector;
	}
	public void execute(Tuple tuple) {
		@SuppressWarnings("unchecked")
		ArrayList<String> tokens=(ArrayList<String>)tuple.getValueByField("tokens");
		@SuppressWarnings("unchecked")
		ArrayList<String> hashtags=(ArrayList<String>)tuple.getValueByField("hashtags");
		
		 int poscount=0;
		 int i=0;
	
		 while(i<tokens.size()){
			 if(posdict.contains(tokens.get(i))){

				 poscount++;}
			 	i++;
		 }
		
		 i=0;
	 while(i<hashtags.size()){
			if( posdict.contains(hashtags.get(i))){
				 poscount++;
				 
			}
			i++;
		
			 
		 }
		//	System.out.println(poscount);
		// posdict.contains();
		// System.out.println(tuple.getString(1));

			_collector.emit(tuple, new Values(poscount, tokens, hashtags, tuple.getString(2),tuple.getLong(3), tuple.getLongByField("start")));
			_collector.ack(tuple);
			
	 }

	  public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("count", "tokens","hashtags", "city", "timelong", "start"));
	  }
	  /**
	   * 
	   * @param FILE path with a text file containing positive words. 1 word per txt file line. method is configured for a dictionary with 2307 words
	   * @return returns a dictionary
	   */
		public HashSet<String> pdictbuilder(String FILE){
			

			String[] zeile=new String[2306];
			//InputStream is = getClass().getResourceAsStream(FILE);
			//String dir = System.getProperty("user.dir");

			File file=new File(FILE);//.getAbsolutePath();
			 try {@SuppressWarnings("resource")

				BufferedReader in = new BufferedReader(new FileReader(file));
				int i=0;
				
				while (i<2306) {zeile[i]=in.readLine();i++;}}
		    catch (IOException e) { e.printStackTrace(); }
			 
		HashSet<String> dict = new HashSet<String>();
		//int o=0;
		for(int o=0;o<2306;o++){
			
		dict.add(zeile[o]);}
		return dict;
		}


}
