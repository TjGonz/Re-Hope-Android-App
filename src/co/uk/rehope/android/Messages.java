package co.uk.rehope.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Messages extends ListActivity {
	private String[] messageImageURLs;
	private String[] messageTitles;
	private String[] messageContents;
	private String[] messageDates;
	private String[] messageMp3URLs;
	private messageListAdapter ela;
	private DrawableManager dm;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messages);

		loadMessages();

	}

	protected void onResume() {
		super.onResume();
		loadMessages();
	}

	void loadMessages() {
		ReHopeDB rehopeDB = new ReHopeDB(this);
		Vector messages = rehopeDB.loadMessages(this);
		dm = new DrawableManager();

		if (messages != null) {
			messageImageURLs = new String[messages.size()];
			messageTitles = new String[messages.size()];
			messageContents = new String[messages.size()];
			messageDates = new String[messages.size()];
			messageMp3URLs = new String[messages.size()];

			for (int i = 0; i < messages.size(); i++) {
				HashMap<?, ?> contentHash = (HashMap<?, ?>) messages.get(i);
				messageImageURLs[i] = contentHash.get("imageURL").toString();
				messageTitles[i] = contentHash.get("title").toString();
				messageContents[i] = contentHash.get("content").toString();
				messageDates[i] = contentHash.get("date").toString();
				messageMp3URLs[i] = contentHash.get("mp3URL").toString();
			}

			ela = new messageListAdapter(this);
			setListAdapter(ela);

			ListView listView = getListView();
			listView.setDivider(null);
			listView.setDividerHeight(6);
			listView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					if (arg1 != null) {
						Intent intent = new Intent(Messages.this,
								MessageDetail.class);
						intent
								.putExtra("messageTitle",
										messageTitles[position]);
						intent.putExtra("messageContent",
								messageContents[position]);
						intent.putExtra("messageImageURL",
								messageImageURLs[position]);
						intent.putExtra("messageMP3URL",
								messageMp3URLs[position]);
						intent.putExtra("messageDate", messageDates[position]);
						rehope ParentActivity = (rehope) Messages.this
								.getParent();
						ParentActivity.launchActivity(intent);
					}

				}

			});
		}
	}

	private class messageListAdapter extends BaseAdapter {

		public messageListAdapter(Context context) {
		}

		public int getCount() {
			return messageImageURLs.length;
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
				Date date = curFormater.parse(messageDates[position]);
				SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
				String monthName = dateFormat.format(date);
				dateFormat = new SimpleDateFormat("dd");
				String dayName = dateFormat.format(date);
				wrapper.getDay().setText(dayName);
				wrapper.getMonth().setText(monthName);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			wrapper.getTitle().setText(messageTitles[position]);
			// wrapper.getDate().setText(messageDates[position]);
			dm.fetchDrawableOnThread(messageImageURLs[position], wrapper
					.getImage());
			// pv.setBackgroundDrawable(getResources().getDrawable(R.drawable.events_row_selector));
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
