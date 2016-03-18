package org.cysoft.decorourbano.model;

public class Guid {
	
	private String guid;
	private long ticketId;
	private long locationId;
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public long getTicketId() {
		return ticketId;
	}
	public void setTicketId(long ticketId) {
		this.ticketId = ticketId;
	}
	
	public long getLocationId() {
		return locationId;
	}
	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}
	
	@Override
	public String toString() {
		return "Guid [guid=" + guid + ", ticketId=" + ticketId + ", locationId=" + locationId + "]";
	}
	
	
}
