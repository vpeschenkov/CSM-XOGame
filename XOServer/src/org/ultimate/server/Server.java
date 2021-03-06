package org.ultimate.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import org.ultimate.database.DBHelper;

public class Server implements ClientListener, ServerReply {

	/*
	 * Private declaration
	 */

	/*	 */
	private ServerSocket serverSocket;

	/*	 */
	private Map<Long, Client> clientList = new HashMap<Long, Client>();

	/*	 */
	private List<Session> sessionList = new LinkedList<Session>();

	/*	 */
	private long clientId = 0;

	/*	 */
	private long sessionId = 0;

	/*	 */
	private final DBHelper db = DBHelper.getInstance(
			"jdbc:h2:tcp://localhost/~/xodb2", "admin", "admin");

	/*   */
	private static final Logger errorLogger = Logger.getLogger("error");

	/*   */
	private static final Logger infoLogger = Logger.getLogger("info");

	/*   */
	private static final Logger debugLogger = Logger.getLogger("debug");

	static {
		debugLogger.setLevel(Level.DEBUG);
	}
	/*
	 * Public declaration
	 */
	public static final int HELLO = 1;
	public static final int USER = 2;
	public static final int PASSWORD = 3;
	public static final int START = 4;
	public static final int WHO_MOVE = 5;
	public static final int STATS = 6;
	public static final int MOV = 7;
	public static final int SESSION = 8;
	public static final int ACCEPT = 9;
	public static final int EXIT = 10;

	public Server() {
		try {
			serverSocket = new ServerSocket(3133);
			infoLogger.info("Server start on port 3135");
			if (debugLogger.isDebugEnabled()) {
				debugLogger.debug("Server start on port 3135");
			}
		} catch (IOException e) {
			errorLogger.error(e.getMessage());
		}

		while (true) {
			try {
				Socket incoming = serverSocket.accept();
				infoLogger.info("Client accepted");
				System.out.println(this);
				Client client = new Client(incoming, this);
				client.setId(generateClientID());
				addClient(client.getId(), client);
				Thread newClient = new Thread(client);
				newClient.start();
				if (debugLogger.isDebugEnabled()) {
					debugLogger
							.debug("Client accepted." + " " + client.getId());
				}
			} catch (IOException e) {
				errorLogger.error(e.getMessage());
			}

		}
	}

	@Override
	public void sendMessage(String message, long id) {
		infoLogger.info("Client send message : " + message);

		String[] term = message.split(" ");
		Client currentClient = getClientForId(id);
		Client parthnerClient = null;

		System.out.println(message);

		Session tmpSession = null;
		long parthnerId = 0;

		if (currentClient.isGame()) {
			tmpSession = getSession(id);
			if (tmpSession != null && tmpSession.isFull()) {
				parthnerId = tmpSession.getfirstId() == id ? tmpSession
						.getSecondId() : tmpSession.getfirstId();
			}
		}

		if (parthnerId != 0)
			parthnerClient = getClientForId(parthnerId);

		int idCommand = parseCommand(term[0]);

		switch (idCommand) {

		case HELLO:
			currentClient.sendMessage("+OK "
					+ ServerReply.OPERATION_WAS_SUCCESSFUL);
			break;

		case USER:
			currentClient.setName(term[1]);
			currentClient.sendMessage("+OK "
					+ ServerReply.OPERATION_WAS_SUCCESSFUL);
			System.out.println("USER " + term[1]);
			break;

		case PASSWORD:
			if (currentClient.getName() != null) {
				if (term[1].length() != 0)
					currentClient.setPassword(term[1]);
				else
					currentClient.setPassword(null);
				if (db.authentication(currentClient.getName(),
						currentClient.getPassword())) {
					currentClient.setAuthentication(true);
					currentClient.sendMessage("+OK "
							+ ServerReply.AUTHENTIFICATIONS_IS_SUCCESFULL);
					infoLogger
							.info("Client is authentifications is succesful.");
					System.out.println("PASSWORD " + term[1]);
				} else {
					int reply = db.createUser(currentClient.getName(),
							currentClient.getPassword());
					if (reply < 200) {
						currentClient.sendMessage("+OK " + reply);
						currentClient.setAuthentication(true);
						infoLogger
								.info("Client is authentifications is succesful.");
						System.out.println("PASSWORD " + term[1]);
					} else
						currentClient.sendMessage("+NO " + reply);
				}
			}
			break;

		case STATS:
			if (currentClient.isAuthentication()) {
				currentClient.sendMessage("+STATS "+String.valueOf(db
						.getWin(currentClient.getName()))
						+ " "
						+ String.valueOf(db.getLosse(currentClient.getName())));
			}
			break;

		case START:
			if (currentClient.isAuthentication()){
				currentClient.setGame(true);
				addClientForSession(currentClient);
			}
			break;	
			
		case MOV:
			if (currentClient.isGame() && tmpSession != null) {
				if (tmpSession.isFull()) {
					if (debugLogger.isDebugEnabled()) {
						debugLogger.debug("MOV " + term[1].trim() + " "
								+ term[2].trim() + ".");
						debugLogger.debug("Client id = "
								+ currentClient.getId() + ".");
					}
					int reply = tmpSession.mov(Integer.valueOf(term[1].trim()),
							Integer.valueOf(term[2].trim()), id);
					if (reply == ServerReply.YOU_MAKE_A_MOVE) {
						currentClient.sendMessage("+OK "
								+ tmpSession.isWin(currentClient.getId()));
						parthnerClient.sendMessage("+MOV " + term[1].trim()
								+ " " + term[2].trim());
						parthnerClient.sendMessage("+OK "
								+ tmpSession.isWin(parthnerClient.getId()));

						reply = tmpSession.isWin(currentClient.getId());
						if (reply > 0) {
							currentClient.setGame(false);
							parthnerClient.setGame(false);

							if (reply == ServerReply.YOU_WIN) {
								db.addWin(currentClient.getName());
								db.addLosse(parthnerClient.getName());
								infoLogger.info("Game is sucesfull!");
							}

							tmpSession.newGame();
						}

					} else {
						currentClient.sendMessage("+NO " + reply);
					}
				} else {
					currentClient.sendMessage("+NO "
							+ ServerReply.NOT_FOUND_PARTHER);
				}
			} else {
				currentClient.sendMessage("+NO " + ServerReply.NOT_LOGGED);
			}
			break;

		case SESSION:
			if (debugLogger.isDebugEnabled()) {
				debugLogger.debug("Session : " + term[1]);
			}
			System.out.println("Session : " + term[1]);
			if (currentClient != null)
				currentClient.sendMessage("+OK " + term[1]);
			if (parthnerClient != null)
				parthnerClient.sendMessage("+OK " + term[1]);
			break;

		case WHO_MOVE:
			if (currentClient.isGame() && tmpSession != null) {
				if (tmpSession != null) {
					currentClient.sendMessage("+OK " + tmpSession.isMove(currentClient.getId()));
				}
			}
			break;
			
		case EXIT:
			infoLogger.info("Client left the server.");
			
			if (tmpSession != null){
				tmpSession.notifClient(String.valueOf(ServerReply.END_GAME));
				tmpSession.remove(id);
			}
			
			currentClient.sendMessage("+OK "
					+ ServerReply.OPERATION_WAS_SUCCESSFUL);
			currentClient.close();

			break;

		default:
			currentClient.sendMessage("+ERROR "
					+ ServerReply.OPERATION_CAN_NOT_BE_PERFORMED);
			errorLogger.error("There is no such command" + term[0]);

		}
	}

	public synchronized Client getFreeClient(Client r) {
		for (Entry<Long, Client> client : clientList.entrySet()) {
			if (!client.getValue().isGame() && !r.equals(client))
				return client.getValue();
		}
		return null;
	}

	public synchronized Session getSession(long id) {
		for (Session session : sessionList) {
			if (session.getfirstId() == id || session.getSecondId() == id)
				return session;
		}
		return null;
	}

	public synchronized Client getClientForId(long id) {
		return clientList.get(id);
	}

	public synchronized Session getFreeSession() {
		for (Session session : sessionList) {
			if (!session.isFull())
				return session;
		}
		return null;
	}

	private synchronized boolean addSession(Session session) {
		if (sessionList.add(session))
			return true;
		return false;
	}

	@Override
	public synchronized boolean addClient(long id, Client client) {
		if (clientList.put(id, client) != null)
			return true;
		return false;
	}

	private synchronized void deleteClientForId(long id) {
		Client client = getClientForId(id);
		clientList.remove(client);
		Session sessionTmp;
		if((sessionTmp = getSession(id))!=null){
			sessionTmp.setFirstId(0);
			sessionTmp.setSecondId(0);
		}
	}

	private synchronized long generateClientID() {
		if (this.clientId < Long.MAX_VALUE)
			this.clientId++;
		else
			this.clientId = 1;
		return this.clientId;
	}

	private synchronized long generateSessionId() {
		if (this.sessionId < Long.MAX_VALUE)
			this.sessionId++;
		else
			this.sessionId = 1;
		return this.sessionId;
	}

	private synchronized void addClientForSession(Client client) {
		if (getFreeSession() == null) {
			Session tmpSession = new Session(this);
			if(tmpSession.getfirstId() != client.getId() && tmpSession.getSecondId() != client.getId()){
				tmpSession.setSessionId(generateSessionId());
				tmpSession.addClientId(client.getId());
				addSession(tmpSession);
			}
		} else {
			Session tmpSession = getFreeSession();
			if(tmpSession.getfirstId() != client.getId() && tmpSession.getSecondId() != client.getId()){
			System.out.println("������ ������ � id = " + client.getId());
			tmpSession.addClientId(client.getId());
			}
		}
	}

	@Override
	public void deleteClient(long id) {
		deleteClientForId(id);
	}

	private int parseCommand(String command) {
		if (command.equalsIgnoreCase("hello"))
			return Server.HELLO;
		if (command.equalsIgnoreCase("user"))
			return Server.USER;
		if (command.equalsIgnoreCase("password"))
			return Server.PASSWORD;
		if (command.equalsIgnoreCase("start"))
			return Server.START;
		if (command.equalsIgnoreCase("whomove"))
			return Server.WHO_MOVE;
		if (command.equalsIgnoreCase("stats"))
			return Server.STATS;
		if (command.equalsIgnoreCase("mov"))
			return Server.MOV;
		if (command.equalsIgnoreCase("accept"))
			return Server.ACCEPT;
		if (command.equalsIgnoreCase("session"))
			return Server.SESSION;
		if (command.equalsIgnoreCase("exit"))
			return Server.EXIT;
		return 0;
	}
}