package org.cysoft.decorourbano.dao;

import java.util.List;

import org.cysoft.decorourbano.common.CyDecoroUrbanoException;
import org.cysoft.decorourbano.model.Guid;
import org.cysoft.decorourbano.model.Segnalazione;

public interface DecoroUrbanoDao {
	
	public List<Guid> getAllGuid() throws CyDecoroUrbanoException;
	public void deleteGuid() throws CyDecoroUrbanoException;
	
	public void add(Segnalazione segn) throws CyDecoroUrbanoException;
	public void update(long ticketId,Segnalazione segn) throws CyDecoroUrbanoException;
	public void delete(long userId) throws CyDecoroUrbanoException;
	
}