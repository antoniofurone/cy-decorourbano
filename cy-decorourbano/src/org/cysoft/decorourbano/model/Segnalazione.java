package org.cysoft.decorourbano.model;

import java.util.Date;

public class Segnalazione {
	
	
	private String title="";
	private String link="";
	private String pubDate="";
	private Date parsedPubDate=null;
	private String description="";
	private String guid="";
	private String category="";
	private String status="";
	private String point="";
	private String enclosureUrl="";
	private long userId;
	private double latitude;
	private double longitude;
	private String text="";
	private long statusId;
	private long categoryId;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPoint() {
		return point;
	}
	public void setPoint(String point) {
		this.point = point;
	}
	public String getEnclosureUrl() {
		return enclosureUrl;
	}
	public void setEnclosureUrl(String enclosureUrl) {
		this.enclosureUrl = enclosureUrl;
	}
	
	public Date getParsedPubDate() {
		return parsedPubDate;
	}
	public void setParsedPubDate(Date parsedPubDate) {
		this.parsedPubDate = parsedPubDate;
	}
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
	public long getStatusId() {
		return statusId;
	}
	public void setStatusId(long statusId) {
		this.statusId = statusId;
	}
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	@Override
	public String toString() {
		return "Segnalazione [parsedPubDate=" + parsedPubDate + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", text=" + text + "]";
	}
	
	
}
