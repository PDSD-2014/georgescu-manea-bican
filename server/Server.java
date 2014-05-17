import java.sql.*;
import java.io.*;
import java.util.logging.*;
import java.net.*;
import java.util.*;

public class Server
{
	Logger logger;

	public void updateLocation(String message)
	{
		int id;
		float latitude, longitude;

		Scanner sc = new Scanner(message);

		sc.nextInt();
		id = sc.nextInt();
		latitude = sc.nextFloat();
		longitude = sc.nextFloat();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();

			String sql = "UPDATE client SET " +
				     "latitude = " + latitude + ", " +
				     "longitude = " + longitude + " " +
				     "WHERE user_id = " + id + ";";

			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch (Exception e) {
			logger.severe("Database error: " + e.getMessage());
		}

		logger.info("New client position: id: " + id + ", latitude: " + latitude + ", longitude: " + longitude); 
	}

	public static void main( String args[] )
	{
		Server server = new Server();

		/* Open log file. */
		server.logger = Logger.getLogger("Log");
		FileHandler fh;

		try {
			fh = new FileHandler("out.log");
			server.logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (SecurityException e) {
			System.err.println("SecurityException: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}

		server.logger.info("line");

		/* Create server socket. */
		ServerSocket in = null;
		try {
			in = new ServerSocket(9000);
		} catch (IOException e) {
			server.logger.severe("Cannot create server socket: IOException: " + e.getMessage());
		}

		while(true) {
			Socket incomingRequest = null;

			/* Wait for request. */
			try {
				incomingRequest = in.accept();
			} catch (IOException e) {
				server.logger.severe("Error when accepting connection: IOException: " + e.getMessage());
			}

			server.logger.info("New request from: " + incomingRequest.getInetAddress());

			/* Read client request. */
			InputStream requestStream = null;
			try {
				requestStream = incomingRequest.getInputStream();
			} catch (IOException e) {
				server.logger.severe("Cannot get input stream: IOException: " + e.getMessage());
			}

			BufferedReader requestReader = new BufferedReader(new InputStreamReader(requestStream));
			try {
				String request = requestReader.readLine();
				server.logger.info("Got request: " + request);

				char message = request.charAt(0);

				switch (message) {
					case '1': server.updateLocation(request);
						  break;
				}


			} catch (IOException e) {
				server.logger.severe("Cannot read input stream: IOException: " + e.getMessage());
			}

			/* Respond to client. */
			OutputStream responseStream = null;
			try {
				responseStream = incomingRequest.getOutputStream();
			} catch (IOException e) {
				server.logger.severe("cannot get output stream: IOException: " + e.getMessage());
			}

			PrintStream writer = new PrintStream(responseStream);
			writer.print("Hello");

			try {
				responseStream.close();
				incomingRequest.close();
				server.logger.info("Closed client stream.");
			} catch (IOException e) {
				server.logger.severe("Error finishing request: IOException: " + e.getMessage());
			}
		}
	}
}
