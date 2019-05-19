package code;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;


public class CityBolt extends BaseRichBolt {

	private static final long serialVersionUID = -2850561143535307700L;
	private static final Logger LOG = LoggerFactory.getLogger(CityBolt.class);
	OutputCollector _collector;

    public void prepare(@SuppressWarnings("rawtypes") Map conf, TopologyContext context, OutputCollector collector) {
	        _collector = collector;
	    }
	   
    @Override
	public void execute(Tuple tuple) {
		
    	long f1=System.nanoTime();
		Status temp=(Status) tuple.getValue(0);
		cityOutput(temp, tuple, f1);

	}
    

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) { 
		declarer.declare(new Fields("tuple", "city", "timelong", "start"));
	}

	/**
	   * cityOutput's task is to emit the Location of the tweet as String   
	   * 
	   * @param temp is the String which contains the tweet.
	   * @param lat (latitude) if temp contains place coordinates they are going to get filtered and put in a string first 
	   * 			lat will be 'converted' to double - lati
	   * @param lon (longitude) if temp contains place coordinates they are going to get filtered and put in a string first
	   * 			lon will be 'converted' to double - longi 
	   * @param temp2
	   * @param i
	   * @param i2
	   * @param tuple
	   */
	public void cityOutput(Status temp, Tuple tuple, long f1){	
		if(temp.getGeoLocation()!=null) {

			double lati=temp.getGeoLocation().getLatitude();
			double longi=temp.getGeoLocation().getLongitude();
			String city="";
				
			if((lati>=51.313447&&lati<=51.66489)&&(longi>=-0.424347&&longi<=0.163422)){
				city="London";		
				_collector.emit(tuple, new Values(temp, city, f1, tuple.getLongByField("start")));
				_collector.ack(tuple);
			}
			else if((lati>=40&&lati<=41)&&(longi>=-74&&longi<=-73)){
				city="New York";
				_collector.emit(tuple, new Values(temp, city, f1, tuple.getLongByField("start")));
				_collector.ack(tuple);
			}
			else if((lati>=36.8&&lati<=37.8)&&(longi>=-122.75&&longi<=-121.75)){
				city="San Francisco";
				_collector.emit(tuple, new Values(temp, city, f1, tuple.getLongByField("start")));
				_collector.ack(tuple);
			}

			
			
			}
		else {
			String ccheck=temp.getPlace().getFullName();
			String city="";

			if(ccheck.indexOf("London")!=-1){
				city="London";		
				_collector.emit(tuple, new Values(temp, city, f1, tuple.getLongByField("start")));
				_collector.ack(tuple);
			}
			else if(ccheck.indexOf("NY")!=-1||ccheck.contains("New York")){
				city="New York"; 
				_collector.emit(tuple, new Values(temp, city, f1, tuple.getLongByField("start")));
				_collector.ack(tuple);
			}
			else if(ccheck.indexOf("San Francisco")!=-1){
				city="San Francisco";		
				_collector.emit(tuple, new Values(temp, city ,f1,tuple.getLongByField("start")));
				_collector.ack(tuple);
			}

	
			}
		}
}
