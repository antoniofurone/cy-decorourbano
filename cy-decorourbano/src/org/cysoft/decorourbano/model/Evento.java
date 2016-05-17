package org.cysoft.decorourbano.model;

public class Evento {
	
	
	private String nome="";
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	private String bundleDescription="";
	public String getBundleDescription() {
		return bundleDescription;
	}
	public void setBundleDescription(String bundleDescription) {
		this.bundleDescription = bundleDescription;
	}

	private String description="";
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	String comune="";
		public String getComune() {
		return comune;
	}
	public void setComune(String comune) {
		this.comune = comune;
	}
	
	private double latitude;
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	private double longitude;
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	private String telefono;
		public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	private String web;
	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	private String email;
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private String dataInizio;
	public String getDataInizio() {
		return dataInizio;
	}
	public void setDataInizio(String dataInizio) {
		this.dataInizio = dataInizio;
	}

	private String dataFine;
	public String getDataFine() {
		return dataFine;
	}

	public void setDataFine(String dataFine) {
		this.dataFine = dataFine;
	}

	private long userId;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	private String tipologia;
	public String getTipologia() {
		return tipologia;
	}
	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}
	
	private String ticket;
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	
	@Override
	public String toString() {
		return "Evento [nome=" + nome + ", description=" + description + ", comune=" + comune + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", telefono=" + telefono + ", web=" + web + ", email=" + email
				+ ", dataInizio=" + dataInizio + ", dataFine=" + dataFine + ", userId=" + userId + ", tipologia="
				+ tipologia + ", ticket=" + ticket + "]";
	}
	

}
