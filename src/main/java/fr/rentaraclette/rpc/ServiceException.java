package fr.rentaraclette.rpc;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiceException(String message) {
		super(message);
	}
	
}
