package com.socialgeomovie.clients;

import java.io.IOException;

import com.socialgeomovie.pojos.Subtitle;

public class a {

	public static void main(String[] args) throws IOException, InterruptedException {
//		OpenSubsClient opsc = new OpenSubsClient();
//		Analyze app = new Analyze();
		//
//		Subtitle subtitle = opsc.getSubtitle("123456");
//		 System.out.println(subtitle.getSubtitleAsString());
		 TwitterClient.getTweets("The Fate Of The Furious");
		 System.out.println(TwitterClient.getTweets("The Fate Of The Furious")); // both work
		//
		// for (String string : TwitterClient.getTweet("Ricardo", 1)) {
		// System.out.println(string);
		// }
		// }

//		String googleEntities = app.filterByType(subtitle.getSubtitleAsString()).toString();
//		System.out.println(googleEntities);

	}
}
