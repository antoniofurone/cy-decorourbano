package org.cysoft.decorourbano.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.cysoft.decorourbano.main.DecoroUrbanoMain;
import org.cysoft.decorourbano.model.IDecoroUrbanoConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CyDecoroUrbanoUtility {
	
	private static final Logger logger = LoggerFactory.getLogger(DecoroUrbanoMain.class);
	
	private static SecureRandom random=new SecureRandom();
	
	public static String genToken(String postFix){
		return new BigInteger(130,random).toString(32)+"-"+postFix;
	}
	
	public static Locale getLocale(String localeCode){
		
		if (localeCode.equalsIgnoreCase(IDecoroUrbanoConst.LOCALE_IT))
			return Locale.ITALIAN;
		else
			return Locale.ENGLISH;
	}

	public static String toCamelCase(String s){
		 String[] parts = s.replaceAll("\\s+", " ").split(" ");
		 String camelCaseString = "";
		 for (String part : parts){
			 	camelCaseString = camelCaseString + (!camelCaseString.equals("")?" ":"")+ toProperCase(part);
		   }
		 return camelCaseString;
		}

	public static String toProperCase(String s) {
		 return s.substring(0, 1).toUpperCase() +
		        s.substring(1).toLowerCase();
	}
	
	   public static final String DATE_ddsMMsyyyy="dd/MM/yyyy";
	   public static final String DATE_yyyy_MM_dd="yyyy-MM-dd";
	   public static final String DATE_yyyy_MM_dd_HH_mm_ss="yyyy-MM-dd HH:mm:ss";
	   
	   public static Date stringToDate(String date,String fmt) throws java.text.ParseException{
		   DateFormat datefmt = new SimpleDateFormat(fmt);
		   return datefmt.parse(date);
	   }
		
	   public static String dateToString(Date date,String fmt) {
		   DateFormat datefmt = new SimpleDateFormat(fmt);
		   return datefmt.format(date);	  
	   }
	   
	   public static Date tryStringToDate(String date) 
			   throws java.text.ParseException{
		   Date dt=null;
		   try {
			dt=stringToDate(date,DATE_ddsMMsyyyy);
		   } catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			   try {
				dt=stringToDate(date,DATE_yyyy_MM_dd);
			} catch (java.text.ParseException e1) {
				// TODO Auto-generated catch block
				throw e1;
			}
		   }
		   return dt;
	   }
	   
	   
	   public static String dateChangeFormat(String sDate,String fmtOut) throws java.text.ParseException{
		   return dateToString(tryStringToDate(sDate),fmtOut);
	   }

	   
	   public static String byteArrayToHex(byte[] a) {
		   StringBuilder sb = new StringBuilder(a.length * 2);
		   for(byte b: a)
		      sb.append(String.format("%02x", b & 0xff));
		   return sb.toString();
		}


	   public static final String unescapeHTML(String s, int f){  
	        String [][] escape =  
	         {{  "&lt;"     , "<" } ,  
	          {  "&gt;"     , ">" } ,  
	          {  "&amp;"    , "&" } ,  
	          {  "&quot;"   , "\"" } ,  
	          {  "&aacute;"  , "á" } ,  
	          {  "&Aacute;"  , "Á" } ,  
	          {  "&eacute;"  , "é" } ,  
	          {  "&Eacute;"  , "É" } ,  
	          {  "&iacute;"  , "í" } ,  
	          {  "&Iacute;"  , "Í" } ,  
	          {  "&oacute;"  , "ó" } ,  
	          {  "&Oacute;"  , "Ó" } ,  
	          {  "&uacute;"  , "ú" } ,  
	          {  "&Uacute;"  , "Ú" } ,  
	          {  "&Ntilde;"  , "ñ" } ,  
	          {  "&Ntilde;"  , "Ñ" } ,  
	          {  "&apos;"   , "'" } ,  
	          {  "&deg;;"   , "º" } ,  
	          {  "&agrave;" , "à" } ,  
	          {  "&Agrave;" , "À" } ,  
	          {  "&acirc;"  , "â" } ,  
	          {  "&auml;"   , "ä" } ,  
	          {  "&Auml;"   , "Ä" } ,  
	          {  "&Acirc;"  , "Â" } ,  
	          {  "&aring;"  , "å" } ,  
	          {  "&Aring;"  , "Å" } ,   
	          {  "&aelig;"  , "æ" } ,   
	          {  "&AElig;"  , "Æ" } ,  
	          {  "&ccedil;" , "ç" } ,  
	          {  "&Ccedil;" , "Ç" } ,  
	          {  "&eacute;" , "é" } ,  
	          {  "&Eacute;" , "É" } ,  
	          {  "&egrave;" , "è" } ,  
	          {  "&Egrave;" , "È" } ,  
	          {  "&ecirc;"  , "ê" } ,  
	          {  "&Ecirc;"  , "Ê" } ,  
	          {  "&euml;"   , "ë" } ,  
	          {  "&Euml;"   , "Ë" } ,  
	          {  "&iuml;"   , "ï" } ,   
	          {  "&Iuml;"   , "Ï" } ,  
	          {  "&ocirc;"  , "ô" } ,  
	          {  "&Ocirc;"  , "Ô" } ,  
	          {  "&ouml;"   , "ö" } ,  
	          {  "&Ouml;"   , "Ö" } ,  
	          {  "&oslash;" , "ø" } ,  
	          {  "&Oslash;" , "Ø" } ,  
	          {  "&szlig;"  , "ß" } ,  
	          {  "&ugrave;" , "ù" } ,  
	          {  "&Ugrave;" , "Ù" } ,  
	          {  "&ucirc;"  , "û" } ,  
	          {  "&Ucirc;"  , "Û" } ,   
	          {  "&uuml;"   , "ü" } ,  
	          {  "&Uuml;"   , "Ü" } ,  
	          {  "&nbsp;"   , " " } ,  
	          {  "&reg;"    , "\u00a9" } ,  
	          {  "&copy;"   , "\u00ae" } ,  
	          {  "&euro;"   , "\u20a0" } ,
	          {  "&#224"   , "à" }, 
	          {  "&#34"   , "\"" }, 
	          {  "&#242"   , "ò" },
	          {  "&#176"   , "°" },
	          {  "&#232"   , "è" },
	          {  "&#38"   , "&" },
	          {  "&#224;"   , "à" }, 
	          {  "&#34;"   , "\"" }, 
	          {  "&#242;"   , "ò" },
	          {  "&#176;"   , "°" },
	          {  "&#232;"   , "è" },
	          {  "&#38;"   , "&" },
	          {  "&#8220", "“"},
	          {  "&#8220;", "“"},
	          {  "&#8221", "”"},
	          {  "&#8221;", "”"},
	          {  "&#8217", "’"},
	          {  "&#8217;", "’"},
	          {  "&#233", "é"},
	          {  "&#233;", "é"},
	          {  "&#235", "ë"},
	          {  "&#235;", "ë"},
	          {  "&#225", "á"},
	          {  "&#225;", "á"},
	          {  "&#250", "ú"},
	          {  "&#250;", "ú"},
	          {  "&#243", "ó"},
	          {  "&#243;", "ó"},
	          {  "&#249", "ù"},
	          {  "&#249;", "ù"},
	          {  "&#10", "\r"},
	          {  "&#10;", "\r"},
	          {  "&#13", "\n"},
	          {  "&#13;", "\n"},
	          {  "&#35", "#"},
	          {  "&#35;", "#"},
	          {  "&#231", "ç"},
	          {  "&#231;", "ç"},
	          {  "&#228", "ä"},
	          {  "&#228;", "ä"}
	       	  
	         };  
	         int i, j, k;  
	           
	         i = s.indexOf("&", f);  
	         if (i > -1) {  
	            j = s.indexOf(";" ,i);
	            if (j==-1){
	            	 j = s.indexOf(" " ,i);
	            	 if (j!=-1)
	            		 j--;
	            }
		        // --------  
	            // we don't start from the beginning   
	            // the next time, to handle the case of  
	            // the &  
	            // thanks to Pieter Hertogh for the bug fix!  
	            f = i + 1;  
	            // --------  
	            if (j > i) {  
	               // ok this is not most optimized way to  
	               // do it, a StringBuffer would be better,  
	               // this is left as an exercise to the reader!  
	               String temp = s.substring(i , j + 1);  
	               // search in escape[][] if temp is there  
	               k = 0;  
	               while (k < escape.length) {  
	                 if (escape[k][0].equals(temp)) break;  
	                 else k++;  
	                 }  
	               if (k < escape.length) {  
	                 s = s.substring(0 , i) + escape[k][1] + s.substring(j + 1);  
	                 return unescapeHTML(s, f); // recursive call  
	                 }  
	               }  
	            }     
	         return s;  
	         }  

	    
	   public static String httpGet(String url,List<NameValuePair> headerAttrs) 
				throws CyDecoroUrbanoException{
			String ret="";
			
			HttpClient httpclient = HttpClients.createDefault();
			URI uri=null;
			try {
				uri = new URI(url);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e.toString());
				throw new CyDecoroUrbanoException(e);
			}
			
			
			HttpGet getRequest = new HttpGet(uri);
			
			if (headerAttrs!=null)
				for(NameValuePair attr:headerAttrs)
					getRequest.setHeader(attr.getName(), attr.getValue());
			
			
			HttpResponse response=null;
			try {
				response = httpclient.execute(getRequest);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e.toString());
				throw new CyDecoroUrbanoException(e);
			}

			if (response.getStatusLine().getStatusCode() != 200) {
				logger.error(response.toString());
				throw new CyDecoroUrbanoException("Failed : HTTP error code : "
				   + response.getStatusLine().getStatusCode());
			}
			
			HttpEntity entity = response.getEntity();

			if (entity != null) {
			    InputStream instream;
				
				try {
					instream = entity.getContent();
				} catch (IllegalStateException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error(e.toString());
					throw new CyDecoroUrbanoException(e);
				}
				
			    try {
			        // do something useful
			    	ret = getStringFromInputStream(instream);
			 
			    } finally {
			        try {
						instream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						logger.error(e.toString());
						throw new CyDecoroUrbanoException(e);
					}
			    }
			}
			
			return ret;
		}

	    
	   private static String getStringFromInputStream(InputStream is) {

			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();

			String line;
			try {

				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return sb.toString();

		}
	    
}
