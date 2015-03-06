package br.com.falbuquerque.bytecodemanipulation.parser;

/**
 * Exception thrown when the program can't find a required class.
 * 
 * @author Felipe Albuquerque
 */
public class ClassNotFoundException extends Exception {

	private static final long serialVersionUID = 7184808888792414373L;

	/**
	 * Builds the exception.
	 */
	public ClassNotFoundException() {
		super();
	}

	/**
	 * Builds the exception.
	 * 
	 * @param message
	 *            the message of the exception
	 */
	public ClassNotFoundException(String message) {
		super(message);
	}

}
