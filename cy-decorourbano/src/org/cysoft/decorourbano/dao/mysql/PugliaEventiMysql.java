package org.cysoft.decorourbano.dao.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.cysoft.decorourbano.common.CyDecoroUrbanoDataSource;
import org.cysoft.decorourbano.common.CyDecoroUrbanoException;
import org.cysoft.decorourbano.dao.PugliaEventiDao;
import org.cysoft.decorourbano.model.Evento;
import org.cysoft.decorourbano.model.Guid;
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

public class PugliaEventiMysql implements PugliaEventiDao{
	
	private static final Logger logger = LoggerFactory.getLogger(PugliaEventiMysql.class);
	
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
		logger.info("PugliaEventiMysql.getAllGuid() >>>");
		
		String query="select DU_N_TICKET_ID,DU_N_LOCATION_ID,DU_S_GUID from BSST_DU_GUID where DU_N_TICKET_ID=0";
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
		
		logger.info("PugliaEventiMysql.getAllGuid() <<<");
		
		return ret;
	}

	@Override
	public void deleteGuid() throws CyDecoroUrbanoException {
		// TODO Auto-generated method stub
		logger.info("DecoroUrbanoMysql.deleteGuid() >>>");
		
		String cmd="delete from BSST_DU_GUID where DU_N_TICKET_ID=0";
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
	public void add(final Evento evento) throws CyDecoroUrbanoException {
		// TODO Auto-generated method stub
		TransactionTemplate txTemplate=new TransactionTemplate(tx);
		txTemplate.execute(new TransactionCallbackWithoutResult(){

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus txStatus) {
				// TODO Auto-generated method stub
				
				try {
					String cmd="insert into BSST_LOC_LOCATION(LOC_S_NAME,LOC_D_CREATION_DATE,LOC_S_DESC,LOC_S_TYPE,LOC_D_LAT,LOC_D_LNG,USR_N_USER_ID)";
					cmd+=" values ";
					cmd+=" (?,now(),?,?,?,?,?)";
					
					JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
					
					jdbcTemplate.update(cmd, new Object[]{
							evento.getNome(),evento.getBundleDescription(),"EventLocation",
							evento.getLatitude(),evento.getLongitude(),
							evento.getUserId()
					});
				
					long locationId=getLastInsertId(jdbcTemplate);
					
					String gUid=evento.getNome()+evento.getDataInizio()+(evento.getDataFine()==null?"":evento.getDataFine());
					
					cmd="insert into BSST_DU_GUID(DU_N_TICKET_ID,DU_N_LOCATION_ID,DU_S_GUID) values (?,?,?)";
					jdbcTemplate.update(cmd, new Object[]{
							0,
							locationId,
							gUid
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
	public void update(final long locationId, final Evento evento) throws CyDecoroUrbanoException {
		// TODO Auto-generated method stub
		TransactionTemplate txTemplate=new TransactionTemplate(tx);
		txTemplate.execute(new TransactionCallbackWithoutResult(){

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus txStatus) {
				// TODO Auto-generated method stub
				
				try {
					
					String cmd="update BSST_LOC_LOCATION set LOC_S_NAME=?,LOC_S_DESC=?,LOC_D_LAT=?,LOC_D_LNG=? where LOC_N_LOCATION_ID=?";
					
					JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
					jdbcTemplate.update(cmd, new Object[]{
							evento.getNome(),evento.getBundleDescription(),evento.getLatitude(),evento.getLongitude(),locationId
						});
					
					
					String gUid=evento.getNome()+evento.getDataInizio()+(evento.getDataFine()==null?"":evento.getDataFine());
					
					cmd="insert into BSST_DU_GUID(DU_N_TICKET_ID,DU_N_LOCATION_ID,DU_S_GUID) values (?,?,?)";
					jdbcTemplate.update(cmd, new Object[]{
							0,
							locationId,
							gUid
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
				
				JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
				
				String cmd="delete from BSST_LOC_LOCATION where USR_N_USER_ID=? and LOC_S_TYPE=? and LOC_N_LOCATION_ID not in (select DU_N_LOCATION_ID from BSST_DU_GUID)";
				
				logger.info(cmd+"["+userId+"]");
				jdbcTemplate.update(cmd, new Object[]{
					userId,"EventLocation"
				});
			
			}

		});

	}
}
