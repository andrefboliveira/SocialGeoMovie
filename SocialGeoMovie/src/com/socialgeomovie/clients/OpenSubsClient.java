package com.socialgeomovie.clients;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;

import com.github.wtekiela.opensub4j.impl.OpenSubtitlesImpl;
import com.github.wtekiela.opensub4j.response.SubtitleFile;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;

public class OpenSubsClient {

	private String URL = "http://api.opensubtitles.org:80/xml-rpc";
	private String UA = "OSTestUserAgentTemp";
	private String LANG = "en";
	private String FORMAT = "UTF-8";
	private OpenSubtitlesImpl os;

	public OpenSubsClient() {
		try {
			os = new OpenSubtitlesImpl(new URL(URL));
			os.login(LANG, UA);
		} catch (XmlRpcException | MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public String getSubtitle(String imdb) {

		try {
			if(imdb.startsWith("tt"))
				imdb = imdb.substring(2, imdb.length());
			int subId = -1;
			for (SubtitleInfo subInfo : os.searchSubtitles(LANG, imdb)) {
				if (subInfo != null && subInfo.getSubtitleFileId() > -1 && subInfo.getLanguage().equals("English")) {
					subId = subInfo.getSubtitleFileId();
					for (SubtitleFile file : os.downloadSubtitles(subId)) {
						if(file != null)
							return file.getContentAsString(FORMAT);
					}
				}
			}

		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
		return "";
	}

}
