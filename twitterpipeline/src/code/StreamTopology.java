package code;
import java.io.Serializable;

/*import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;

import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
*/
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
//import org.apache.storm.StormSubmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import storm.starter.bolt.PrinterBolt;
//import storm.starter.spout.TwitterSampleSpout;
/**
 * This class implements the hierarchy of the Storm topology and invokes the cluster
 * @author Kingpfogel
 * @version 1.0
 * 
 */
public class StreamTopology implements Serializable {  

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(StreamTopology.class);

    public static void main(String[] args) throws AuthorizationException {
    	//consumerKey, consumerSecret, accessToken& accesTokenSecret needed to login to the twitter stream
    	
    	String topologyName = "default - test";
    	if(args.length>0) {
    		topologyName = args[0];
    	}
    	
    	String consumerKey = "Kt3jbjorVdwlWlfviztjxs0GY"; 
        String consumerSecret = "nqCceAMvd5E5DNOaCNTVD2jMu1Sac2meSIVkEsbLZ4MrLRjUtp"; 
        String accessToken = "3161149401-W8u7552y84sRHK75cLOSd8O8pd1ex2BlNsYjjD4"; 
        String accessTokenSecret = "CgDsJ74LN4EySdr4cUVHH9gMcoiU5m1YKzzrhsE2Se0mL";
        
        //String FILE=args[0];
        //starting time f1
        long f1=System.currentTimeMillis();
        
        
        /********************************************************************
         * building a storm topology, connecting spout to bolt and bolt to bolt
         * streamingspout	:gets the tweets emitting them to	->
         * citybolt			:references a city to a tweet		->
         * tokenizerbolt	:tokenizes all words and hashtags	->
         * positivebolt		:checking tokens for pos.dictionary occurrence
         * 														->
         * negativebolt		:checking tokens for neg.dictionary occurrence&output
         * 
        **********************************************************************/
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("twitter", new StreamingSpout(consumerKey, consumerSecret,
                                accessToken, accessTokenSecret));
        builder.setBolt("city", new CityBolt())
               .fieldsGrouping("twitter", new Fields("tweet", "start"));
        builder.setBolt("tokenizer", (IRichBolt) new TokenizerBolt())
        		.fieldsGrouping("city", new Fields("tuple", "city","timelong", "start"));
        builder.setBolt("positive", new PositiveBolt())
 				.fieldsGrouping("tokenizer", new Fields("tokens", "hashtags", "city", "timelong","start"));


        if(args.length==2) {
            builder.setBolt("negative", (IRichBolt) new NegativeBolt(f1, true))
        				.fieldsGrouping("positive", new Fields("count","tokens","hashtags", "city", "timelong","start"));
            Config conf = new Config();
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(topologyName, conf, builder.createTopology());
        } else {
            builder.setBolt("negative", (IRichBolt) new NegativeBolt(f1, false))
    				.fieldsGrouping("positive", new Fields("count","tokens","hashtags", "city", "timelong","start"));
            Config conf = new Config();
            //conf.setDebug(true);
    		conf.setNumWorkers(2);
            //conf.setMaxSpoutPending(5000);
            try {
      			StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
      			} catch (AlreadyAliveException e) {
      				e.printStackTrace();
      			} catch (InvalidTopologyException e) {
      				e.printStackTrace();
      			}
        }

    }
}


