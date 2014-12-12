package com.example.exercise_motivator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.signup.R;

public class CalorieActivity extends Activity {

	public EditText calorieText;
	private String cal = null;
	private String id = "123";
	
	private static String PATH_URL = "http://192.168.0.4:8080/ExerciseMotiator-web/webresources/Cal";

	private Button stopBtn;
	private CaloriesBR calBR;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calorie);
		initValue();
		calBR = new CaloriesBR();
		IntentFilter filterSend = new IntentFilter();
		filterSend.addAction("EM.CALORIE");
		filterSend.addAction("EM.ID");
		registerReceiver(calBR, filterSend);

		Intent intent1 = new Intent("EM.REQUEST");
		sendBroadcast(intent1);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	void initValue() {
		this.calorieText = (EditText) findViewById(R.id.kalorieText);
		this.stopBtn = (Button) findViewById(R.id.stopCalBtn);

		stopBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				new GetTask().execute();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calorie, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class CaloriesBR extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.d("BR GET", "BR GET");
			if (action.equals("EM.CALORIE")) {
				String calS = intent.getStringExtra("cal");
				Log.d("BRCAL", calS);
				cal = calS;
				if (!cal.isEmpty())
					calorieText.setText("Calories : " + cal);
			} else if (action.equals("EM.ID")) {
				String idS = intent.getStringExtra("ID");
				Log.d("BRID", idS);
				id = idS;
			}
		}

	}

	class GetTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			System.out.println("AsyncTask is doing");

			String data = null;
			HttpClient client = new DefaultHttpClient();

			String param = "?id=" + id + "&cal=" + cal;
			String sFinal = PATH_URL + param;
			HttpGet get = new HttpGet(sFinal);

			get.addHeader("Content-Type", "application/json");

			try {
				// StringEntity se = new StringEntity(data);

				// get.setEntity(se);

				HttpResponse response = client.execute(get);

				HttpEntity entity = response.getEntity();

				data = EntityUtils.toString(entity);

				if (!data.equals("success")) {
					System.out.println("SUCCESS");
				} else {
					System.out.println("FAIL");
				}

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d("IOExeption", "IOError");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
	}
}
