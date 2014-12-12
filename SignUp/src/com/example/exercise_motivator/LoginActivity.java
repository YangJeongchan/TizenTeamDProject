package com.example.exercise_motivator;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.exercise_motivator.HelloAccessoryProviderService.LocalIBinder;
import com.example.signup.R;
import com.facebook.Session;
import com.google.gson.Gson;

public class LoginActivity extends FragmentActivity {
	// Constants
	private static final int LOGIN_SUCCCESS = 1;
	private static final int LOGIN_FAIL = 2;
	private static final int LOGOUT = 3;
	private static final int SELECT_IMAGE = 4;

	// For Facebook
	private static final List<String> READ_PERMISSIONS = Arrays
			.asList("user_status");
	private static final List<String> PUBLISH_PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final List<String> PERMISSIONS = Arrays.asList(
			"user_status", "publish_actions");

	private String tempID = "1";
	// Path
	private static String PATH_URL = "http://192.168.0.4:8080/ExerciseMotiator-web/webresources/LoginService";
	// private static String FILE_URL =
	// "http://192.168.0.4:8080/ExerciseMotiator-web/webresources/LoginService";
	private boolean m_bound = false;

	private HelloAccessoryProviderService m_helloService;
	private RequestBR reBR;
	private ServiceConnection mConnection  = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			m_bound = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.d("SERVICESTART", "Service is started");
			LocalIBinder binder = (LocalIBinder) service;
			m_helloService = binder.getService();
			m_bound = true;
//			Intent intent = new Intent("ID");
//
//			intent.putExtra("id", tempID); // should change
//			sendBroadcast(intent);
		}
	};
	public class RequestBR extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Log.d("BR REQUEST", "BR REQUEST");
			if (action.equals("EM.REQUEST")) {
				Intent intent1 = new Intent("EM.ID");
				String ids = null;
//				if(personInfo != null)
//					 ids = personInfo.getId().toString(); 
//				else
					ids = tempID;
					
				intent1.putExtra("ID",ids );
				sendBroadcast(intent1);
			}else if(action.equals("EM.FACEOK"))
			{
				showFaceRecognition(1);
			}
			else if(action.equals("EM.FACEFAIL"))
			{
				showFaceRecognition(0);
			}
		}
	}

	private void showFaceRecognition(int result){
		if( result == 0) //fail
		{
			Toast.makeText(getApplicationContext(),"Face is not correct" , Toast.LENGTH_LONG).show();
		}
		else
		{
			Toast.makeText(getApplicationContext(),"Face is correct" , Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Intent intent = new Intent(this, HelloAccessoryProviderService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		
	}

	// UI
	private EditText editUserId;
	private EditText editPassword;
	private Button loginButton;
	private Button registerButton;
	private Button picButton;
	private ImageView faceImage;
	private Empeople personInfo;
	private String token;
	private File imageFile;
	private boolean returnFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		personInfo = new Empeople();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setupVariables();
		
		reBR = new RequestBR();
		IntentFilter filterSend = new IntentFilter();
	    filterSend.addAction("EM.REQUEST");
	    filterSend.addAction("EM.FACEOK");
	    filterSend.addAction("EM.FACEFAIL");
	    registerReceiver(reBR, filterSend);
	}

	/**
	 * Set up variables
	 */

	private void setupVariables() {
		editUserId = (EditText) findViewById(R.id.usernameET);
		editPassword = (EditText) findViewById(R.id.passwordET);
		loginButton = (Button) findViewById(R.id.loginBtn);
		picButton = (Button) findViewById(R.id.picBtn);
		// fbLoginButton = (Button) findViewById(R.id.fbLoginBtn);
		registerButton = (Button) findViewById(R.id.regsiterBtn);
		faceImage = (ImageView) findViewById(R.id.faceImage);
		returnFlag = true;
		loginButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// goKaloriePage();
				 if (loginButton.getText().toString().equals("Logout")) {
				 Intent i = new Intent();
				 startActivityForResult(i, LOGOUT);
				 return;
				 }
				
				 String userId = editUserId.getText().toString();
				 String userPw = editPassword.getText().toString();
				
				 personInfo.setIdname(userId);
				 personInfo.setPw(userPw);
				 Log.d("Befor asnyck", userPw);
				 new GetTask().execute(personInfo);
				 new PostFile().execute(personInfo);
				
				 if(returnFlag)
				 {
					 returnFlag = false;
				 	Toast.makeText(getApplicationContext(),"Your face is wrong",0 );
				 }
			}
		});

		picButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doSelectImage();
			}
		});

		registerButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				goRegister();
			}
		});

	}

	private void doSelectImage() {
		Intent i = new Intent(Intent.ACTION_PICK);
		i.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);

		try {
			startActivityForResult(i, SELECT_IMAGE);
		} catch (android.content.ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Execute SignUpActivity
	 */
	private void goRegister() {
		Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
		i.putExtra("token", personInfo.getToken());
		startActivity(i);
	}

	private String loginInfoToJSON(Empeople p) {
		Gson gson = new Gson();
		String json = gson.toJson(p);

		return json;
	}

	private boolean copyFile(File file, String save_file) {
		boolean result;
		if (file != null && file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				FileOutputStream newfos = new FileOutputStream(save_file);
				int readcount = 0;
				byte[] buffer = new byte[1024];
				while ((readcount = fis.read(buffer, 0, 1024)) != -1) {
					newfos.write(buffer, 0, readcount);
				}
				newfos.close();
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	private void goKaloriePage() {
//		Intent i = new Intent(getApplicationContext(), CalorieActivity.class);
//		startActivity(i);
	
		
	}
	public String getPath(Uri uri) {
        if( uri == null ) {
            return null;
        }
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case LOGIN_SUCCCESS: {
				loginButton.setText("Logout");
				break;
			}
			case LOGIN_FAIL: {
				break;
			}
			case LOGOUT: {
				loginButton.setText("Login");
				break;
			}
			case SELECT_IMAGE: {
				 Uri selectedImageUri = intent.getData();
				 if (Build.VERSION.SDK_INT < 19) {
		                String selectedImagePath = getPath(selectedImageUri);
		                Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
		                faceImage.setImageBitmap(bitmap);

		            }
		            else {
		                ParcelFileDescriptor parcelFileDescriptor;
		                try {
		                    parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r");
		                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
		                    Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
		                    parcelFileDescriptor.close();
		                    faceImage.setImageBitmap(image);

		                } catch (FileNotFoundException e) {
		                    e.printStackTrace();
		                } catch (IOException e) {
		                    // TODO Auto-generated catch block
		                    e.printStackTrace();
		                }
		            }
			
				BitmapDrawable d = (BitmapDrawable) faceImage.getDrawable();
				Bitmap imageB = d.getBitmap();
				File fileDir = getApplicationContext().getFilesDir();
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				String fileName = "image";
				File filechche = new File(fileDir.getAbsolutePath() + "/"
						+ fileName);

				OutputStream out = null;

				try {
					filechche.createNewFile();
					out = new FileOutputStream(filechche);

					imageB.compress(CompressFormat.JPEG, 100, out);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				imageFile = filechche;
				break;
			}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (Session.getActiveSession() != null) {
			Session.getActiveSession().closeAndClearTokenInformation();
		}

		Session.setActiveSession(null);
		Log.i("Facebook Activity", "Logged out...");
	}

	class GetTask extends AsyncTask<Empeople, String, String> {

		@Override
		protected String doInBackground(Empeople... params) {
			// TODO Auto-generated method stub
			System.out.println("AsyncTask is doing");
			Empeople p = params[0];
			String data = loginInfoToJSON(p);
			String fileName = personInfo.getIdname() + "face";
			personInfo.setFileName(fileName);
			HttpClient client = new DefaultHttpClient();
			personInfo.setFileName(personInfo.getIdname() + "face");
			String param = "?id=" + personInfo.getIdname() + "&pw="
					+ personInfo.getPw() + "&fileName="
					+ personInfo.getFileName();
			String sFinal = PATH_URL + param;
			HttpGet get = new HttpGet(sFinal);

			get.addHeader("Content-Type", "application/json");

			try {
				// StringEntity se = new StringEntity(data);

				// get.setEntity(se);

				HttpResponse response = client.execute(get);

				HttpEntity entity = response.getEntity();

				data = EntityUtils.toString(entity);

				if (!data.equals("false")) {
					System.out.println("Data get Success");
					personInfo.setId((long) Integer.parseInt(data));
					personInfo.setFileName(data + ".jpg");
					System.out.println("Request sent successfully!");

				} else {
					returnFlag = true;
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

	class PostFile extends AsyncTask<Empeople, String, String> {

		@Override
		protected String doInBackground(Empeople... params) {
			// TODO Auto-generated method stub
		
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(PATH_URL);

			// String newFilePath =
			// imageFile.getAbsolutePath().replace(imageFile.getName(),
			// "")+personInfo.getFileName();
			// File sdcard = Environment.getExternalStorageDirectory();

			File fileDir = getApplicationContext().getFilesDir();
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			String path = fileDir.getAbsolutePath() + "/"
					+ personInfo.getFileName();
			boolean val = copyFile(imageFile, path);

			File newFile = new File(path);
			FileBody fileContent = new FileBody(newFile);

			try {
				StringBody comment = new StringBody("Filename: "
						+ personInfo.getId() + ".jpg");
				MultipartEntity reqEntity = new MultipartEntity();

				reqEntity.addPart("file", fileContent);
				httppost.setEntity(reqEntity);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity resEntity = response.getEntity();
				String data = EntityUtils.toString(resEntity);

				if (data.equals("success")) {
					
					// Toast.makeText(getApplicationContext(),
					// "Your face is correct",0 );
					Intent intent1 = new Intent("EM.FACEOK");
				
					sendBroadcast(intent1);

				} else {
					
					Intent intent1 = new Intent("EM.FACEFAIL");
					
					sendBroadcast(intent1);
					// returnFlag = true;
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
