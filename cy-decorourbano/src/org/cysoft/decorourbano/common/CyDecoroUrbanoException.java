package org.cysoft.decorourbano.common;

public class CyDecoroUrbanoException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CyDecoroUrbanoException(Exception e){
		super(e);
	}

	public CyDecoroUrbanoException(String  msg){
		super(msg);
	}
	
}
