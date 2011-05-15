package co.uk.rehope.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Events extends ListActivity {
	private String[] eventImageURLs;
	private String[] eventTitles;
	private String[] eventContents;
	private String[] eventDates;
	private String[] eventCityURLs;
	private EventListAdapter ela;
	private DrawableManager dm;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);

		loadEvents();

	}

	protected void onResume() {
		super.onResume();
		loadEvents();
	}

	void loadEvents() {
		ReHopeDB rehopeDB = new ReHopeDB(this);
		Vector events = rehopeDB.loadEvents(this);
		dm = new DrawableManager();

		if (events != null) {
			eventImageURLs = new String[events.size()];
			eventTitles = new String[events.size()];
			eventContents = new String[events.size()];
			eventDates = new String[events.size()];
			eventCityURLs = new String[events.size()];

			for (int i = 0; i < events.size(); i++) {
				HashMap<?, ?> contentHash = (HashMap<?, ?>) events.get(i);
				eventImageURLs[i] = contentHash.get("imageURL").toString();
				eventTitles[i] = contentHash.get("title").toString();
				eventContents[i] = contentHash.get("content").toString();
				eventDates[i] = contentHash.get("date").toString();
				eventCityURLs[i] = contentHash.get("cityURL").toString();
			}

			ela = new EventListAdapter(this);
			setListAdapter(ela);

			ListView listView = getListView();

			listView.setDivider(null);
			listView.setDividerHeight(6);
			listView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					if (arg1 != null) {
						Intent intent = new Intent(Events.this,
								EventDetail.class);
						intent.putExtra("eventTitle", eventTitles[position]);
						intent
								.putExtra("eventContent",
										eventContents[position]);
						intent.putExtra("eventImageURL",
								eventImageURLs[position]);
						intent
								.putExtra("eventCityURL",
										eventCityURLs[position]);
						intent.putExtra("eventDate", eventDates[position]);
						rehope ParentActivity = (rehope) Events.this
								.getParent();
						ParentActivity.launchActivity(intent);
					}

				}

			});
		}
	}

	private class EventListAdapter extends BaseAdapter {

		public EventListAdapter(Context context) {
		}

		public int getCount() {
			return eventImageURLs.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View pv = convertView;
			ViewWrapper wrapper = null;
			if (pv == null) {
				LayoutInflater inflater = getLayoutInflater();
				pv = inflater.inflate(R.layout.events_row, parent, false);
				wrapper = new ViewWrapper(pv);
				pv.setTag(wrapper);
				wrapper = new ViewWrapper(pv);
				pv.setTag(wrapper);
			} else {
				wrapper = (ViewWrapper) pv.getTag();
			}

			SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date date = curFormater.parse(eventDates[position]);
				SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
				String monthName = dateFormat.format(date);
				dateFormat = new SimpleDateFormat("dd");
				String dayName = dateFormat.format(date);
				wrapper.getDay().setText(dayName);
				wrapper.getMonth().setText(monthName);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			wrapper.getTitle().setText(eventTitles[position]);
			dm.fetchDrawableOnThread(eventImageURLs[position], wrapper
					.getImage());
			return pv;

		}

	}

	class ViewWrapper {
		View base;
		TextView title = null;
		TextView day = null;
		TextView month = null;
		ImageView image = null;

		ViewWrapper(View base) {
			this.base = base;
		}

		TextView getTitle() {
			if (title == null) {
				title = (TextView) base.findViewById(R.id.title);
			}
			return (title);
		}

		TextView getDay() {
			if (day == null) {
				day = (TextView) base.findViewById(R.id.day);
			}
			return (day);
		}

		TextView getMonth() {
			if (month == null) {
				month = (TextView) base.findViewById(R.id.month);
			}
			return (month);
		}

		ImageView getImage() {
			if (image == null) {
				image = (ImageView) base.findViewById(R.id.image);
			}
			return (image);
		}
	}

}
