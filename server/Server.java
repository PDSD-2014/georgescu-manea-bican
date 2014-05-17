import java.sql.*;
import java.io.*;

public class Server
{
	public static void main( String args[] )
	{
		/* Open log file. */
		FileWriter fw;
		try {
			fw = new FileWriter("out.log", true);
			fw.write("line\n");
			fw.close();
		} catch(IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}

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
