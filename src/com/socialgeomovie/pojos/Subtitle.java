package com.socialgeomovie.pojos;

public class Subtitle {

	private Integer subtitleId;
	private String language;
	private String fileName;
	private String format;
	private String downloadLink;
	private String subtitleAsString;
	private String movieId;

	public Subtitle(Integer subtitleId, String language, String fileName, String format, String downloadLink,
			String subtitleAsString, String movieId) {
		this.subtitleId = subtitleId;
		this.language = language;
		this.fileName = fileName;
		this.format = format;
		this.downloadLink = downloadLink;
		this.subtitleAsString = subtitleAsString;
		this.movieId = movieId;
	}

	public Integer getSubtitleId() {
		return subtitleId;
	}

	public void setSubtitleId(Integer subtitleId) {
		this.subtitleId = subtitleId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getDownloadLink() {
		return downloadLink;
	}

	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

	public String getSubtitleAsString() {
		return subtitleAsString;
	}

	public void setSubtitleAsString(String subtitleAsString) {
		this.subtitleAsString = subtitleAsString;
	}

	public String getMovieId() {
		return movieId;
	}

	public void setMovieId(String movieId) {
		this.movieId = movieId;
	}

	@Override
	public String toString() {
		return "Subtitle [subtitleId=" + subtitleId + ", language=" + language + ", fileName=" + fileName + ", format="
				+ format + ", downloadLink=" + downloadLink + ", subtitleAsString=" + subtitleAsString + ", movieId="
				+ movieId + "]";
	}

	
}