package org.haldean.gesturecontrol;

import android.content.SharedPreferences;

final class ServerPreferences {
	private static final String ADDR_KEY = "addr";
	private static final String HTTP_PORT_KEY = "http_port";
	private static final String SOCKET_PORT_KEY = "socket_port";

	private static ServerPreferences instance = null;

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

	int getHostHttpPort() {
		return Integer.valueOf(prefs.getString(HTTP_PORT_KEY, "80"));
	}

	int getHostSocketPort() {
		return Integer.valueOf(prefs.getString(SOCKET_PORT_KEY, "9000"));
	}
}
