package org.haldean.gesturecontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ColorView extends View {
  private Paint colorPaint, strokePaint, dimStrokePaint;
  private RectF drawRect;
  private boolean drawStroke;
  
  public ColorView(Context ctx, AttributeSet attrs) {
    super(ctx, attrs);
    colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    strokePaint.setStyle(Paint.Style.STROKE);
    strokePaint.setARGB(255, 255, 255, 255);
    strokePaint.setStrokeWidth(20);
    
    dimStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    dimStrokePaint.setStyle(Paint.Style.STROKE);
    dimStrokePaint.setARGB(255, 50, 50, 50);
    dimStrokePaint.setStrokeWidth(20);
    
    drawRect = new RectF(0, 0, 0, 0);
  }
  
  public void setColor(boolean stroke, double h, double s, double v) {
    float[] hsv = {(float) h * 360f, (float) s, (float) v};
    colorPaint.setColor(Color.HSVToColor(hsv));
    drawStroke = stroke;
    invalidate();
  }
  
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawOval(drawRect, colorPaint);
    canvas.drawOval(drawRect, drawStroke ? strokePaint : dimStrokePaint);
  }
    
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int padding = getPaddingLeft() + getPaddingRight();
    int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec) - padding;
    int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec) - padding;
    int dim = width < height ? width : height;
    setMeasuredDimension(dim, dim);
    drawRect.set(10, 10, dim - 20, dim - 20);
  }
}
