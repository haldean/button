package org.haldean.gesturecontrol;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.LinearLayout;

public class OrientationTrackerActivity extends Activity implements
		SensorEventListener {
	private static final int SAMPLE_SIZE = 50;
	private float lastSamplesX[], lastSamplesY[];
	private boolean listenMode = false;
	private boolean showingSettings = false;
	
	private ColorViewFragment mColorFragment;
	private ServerPrefsFragment mPrefsFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_orientation_tracker);
		
		mColorFragment = new ColorViewFragment();
		mPrefsFragment = new ServerPrefsFragment();

		lastSamplesX = new float[SAMPLE_SIZE];
		lastSamplesY = new float[SAMPLE_SIZE];

		SensorManager manager = (SensorManager) getApplicationContext()
				.getSystemService(SENSOR_SERVICE);
		manager.registerListener(this,
				manager.getDefaultSensor(Sensor.TYPE_GRAVITY),
				SensorManager.SENSOR_DELAY_FASTEST);
		manager.registerListener(this,
				manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);

		findViewById(R.id.fragment_container).setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (showingSettings) {
							return false;
						}
						if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
							startListenMode();
						} else if (event.getActionMasked() == MotionEvent.ACTION_CANCEL
								|| event.getActionMasked() == MotionEvent.ACTION_UP) {
							endListenMode();
						}
						return true;
					}
				});
		
		setShowingSettings(false);
		ServerPreferences sp = ServerPreferences.getInstance(PreferenceManager
				.getDefaultSharedPreferences(this));
		if (sp.getHostAddress() == null) {
			setShowingSettings(true);
		}
	}
	
	public void setShowingSettings(boolean showing) {
		showingSettings = showing;
		
		Fragment repl = showingSettings ? mPrefsFragment : mColorFragment;
		FragmentManager manager = getFragmentManager();
		FragmentTransaction tx = manager.beginTransaction();
		tx.replace(R.id.fragment_container, repl);
		tx.addToBackStack(null);
		tx.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_orientation_tracker, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * Called when receiving an update and in listen mode.
	 */
	private void onUpdate(float x, float y) {
		float hmin = -5, hmax = 5;
		// Constrain y to [min, max]
		y = y > hmax ? hmax : y < hmin ? hmin : y;
		// Map [min, max] to [0, 1]
		double h = (y - hmin) / (hmax - hmin);
		// Take pow to give more weight to the red-green area of the spectrum
		h = Math.pow(h, 2);

		float vmin = 0, vmax = 5;
		// Constrain x to [min, max]
		x = x > vmax ? vmax : x < vmin ? vmin : x;
		// Map [min, max] to [0, 1]
		double v = (x - vmin) / (vmax - vmin);
		v = Math.pow(v, 2);

		Intent lightUpdateIntent = new Intent(this, LightsService.class);
		lightUpdateIntent.putExtra("hue", h);
		lightUpdateIntent.putExtra("value", v);

		mColorFragment.setColor(listenMode, h, 1., v);
		if (listenMode)
			startService(lightUpdateIntent);
	}

	private void startListenMode() {
		listenMode = true;
	}

	private void endListenMode() {
		listenMode = false;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (showingSettings) return;
		if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
			float x, y;
			x = event.values[0];
			y = event.values[1];

			onUpdate(x, y);

			for (int i = 0; i < SAMPLE_SIZE; i++) {
				if (i < SAMPLE_SIZE - 1) {
					lastSamplesX[i] = lastSamplesX[i + 1];
					lastSamplesY[i] = lastSamplesY[i + 1];
				} else {
					lastSamplesX[i] = x;
					lastSamplesY[i] = y;
				}
			}
		}
	}
}
