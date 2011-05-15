package co.uk.rehope.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageDetail extends Activity {

	private String messageTitle = "";
	private String messageContent = "";
	private String messageImageURL = "";
	private String messageMP3URL = "";
	private String messageDate = "";
	private DrawableManager dm;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_message);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			messageTitle = extras.getString("messageTitle");
			messageContent = extras.getString("messageContent");
			messageImageURL = extras.getString("messageImageURL");
			messageMP3URL = extras.getString("messageMP3URL");
			messageDate = extras.getString("messageDate");
		}

		setTitle("Re:Hope - " + messageTitle);

		TextView title = (TextView) findViewById(R.id.detail_title);
		title.setText(messageTitle);

		TextView content = (TextView) findViewById(R.id.detail_content);
		content.setText(messageContent);

		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try {
			date = curFormater.parse(messageDate);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
			String monthName = dateFormat.format(date);
			dateFormat = new SimpleDateFormat("dd");
			String dayName = dateFormat.format(date);
			TextView day = (TextView) findViewById(R.id.detail_day);
			day.setText(dayName);
			TextView month = (TextView) findViewById(R.id.detail_month);
			month.setText(monthName);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dm = new DrawableManager();
		ImageView header = (ImageView) findViewById(R.id.header);
		dm.fetchDrawableOnThread(messageImageURL, header);

		Button cityButton = (Button) findViewById(R.id.city_button);
		cityButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				try {
					Intent i = new Intent(Intent.ACTION_VIEW).setDataAndType(
							Uri.parse(messageMP3URL), "audio/mp3");
					startActivity(i);
				} catch (Exception e) {
					Toast
							.makeText(
									MessageDetail.this,
									"No application found to stream audio. Please click the sermon link to download the message.",
									Toast.LENGTH_LONG).show();
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse("http://rehope.co.uk/media.html"));
					startActivity(i);
				}

			}
		});

	}

}
