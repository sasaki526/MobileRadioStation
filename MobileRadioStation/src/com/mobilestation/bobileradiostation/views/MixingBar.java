package com.mobilestation.bobileradiostation.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class MixingBar extends SeekBar {

	public MixingBar(Context context){
		super(context);
	}
	
	public MixingBar(Context context, AttributeSet attrs) {
		super(context, attrs);	
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		
		canvas.rotate(270);
		canvas.translate(-getHeight(),0);
		
		super.onDraw(canvas);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(),getMeasuredWidth());
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if ( !isEnabled()){
			return false;
		}
		
		int id = event.getAction();
		if ( MotionEvent.ACTION_DOWN == id || 
				MotionEvent.ACTION_UP == id ||
				MotionEvent.ACTION_MOVE == id ){
				
			int i = 0;
			i = getMax() - (int)( getMax() * event.getY() / getHeight());
			setProgress(i);
			onSizeChanged(getWidth(),getHeight(),0,0);
			
		}else{
			
		}
		return true;
	}
	
	

}
