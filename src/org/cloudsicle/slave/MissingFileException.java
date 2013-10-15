package org.cloudsicle.slave;

public class MissingFileException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MissingFileException(String s){
		super(s);
	}
}
