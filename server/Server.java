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

	public String getLocation(String message)
	{
		int id;
		float latitude, longitude;
		Scanner sc = new Scanner(message);
		StringBuffer sbuf = new StringBuffer();

		sc.nextInt();
		id = sc.nextInt();

		sbuf.append("2 " + id + " ");

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();

			String sql = "SELECT latitude, longitude FROM client " +
				     "WHERE user_id = " + id + ";";

			ResultSet rs = stmt.executeQuery(sql);
			latitude = rs.getFloat("latitude");
			longitude = rs.getFloat("longitude");
			System.out.println(latitude + " " + longitude);

			sbuf.append(latitude + " ");
			sbuf.append(longitude + " ");
			sbuf.append("\n");

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			logger.severe("Database error: " + e.getMessage());
		}

		return sbuf.toString();
	}

	public void logoutUser(String message)
	{
		int id;
		Scanner sc = new Scanner(message);

		sc.nextInt();
		id = sc.nextInt();

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();

			String sql = "UPDATE client SET online = 0 where user_id = " + id + ";";

			stmt.executeUpdate(sql);

			stmt.close();
			c.close();
		} catch (Exception e) {
			logger.severe("Database error: " + e.getMessage());
		}
	}

	public String authentifyUser(String message)
	{
		String email, password, dbpassword = null;
		String name = null, surname = null;
		int id = -1;
		Scanner sc = new Scanner(message);
		StringBuffer sbuf = new StringBuffer();

		sc.nextInt();
		email = sc.next();
		password = sc.next();

		sbuf.append("4 ");

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();

			String sql = "SELECT user_id, name, surname, password FROM client " +
				     "WHERE email = '" + email + "';";

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				id = rs.getInt("user_id");
				surname = rs.getString("surname");
				name = rs.getString("name");
				dbpassword = rs.getString("password");
			}

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			logger.severe("Database error: " + e.getMessage());
		}

		/* We have no dbpassword => no such user in the database. */
		if (dbpassword == null) {
			sbuf.append("1\n");
			logger.info("No such user: " + email);
		} else
			if (!dbpassword.equals(password)) { /* Wrong password. */
				sbuf.append("2\n");
				logger.info("Wrong password: " + email);
			} else {
				sbuf.append("0 " + id + " " + surname + " " + name + "\n");
				logger.info("Authentified user: " + email);
				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:test.db");
					stmt = c.createStatement();

					String sql = "UPDATE client SET online = 1 where user_id = " + id + ";";

					stmt.executeUpdate(sql);

					stmt.close();
					c.close();
				} catch (Exception e) {
					logger.severe("Database error: " + e.getMessage());
				}
			}

		return sbuf.toString();
	}

	public String registerNewUser(String message)
	{
		String email, password, dbemail;
		String name = null, surname = null;
		int id = -1;
		Scanner sc = new Scanner(message);
		StringBuffer sbuf = new StringBuffer();
		boolean out = false;

		sc.nextInt();
		surname = sc.next();
		name = sc.next();
		email = sc.next();
		password = sc.next();

		sbuf.append("5 ");

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();

			String sql = "SELECT user_id,email FROM client;";

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				id = rs.getInt("user_id");
				dbemail = rs.getString("email");
				if (dbemail.equals(email)) { /* User already exists. */
					out = true;
					sbuf.append("1\n");
				}
			}

			if (out == false) {
				id++;
				sql = "INSERT INTO client VALUES " +
					     "(" + id + ", " + "'" + email + "'" + ", " +
					     "'" + password + "'" + ", " + "'" + name + "'" + ", " +
					     "'" + surname + "'," + " -1, -1, 0);";

				stmt.executeUpdate(sql);

				sbuf.append("0 " + id + "\n");

				logger.info("Added user: " + email);
			}

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			logger.severe("Database error: " + e.getMessage());
		}

		return sbuf.toString();
	}

	public String listClients(String message)
	{
		String email, password, dbemail;
		String name, surname;
		int id, user_id;
		int clients = 0;
		Scanner sc = new Scanner(message);
		StringBuffer sbuf = new StringBuffer();
		StringBuffer aux = new StringBuffer();
		boolean out = false;

		sc.nextInt();
		id = sc.nextInt();

		sbuf.append("6");

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			stmt = c.createStatement();

			String sql = "SELECT user_id,surname,name FROM client;";

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				user_id = rs.getInt("user_id");
				surname = rs.getString("surname");
				name = rs.getString("name");

				if (user_id != id) {
					aux.append(" " + user_id + " " + surname + " " + name);
					clients++;
				}
			}

			sbuf.append(" " + clients);

			sbuf.append(aux.toString());

			sbuf.append("\n");

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			logger.severe("Database error: " + e.getMessage());
		}

		return sbuf.toString();
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
			String response = null;

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

				if (request == null)
					continue;

				char message = request.charAt(0);

				switch (message) {
					case '1': server.updateLocation(request);
						  break;
					case '2': response = server.getLocation(request);
						  break;
					case '3': server.logoutUser(request);
						  break;
					case '4': response = server.authentifyUser(request);
						  break;
					case '5': response = server.registerNewUser(request);
						  break;
					case '6': response = server.listClients(request);
						  break;
					default: continue;
				}


			} catch (IOException e) {
				server.logger.severe("Cannot read input stream: IOException: " + e.getMessage());
			}

			if (response != null) {
				/* Respond to client. */
				OutputStream responseStream = null;
				try {
					responseStream = incomingRequest.getOutputStream();
				} catch (IOException e) {
					server.logger.severe("cannot get output stream: IOException: " + e.getMessage());
				}

				PrintStream writer = new PrintStream(responseStream);
				writer.print(response);

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
}
