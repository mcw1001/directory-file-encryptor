package application;

public class GenException extends Exception {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	
	public GenException() {
	}
	
	public GenException(String message, Throwable throwable) {
	  super(message, throwable);
	}
}
