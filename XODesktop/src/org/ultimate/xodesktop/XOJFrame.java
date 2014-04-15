package org.ultimate.xodesktop;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class XOJFrame extends JFrame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2648268700084080783L;
	private static int state = 1;
	private JComponent comp;

	public void switchComponent() {

		if (state == 0) {
			remove(comp);
			comp = new XOMainPanel(this);
			add(comp);
			validate();
			pack();
			setLocationRelativeTo(null);
			state = 1;
		}

		if (state == 1) {
			remove(comp);
			comp = new XOBorderPanel();
			add(comp);
			new XOJMenu(this);
			validate();
			pack();
			setLocationRelativeTo(null);
			state = 0;
		}
	}

	@Override
	public void run() {
		setTitle("XO");
		addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				Object[] options = { "Да", "Нет!" };
				int n = JOptionPane
						.showOptionDialog(e.getWindow(), "Покинуть игру?",
								"Подтверждение", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);
				if (n == 0) {
					e.getWindow().setVisible(false);
					XOClient client = XOClient.getInstance();
					client.sendMessage("exit");
					client.setRunning(false);
					System.exit(0);
				}
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}
		});
		setResizable(false);
		comp = new XOMainPanel(this);
		add(comp);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}
}