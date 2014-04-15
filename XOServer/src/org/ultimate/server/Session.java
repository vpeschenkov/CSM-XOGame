package org.ultimate.server;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Session implements ServerReply {
	/*
	 * Private declaration
	 */

	/* ID of the first customer */
	private long firstId;

	/*	 */
	private long player;

	/* ID of the second customer */
	private long secondId;

	/* ID of the session */
	private long sessionId;

	/*	 */
	private long prevStep;

	/*	 */
	private boolean isFull = false;

	/*	 */
	private boolean isGame = false;

	/*	 */
	private final long map[][] = new long[3][3];

	/*  */
	private ClientListener sender;

	/*   */
	private static final Logger debugLogger = Logger.getLogger("debug");

	static {
		debugLogger.setLevel(Level.DEBUG);
	}

	public Session(ClientListener sender) {
		this.sender = sender;
		this.firstId = 0;
		this.secondId = 0;
		this.prevStep = 0;
		if (debugLogger.isDebugEnabled())
			debugLogger.debug("Create a new session.");
	}

	public int isWin(long id) {
		if (ifWin(secondId) == ServerReply.YOU_WIN && id == secondId) {
			setGame(false);
			return ServerReply.YOU_WIN;
		}

		if (ifWin(firstId) == ServerReply.YOU_WIN && id == firstId) {
			setGame(false);
			return ServerReply.YOU_WIN;
		}

		if (ifWin(secondId) == ServerReply.YOU_WIN && id != secondId) {
			setGame(false);
			return ServerReply.YOU_LOSSE;
		}

		if (ifWin(firstId) == ServerReply.YOU_WIN && id != firstId) {
			setGame(false);
			return ServerReply.YOU_LOSSE;
		}

		if (ifWin(-1) == ServerReply.DREW) {
			setGame(false);
			return ServerReply.DREW;
		}
		return 0;
	}

	private int ifWin(long id) {
		boolean win = false;
		int i, j;

		if (firstId == id)
			player = firstId;
		else
			player = secondId;

		for (i = 0; i < 3; i++) {
			for (j = 0; j < 3; j++) {
				if (map[i][j] == player) {
					win = true;
				} else {
					win = false;
					break;
				}
			}
			if (win)
				return ServerReply.YOU_WIN;
		}

		for (j = 0; j < 3; j++) {
			for (i = 0; i < 3; i++) {
				if (map[i][j] == player) {
					win = true;
				} else {
					win = false;
					break;
				}
			}
			if (win)
				return ServerReply.YOU_WIN;
		}

		if ((map[0][0] == player && map[1][1] == player && map[2][2] == player)
				|| (map[0][2] == player && map[1][1] == player && map[2][0] == player))
			return ServerReply.YOU_WIN;

		int count = 0;

		for (i = 0; i < 3; i++) {
			for (j = 0; j < 3; j++) {
				if (map[i][j] > 0) {
					count++;
				} else
					break;
			}
			if (count == 9)
				return ServerReply.DREW;
		}

		return 0;
	}

	public int mov(int x, int y, long id) {
		long player;
		if (x < 0 || x > 2 || y < 0 || y > 2)
			return ServerReply.CAN_NOT_MAKE_A_MOVE;

		if (isWin(id) > 0)
			return isWin(id);

		// if the game has been completed
		if (firstId == id)
			player = firstId;
		else
			player = secondId;

		if (player == prevStep)
			return ServerReply.ALREADY_GOING;

		if (map[x][y] == 0) {
			map[x][y] = player;

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					System.out.print(map[i][j] + " ");
				}
				System.out.println();
			}

			prevStep = player;

			return ServerReply.YOU_MAKE_A_MOVE;
		} else
			return ServerReply.ALREADY_GOING;
	}

	public boolean addClientId(long id) {

		if (firstId == 0) {
			firstId = id;
			prevStep = id;
		} else if (secondId == 0)
			secondId = id;

		if (firstId > 0 && secondId > 0) {
			isFull = true;
			setGame(true);
			notifClient(String.valueOf(ServerReply.START_GAME));
		}

		prevStep = secondId;

		if (debugLogger.isDebugEnabled()) {
			debugLogger.debug("Client added (id = " + id + ") to the session.");
			debugLogger.debug(isFull);
		}

		return true;
	}

	public boolean remove(long id) {
		if (debugLogger.isDebugEnabled()) {
			debugLogger.debug("Client deleted (id = " + id
					+ ") to the session.");
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				map[i][j] = 0;
			}
		}
		newGame();
		return true;
	}

	public boolean isFull() {
		return isFull;
	}

	public void setSessionId(long id) {
		sessionId = id;
	}

	public long getfirstId() {
		return firstId;
	}

	/**
	 * @param firstId
	 *            the firstId to set
	 */
	public void setFirstId(long firstId) {
		this.firstId = firstId;
	}

	/**
	 * @param secondId
	 *            the secondId to set
	 */
	public void setSecondId(long secondId) {
		this.secondId = secondId;
	}

	public long getSecondId() {
		return secondId;
	}

	public boolean isGame() {
		return isGame;
	}

	public void notifClient(String notif) {
		if (firstId != 0) {
			sender.sendMessage("SESSION " + notif, firstId);
		} else if (secondId != 0)
			sender.sendMessage("SESSION " + notif, secondId);
	}

	public void newGame() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				map[i][j] = 0;
			}
		}

		firstId = 0;
		secondId = 0;
		setGame(true);
	}

	public long getSessionId() {
		return sessionId;
	}

	private void setGame(boolean isGame) {
		this.isGame = isGame;
	}

	/**
	 * @return the player
	 */
	public long getPlayer() {
		return player;
	}

	/**
	 * @return the isMove
	 */
	public int isMove(long id) {
		return ((id == prevStep && isWin(id) != ServerReply.YOU_WIN && isWin(id) != ServerReply.YOU_LOSSE) ? ServerReply.YOU_NOT_MOV
				: ServerReply.YOU_MOV);
	}
}