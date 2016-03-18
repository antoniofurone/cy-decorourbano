package org.cysoft.decorourbano.main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cysoft.decorourbano.common.CyDecoroUrbanoException;
import org.cysoft.decorourbano.common.CyDecoroUrbanoUtility;
import org.cysoft.decorourbano.dao.DecoroUrbanoDao;
import org.cysoft.decorourbano.model.Guid;
import org.cysoft.decorourbano.model.IDecoroUrbanoConst;
import org.cysoft.decorourbano.model.Segnalazione;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DecoroUrbanoTask {

	private static final Logger logger = LoggerFactory.getLogger(DecoroUrbanoTask.class);
	
	
	@Autowired
	Environment environment;
	
	@Autowired
	DecoroUrbanoDao decoroUrbanoDao;
	
	
	public void exec() throws CyDecoroUrbanoException{
		
		String geoRssURL=environment.getProperty("du.geoRssURL");
		if (geoRssURL==null || geoRssURL.equals("")){
			throw new CyDecoroUrbanoException("GeoRssURL not configured !");
		}
		logger.info("GeoRssUrl="+geoRssURL);
		
		String userId=environment.getProperty("du.userId");
		if (userId==null || userId.equals("")){
			throw new CyDecoroUrbanoException("UserId not configured !");
		}
		
		String sDateStart=environment.getProperty("du.dateStart");
		if (sDateStart==null || sDateStart.equals("")){
			throw new CyDecoroUrbanoException("DateStart not configured !");
		}
		
		
		try {
			Date dateStart=CyDecoroUrbanoUtility.tryStringToDate(sDateStart);
			
			//URL url = new URL(geoRssURL);
			//URLConnection urlConnection=url.openConnection();
			//InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			
			InputStream in = new FileInputStream("C:/Users/ns293854/Downloads/carovigno.rss");
			
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder=factory.newDocumentBuilder();
			Document doc = docBuilder.parse( in );
			doc.getDocumentElement().normalize();
			
			List<Segnalazione> segns=new ArrayList<Segnalazione>();
			
			NodeList itemElements = doc.getElementsByTagName("item");
			for (int i = 0; i < itemElements.getLength(); i++) {
				Node itemElement = itemElements.item(i);
				NodeList childNodes=itemElement.getChildNodes();
				
				Segnalazione segn=new Segnalazione();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node node = childNodes.item(j);
					
					if (node.getNodeName().equalsIgnoreCase("guid"))
						segn.setGuid(node.getTextContent());	
					
					if (node.getNodeName().equalsIgnoreCase("title"))
						segn.setTitle(node.getTextContent());	
					
					if (node.getNodeName().equalsIgnoreCase("pubDate"))
						segn.setPubDate(node.getTextContent());	
					
					if (node.getNodeName().equalsIgnoreCase("description"))
						segn.setDescription(node.getTextContent());	
					
					if (node.getNodeName().equalsIgnoreCase("point"))
						segn.setPoint(node.getTextContent());	
					
					if (node.getNodeName().equalsIgnoreCase("link"))
						segn.setLink(node.getTextContent());	
					
					if (node.getNodeName().equalsIgnoreCase("enclosure")){
						if (node.getAttributes()!=null && node.getAttributes().getNamedItem("url")!=null)
							segn.setEnclosureUrl(node.getAttributes().getNamedItem("url").getTextContent());	
					}
				
					if (node.getNodeName().equalsIgnoreCase("category")){
						String category=node.getTextContent();
						if (category.equalsIgnoreCase(IDecoroUrbanoConst.STATUS_IN_ATTESA)||
							category.equalsIgnoreCase(IDecoroUrbanoConst.STATUS_IN_CARICO)||
							category.equalsIgnoreCase(IDecoroUrbanoConst.STATUS_RISOLTA))
							segn.setStatus(category);
						else
							segn.setCategory(category);
					}
						
					
				}
				
				DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",Locale.US);
				Date parsedDate=formatter.parse(segn.getPubDate());
				
				segn.setParsedPubDate(parsedDate);
				segn.setUserId(Long.parseLong(userId));
				
				StringTokenizer st = new StringTokenizer(segn.getPoint());
				if (st.hasMoreTokens())
					segn.setLatitude(Double.parseDouble(st.nextToken()));
				if (st.hasMoreTokens())
					segn.setLongitude(Double.parseDouble(st.nextToken()));
				
				if (!segn.getParsedPubDate().before(dateStart)){
					
					String head=segn.getTitle()+" - "+segn.getDescription()+" ";
					String status="\nCategoria: "+segn.getCategory()+" - Stato: "+segn.getStatus();
					String link="\nLink: "+segn.getLink()+" ";
					String foto="";
					if (segn.getEnclosureUrl()!=null && !segn.equals(""))
						foto+="\nFoto: "+segn.getEnclosureUrl()+" ";
					
					String text=head+status+link+foto;
					if (text.length()>500){
						text=head+status+link;
						if (text.length()>500)
							text=head.substring(0, 500-((status+link).length())-3)+"..."+status+link;
					}
						
					segn.setText(text);
					
					segn.setStatusId(getStatusId(segn.getStatus()));
					segn.setCategoryId(getCategoryId(segn.getCategory()));
					
					segns.add(segn);
				}
			}
			
			in.close();
			
			List<Guid> guids=decoroUrbanoDao.getAllGuid();
			Map <String,Guid> guidMap=new LinkedHashMap<String,Guid>(); 
			for(Guid g:guids){
				guidMap.put(g.getGuid(),g);
			}
			logger.info("GuidMap size="+guidMap.size());
			
			decoroUrbanoDao.deleteGuid();
			
			for (Segnalazione segn:segns){
				if (guidMap.containsKey(segn.getGuid())){
					// update
				}
				else
				{
					// add
					decoroUrbanoDao.add(segn);
				}
			}
			
		} catch (IOException | ParserConfigurationException | SAXException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.toString());
			throw new CyDecoroUrbanoException(e);
		}
		
	}
	
	
	private long getStatusId(String status){
		long ret=1;
		if (status.equalsIgnoreCase(IDecoroUrbanoConst.STATUS_IN_CARICO))
			ret=2;
		if (status.equalsIgnoreCase(IDecoroUrbanoConst.STATUS_RISOLTA))
			ret=2;
		return ret;
	}

	private long getCategoryId(String category){
		long ret=4;
		if (category.equalsIgnoreCase(IDecoroUrbanoConst.CATEGORY_STRADE))
			ret=1;
		if (category.equalsIgnoreCase(IDecoroUrbanoConst.CATEGORY_RIFIUTI))
			ret=2;
		return ret;
	}
	
}
