package org.haldean.gesturecontrol;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class LightsService extends IntentService {
  /* The required percent difference between updates. */
  private static final String SERVER_ADDR = "tau.haldean.org";
  private static final int SERVER_PORT = 9000;

  private Socket sock;

  static final double MIN_DIFF = 0.05f;
  static double lastHueUpdate = Double.NaN;
  static double lastValueUpdate = Double.NaN;

  public LightsService() {
    super("LightsService");
  }

  private void establishConnection() {
    try {
      InetAddress addr = InetAddress.getByName(SERVER_ADDR);
      sock = new Socket(addr, SERVER_PORT);
    } catch (IOException e) {
      Log.e("LightsService", "Couldn't establish sockets; falling back to HTTP API.", e);
      sock = null;
    }
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    double hue = intent.getDoubleExtra("hue", 0);
    double value = intent.getDoubleExtra("value", 1);

    if (sock == null) establishConnection();
    
    boolean hueShouldChange = (
        Math.abs(hue - lastHueUpdate) / lastHueUpdate > MIN_DIFF
        || Double.isNaN(lastHueUpdate));
    boolean valueShouldChange = (
        Math.abs(value - lastValueUpdate) / lastValueUpdate > MIN_DIFF
        || Double.isNaN(lastValueUpdate));

    if (hueShouldChange || valueShouldChange) {
      lastHueUpdate = hue;
      lastValueUpdate = value;

      // If the socket is available we use it, if not, use the HTTP API.
      if (sock != null) {
        byte[] buffer = (Double.toString(hue) + ",1," + Double.toString(value) + "\n").getBytes();
        try {
          sock.getOutputStream().write(buffer);
        } catch (IOException e) {
          Log.e("LightsService", "Couldn't write to open socket; falling back to HTTP API.", e);
          sock = null;
        }
        
      } else {
        try {
          URL updateUrl = new URL(
              "http://192.168.2.115/hsvapi?h=" + Double.toString(hue) +
              "&s=1&v=" + Double.toString(value));
          HttpURLConnection conn = (HttpURLConnection) updateUrl.openConnection();
          Scanner stream = new Scanner(conn.getInputStream());
          while (stream.hasNext()) { stream.nextLine(); }
          stream.close();
          conn.disconnect();

        } catch (Exception e) {
          Log.e("LightsService", "Failed to ping update URL.", e);
        }
      }
    }
  }
}
