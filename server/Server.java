import java.sql.*;
import java.io.*;
import java.util.logging.*;

public class Server
{
	public static void main( String args[] )
	{
		/* Open log file. */

		Logger logger = Logger.getLogger("Log");
		FileHandler fh;

		try {
			fh = new FileHandler("out.log");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (SecurityException e) {
			System.err.println("SecurityException: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}

		logger.info("line");

		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("Opened database successfully");
	}
}
