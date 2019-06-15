package com.hr.techlabapp.CustomViews;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;

public final class ViewFinderResultPointCallback implements ResultPointCallback {

	private final ViewFinderView viewfinderView;

	public ViewFinderResultPointCallback(ViewFinderView viewfinderView) {
		this.viewfinderView = viewfinderView;
	}

	@Override
	public void foundPossibleResultPoint(ResultPoint point) {
		viewfinderView.addPossibleResultPoint(point);
	}
}