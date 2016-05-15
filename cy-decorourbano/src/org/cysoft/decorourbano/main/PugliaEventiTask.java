package org.cysoft.decorourbano.main;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
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
			
			URL url = new URL(pugliaEventiXmlUrl);
			URLConnection urlConnection=url.openConnection();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			
			//InputStream in = new FileInputStream("/home/antonio/Scaricati/reporteventi.xml");
			
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder=factory.newDocumentBuilder();
			Document doc = docBuilder.parse( in );
			doc.getDocumentElement().normalize();
			
			
			List<Evento> eventi=new ArrayList<Evento>();
			
			NodeList itemElements = doc.getElementsByTagName("Table1");
			for (int i = 0; i < itemElements.getLength(); i++) {
				Node itemElement = itemElements.item(i);
				NodeList childNodes=itemElement.getChildNodes();
				Evento evento=new Evento();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node node = childNodes.item(j);
					
					if (node.getNodeName().equalsIgnoreCase("nome")){
						evento.setNome(CyDecoroUrbanoUtility.unescapeHTML(node.getTextContent(),0).trim());
					}
					
					if (node.getNodeName().equalsIgnoreCase("abstract")){
						evento.setDescription(CyDecoroUrbanoUtility.unescapeHTML(node.getTextContent(),0).trim());
					}

					if (node.getNodeName().equalsIgnoreCase("comune")){
						evento.setComune(CyDecoroUrbanoUtility.unescapeHTML(node.getTextContent(),0).trim());
						
						String response=null;
						try {
							response=CyDecoroUrbanoUtility.httpGet("http://maps.googleapis.com/maps/api/geocode/json?address="
									+URLEncoder.encode(evento.getComune(), "UTF-8"), 
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
									evento.setLatitude(geoLocation.getResults().get(0).
											getGeometry().getLocation().getLat());
									evento.setLongitude(geoLocation.getResults().get(0).
											getGeometry().getLocation().getLng());
									
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
							bundle+=", il "+evento.getDataInizio()+"\n";
					
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
				eventi.add(evento);
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

	

}
