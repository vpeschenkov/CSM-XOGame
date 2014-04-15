package org.ultimate.xodesktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class XOJMenu {
	private JMenuBar jMenuBar;
	private JMenu jMenu;
	private JMenuItem jMenuItem;

	public XOJMenu(final JFrame jFrame) {
		jMenuBar = new JMenuBar();
		jMenu = new JMenu("Меню");
		jMenuBar.add(jMenu);

		jMenuItem = new JMenuItem("Новая игра");
		jMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				XOClient client = XOClient.getInstance();
				client.sendMessage("start");
			}

		});
		jMenu.add(jMenuItem);

		jMenuItem = new JMenuItem("Выход");
		jMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] options = { "Да", "Нет!" };
				int n = JOptionPane.showOptionDialog(jFrame,
						"Покинуть игру?", "Подтверждение",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (n == 0) {
					jFrame.setVisible(false);
					XOClient client = XOClient.getInstance();
					client.sendMessage("exit");
					client.setRunning(false);
					System.exit(0);
				}
			}

		});
		jMenu.add(jMenuItem);

		jFrame.setJMenuBar(jMenuBar);
	}
}