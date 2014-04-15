package org.ultimate.xodesktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class XOClient implements Runnable {
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private boolean isGame = true;
	private int replyStatus;
	private boolean isReply;
	private boolean running = true;
	private String reply;
	private final List<ActionListener> listeners = new ArrayList<ActionListener>();
	private static XOClient clientInstance;

	static synchronized XOClient getInstance() {
		if (clientInstance == null) {
			clientInstance = new XOClient(new InetSocketAddress(
					"localhost", 3333));
			Thread t = new Thread(clientInstance, "");
			t.start();
		}
		return clientInstance;
	}

	private XOClient(InetSocketAddress inetAddress) {
		try {
			socket = new Socket(inetAddress.getAddress(), inetAddress.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addActionListener(ActionListener l) {
		listeners.add(l);
	}

	public void removeActionListener(ActionListener l) {
		listeners.remove(l);
	}

	public void reply() {
		ActionEvent event = new ActionEvent(this, 0, String.valueOf(this
				.getReply()));
		for (ActionListener l : listeners) {
			l.actionPerformed(event);
		}
	}

	/**
	 * @param message
	 *            the message to set and send this message to server
	 */
	public synchronized boolean sendMessage(String message) {
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isGame;
	}

	@Override
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			String getProtocolCommand;
			String[] replyCode;

			while (((getProtocolCommand = in.readLine()) != null) && running) {
				replyCode = getProtocolCommand.split(" ");

				setReplyStatus(Integer.valueOf(replyCode[1]));
				setReply(getProtocolCommand);
				reply();
				System.out.println("Server send : "
						+ Integer.valueOf(replyCode[1]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the isGame
	 */
	public boolean isGame() {
		return isGame;
	}

	/**
	 * @param isGame
	 *            the isGame to set
	 */
	public void setGame(boolean isGame) {
		this.isGame = isGame;
	}

	/**
	 * @return the replyStatus
	 */
	public synchronized int getReplyStatus() {
		setReply(false);
		return replyStatus;
	}

	/**
	 * @param replyStatus
	 *            the replyStatus to set
	 */
	public void setReplyStatus(int replyStatus) {
		this.replyStatus = replyStatus;
	}

	/**
	 * @return the isReply
	 */
	public boolean isReply() {
		return isReply;
	}

	/**
	 * @param isReply
	 *            the isReply to set
	 */
	public void setReply(boolean isReply) {
		this.isReply = isReply;
	}

	/**
	 * @return the reply
	 */
	public String getReply() {
		return reply;
	}

	/**
	 * @param reply
	 *            the reply to set
	 */
	public void setReply(String reply) {
		this.reply = reply;
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running
	 *            the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
}