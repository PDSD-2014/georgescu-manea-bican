import java.sql.*;
import java.io.*;
import java.util.logging.*;
import java.net.*;

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

		/* Create server socket. */
		ServerSocket in = null;
		try {
			in = new ServerSocket(9000);
		} catch (IOException e) {
			logger.severe("Cannot create server socket: IOException: " + e.getMessage());
		}

		while(true) {
			Socket incomingRequest = null;

			/* Wait for request. */
			try {
				incomingRequest = in.accept();
			} catch (IOException e) {
				logger.severe("Error when accepting connection: IOException: " + e.getMessage());
			}

			logger.info("New request from: " + incomingRequest.getInetAddress());

			/* Read client request. */
			InputStream requestStream = null;
			try {
				requestStream = incomingRequest.getInputStream();
			} catch (IOException e) {
				logger.severe("Cannot get input stream: IOException: " + e.getMessage());
			}

			BufferedReader requestReader = new BufferedReader(new InputStreamReader(requestStream));
			try {
				String request = requestReader.readLine();
				char message = request.charAt(0);
				logger.info("Got message type " + message);
			} catch (IOException e) {
				logger.severe("Cannot read input stream: IOException: " + e.getMessage());
			}

			/* Respond to client. */
			OutputStream responseStream = null;
			try {
				responseStream = incomingRequest.getOutputStream();
			} catch (IOException e) {
				logger.severe("cannot get output stream: IOException: " + e.getMessage());
			}

			PrintStream writer = new PrintStream(responseStream);
			writer.print("Hello");
			logger.severe("sent");

			try {
				responseStream.close();
				incomingRequest.close();
				logger.info("Closed client stream.");
			} catch (IOException e) {
				logger.severe("Error finishing request: IOException: " + e.getMessage());
			}
		}


//		Connection c = null;
//		try {
//			Class.forName("org.sqlite.JDBC");
//			c = DriverManager.getConnection("jdbc:sqlite:test.db");
//		} catch ( Exception e ) {
//			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//			System.exit(0);
//		}
//		System.out.println("Opened database successfully");
	}
}
