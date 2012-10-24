package org.haldean.gesturecontrol;

import android.content.SharedPreferences;

final class ServerPreferences {
  private static final String ADDR_KEY = "addr";
  private static final String HTTP_PORT_KEY = "http_port";
  private static final String SOCKET_PORT_KEY = "socket_port";
  
  private static ServerPreferences instance = null;
  
  static ServerPreferences getInstance() {
    return instance;
  }
  
  static ServerPreferences getInstance(SharedPreferences prefs) {
    if (instance == null) {
      instance = new ServerPreferences(prefs);
    }
    return instance;
  }
  
  private SharedPreferences prefs;
  
  private ServerPreferences(SharedPreferences prefs) {
    this.prefs = prefs;
  }
  
  String getHostAddress() {
    return prefs.getString(ADDR_KEY, null);
  }
  
  void setHostAddress(String addr) {
    prefs.edit().putString(ADDR_KEY, addr);
  }
  
  int getHostHttpPort() {
    return prefs.getInt(HTTP_PORT_KEY, -1);
  }
  
  void setHostHttpPort(int port) {
    prefs.edit().putInt(HTTP_PORT_KEY, port);
  }

  int getHostSocketPort() {
    return prefs.getInt(SOCKET_PORT_KEY, -1);
  }
  
  void setHostSocketPort(int port) {
    prefs.edit().putInt(SOCKET_PORT_KEY, port);    
  }  
}
