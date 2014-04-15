package org.ultimate.xoandroid;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class XOSquare extends View {

	private Paint p;
	private int count;
	private int state = -1;
	private boolean isClicable = true;
	private final List<OnClickListener> listeners = new ArrayList<OnClickListener>();
	private final RectF mRectF = new RectF();

	public XOSquare(Context ctx) {
		super(ctx);
		p = new Paint();
		this.setMinimumHeight(100);
		this.setMinimumWidth(100);
	}

	public XOSquare(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		p = new Paint();
		this.setMinimumHeight(100);
		this.setMinimumWidth(100);
	}

	public XOSquare(Context context, AttributeSet attrs) {
		super(context, attrs);
		p = new Paint();
		this.setMinimumHeight(100);
		this.setMinimumWidth(100);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int viewWidthHalf = this.getMeasuredWidth();
		int viewHeightHalf = this.getMeasuredHeight();

		p.setAntiAlias(true);
		p.setColor(Color.WHITE);
		p.setStyle(Style.FILL);
		canvas.drawRect(0, 0, viewWidthHalf, viewHeightHalf, p);
		p.setColor(Color.BLACK);
		p.setStrokeWidth(1);
		p.setStyle(Style.STROKE);
		canvas.drawRect(0, 0, viewWidthHalf, viewHeightHalf, p);

		if (getState() == 0) {
			p.setColor(Color.BLACK);
			p.setStyle(Style.STROKE);
			p.setStrokeWidth(2.0f);
			canvas.drawLine(0, 0, viewWidthHalf, viewHeightHalf, p);
			canvas.drawLine(viewHeightHalf, 0, 0, viewHeightHalf, p);
		}

		if (getState() == 1) {
			p.setColor(Color.BLACK);
			p.setStyle(Style.STROKE);
			p.setStrokeWidth(2.0f);
			mRectF.set(0, 0, viewWidthHalf, viewHeightHalf);
			canvas.drawOval(mRectF, p);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		click();
		return false;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int size = width > height ? height : width;
		setMeasuredDimension(size, size);
	}

	public void addActionListener(OnClickListener l) {
		listeners.add(l);
	}

	public void click() {
		for (OnClickListener l : listeners) {
			l.onClick(this);
		}
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(int state) {
		if (isClicable()) {
			setClicable(false);
			this.state = state;
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

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

}