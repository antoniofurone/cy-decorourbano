package org.cysoft.decorourbano.dao;

import java.util.List;

import org.cysoft.decorourbano.common.CyDecoroUrbanoException;
import org.cysoft.decorourbano.model.Evento;
import org.cysoft.decorourbano.model.Guid;

public interface PugliaEventiDao {
	
	public List<Guid> getAllGuid() throws CyDecoroUrbanoException;
	public void deleteGuid() throws CyDecoroUrbanoException;
	
	public void add(Evento evento) throws CyDecoroUrbanoException;
	public void update(long locationId,Evento evento) throws CyDecoroUrbanoException;
	public void delete(long userId) throws CyDecoroUrbanoException;
	
}