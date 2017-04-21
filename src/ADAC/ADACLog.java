package ADAC;

/**
 * Simple logging interface for the ADAC interpreter. Simply display logging and
 * error messages.
 * 
 * @author neil
 *
 */
public interface ADACLog {

	public void log(String text);

	public void error(String title, String text);

}
