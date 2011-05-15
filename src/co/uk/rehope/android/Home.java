package co.uk.rehope.android;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import co.uk.rehope.android.ReHopeGallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Home extends Activity implements LocationListener {
	/** Called when the activity is first created. */
	
	//these fields must be set!
	
	//Set to your WordPress blog's xml-rpc url
	//e.g. private String blogURL = "appblog.com/xmlrpc.php";
	private String blogURL = "http://test.com";
	
	//GPS coordinates of church location
	private double latitude = 55.872443;
	private double longitude = -4.30476;
	
	//seriously, set the above fields!
	
	public String[] headerURLs;
	public String[] featureLinks;
	public Bitmap largeEventImage;
	public Bitmap smallEventImage;
	public HashMap largeEventText;
	public HashMap smallEventText;
	private XMLRPCClient client;

	private String largeEventTitle = "";
	private String largeEventContent = "";
	private String largeEventImageURL = "";
	private String largeEventCityURL = "";
	private String largeEventDate = "";

	private String smallEventTitle = "";
	private String smallEventContent = "";
	private String smallEventImageURL = "";
	private String smallEventCityURL = "";
	private String smallEventDate = "";

	LocationManager locationManager;
	private int ID_DIALOG_REFRESHING = 1;
	ProgressDialog loadingDialog;
	private boolean countDownIsRunning = false;
	ProgressBar pb;
	DrawableManager dm;
	boolean largeScreen = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		getFeaturedImages(false);
		String test = "hi";

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		if (width > 480 || height > 480) {
			largeScreen = true;
		}
		if (!largeScreen) {
			RelativeLayout smallEvents = (RelativeLayout) findViewById(R.id.events_small);
			smallEvents.setVisibility(View.GONE);
		}

	}

	private void getFeaturedImages(boolean force) {

		ReHopeDB rehopeDB = new ReHopeDB(Home.this);
		long lastRefresh = rehopeDB.getLastRefresh(Home.this);

		long now = System.currentTimeMillis();
		long test = now - lastRefresh;
		if (((now - lastRefresh) > 604800000) || force) {
			if (lastRefresh == 0) {
				rehopeDB.setLastRefresh(Home.this, true);
			} else {
				rehopeDB.setLastRefresh(Home.this, false);
			}
			new getHomeTabDataTask().execute();
		} else {
			Vector events = rehopeDB.loadEvents(this);
			dm = new DrawableManager();

			if (events.size() > 0) {

				for (int i = 0; i < 2; i++) {
					HashMap<?, ?> contentHash = (HashMap<?, ?>) events.get(i);
					SimpleDateFormat curFormater = new SimpleDateFormat(
							"yyyy-MM-dd");
					try {
						Date date = curFormater.parse(contentHash.get("date")
								.toString());
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"MMM");
						String monthName = dateFormat.format(date);
						dateFormat = new SimpleDateFormat("dd");
						String dayName = dateFormat.format(date);
						if (i == 0) {
							ImageView lrgImage = (ImageView) findViewById(R.id.events_square_image);
							largeEventImageURL = contentHash.get("imageURL")
									.toString();
							dm.fetchDrawableOnThread(largeEventImageURL,
									lrgImage);
							TextView event_title = (TextView) findViewById(R.id.events_home_event_title);
							largeEventTitle = contentHash.get("title")
									.toString();
							event_title.setText(largeEventTitle);
							TextView event_content = (TextView) findViewById(R.id.events_home_event_content);
							largeEventContent = (contentHash.get("content")
									.toString());
							event_content.setText(largeEventContent);
							TextView event_day = (TextView) findViewById(R.id.events_home_date_digits);
							event_day.setText(dayName);
							TextView event_month = (TextView) findViewById(R.id.events_home_date_month_name);
							event_month.setText(monthName);
							largeEventDate = (contentHash.get("date")
									.toString());
							largeEventCityURL = (contentHash.get("cityURL")
									.toString());
						} else {
							smallEventImageURL = contentHash.get("imageURL")
									.toString();
							smallEventTitle = contentHash.get("title")
									.toString();
							smallEventContent = (contentHash.get("content")
									.toString());
							smallEventDate = (contentHash.get("date")
									.toString());
							smallEventCityURL = (contentHash.get("cityURL")
									.toString());
							ImageView lrgImage = (ImageView) findViewById(R.id.events_small_square_image);
							dm.fetchDrawableOnThread(smallEventImageURL,
									lrgImage);
							TextView small_event_title = (TextView) findViewById(R.id.events_home_small_event_title);
							small_event_title.setText(smallEventTitle);
							TextView small_event_day = (TextView) findViewById(R.id.events_home_small_date_digits);
							small_event_day.setText(dayName);
							TextView small_event_month = (TextView) findViewById(R.id.events_home_small_date_month_name);
							small_event_month.setText(monthName);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}

			Vector features = rehopeDB.loadFeatures(this);

			if (features.size() > 0) {
				headerURLs = new String[features.size()];
				featureLinks = new String[features.size()];
				for (int i = 0; i < features.size(); i++) {
					HashMap<?, ?> contentHash = (HashMap<?, ?>) features.get(i);
					headerURLs[i] = contentHash.get("imageURL").toString();
					featureLinks[i] = contentHash.get("link").toString();
				}
			}

			timerTime();

			RelativeLayout eventsLarge = (RelativeLayout) findViewById(R.id.events_large);
			RelativeLayout eventsSmall = (RelativeLayout) findViewById(R.id.events_small);
			RelativeLayout homeFooter = (RelativeLayout) findViewById(R.id.home_footer);
			eventsLarge.setVisibility(View.VISIBLE);
			eventsSmall.setVisibility(View.VISIBLE);
			homeFooter.setVisibility(View.VISIBLE);

			eventsLarge.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Intent intent = new Intent(Home.this, EventDetail.class);
					intent.putExtra("eventTitle", largeEventTitle);
					intent.putExtra("eventContent", largeEventContent);
					intent.putExtra("eventImageURL", largeEventImageURL);
					intent.putExtra("eventCityURL", largeEventCityURL);
					intent.putExtra("eventDate", largeEventDate);
					rehope ParentActivity = (rehope) Home.this.getParent();
					ParentActivity.launchActivity(intent);

				}
			});

			eventsSmall.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Intent intent = new Intent(Home.this, EventDetail.class);
					intent.putExtra("eventTitle", smallEventTitle);
					intent.putExtra("eventContent", smallEventContent);
					intent.putExtra("eventImageURL", smallEventImageURL);
					intent.putExtra("eventCityURL", smallEventCityURL);
					intent.putExtra("eventDate", smallEventDate);
					rehope ParentActivity = (rehope) Home.this.getParent();
					ParentActivity.launchActivity(intent);

				}
			});

		}

	}

	public class ImageAdapter extends BaseAdapter {
		// int mGalleryItemBackground;
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
			/*
			 * TypedArray a = obtainStyledAttributes(R.styleable.HelloGallery);
			 * mGalleryItemBackground = a.getResourceId(
			 * R.styleable.HelloGallery_android_galleryItemBackground, 0);
			 * a.recycle();
			 */
		}

		public int getCount() {
			if (headerURLs != null) {
				return headerURLs.length;
			} else {
				return 0;
			}
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			ImageView i = (ImageView) inflater.inflate(R.layout.slider_image,
					parent, false);

			// i.setImageResource(mImageIds[position]);
			// i.setImageDrawable(dm.fetchDrawable());
			try {
				dm.fetchDrawableOnThread(headerURLs[position].toString(), i);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			// i.setBackgroundResource(mGalleryItemBackground);

			return i;
		}
	}

	private class getHomeTabDataTask extends AsyncTask<String, Void, Boolean> {

		protected void onPreExecute() {
			pb = (ProgressBar) findViewById(R.id.progress);
			pb.setVisibility(View.VISIBLE);
		}

		protected void onPostExecute(Boolean error) {

			if (error) {
				// bad connection
				pb.setVisibility(View.INVISIBLE);
				Toast
						.makeText(
								Home.this,
								"Sorry, there was a connection error. Please try again later.",
								Toast.LENGTH_SHORT).show();
			} else {
				pb.setVisibility(View.INVISIBLE);
				new getEventsTask().execute();
			}
		}

		@Override
		protected Boolean doInBackground(String... args) {

			client = new XMLRPCClient(blogURL);
			boolean connectionError = false;
			Object[] result = null;
			ReHopeDB rehopeDB = new ReHopeDB(Home.this);
			rehopeDB.clearFeatures(Home.this);
			try {
				result = (Object[]) client.call("rehope.getHomeTabData");
				HashMap contentHash = new HashMap();
				int ctr = 0;
				headerURLs = new String[3];
				for (Object item : result) {
					contentHash = (HashMap) result[ctr];
					if (ctr < 3) {
						rehopeDB.saveFeature(Home.this, contentHash);
						headerURLs[ctr] = contentHash.get("image").toString();
					}
					ctr++;
				}
				return false;
			} catch (XMLRPCException e) {
				e.printStackTrace();
				Log.i("REHOPE", e.getLocalizedMessage());
				connectionError = true;
			}

			if (connectionError) {
				return true;
			}
			HashMap contentHash = new HashMap();
			String[] test = new String[result.length];
			test = (String[]) result;
			// contentHash = (HashMap) result;

			return false;
		}

	}

	private class getEventsTask extends AsyncTask<String, Void, Boolean> {

		protected void onPreExecute() {
			// showDialog(ID_DIALOG_REFRESHING);
		}

		protected void onPostExecute(Boolean error) {

			if (error) {
				// bad connection
				// dismissDialog(ID_DIALOG_REFRESHING);
				Toast
						.makeText(
								Home.this,
								"Sorry, there was a connection error. Please try again later.",
								Toast.LENGTH_SHORT).show();
			} else {
				new getMessagesTask().execute();
			}
		}

		@Override
		protected Boolean doInBackground(String... args) {

			client = new XMLRPCClient(blogURL);
			ReHopeDB rehopeDB = new ReHopeDB(Home.this);
			rehopeDB.clearEvents(Home.this);
			boolean connectionError = false;
			Object[] result = null;
			try {
				result = (Object[]) client.call("rehope.getEvents");
				HashMap contentHash = new HashMap();
				int ctr = 0;
				if (result != null) {
					rehopeDB.clearEvents(Home.this);
					for (Object item : result) {
						contentHash = (HashMap) result[ctr];
						rehopeDB.saveEvent(Home.this, contentHash);
						ctr++;
					}
				}
				return false;
			} catch (XMLRPCException e) {
				connectionError = true;
			}

			if (connectionError) {
				return true;
			}
			HashMap contentHash = new HashMap();
			String[] test = new String[result.length];
			test = (String[]) result;
			// contentHash = (HashMap) result;

			return false;
		}

	}

	private class getMessagesTask extends AsyncTask<String, Void, Boolean> {

		protected void onPreExecute() {
			// showDialog(ID_DIALOG_REFRESHING);
		}

		protected void onPostExecute(Boolean error) {

			if (error) {
				// bad connection
				// dismissDialog(ID_DIALOG_REFRESHING);
				Toast
						.makeText(
								Home.this,
								"Sorry, there was a connection error. Please try again later.",
								Toast.LENGTH_SHORT).show();
			} else {
				getFeaturedImages(false);
			}
		}

		@Override
		protected Boolean doInBackground(String... args) {

			client = new XMLRPCClient(blogURL);
			ReHopeDB rehopeDB = new ReHopeDB(Home.this);
			rehopeDB.clearMessages(Home.this);
			boolean connectionError = false;
			Object[] result = null;
			try {
				result = (Object[]) client.call("rehope.getMessages");
				HashMap contentHash = new HashMap();
				int ctr = 0;
				if (result != null) {
					rehopeDB.clearMessages(Home.this);
					for (Object item : result) {
						contentHash = (HashMap) result[ctr];
						long now = System.currentTimeMillis();
						rehopeDB.saveMessage(Home.this, contentHash);
						ctr++;
					}
				}
				return false;
			} catch (XMLRPCException e) {
				connectionError = true;
			}

			if (connectionError) {
				return true;
			}
			HashMap contentHash = new HashMap();
			String[] test = new String[result.length];
			test = (String[]) result;
			// contentHash = (HashMap) result;

			return false;
		}

	}

	public void attemptGPSFix() {
		/*
		 * locationManager = (LocationManager)
		 * getSystemService(Context.LOCATION_SERVICE); //Location location =
		 * locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		 * locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		 * 1000L, 100.0f, Home.this); if (location != null) { //GPS coords for
		 * kelvin stevenson memorial church Location reHopeLocation = new
		 * Location("reverseGeocoded");
		 * reHopeLocation.setLatitude(55.87756181344028);
		 * reHopeLocation.setLongitude(-4.278960227966309); int distance =
		 * (int)location.distanceTo(reHopeLocation);
		 * 
		 * TextView tvDistance = (TextView)
		 * findViewById(R.id.home_footer_loc_label);
		 * tvDistance.setText("You are ~" + distance + "m from Re:Hope");
		 * 
		 * } else {
		 * locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		 * 1000L, 100.0f, Home.this); }
		 */

	}

	public void timerTime() {
		final TextView hoursDigit = (TextView) findViewById(R.id.home_footer_hours_digit);
		final TextView minutesDigit = (TextView) findViewById(R.id.home_footer_minutes_digit);
		final TextView secondsDigit = (TextView) findViewById(R.id.home_footer_seconds_digit);

		ReHopeGallery gallery = (ReHopeGallery) findViewById(R.id.gallery);
		gallery.setAdapter(new ImageAdapter(Home.this));
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				if (featureLinks[position].equals("events")) {
					rehope ParentActivity = (rehope) Home.this.getParent();
					ParentActivity.switchTab(1);
				} else if (featureLinks[position].equals("messages")) {
					rehope ParentActivity = (rehope) Home.this.getParent();
					ParentActivity.switchTab(2);
				} else {
					try {
						URL linkURL = new URL(featureLinks[position]);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						Uri data = Uri.parse(featureLinks[position]);
						intent.setData(data);
						startActivity(intent);
					} catch (MalformedURLException e) {

					}
				}

			}
		});

		Date today = new Date();
		Calendar cal = Calendar.getInstance();

		// set timer to countdown to next Sunday at 11am or 6:30pm
		cal.setTime(today);
		if (cal.get(Calendar.DAY_OF_WEEK) > 1) {
			cal.add(Calendar.WEEK_OF_MONTH, 1);
			cal.set(Calendar.DAY_OF_WEEK, 1);
			cal.set(Calendar.HOUR_OF_DAY, 11);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
		} else if (cal.get(Calendar.HOUR_OF_DAY) < 11) {
			cal.set(Calendar.HOUR_OF_DAY, 11);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
		} else if (cal.get(Calendar.HOUR_OF_DAY) > 11 && cal.get(Calendar.HOUR_OF_DAY) < 19) {
			cal.set(Calendar.HOUR_OF_DAY, 18);
			cal.set(Calendar.MINUTE, 30);
			cal.set(Calendar.MILLISECOND, 0);
		} else {
			cal.add(Calendar.WEEK_OF_MONTH, 1);
			cal.set(Calendar.DAY_OF_WEEK, 1);
			cal.set(Calendar.HOUR_OF_DAY, 11);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}

		// calculate miliseconds from start date
		long milisecLeft = cal.getTime().getTime() - today.getTime();

		if (milisecLeft > 604800000) {
			milisecLeft -= 604800000;
		}

		// timer time
		if (!countDownIsRunning) {
			new CountDownTimer(milisecLeft, 1000) {
				private String sHours;
				private String sMinutes;
				private String sSeconds;

				public void onTick(long millisUntilFinished) {
					countDownIsRunning = true;
					int hours = (int) (millisUntilFinished / 3600000);
					if (hours < 10) {
						sHours = "0";
					} else {
						sHours = "";
					}
					hoursDigit.setText(sHours + String.valueOf(hours));
					int minutes = (int) (millisUntilFinished % 3600000) / 60000;
					if (minutes < 10) {
						sMinutes = "0";
					} else {
						sMinutes = "";
					}
					minutesDigit.setText(sMinutes + String.valueOf(minutes));
					int seconds = (int) ((millisUntilFinished % 60000) / 1000);
					if (seconds < 10) {
						sSeconds = "0";
					} else {
						sSeconds = "";
					}
					secondsDigit.setText(sSeconds + String.valueOf(seconds));

				}

				public void onFinish() {
					// hoursDigit.setText("Now!");
				}
			}.start();
		}

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Location location =
		// locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		boolean gps_enabled = false;
		boolean network_enabled = false;

		// exceptions will be thrown if provider is not permitted.
		try {
			gps_enabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			network_enabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		if (gps_enabled)
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, Home.this);
		if (network_enabled)
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, Home.this);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000L, 100.0f, Home.this);

		RelativeLayout eventsLarge = (RelativeLayout) findViewById(R.id.events_large);
		RelativeLayout eventsSmall = (RelativeLayout) findViewById(R.id.events_small);
		RelativeLayout homeFooter = (RelativeLayout) findViewById(R.id.home_footer);
		eventsLarge.setVisibility(View.VISIBLE);
		eventsSmall.setVisibility(View.VISIBLE);
		homeFooter.setVisibility(View.VISIBLE);

	}

	public void onLocationChanged(Location location) {
		if (location != null) {
			
			String provider = location.getProvider();
			Location churchLocation = new Location("reverseGeocoded");
			// change to your church's lat/long
			churchLocation.setLatitude(latitude);
			churchLocation.setLongitude(longitude);
			
			int distance = (int) location.distanceTo(churchLocation);

			TextView tvDistance = (TextView) findViewById(R.id.home_footer_loc_label);
			tvDistance.setText("You are ~" + distance + "m from Re:Hope");
			if (location.equals("gps")) {
				locationManager.removeUpdates(Home.this);
			}
		}

	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void onPause() {
		super.onPause();
		if (locationManager != null) {
			locationManager.removeUpdates(Home.this);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == ID_DIALOG_REFRESHING) {
			loadingDialog = new ProgressDialog(this);
			loadingDialog.setTitle("Refreshing");
			loadingDialog
					.setMessage("Please wait while we get the latest and greatest Re:Hope info!");
			loadingDialog.setCancelable(true);
			return loadingDialog;
		}

		return super.onCreateDialog(id);
	}

	// Add settings to menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "Refresh");
		MenuItem menuItem1 = menu.findItem(0);
		menuItem1.setIcon(R.drawable.ic_menu_refresh);

		return true;
	}

	// Menu actions
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			getFeaturedImages(true);
			return true;
		}
		return true;

	}

	public void locationClickHandler(View target) {
		// change to your church's lat/long
		String uri = "geo:" + String.valueOf(latitude) + "," + String.valueOf(longitude);
		startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri
				.parse(uri)));
	}
}
