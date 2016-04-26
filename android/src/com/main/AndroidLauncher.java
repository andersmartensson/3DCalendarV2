package com.main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import view.MainView;

public class AndroidLauncher extends AndroidApplication {
	private static Context context;

	@Override
	protected void onCreate (Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		if(getContext().checkCallingOrSelfPermission("android.permission.INTERNET") == PackageManager.PERMISSION_GRANTED) {
			Gdx.app.log("INFO", "PERMISSION GRANTED");
			System.out.println("Permission granted");
			context = getContext();
		} else {
			Gdx.app.log("ERROR", "PERMISSION DENIED");
			System.out.println("Permission denied");
			Gdx.app.exit();
		}
		initialize(new MainView(), config);
		//Statics.Andorid_Context = context;

	}
}
