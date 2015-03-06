
package br.com.falbuquerque.bytecodemanipulation.parser;

/**
 * Exception thrown when the an unexpected exception occurs during the execution of the program.
 * 
 * @author Felipe Albuquerque 
 */
public class UnexpectedException extends RuntimeException {

	private static final long serialVersionUID = 2093093117384210354L;

	/**
	 * Builds the exception.
	 */
	public UnexpectedException() {
		super();
	}
	
	/**
	 * Builds the exception.
	 * 
	 * @param message the message of the exception
	 */
	public UnexpectedException(final String message) {
		super(message);
	}
	
	/**
	 * Builds the exception.
	 * 
	 * @param nestedException exception that caused the creation of this exception
	 */
	public UnexpectedException(final Exception nestedException) {
		super(nestedException);
	}
	
}
