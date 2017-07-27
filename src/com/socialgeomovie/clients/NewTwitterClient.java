package com.socialgeomovie.clients;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class NewTwitterClient
{
	private static final String CONSUMER_KEY = "E7uTIdET9uIZroI6w8GdgogsV";
	private static final String CONSUMER_SECRET = "ZFCzHryhBHLovx06kUcAwYkVAoySGwCrNkr10bdspVmgAF1fQK";
	private static final String ACCESS_TOKEN = "841224583051149313-vvUu5cxygGm9t1uZg0QPSgdgs9BTkww";
	private static final String ACCESS_TOKEN_SECRET = "fHj97HjevuabndTH62GIKBfIaCCafENyKsPOXUSxTiJVg";

	public List<HashMap<String, Object>> fetchTweets(String term)
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	      .setOAuthConsumerKey(CONSUMER_KEY)
	      .setOAuthConsumerSecret(CONSUMER_SECRET)
	      .setOAuthAccessToken(ACCESS_TOKEN)
	      .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
	    Twitter twitter = new TwitterFactory(cb.build()).getInstance();
	    Query query = new Query(term);
	    query.setLang("en");
	    QueryResult result;
	    List<HashMap<String, Object>> tweets = new ArrayList<HashMap<String,Object>>();
		try 
		{
			result = twitter.search(query);
			
			for (Status status : result.getTweets()) 
		    {
				HashMap<String, Object> tweet = new HashMap<String, Object>();
				tweet.put("user", status.getUser().getScreenName());
				tweet.put("text", status.getText());
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				Date date = status.getCreatedAt();
				tweet.put("date",dateFormat.format(date));
				tweet.put("retweet_count", status.getRetweetCount());
				tweet.put("url", "https://twitter.com/"+tweet.get("user")+"/status/"+status.getId());
				tweets.add(tweet);
//		        System.out.println("@" + status.getUser().getScreenName() + " : " + status.getText() + " : " + status.getCreatedAt());
		    }
		} 
		catch (TwitterException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tweets;
	}
}
