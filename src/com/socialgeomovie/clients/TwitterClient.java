package com.socialgeomovie.clients;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
	
	public ArrayList<String> getTweet(int nrTweets) throws InterruptedException{
		
		// Create an appropriately sized blocking queue
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(nrTweets);
		BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);
		
		// Authenticate via OAuth
		Authentication hosebirdAuth = new OAuth1(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);

		// Declare the host we want to connect to, the endpoint, and authentication
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

		// Build a hosebird client
		ClientBuilder builder = new ClientBuilder()
				.hosts(hosebirdHosts)						// add Constants.STREAM_HOST
				.authentication(hosebirdAuth)
				.endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue))
				.eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events
		
		// Creates a client
		Client hosebirdClient = builder.build();
		
		// Attempts to establish a connection
		hosebirdClient.connect();
				
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
		
		hosebirdClient.connect();
		
		// Creates a list to save the iterated tweets
		ArrayList<String> tweetsList = new ArrayList<String>();
		while (!hosebirdClient.isDone()) {
			String message = msgQueue.take();
			tweetsList.add(message); 
		} 	
				
		hosebirdClient.stop();
		return tweetsList;
  }
	
}
