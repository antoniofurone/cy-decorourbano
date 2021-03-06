package org.cysoft.decorourbano.dao.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.cysoft.decorourbano.common.CyDecoroUrbanoDataSource;
import org.cysoft.decorourbano.common.CyDecoroUrbanoException;
import org.cysoft.decorourbano.common.CyDecoroUrbanoUtility;
import org.cysoft.decorourbano.dao.DecoroUrbanoDao;
import org.cysoft.decorourbano.model.Guid;
import org.cysoft.decorourbano.model.Segnalazione;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class DecoroUrbanoMysql implements DecoroUrbanoDao{
	
	private static final Logger logger = LoggerFactory.getLogger(DecoroUrbanoMysql.class);
	
	protected DataSourceTransactionManager tx=null;
	@Autowired
	public void setTransactionManager(DataSourceTransactionManager tx){
			this.tx=tx;
	}
	
	protected CyDecoroUrbanoDataSource ds=null;
	@Autowired
	public void setMySqlDataSource(CyDecoroUrbanoDataSource ds){
			this.ds=ds;
	}
	
	protected Environment env;
	@Autowired
	public void setEnvironment(Environment env){
		this.env=env;
	}
	
	protected long getLastInsertId(JdbcTemplate jdbcTemplate){
		
		String query="SELECT LAST_INSERT_ID()";
		return jdbcTemplate.queryForObject(query, new Object[] { },new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            	return rs.getLong(1);
            }
        });
	}

	@Override
	public List<Guid> getAllGuid() throws CyDecoroUrbanoException {
		// TODO Auto-generated method stub
		logger.info("DecoroUrbanoMysql.getAllGuid() >>>");
		
		String query="select DU_N_TICKET_ID,DU_N_LOCATION_ID,DU_S_GUID from BSST_DU_GUID where DU_N_TICKET_ID<>0";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
		logger.info(query);
		
		List<Guid> ret =null;
		try {
		ret=jdbcTemplate.query(
                query, 
                new RowMapper<Guid>() {
                    @Override
                    public  Guid mapRow(ResultSet rs, int rowNum) throws SQLException {
                    	Guid guid=new Guid();
                    	guid.setGuid(rs.getString("DU_S_GUID"));
                        guid.setTicketId(rs.getLong("DU_N_TICKET_ID"));
                        guid.setLocationId(rs.getLong("DU_N_LOCATION_ID"));
                        return guid;
		            }
                });
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			throw new CyDecoroUrbanoException(e);
		}
		
		logger.info("DecoroUrbanoMysql.getAllGuid() <<<");
		
		return ret;
	}

	@Override
	public void deleteGuid() throws CyDecoroUrbanoException {
		// TODO Auto-generated method stub
		logger.info("DecoroUrbanoMysql.deleteGuid() >>>");
		
		String cmd="delete from BSST_DU_GUID where DU_N_TICKET_ID<>0";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
		logger.info(cmd);
		
		try {
			jdbcTemplate.update(cmd, new Object[]{
				});
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			throw new CyDecoroUrbanoException(e);
		} 
		
		logger.info("DecoroUrbanoMysql.deleteGuid() <<<");
	}

	@Override
	public void add(final Segnalazione segn) throws CyDecoroUrbanoException {
		// TODO Auto-generated method stub
		TransactionTemplate txTemplate=new TransactionTemplate(tx);
		txTemplate.execute(new TransactionCallbackWithoutResult(){

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus txStatus) {
				// TODO Auto-generated method stub
			
				String cmd="insert into BSST_TIC_TICKET(TIC_S_TEXT,TIC_D_CREATION_DATE,USR_N_USER_ID,TCA_N_CATEGORY_ID,TST_N_STATUS_ID)";
				cmd+=" values ";
				cmd+=" (?,?,?,?,?)";
				
				JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
				logger.info(cmd+"["+segn+"]");
				
				try {
					jdbcTemplate.update(cmd, new Object[]{
							segn.getText(), CyDecoroUrbanoUtility.dateToString(segn.getParsedPubDate(), 
							CyDecoroUrbanoUtility.DATE_yyyy_MM_dd_HH_mm_ss),
							segn.getUserId(),segn.getCategoryId(),segn.getStatusId()
						});
					
				long ticketId=getLastInsertId(jdbcTemplate);
				logger.info("ticketId="+ticketId);
				
				cmd="insert into BSST_LOC_LOCATION(LOC_S_NAME,LOC_D_CREATION_DATE,LOC_S_TYPE,LOC_D_LAT,LOC_D_LNG,USR_N_USER_ID)";
				cmd+=" values ";
				cmd+=" (?,now(),?,?,?,?)";
				
				jdbcTemplate.update(cmd, new Object[]{
						"Ticket #"+ticketId,"Ticket",
						segn.getLatitude(),segn.getLongitude(),
						segn.getUserId()
				});
			
				long locationId=getLastInsertId(jdbcTemplate);
				logger.info("locationId="+locationId);
				
				cmd="update BSST_TIC_TICKET set LOC_N_LOCATION_ID=? where TIC_N_TICKET_ID=?";
				jdbcTemplate.update(cmd, new Object[]{
						locationId,
						ticketId
				});
				
				
				cmd="insert into BSST_DU_GUID(DU_N_TICKET_ID,DU_N_LOCATION_ID,DU_S_GUID) values (?,?,?)";
				jdbcTemplate.update(cmd, new Object[]{
						ticketId,
						locationId,
						segn.getGuid()
				});
				
				} catch (DataAccessException e) {
					// TODO Auto-generated catch block
					logger.error(e.toString());
					throw new RuntimeException(e);
				}
			
			}

	});
		
	}

	@Override
	public void update(final long ticketId,final long locationId, final Segnalazione segn) throws CyDecoroUrbanoException {
		// TODO Auto-generated method stub
		TransactionTemplate txTemplate=new TransactionTemplate(tx);
		txTemplate.execute(new TransactionCallbackWithoutResult(){

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus txStatus) {
				// TODO Auto-generated method stub
				
				String cmd="update BSST_TIC_TICKET set TIC_S_TEXT=?,TIC_D_CREATION_DATE=?,USR_N_USER_ID=?,TCA_N_CATEGORY_ID=?,TST_N_STATUS_ID=? ";
				cmd+="where TIC_N_TICKET_ID=?";
				
				JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
				logger.info(cmd+"["+ticketId+","+locationId+","+segn+"]");
			
				try {
					jdbcTemplate.update(cmd, new Object[]{
							segn.getText(), CyDecoroUrbanoUtility.dateToString(segn.getParsedPubDate(), 
							CyDecoroUrbanoUtility.DATE_yyyy_MM_dd_HH_mm_ss),
							segn.getUserId(),segn.getCategoryId(),segn.getStatusId(),ticketId
						});
					
					
					cmd="update BSST_LOC_LOCATION set LOC_D_LAT=?,LOC_D_LNG=? where LOC_N_LOCATION_ID=?";
					jdbcTemplate.update(cmd, new Object[]{
							segn.getLatitude(),segn.getLongitude(),locationId
						});
					
					cmd="insert into BSST_DU_GUID(DU_N_TICKET_ID,DU_N_LOCATION_ID,DU_S_GUID) values (?,?,?)";
					jdbcTemplate.update(cmd, new Object[]{
							ticketId,
							locationId,
							segn.getGuid()
					});
					
				} catch (DataAccessException e) {
					// TODO Auto-generated catch block
					logger.error(e.toString());
					throw new RuntimeException(e);
				}
			
			}

		});
		
	}

	@Override
	public void delete(final long userId) throws CyDecoroUrbanoException {
		// TODO Auto-generated method stub
		TransactionTemplate txTemplate=new TransactionTemplate(tx);
		txTemplate.execute(new TransactionCallbackWithoutResult(){

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus txStatus) {
				// TODO Auto-generated method stub
				String cmd="delete from BSST_TIC_TICKET where USR_N_USER_ID=? and TIC_N_TICKET_ID not in (select DU_N_TICKET_ID from BSST_DU_GUID)";
				
				JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
				logger.info(cmd+"["+userId+"]");
				jdbcTemplate.update(cmd, new Object[]{
					userId
				});
			
				cmd="delete from BSST_LOC_LOCATION where USR_N_USER_ID=? and LOC_S_TYPE=? and LOC_N_LOCATION_ID not in (select DU_N_LOCATION_ID from BSST_DU_GUID)";
				
				logger.info(cmd+"["+userId+"]");
				jdbcTemplate.update(cmd, new Object[]{
					userId,"Ticket"
				});
			
			}

		});

	}
}
