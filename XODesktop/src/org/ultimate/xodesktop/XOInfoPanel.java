package org.ultimate.xodesktop;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class XOInfoPanel extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2127878325558440349L;
	private String message = new String();
	private Dimension preferredSize;
	private boolean isCenter;

	public XOInfoPanel(int width, int height) {
		preferredSize = new Dimension(width, height);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, preferredSize.width, preferredSize.height - 1);
		g2d.setStroke(new BasicStroke(3.0f));
		g2d.setColor(Color.BLACK);
		g2d.drawRect(0, 0, preferredSize.width, preferredSize.height - 1);
		if (isCenter()) {
			g2d.drawString(message,
					(preferredSize.width >> 1) - (message.length()*3),
					(preferredSize.height >> 1) + 5);
		} else {
			g2d.drawString(message, 5, (preferredSize.height >> 1) + 5);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
		this.repaint();
	}

	/**
	 * @param isCenter
	 *            the isCenter to set
	 */
	public void setCenter(boolean isCenter) {
		this.isCenter = isCenter;
	}

	/**
	 * @return the isCenter
	 */
	public boolean isCenter() {
		return isCenter;
	}

}