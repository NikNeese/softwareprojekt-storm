package code;




import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

/**
 * This class implements the Spout which enters the TwitterStreaming API by using the consumerKey, 
 * consumerSecret, accessToken and accessTokenSecret
 * to verify the login
 * 
 *
 */
public class StreamingSpout extends BaseRichSpout {

	private static final long serialVersionUID = -95211448556012983L;
	SpoutOutputCollector _collector;
	//queue is going to be used to collect tweets from the twitter4j StatusListener
	LinkedBlockingQueue<Status> queue = null;
	TwitterStream _twitterStream;
	String consumerKey;
	String consumerSecret;
	String accessToken;
	String accessTokenSecret;


	public StreamingSpout(String consumerKey, String consumerSecret,
			String accessToken, String accessTokenSecret) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
		
	}

	public StreamingSpout() {
	
	}

	@Override
	public void open(@SuppressWarnings("rawtypes") Map conf, TopologyContext context,
			SpoutOutputCollector collector) {

		queue= new LinkedBlockingQueue<Status>(1000);
		_collector = collector;

		StatusListener listener = new StatusListener() {
			/**
			 * i decided to pick only certain parts of the tweet: 
			 * content(text that got posted), 
			 * timetag of the post, 
			 * geolocation as coordinates and 
			 * places by placenames
			 */
			@Override
			public void onStatus(Status status) {
			
				queue.offer(status);
				//queue.offer(status.getCreatedAt()+"-Text1="+status.getText()+"-geoLocation="+status.getGeoLocation()+"-Place1="+status.getPlace());
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice sdn) {
			}

			@Override
			public void onTrackLimitationNotice(int i) {
			}

			@Override
			public void onScrubGeo(long l, long l1) {
			}

			@Override
			public void onException(Exception ex) {
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
			

			}

		};

		TwitterStream twitterStream = new TwitterStreamFactory(
				new ConfigurationBuilder().setJSONStoreEnabled(true).build())
				.getInstance();
		
		twitterStream.addListener(listener);
		twitterStream.setOAuthConsumer(consumerKey, consumerSecret);
		AccessToken token = new AccessToken(accessToken, accessTokenSecret);
		twitterStream.setOAuthAccessToken(token);
		
		/**
		 * the twitterstream can be modified by a filter.  
		 * filtering locations needs a bounding box as request.
		 * e.g. one bounding box is  first pair are coordinates of the south west, 2nd north east
		 * {double, double}{double, double}
		 */

		FilterQuery query = new FilterQuery();
		double orte[][]={{-74,40},{-73,41},{-122.75,36.8},{-121.75,37.8},{-0.424347,51.313447},{0.163422,51.66489}};
		query.locations(orte);
		twitterStream.filter(query);
		

	}
	/**
	 * nextTuple() is one of the methods used in spouts
	 * here it the queue offers a status in form of a not empty string it is emitted
	 */
	@Override
	public void nextTuple() {
		long startspout=System.currentTimeMillis();
		Status ret = queue.poll();
		/*Status*///String ret = queue.poll();
		if (ret == null) {
			Utils.sleep(50);
		} else {
			_collector.emit(new Values(ret, startspout));

		}
	}
/**
 * u can use the close method to stop streaming
 */
	@Override
	public void close() {
		_twitterStream.shutdown();
	}

	/**
	 * the emitted values getting a name tag
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet", "start"));
	}

}
