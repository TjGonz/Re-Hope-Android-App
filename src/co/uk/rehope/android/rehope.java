package co.uk.rehope.android;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class rehope extends TabActivity implements TabHost.TabContentFactory {
	/** Called when the activity is first created. */
	public String cityURL = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		TabHost tabHost = getTabHost();
		Intent tab1 = new Intent(this, Home.class);
		Intent tab2 = new Intent(this, Events.class);
		Intent tab3 = new Intent(this, Messages.class);
		Intent tab4 = new Intent(this, TheCity.class);
		tabHost.addTab(tabHost.newTabSpec("home").setIndicator("Home")
				.setContent(tab1));
		tabHost.addTab(tabHost.newTabSpec("events").setIndicator("Events")
				.setContent(tab2));
		tabHost.addTab(tabHost.newTabSpec("messages").setIndicator("Messages")
				.setContent(tab3));
		tabHost.addTab(tabHost.newTabSpec("city").setIndicator("The City")
				.setContent(tab4));

		setupUI();
	}

	public void switchTab(int tab) {
		final RadioButton rbSecond = (RadioButton) findViewById(R.id.second);
		rbSecond.setChecked(true);
	}

	public View createTabContent(String tag) {
		TextView tv = new TextView(this);
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(20);
		if (tag.equals("home")) {
			tv.setText("Home");
		} else if (tag.equals("events")) {
			tv.setText("Events");
		} else if (tag.equals("messages")) {
			tv.setText("Messages");
		} else if (tag.equals("city")) {
			tv.setText("The City");
		}
		return tv;
	}

	private void setupUI() {
		final RadioButton rbFirst = (RadioButton) findViewById(R.id.first);
		final RadioButton rbSecond = (RadioButton) findViewById(R.id.second);
		final RadioButton rbThird = (RadioButton) findViewById(R.id.third);
		final RadioButton rbFourth = (RadioButton) findViewById(R.id.fourth);
		rbFirst.setButtonDrawable(R.drawable.home);
		rbSecond.setButtonDrawable(R.drawable.events);
		rbThird.setButtonDrawable(R.drawable.messages);
		rbFourth.setButtonDrawable(R.drawable.city);
		RadioGroup rg = (RadioGroup) findViewById(R.id.states);
		rbFirst.setButtonDrawable(getResources().getDrawable(
				R.drawable.home_active));
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, final int checkedId) {
				switch (checkedId) {
				case R.id.first:
					getTabHost().setCurrentTab(0);
					rbFirst.setButtonDrawable(getResources().getDrawable(
							R.drawable.home_active));
					rbSecond.setButtonDrawable(getResources().getDrawable(
							R.drawable.events));
					rbThird.setButtonDrawable(getResources().getDrawable(
							R.drawable.messages));
					rbFourth.setButtonDrawable(getResources().getDrawable(
							R.drawable.city));
					break;
				case R.id.second:
					getTabHost().setCurrentTab(1);
					rbSecond.setButtonDrawable(getResources().getDrawable(
							R.drawable.events_active));
					rbFirst.setButtonDrawable(getResources().getDrawable(
							R.drawable.home));
					rbThird.setButtonDrawable(getResources().getDrawable(
							R.drawable.messages));
					rbFourth.setButtonDrawable(getResources().getDrawable(
							R.drawable.city));
					break;
				case R.id.third:
					getTabHost().setCurrentTab(2);
					rbThird.setButtonDrawable(getResources().getDrawable(
							R.drawable.messages_active));
					rbFirst.setButtonDrawable(getResources().getDrawable(
							R.drawable.home));
					rbSecond.setButtonDrawable(getResources().getDrawable(
							R.drawable.events));
					rbFourth.setButtonDrawable(getResources().getDrawable(
							R.drawable.city));
					break;
				case R.id.fourth:
					getTabHost().setCurrentTab(3);
					rbFourth.setButtonDrawable(getResources().getDrawable(
							R.drawable.city_active));
					rbFirst.setButtonDrawable(getResources().getDrawable(
							R.drawable.home));
					rbSecond.setButtonDrawable(getResources().getDrawable(
							R.drawable.events));
					rbThird.setButtonDrawable(getResources().getDrawable(
							R.drawable.messages));
					break;
				}
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			cityURL = extras.getString("cityURL");
			if (cityURL != null) {
				final RadioButton rbFourth = (RadioButton) findViewById(R.id.fourth);
				rbFourth.setChecked(true);

			}
		}
	}

	public void launchActivity(Intent intent) {
		startActivityForResult(intent, 0);

	}

}
