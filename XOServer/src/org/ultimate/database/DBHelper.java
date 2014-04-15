package org.ultimate.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ultimate.server.ServerReply;

public class DBHelper {
	/*
	 * Private declaration
	 */

	/*	 */
	private static DBHelper instance;

	/*	 */
	private ResultSet result;

	/*	 */
	private Connection connection;

	/*	 */
	private Statement statement;

	/*   */
	private static final Logger errorLogger = Logger.getLogger("error");

	/*   */
	private static final Logger debugLogger = Logger.getLogger("debug");

	static {
		debugLogger.setLevel(Level.DEBUG);
	}

	public static synchronized DBHelper getInstance(String patch, String user,
			String password) {
		if (instance == null) {
			instance = new DBHelper(patch, user, password);
		}
		return instance;
	}

	private DBHelper(String patch, String user, String password) {
		try {
			Class.forName("org.h2.Driver").newInstance();
			connection = DriverManager.getConnection(patch, user, password);
			statement = connection.createStatement();
		} catch (Exception e) {
			errorLogger.error(e.getMessage());
			if (debugLogger.isDebugEnabled())
				debugLogger.debug(e.getMessage());
		}

	}

	public int getId(String name) {
		try {
			result = statement
					.executeQuery("select top(1) _ID from USERS where _name = '"
							+ name + "'");
			result.next();
			return result.getInt("_ID");
		} catch (SQLException e) {
			errorLogger.error(e.getMessage());
			if (debugLogger.isDebugEnabled())
				debugLogger.debug(e.getMessage());
			return 0;
		}
	}

	public int getWin(String name) {
		try {
			result = statement
					.executeQuery("select top(1) _win from users where _name = '"
							+ name + "';");
			result.next();
			return result.getInt("_win");
		} catch (SQLException e) {
			errorLogger.error(e.getMessage());
			if (debugLogger.isDebugEnabled())
				debugLogger.debug(e.getMessage());
		}
		return 0;
	}

	public int getLosse(String name) {
		try {
			result = statement
					.executeQuery("select top(1) _losse from users where _name = '"
							+ name + "';");
			result.next();
			return result.getInt("_losse");
		} catch (SQLException e) {
			errorLogger.error(e.getMessage());
			if (debugLogger.isDebugEnabled())
				debugLogger.debug(e.getMessage());
		}
		return 0;
	}

	public synchronized boolean authentication(String name, String password) {
		try {
			result = statement
					.executeQuery("select exists(select _name from users where _name = '"
							+ name + "') AS _auth;");
			result.next();
			if (result.getBoolean("_auth")) {
				result = statement
						.executeQuery("select exists(select _name from users where _name ='"
								+ name
								+ "' and "
								+ "_password = '"
								+ password
								+ "') AS _auth;");

				result.next();
				if (result.getBoolean("_auth"))
					return true;
			}
		} catch (SQLException e) {
			errorLogger.error(e.getMessage());
			if (debugLogger.isDebugEnabled())
				debugLogger.debug(e.getMessage());
			return false;
		}
		return false;
	}

	public synchronized int createUser(String name, String password) {
		try {
			if (!authentication(name, password)) {
				result = statement
						.executeQuery("select exists(select _name from users where _name = '"
								+ name + "') AS _auth;");
				result.next();
				if (!result.getBoolean("_auth")) {
					statement
							.executeUpdate("insert into users(_name, _password) "
									+ "values('"
									+ name
									+ "','"
									+ password
									+ "');");
					return ServerReply.AUTHENTIFICATIONS_IS_SUCCESFULL;
				} else
					return ServerReply.THIS_NAME_IS_ALREADY_TAKEN;
			}
		} catch (SQLException e) {
			errorLogger.error(e.getMessage());
			if (debugLogger.isDebugEnabled())
				debugLogger.debug(e.getMessage());
			return 0;
		}
		return 0;
	}

	public synchronized boolean addWin(String name) {
		try {
			statement
					.executeUpdate("update users set _win=_win + 1 where _name = '"
							+ name +"'");
			return true;
		} catch (SQLException e) {
			errorLogger.error(e.getMessage());
			if (debugLogger.isDebugEnabled())
				debugLogger.debug(e.getMessage());
			return false;
		}
	}

	public synchronized boolean addLosse(String name) {
		try {
			statement
					.executeUpdate("update users set _losse=_losse + 1 where _name = '"
							+ name +"'");
			return true;
		} catch (SQLException e) {
			errorLogger.error(e.getMessage());
			if (debugLogger.isDebugEnabled())
				debugLogger.debug(e.getMessage());
			return false;
		}
	}
}