package com.main;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import view.MainView;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new MainView(), config);
		if(getContext().checkCallingOrSelfPermission("android.permission.INTERNET") == PackageManager.PERMISSION_GRANTED) {
			Gdx.app.log("INFO", "PERMISSION GRANTED");
			System.out.println("Permission granted");
		} else {
			Gdx.app.log("ERROR", "PERMISSION DENIED");
			System.out.println("Permission denied");
			Gdx.app.exit();
		}
	}
}
