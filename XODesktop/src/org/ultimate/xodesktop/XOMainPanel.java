package org.ultimate.xodesktop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class XOMainPanel extends JComponent implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1620597373317514731L;
	private XOJFrame frame;
	private final JTextField tfUserName = new JTextField(18);
	private final JPasswordField tfPassword = new JPasswordField(18);
	private final JButton btLogin = new JButton("Войти");
	private Preferences prefs;
	private final XOClient client = XOClient.getInstance();
	
	public XOMainPanel(final XOJFrame frame) {
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.CENTER);
		this.setLayout(fl);
		this.frame = frame;

		client.addActionListener(this);

		prefs = Preferences.userNodeForPackage(this.getClass());
		if (prefs.isUserNode()) {
			tfUserName.setText(prefs.get("USER_NAME", null));
			tfPassword.setText(prefs.get("USER_PASSWORD", null));
		}

		tfUserName.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
				Color.GREEN));
		tfUserName.setHorizontalAlignment(JTextField.CENTER);

		tfPassword.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
				Color.GREEN));
		tfPassword.setHorizontalAlignment(JTextField.CENTER);

		btLogin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				if (tfUserName.getText().length() != 0) {
					prefs.put("USER_NAME", tfUserName.getText());
					prefs.put("USER_PASSWORD",
							String.valueOf(tfPassword.getPassword()));

					client.sendMessage("user " + tfUserName.getText());
					client.sendMessage("password "
							+ String.valueOf(tfPassword.getPassword()));

				} else {
					JOptionPane.showMessageDialog(frame,
							"Введите имя и пароль.", "Неверные данные",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		add(new JLabel("Введите имя:"));
		add(tfUserName);
		add(new JLabel("Введите пароль:"));
		add(tfPassword);
		add(btLogin);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(0, 0, 256, 128);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(256, 128);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		int replyStatus = Integer.valueOf(evt.getActionCommand().split(" ")[1]);
		
		System.out.println(evt.getActionCommand());
		
		if (replyStatus == XOServerReply.AUTHENTIFICATIONS_IS_SUCCESFULL) {
			JOptionPane.showMessageDialog(frame, "Авторизация прошла успешно!", "Авторизация", JOptionPane.INFORMATION_MESSAGE);
			client.removeActionListener(this);
			frame.switchComponent();
		}

		if (replyStatus == XOServerReply.THIS_NAME_IS_ALREADY_TAKEN) {
			JOptionPane.showMessageDialog(frame, "Такое имя уже занято!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
		}

		if (replyStatus == XOServerReply.OPERATION_WAS_NOT_SUCCESFUL) {
			JOptionPane.showMessageDialog(frame, "Неизвестная ошибка!!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
		}
	}
}