package com.socialgeomovie.clients;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterClient {

	private static final String CONSUMER_KEY = "E7uTIdET9uIZroI6w8GdgogsV";
	private static final String CONSUMER_SECRET = "ZFCzHryhBHLovx06kUcAwYkVAoySGwCrNkr10bdspVmgAF1fQK";
	private static final String ACCESS_TOKEN = "841224583051149313-vvUu5cxygGm9t1uZg0QPSgdgs9BTkww";
	private static final String ACCESS_TOKEN_SECRET = "fHj97HjevuabndTH62GIKBfIaCCafENyKsPOXUSxTiJVg";
	
	public Object getTweet(){
		
		Authentication hosebirdAuth = new OAuth1(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
		
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
		BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);

		// Declare the host we want to connect to, the endpoint, and authentication
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		
		// Followings and track terms
		List<Long> followings = new ArrayList<Long>();
		followings.add(1234L);
		followings.add(566788L);
		List<String> terms = new ArrayList<String>();
		terms.add("twitter");
		terms.add("api");
		List<String> languages = new ArrayList<String>();
		languages.add("en");
		languages.add("pt");
		hosebirdEndpoint.followings(followings);
		hosebirdEndpoint.trackTerms(terms);
		hosebirdEndpoint.languages(languages);
		
		ClientBuilder builder = new ClientBuilder()
				  .name("Hosebird-Client-01")                              // optional: mainly for the logs
				  .hosts(hosebirdHosts)
				  .authentication(hosebirdAuth)
				  .endpoint(hosebirdEndpoint)
				  .processor(new StringDelimitedProcessor(msgQueue))
				  .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events

				Client hosebirdClient = builder.build();
				// Attempts to establish a connection.
				hosebirdClient.connect();
				
		// on a different thread, or multiple different threads....
		Gson gson = new Gson();
		while (!hosebirdClient.isDone())
		{
			try
			{
				String msg = msgQueue.take();
				Type token = new TypeToken<HashMap<String,Object>>(){}.getType();
				Map<String,Object> tweet = gson.fromJson(msg, token);
				return(tweet.get("text"));
			} 
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				//something(msg);
				//profit();
			}		
				
		hosebirdClient.stop();
	}

}
