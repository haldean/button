package org.haldean.gesturecontrol;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ColorViewFragment extends Fragment {
	private ColorView colorView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
		View v = inflater.inflate(R.layout.fragment_color_view, container, false);
		colorView = (ColorView) v.findViewById(R.id.color);
		return v;
	}
	
	public void setColor(boolean listenMode, double h, double d, double v) {
		if (colorView != null) {
			colorView.setColor(listenMode, h, d, v);
		} else {
			Log.e("ColorViewFragment", "color view is null");
		}
	}
}
