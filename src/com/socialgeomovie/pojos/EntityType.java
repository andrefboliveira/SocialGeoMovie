/**
 * 
 */
package com.socialgeomovie.pojos;

import com.google.cloud.language.v1.Entity.Type;

/**
 * @author r.vallejo
 *
 */
public class EntityType {

	private Type type;
	private String value;
	private String subtitleQuoteMatch;
	private int beginOffset;

	/**
	 * @param type
	 * @param value
	 * @param subtitleQuoteMatch
	 * @param beginOffset
	 */
	public EntityType(Type type, String value, int beginOffset, String subtitleQuoteMatch) {
		this.type = type;
		this.value = value;
		this.subtitleQuoteMatch = subtitleQuoteMatch;
		this.beginOffset = beginOffset;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * @return the subtitleQuoteMatch
	 */
	public String getSubtitleQuoteMatch() {
		return subtitleQuoteMatch;
	}

	/**
	 * @param subtitleQuoteMatch the subtitleQuoteMatch to set
	 */
	public void setSubtitleQuoteMatch(String subtitleQuoteMatch) {
		this.subtitleQuoteMatch = subtitleQuoteMatch;
	}

	/**
	 * @return the beginOffset
	 */
	public int getBeginOffset() {
		return beginOffset;
	}

	/**
	 * @param beginOffset the beginOffset to set
	 */
	public void setBeginOffset(int beginOffset) {
		this.beginOffset = beginOffset;
	}

	@Override
	public String toString() {
		return "EntityType [" + (type != null ? "type=" + type + ", " : "")
				+ (value != null ? "value=" + value + ", " : "")
				+ (subtitleQuoteMatch != null ? "subtitleQuoteMatch=" + subtitleQuoteMatch + ", " : "") + "beginOffset="
				+ beginOffset + "]\n";
	}

}
