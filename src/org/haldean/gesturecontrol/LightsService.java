package org.haldean.gesturecontrol;

import java.io.IOException;
import java.io.OutputStream;
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
  static String hostAddr = "tau.haldean.org";
  static int socketPort = 9000;
  static int httpPort = 80;

  static final double MIN_DIFF = 0.05f;
  static double lastHueUpdate = Double.NaN;
  static double lastValueUpdate = Double.NaN;

  private Socket sock;
  
  public LightsService() {
    super("LightsService");
    hostAddr = ServerPreferences.getInstance().getHostAddress();
    socketPort = ServerPreferences.getInstance().getHostSocketPort();
    httpPort = ServerPreferences.getInstance().getHostHttpPort();
  }

  private void establishConnection() {
    try {
      InetAddress addr = InetAddress.getByName(hostAddr);
      sock = new Socket(addr, socketPort);
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
          OutputStream out = sock.getOutputStream();
          out.write(buffer);
          out.flush();
        } catch (IOException e) {
          Log.e("LightsService", "Couldn't write to open socket; falling back to HTTP API.", e);
          sock = null;
        }
        
      } else {
        try {
          URL updateUrl = new URL(
              "http://" + hostAddr + ":" + httpPort + "/hsvapi?h=" + Double.toString(hue) +
              "&s=1&v=" + Double.toString(value));
          
          HttpURLConnection conn = (HttpURLConnection) updateUrl.openConnection();
          
          // Force the reading of the stream
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
