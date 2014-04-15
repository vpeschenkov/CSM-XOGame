package org.ultimate.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Client implements Runnable {
	/*
	 * Private declaration
	 */

	/*	 */
	private Socket socket;

	/*	 */
	private String name;

	/*	 */
	private String password;

	/*	 */
	private ClientListener sender;

	/*	 */
	private InputStream inStream;

	/*	 */
	private OutputStream outStream;

	/*	 */
	private long id;

	/*	 */
	private boolean isGame = false;

	/*	 */
	private boolean isDone = false;

	/* */
	private boolean isAuthentication = false;

	/*   */
	private static final Logger errorLogger = Logger.getLogger("error");

	/*   */
	private static final Logger debugLogger = Logger.getLogger("debug");
	
	/*private Timer timer = new Timer(5000, new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {

			
		}});
	*/
	static{
		debugLogger.setLevel(Level.DEBUG);
	}

	public Client(Socket socket, ClientListener sender) {
		this.socket = socket;
		setSender(sender);
		try {
			inStream = socket.getInputStream();
			outStream = socket.getOutputStream();
		} catch (IOException e) {
			errorLogger.error(e.getMessage());
			if (debugLogger.isDebugEnabled())
				debugLogger.debug(e.getMessage());
		}

	}

	@Override
	public void run() {
		Scanner in = new Scanner(inStream);
		PrintWriter out = new PrintWriter(outStream, true);

		while (!isDone && in.hasNext()) {
			String message = in.nextLine();
			sender.sendMessage(message, id);
		}

		try {
			socket.close();
		} catch (IOException e) {
			errorLogger.error(e.getMessage());
			if (debugLogger.isDebugEnabled())
				debugLogger.debug(e.getMessage());
		}

		in.close();
		out.close();
	}

	public void close() {
		try {
			socket.close();
			isDone = true;
			sender.deleteClient(id);
		} catch (IOException e) {
			errorLogger.error(e.getMessage());
			if (debugLogger.isDebugEnabled())
				debugLogger.debug(e.getMessage());
		}
	}

	/**
	 * @return the sernder
	 */
	public ClientListener getSender() {
		return sender;
	}

	/**
	 * @param sernder
	 *            the sernder to set
	 */
	public void setSender(ClientListener sender) {
		this.sender = sender;
	}

	/**
	 * @return the name
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isGame() {
		return isGame;
	}

	public void setGame(boolean isGame) {
		this.isGame = isGame;
	}

	public void sendMessage(String message) {
		PrintWriter out = new PrintWriter(outStream, true);
		out.println(message);
	}

	public boolean equals(Object arg) {
		if (arg == null)
			return false;

		if (arg == this)
			return false;
		if (arg.getClass() == this.getClass()) {
			if (((Client) arg).getId() == this.getId())
				return true;
			return false;
		}

		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAuthentication() {
		return isAuthentication;
	}

	public void setAuthentication(boolean isAuthentication) {
		this.isAuthentication = isAuthentication;
	}
}