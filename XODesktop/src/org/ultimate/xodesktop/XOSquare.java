package org.ultimate.xodesktop;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class XOSquare extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int count;
	private int state = -1;
	private boolean isClicable = true;
	private final List<ActionListener> listeners = new ArrayList<ActionListener>();

	public XOSquare(int count) {
		setCount(count);
		setName(String.valueOf(count));
		setSize(128, 128);
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent evt) {
				if(isClicable()){
				System.out.println("Клик по поле с номером: "
						+ ((XOSquare) (evt.getComponent())).getCount());
				click();
				}
			}

			@Override
			public void mouseEntered(MouseEvent evt) {
			}

			@Override
			public void mouseExited(MouseEvent evt) {
			}

			@Override
			public void mousePressed(MouseEvent evt) {
			}

			@Override
			public void mouseReleased(MouseEvent evt) {
			}

		});
	}

	public void addActionListener(ActionListener l) {
		listeners.add(l);
	}

	public void click() {
		ActionEvent event = new ActionEvent(this, 0, String.valueOf(this
				.getCount()));
		for (ActionListener l : listeners) {
			l.actionPerformed(event);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		if (getState() == -1) {
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, 128, 127);
		}

		if (getState() == 0) {
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, 128, 127);
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(3.0f));
			g2d.drawLine(10, 10, 118, 118);
			g2d.drawLine(10, 118, 118, 10);
			g2d.setStroke(new BasicStroke(1.0f));
		}

		if (getState() == 1) {
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, 128, 127);
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(3.0f));
			g2d.drawOval(5, 5, 118, 118);
			g2d.setStroke(new BasicStroke(1.0f));
		}

		g2d.setStroke(new BasicStroke(3.0f));
		g2d.setColor(Color.BLACK);
		g2d.drawRect(0, 0, 128, 127);

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(128, 128);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		if (isClicable()) {
			setClicable(false);
			this.state = state;
			repaint();
		}
	}

	/**
	 * @return the isClicable
	 */
	public boolean isClicable() {
		return isClicable;
	}

	/**
	 * @param isClicable
	 *            the isClicable to set
	 */
	public void setClicable(boolean isClicable) {
		this.isClicable = isClicable;
	}
}