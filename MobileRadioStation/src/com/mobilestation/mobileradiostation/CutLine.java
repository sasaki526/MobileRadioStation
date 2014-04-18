package com.mobilestation.mobileradiostation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CutLine extends View {
	 
	  private Paint mPaint;
	  private float imageX;
	  private float imageY;
	  private Bitmap mBitmap;
	 
	  public CutLine(Context context, AttributeSet attrs) {
	   super(context, attrs);
	   mPaint = new Paint();
	  }
	 
	  @Override
	  protected void onDraw(Canvas canvas) {
		  mPaint.setColor(Color.argb(255, 0, 0, 0));
		  // canvas.drawLine(0, 0, 10, 20, mPaint);
	    float[] pts = {50, 100, 50, 400};
	    canvas.drawLines(pts, mPaint);

/*	   mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
	   canvas.drawBitmap(mBitmap, imageX - mBitmap.getWidth() / 2,
	     imageY - mBitmap.getHeight() / 2, mPaint);*/
	  }
	 
	  @Override
	  public boolean onTouchEvent(MotionEvent event) {
	   // êGÇÈ
	   if (event.getAction() == MotionEvent.ACTION_DOWN) {
		   imageX = event.getX();
		   imageY = event.getY();
	   }
	   // êGÇ¡ÇΩÇ‹Ç‹ÉXÉâÉCÉh
	   else if (event.getAction() == MotionEvent.ACTION_MOVE) {
		   imageX = event.getX();
		   imageY = event.getY();
	   }
	 
	   // ó£Ç∑
	   else if (event.getAction() == MotionEvent.ACTION_UP) {
		   imageX = event.getX();
		   imageY = event.getY();
	   }
	 
	   // çƒï`âÊÇÃéwé¶
	   invalidate();
	 
	   return true;
	  }
	 
	 }
