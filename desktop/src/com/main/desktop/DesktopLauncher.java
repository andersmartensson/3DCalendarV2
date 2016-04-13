package com.main.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Calendar";
		config.width = 1920;
		config.height = 1080;
		config.foregroundFPS = 30;
		config.vSyncEnabled = true;
		config.fullscreen = false;
		new LwjglApplication(new view.MainView(), config);
	}
}
