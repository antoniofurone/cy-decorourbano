package org.cysoft.decorourbano.main;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cysoft.decorourbano.common.CyDecoroUrbanoException;
import org.cysoft.decorourbano.common.CyDecoroUrbanoUtility;
import org.cysoft.decorourbano.dao.PugliaEventiDao;
import org.cysoft.decorourbano.model.Evento;
import org.cysoft.decorourbano.model.GeoLocation;
import org.cysoft.decorourbano.model.Guid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

public class PugliaEventiTask {
	
	
private static final Logger logger = LoggerFactory.getLogger(DecoroUrbanoTask.class);
	
	
	@Autowired
	Environment environment;
	
	@Autowired
	PugliaEventiDao pugliaEventiDao;
	
	
	public void exec() throws CyDecoroUrbanoException{

		
		String pugliaEventiXmlUrl=environment.getProperty("pugliaventi.xmlURL");
		if (pugliaEventiXmlUrl==null || pugliaEventiXmlUrl.equals("")){
			throw new CyDecoroUrbanoException("xmlURL not configured !");
		}
		logger.info("XmlUrl="+pugliaEventiXmlUrl);
		
		String userId=environment.getProperty("du.userId");
		if (userId==null || userId.equals("")){
			throw new CyDecoroUrbanoException("UserId not configured !");
		}
		
		try {
			
			Calendar calendar=Calendar.getInstance();
			Date now10=new Date(calendar.getTime().getTime()-1000*60*60*24*10);
			logger.info("Date rif="+now10.toString());

			
			URL url = new URL(pugliaEventiXmlUrl);
			URLConnection urlConnection=url.openConnection();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			
			//InputStream in = new FileInputStream("/home/antonio/Scaricati/reporteventi.xml");
			
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder=factory.newDocumentBuilder();
			Document doc = docBuilder.parse( in );
			doc.getDocumentElement().normalize();
			
			
			List<Evento> eventi=new ArrayList<Evento>();
			Map <String,LatLong> locMap=new LinkedHashMap<String,LatLong>();
			
			NodeList itemElements = doc.getElementsByTagName("Table1");
			for (int i = 0; i < itemElements.getLength(); i++) {
				Node itemElement = itemElements.item(i);
				NodeList childNodes=itemElement.getChildNodes();
				Evento evento=new Evento();
				
				boolean locationFound=false;
				
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node node = childNodes.item(j);
					
					if (node.getNodeName().equalsIgnoreCase("nome")){
						evento.setNome(CyDecoroUrbanoUtility.unescapeHTML(node.getTextContent(),0).trim());
					}
					
					if (node.getNodeName().equalsIgnoreCase("descrizione")){
						String description=node.getTextContent().trim();
						if (description.length()>500)
							description=description.substring(0, 500) +"[...]";
						evento.setDescription(CyDecoroUrbanoUtility.unescapeHTML(description,0));
					}
					
					if (node.getNodeName().equalsIgnoreCase("tipologia")){
						evento.setTipologia(CyDecoroUrbanoUtility.unescapeHTML(node.getTextContent(),0).trim());
					}

					if (node.getNodeName().equalsIgnoreCase("tipoticket")){
						evento.setTicket(CyDecoroUrbanoUtility.unescapeHTML(node.getTextContent(),0).trim());
					}
					
					
					if (node.getNodeName().equalsIgnoreCase("comune")){
						evento.setComune(CyDecoroUrbanoUtility.unescapeHTML(node.getTextContent(),0).trim());
						logger.info("find location for <"+evento.getComune()+">");
						
						LatLong latLong=null;
						
						if (locMap.containsKey(evento.getComune()))
							latLong=locMap.get(evento.getComune());
						else
							{
								logger.info("isn't in map");
								latLong=getLocation(evento.getComune());
								if (latLong==null){
									
									try {
										logger.info("Retry after 2 second....");
										Thread.sleep(2000);
										latLong=getLocation(evento.getComune());
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										logger.error(e.getMessage());
										throw new CyDecoroUrbanoException(e); 
									}
								}
							}
						
						logger.info("location="+latLong);
						if (latLong!=null){
							evento.setLatitude(latLong.getLatitude());
							evento.setLongitude(latLong.getLongitude());
							locationFound=true;
							
							locMap.put(evento.getComune(), latLong);
						}
						
					} // end comune
					
					if (node.getNodeName().equalsIgnoreCase("dataInizio")){
						evento.setDataInizio(node.getTextContent().trim());
					}
					if (node.getNodeName().equalsIgnoreCase("dataFine")){
						evento.setDataFine(node.getTextContent().trim());
					}
					if (node.getNodeName().equalsIgnoreCase("tel")){
						evento.setTelefono(node.getTextContent().trim());
					}
					if (node.getNodeName().equalsIgnoreCase("email")){
						evento.setEmail(node.getTextContent().trim());
					}
					if (node.getNodeName().equalsIgnoreCase("web")){
						evento.setWeb(node.getTextContent().trim());
					}
					
					
					if (evento.getNome().length()>50) evento.setNome(evento.getNome().substring(0, 50));
					String bundle=evento.getComune();
					if (evento.getDataFine()==null || evento.getDataFine().equals("") || evento.getDataInizio().equals(evento.getDataFine()))
							bundle+=", dal "+evento.getDataInizio()+" al "+evento.getDataFine()+";\n";
					else
							bundle+=", il "+evento.getDataInizio()+";\n";
					
					if (evento.getTipologia()!=null && !evento.getTipologia().equals(""))
						bundle+="tipologia: "+evento.getTipologia()+";\n";
					if (evento.getTicket()!=null && !evento.getTicket().equals(""))
						bundle+="ticket: "+evento.getTicket()+";\n";
					if (evento.getTelefono()!=null && !evento.getTelefono().equals(""))
						bundle+="telefono: "+evento.getTelefono()+";\n";
					if (evento.getEmail()!=null && !evento.getEmail().equals(""))
						bundle+="email: "+evento.getEmail()+";\n\n";
					if (evento.getWeb()!=null && !evento.getWeb().equals(""))
						bundle+=evento.getWeb()+";\n\n";
					bundle+=evento.getDescription();
					
					evento.setBundleDescription(bundle);
					evento.setUserId(Long.parseLong(userId));
					
				}
				
				if (locationFound){
					// check date
					String sDataEnd=null;
					if (evento.getDataFine()==null || evento.getDataFine().equals(""))
						sDataEnd=evento.getDataInizio();
					else
						sDataEnd=evento.getDataInizio();
					sDataEnd=changeMonth(sDataEnd);
					
					Date dataEnd=null;
					try {
						dataEnd=CyDecoroUrbanoUtility.tryStringToDateDDMMYY(changeMonth(sDataEnd));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						logger.error(e.toString());
					}
					if (dataEnd!=null && !dataEnd.before(now10))
						eventi.add(evento);
					else
						logger.warn(evento.getNome()+" jumped for date "+sDataEnd+":"+
								CyDecoroUrbanoUtility.dateToString(dataEnd, "dd/MM/yyyy"));
					}
				else
					logger.warn(evento.getNome()+" jumped for location");
			}
			
			in.close();
			
			
			List<Guid> guids=pugliaEventiDao.getAllGuid();
			Map <String,Guid> guidMap=new LinkedHashMap<String,Guid>(); 
			for(Guid g:guids){
				guidMap.put(g.getGuid(),g);
			}
			logger.info("GuidMap size="+guidMap.size());
			
			pugliaEventiDao.deleteGuid();
			
			for (Evento evento:eventi){
				logger.info("Elab evento:"+evento.getNome());
				String key=evento.getNome()+evento.getDataInizio()+(evento.getDataFine()==null?"":evento.getDataFine());
				if (guidMap.containsKey(key)){
						Guid guid=guidMap.get(key);
						pugliaEventiDao.update(guid.getLocationId(),evento);
						logger.info("<-- update...");
					}
				else
					{
						pugliaEventiDao.add(evento);
						logger.info("<-- insert...");
					}
			}
			
			pugliaEventiDao.delete(Long.parseLong(userId));
	
		} catch (IOException | ParserConfigurationException | SAXException /*| ParseException*/ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.toString());
			throw new CyDecoroUrbanoException(e);
		}
		
	}
	
	private String changeMonth(String date){
		return date
			.replaceFirst("gen", "01").replaceFirst("GEN", "01").replaceFirst("Gen", "01")
			.replaceFirst("feb", "02").replaceFirst("FEB", "02").replaceFirst("Feb", "02")
			.replaceFirst("mar", "03").replaceFirst("MAR", "03").replaceFirst("Mar", "03")
			.replaceFirst("apr", "04").replaceFirst("APR", "04").replaceFirst("Apr", "04")
			.replaceFirst("mag", "05").replaceFirst("MAG", "05").replaceFirst("Mag", "05")
			.replaceFirst("giu", "06").replaceFirst("GIU", "06").replaceFirst("Giu", "06")
			.replaceFirst("lug", "07").replaceFirst("LUG", "07").replaceFirst("Lug", "07")
			.replaceFirst("ago", "08").replaceFirst("AGO", "08").replaceFirst("Ago", "08")
			.replaceFirst("set", "09").replaceFirst("SET", "09").replaceFirst("Set", "09")
			.replaceFirst("ott", "10").replaceFirst("OTT", "10").replaceFirst("Ott", "10")
			.replaceFirst("nov", "11").replaceFirst("NOV", "11").replaceFirst("Nov", "11")
			.replaceFirst("dic", "12").replaceFirst("DIC", "12").replaceFirst("Dic", "12");
	}
	
	
	private LatLong getLocation(String address) 
		throws CyDecoroUrbanoException
	{
		LatLong latLong=null;
		
		String response=null;
		try {
			response=CyDecoroUrbanoUtility.httpGet("http://maps.googleapis.com/maps/api/geocode/json?address="
					+URLEncoder.encode(address, "UTF-8"), 
					null);
		} catch (CyDecoroUrbanoException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			logger.error("response="+response);
			throw new CyDecoroUrbanoException(e);
		}
		
		GeoLocation geoLocation =null; 
		try{
			geoLocation=	new Gson().fromJson(response, GeoLocation.class);
		}
		catch(Exception e){
			logger.error(e.toString());
			logger.error("response="+response);
			throw new CyDecoroUrbanoException(e);
		}
		
		if (geoLocation!=null && geoLocation.getStatus()!=null 
			&& geoLocation.getStatus().equals("OK") && geoLocation.getResults()!=null 
			&& !geoLocation.getResults().isEmpty()
			)
			if (geoLocation.getResults().get(0).getGeometry()!=null
				&& geoLocation.getResults().get(0).getGeometry().getLocation()!=null){
					latLong=new LatLong(geoLocation.getResults().get(0).getGeometry().getLocation().getLat(),
								geoLocation.getResults().get(0).getGeometry().getLocation().getLng()
							);
				}
		
		return latLong;
	}
	

	private class LatLong{
		
		public LatLong(double latitude,double longitude){
			this.latitude=latitude;
			this.longitude=longitude;
		}
		
		private double latitude;
		private double longitude;
		public double getLatitude() {
			return latitude;
		}
		
		public double getLongitude() {
			return longitude;
		}
		
		@Override
		public String toString() {
			return "LatLong [latitude=" + latitude + ", longitude=" + longitude + "]";
		}
		
	}
	
	

}
