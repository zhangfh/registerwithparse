package com.burns.android.registerwithparse;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import android.app.Application;

public class RegisterWithParseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		Parse.initialize(this, "iVbgkSyJwhsq7drmoo2Dyc3hC7cEhnx9VJfzB6uU", "8TamQ7KDAQTsdjYC9NppmIOB5EZvbR6md0TXWLg3");


		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		// Optionally enable public read access.
		// defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
	}

}
