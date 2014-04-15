package org.ultimate.xoandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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
			clientInstance = new XOClient(new InetSocketAddress("10.0.2.2",
					3333));
			Thread t = new Thread(clientInstance, "");
			t.start();
		}
		return clientInstance;
	}

	private XOClient(InetSocketAddress inetAddress) {

	}

	public void addActionListener(ActionListener l) {
		listeners.add(l);
	}

	public void removeActionListener(ActionListener l) {
		listeners.remove(l);
	}

	public void reply() {
		for (ActionListener l : listeners) {
			l.actionPerformed(String.valueOf(this.getReply()));
		}
	}

	/**
	 * @param message
	 *            the message to set and send this message to server
	 */
	public boolean sendMessage(final String message) {
		out.println(message);
		return false;
	}

	@Override
	public void run() {

		try {
			socket = new Socket("ultimate-xo.no-ip.org", 3333);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String getProtocolCommand;
		String[] replyCode;

		try {
			while (((getProtocolCommand = in.readLine()) != null) && running) {
				replyCode = getProtocolCommand.split(" ");
				setReplyStatus(Integer.valueOf(replyCode[1]));
				setReply(getProtocolCommand);
				reply();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
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
	 * the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

}