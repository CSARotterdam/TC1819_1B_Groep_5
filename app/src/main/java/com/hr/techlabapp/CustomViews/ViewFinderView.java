package com.hr.techlabapp.CustomViews;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.camera.core.CameraCaptureMetaData;
import androidx.camera.core.CameraDeviceSurfaceManager;
import androidx.camera.core.CameraInfo;
import androidx.core.content.ContextCompat;

import com.google.zxing.ResultPoint;
import com.hr.techlabapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public class ViewFinderView extends View {

	private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
	private static final long ANIMATION_DELAY = 80L;
	private static final int CURRENT_POINT_OPACITY = 0xA0;
	private static final int MAX_RESULT_POINTS = 20;
	private static final int POINT_SIZE = 6;

	private final Paint paint;
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;
	private final int laserColor;
	private final int resultPointColor;
	private int scannerAlpha;
	private List<ResultPoint> possibleResultPoint;
	private List<ResultPoint> lastPossibleResultPoints;

	private Rect frame;

	private Rect previewFrame;

	public ViewFinderView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

		// Initialize these once for performance rather than calling them every time in onDraw().
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		maskColor = ContextCompat.getColor(getContext(), R.color.viewfinder_mask);
		resultColor = ContextCompat.getColor(getContext(), R.color.result_view);
		laserColor = ContextCompat.getColor(getContext(), R.color.viewfinder_laser);
		resultPointColor = ContextCompat.getColor(getContext(), R.color.possible_result_points);
		scannerAlpha = 0;
		possibleResultPoint = new ArrayList<>(5);
		lastPossibleResultPoints = null;
	}

	public void setFrame(Rect frame, Rect previewFrame) {
		this.frame = frame;
		this.previewFrame = previewFrame;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (frame == null | previewFrame == null) {
			return;
		}
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		if (resultBitmap != null) {
			paint.setAlpha(CURRENT_POINT_OPACITY);
			canvas.drawBitmap(resultBitmap, null, frame, paint);
		} else {
			// Draw a red "laser scanner" line through the middle to show decoding is active
			paint.setColor(laserColor);
			paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
			scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
			int middle = frame.height() / 2 + frame.top;
			canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

			float scaleX = frame.width() / (float) previewFrame.width();
			float scaleY = frame.height() / (float) previewFrame.height();
			List<ResultPoint> currentPossible = possibleResultPoint;
			List<ResultPoint> currentLast = lastPossibleResultPoints;

			int frameLeft = frame.left;
			int frameTop = frame.top;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				possibleResultPoint = new ArrayList<>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(CURRENT_POINT_OPACITY);
				paint.setColor(resultPointColor);
				synchronized (currentPossible) {
					for (ResultPoint point : currentPossible) {
						canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
								frameTop + (int) (point.getY()),
								POINT_SIZE, paint);
					}
				}
			}
			if (currentLast != null) {
				paint.setAlpha(CURRENT_POINT_OPACITY / 2);
				paint.setColor(resultPointColor);
				synchronized (currentLast) {
					float radius = POINT_SIZE / 2.0F;
					for (ResultPoint point : currentLast) {
						canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
								frameTop + (int) (point.getY() * scaleY),
								radius, paint);
					}
				}
			}

			// Request another update at the animation interval, but only repaint the laser line,
			// not the entire viewfinder mask.
			postInvalidateDelayed(ANIMATION_DELAY,
					frame.left - POINT_SIZE,
					frame.top - POINT_SIZE,
					frame.right + POINT_SIZE,
					frame.bottom + POINT_SIZE);
		}
	}


	public void drawViewFinder() {
		Bitmap resultBitmap = this.resultBitmap;
		this.resultBitmap = null;
		if (resultBitmap != null) {
			resultBitmap.recycle();
		}
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		List<ResultPoint> points = possibleResultPoint;
		synchronized (points) {
			points.add(point);
			int size = points.size();
			if (size > MAX_RESULT_POINTS) { // trim it
				points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
			}
		}
	}
}
