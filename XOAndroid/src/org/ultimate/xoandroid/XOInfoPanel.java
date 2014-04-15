package org.ultimate.xoandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class XOInfoPanel extends View {
	private Paint p = new Paint();
	private String message = new String();
	private boolean isCenter;

	public XOInfoPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public XOInfoPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public XOInfoPanel(Context context) {
		super(context);
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
		p.setStyle(Style.STROKE);
		canvas.drawRect(0, 0, viewWidthHalf, viewHeightHalf, p);
		p.setTextSize(24);
		canvas.drawText(message, (viewWidthHalf >> 1) - (message.length() * 6), (viewHeightHalf >> 1) + 5, p);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		setMeasuredDimension(metrics.widthPixels, ((metrics.heightPixels - metrics.widthPixels)>>1));
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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