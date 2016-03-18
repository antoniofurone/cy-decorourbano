package org.cysoft.decorourbano.main;

import org.cysoft.decorourbano.common.CyDecoroUrbanoDataSource;
import org.cysoft.decorourbano.common.CyDecoroUrbanoException;
import org.cysoft.decorourbano.dao.DecoroUrbanoDao;
import org.cysoft.decorourbano.dao.mysql.DecoroUrbanoMysql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@PropertySource("classpath:cy-decorourbano.properties")
@EnableScheduling
public class DecoroUrbanoMain {
	
	private static final Logger logger = LoggerFactory.getLogger(DecoroUrbanoMain.class);
	

	@Autowired
	Environment environment;
	
	@Autowired
	DecoroUrbanoTask decoroUrbanoTask;
	
	@Bean
	@Description("DecoroUrbano Dao")
	public DecoroUrbanoDao decoroUrbanoDao(){
		DecoroUrbanoDao decoroUrbanoDao=new DecoroUrbanoMysql();
		return decoroUrbanoDao;
	}
	
	@Bean
	@Description("Decoro Urbano Task")
	public DecoroUrbanoTask decoroUrbanoTask(){
		DecoroUrbanoTask decoroUrbanoTask=new DecoroUrbanoTask();
		return decoroUrbanoTask;
	 }
	
	
	@Bean
	@Description("MySql Data Source Rest ")
	public CyDecoroUrbanoDataSource mySqlDS() {
		 
		 logger.info("DecoroUrbanoMain.mySqlDS() >>>");
		
		 CyDecoroUrbanoDataSource mySqlDs = new CyDecoroUrbanoDataSource();
		 String driver=environment.getProperty("mysql.driver");
		 logger.info("mysql.driver="+driver);
		 String url=environment.getProperty("mysql.url");
		 logger.info("mysql.url="+url);
		 String user=environment.getProperty("mysql.user");
		 logger.info("mysql.user="+user);
		 String psw=environment.getProperty("mysql.psw");
		 
		 mySqlDs.setDriverClassName(driver);
	     mySqlDs.setUrl(url);
	     mySqlDs.setUsername(user);
         mySqlDs.setPassword(psw);
	     
        
		 logger.info("DecoroUrbanoMain.mySqlDS() <<<");
	     
		 return mySqlDs;
	 }
	
	 @Bean
	 @Description("Transaction Manager Rest ")
	 public DataSourceTransactionManager transactionManager() {
		 logger.info("DecoroUrbanoMain.transactionManager() >>>");
		 DataSourceTransactionManager transactionManager=new DataSourceTransactionManager(mySqlDS());
		 logger.info("DecoroUrbanoMain.transactionManager() <<<");
 		 return transactionManager;
	 }
	
	
	@Scheduled(fixedDelay=3600000)
	 public void execTask() {
	     // something that should execute periodically
		 logger.info("DecoroUrbanoMain.execTask() >>>");
		 try {
			decoroUrbanoTask.exec();
		} catch (CyDecoroUrbanoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.toString());
		}
		 logger.info("DecoroUrbanoMain.execTask() <<<");
		 
	 }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.setProperty("http.proxyHost", "10.29.176.1");
		System.setProperty("http.proxyPort", "8080");
		
		logger.info("Start Decoro Urbano ...");
    	SpringApplication.run(DecoroUrbanoMain.class, args);
    
	}

}
