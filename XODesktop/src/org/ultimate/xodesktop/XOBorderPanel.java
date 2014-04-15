package org.ultimate.xodesktop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class XOBorderPanel extends JComponent implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final XOInfoPanel infoGame = new XOInfoPanel(386, 30);
	private final XOInfoPanel infoUser = new XOInfoPanel(386, 30);
	private final List<XOSquare> square = new ArrayList<XOSquare>();
	private final XOClient client = XOClient.getInstance();
	private String userName;
	private boolean isMove = false;
	private boolean isGame = false;
	private int index;
	private Preferences prefs;

	public XOBorderPanel() {
		setOpaque(true);
		setPreferredSize(new Dimension(400, 460));
		FlowLayout fl = new FlowLayout();
		fl.setHgap(1);
		fl.setVgap(1);
		this.setLayout(fl);

		add(infoUser);
		infoUser.setCenter(true);

		prefs = Preferences.userNodeForPackage(this.getClass());
		if (prefs.isUserNode()) {
			userName = prefs.get("USER_NAME", null);
			infoUser.setMessage(userName + ": " + "win = "
					+ prefs.getInt("USER_WIN", 0) + " " + " losse = "
					+ prefs.getInt("USER_LOSSE", 0));
		}

		addXOSquareCOmponents(9);

		setInfoText("Ожидаем противника!");
		add(infoGame);
		client.addActionListener(this);
		client.sendMessage("stats");
		client.sendMessage("start");
		setMove(false);
	}

	private void addXOSquareCOmponents(int length) {
		for (int i = 0; i < length; i++) {
			square.add(new XOSquare(i));
		}

		for (int i = 0; i < length; i++) {
			square.get(i).addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent evt) {
					index = Integer.valueOf(evt.getActionCommand());
					if (isMove) {
						client.sendMessage("mov " + (int) (index / 3) + " "
								+ index % 3);
						((XOSquare) evt.getSource()).setState(0);
						setMove(false);
						client.sendMessage("whomove");
						setInfoText("Ходит противник!");
					} else {
						if (isGame)
							setInfoText("Ход противника!");
					}

				}

			});
			add(square.get(i));
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.LIGHT_GRAY);
	}

	public void setInfoText(String message) {
		infoGame.setMessage(message);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		int replyStatus = Integer.valueOf(evt.getActionCommand().split(" ")[1]), n;
		System.out.println(evt.getActionCommand());
		if (evt.getActionCommand().split(" ")[0].equalsIgnoreCase("+mov")) {
			System.out.println(evt.getActionCommand());
			int x = Integer.valueOf(evt.getActionCommand().split(" ")[1]);
			int y = Integer.valueOf(evt.getActionCommand().split(" ")[2]);
			square.get(x * 3 + y).setState(1);
			setMove(true);
			setInfoText("Ваш ход");
		} else {
			if (evt.getActionCommand().split(" ")[0].equalsIgnoreCase("+stats")) {
				System.out.println(evt.getActionCommand());
				int x = Integer.valueOf(evt.getActionCommand().split(" ")[1]);
				int y = Integer.valueOf(evt.getActionCommand().split(" ")[2]);
				prefs.putInt("USER_WIN", x);
				prefs.putInt("USER_LOSSE", y);
				if (prefs.isUserNode()) {
					infoUser.setMessage(userName + ": " + "win = "
							+ x + " " + " losse = "
							+ y);
				}

			} else {
				switch (replyStatus) {

				case XOServerReply.ADDE_IN_QUEUE:
					setInfoText("Ожидаем противника!");
					break;

				case XOServerReply.START_GAME:
					setInfoText("Игра началась");
					isGame = true;
					client.sendMessage("whomove");
					break;

				case XOServerReply.YOU_MOV:
					setInfoText("Ваш ход!");
					setMove(true);
					break;

				case XOServerReply.END_GAME:
					setInfoText("Противник покинул игру!");
					isGame = false;
					client.sendMessage("start");
					setMove(false);
					squareUp(9);
					break;

				case XOServerReply.YOU_NOT_MOV:
					setInfoText("Ходит противник");
					setMove(false);
					break;

				case XOServerReply.PARTHNER_MAKE_A_MOVE:
					setInfoText("Ваш ход");
					setMove(true);
					break;

				case XOServerReply.DREW:
					setInfoText("Ничья!");
					setMove(false);

					n = JOptionPane.showConfirmDialog(XOBorderPanel.this,
							"Ничья! Начать новую игру?", "Ничья!",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.INFORMATION_MESSAGE);
					if (n == JOptionPane.YES_OPTION) {
						client.sendMessage("start");
						setInfoText("Ожидание противника!");
						squareUp(9);
					} else {
						squareUp(9);
					}
					break;

				case XOServerReply.YOU_LOSSE:
					setMove(false);
					
					client.sendMessage("stats");
					setInfoText("Вы проиграли!");
					isGame = false;
					n = JOptionPane.showConfirmDialog(XOBorderPanel.this,
							"Вы проиграли! Начать новую игру?", "Проирали!",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.INFORMATION_MESSAGE);
					if (n == JOptionPane.YES_OPTION) {
						client.sendMessage("start");
						setInfoText("Ожидание противника!");
						squareUp(9);
					} else {
						squareUp(9);
					}
					break;

				case XOServerReply.YOU_WIN:
					setMove(false);
					isGame = false;
					client.sendMessage("stats");
					setInfoText("Вы победили!");

					n = JOptionPane.showConfirmDialog(XOBorderPanel.this,
							"Поздравляем, Вы победили! Начать новую игру?",
							"Победа!", JOptionPane.YES_NO_OPTION,
							JOptionPane.INFORMATION_MESSAGE);
					if (n == JOptionPane.YES_OPTION) {
						client.sendMessage("start");
						setInfoText("Ожидание противника!");
						squareUp(9);
					} else {
						squareUp(9);
					}
					break;
				}
			}
		}
	}

	private void squareUp(int length) {
		for (int i = 0; i < length; i++) {
			square.get(i).setClicable(true);
			square.get(i).setState(-1);
			square.get(i).setClicable(true);
		}
	}

	/**
	 * @return the isMove
	 */
	public boolean isMove() {
		return isMove;
	}

	/**
	 * @param isMove
	 *            the isMove to set
	 */
	public void setMove(boolean isMove) {
		this.isMove = isMove;
	}
}