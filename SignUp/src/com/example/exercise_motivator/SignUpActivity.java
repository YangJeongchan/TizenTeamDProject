package com.example.exercise_motivator;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.signup.R;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.google.gson.Gson;

/**
 * Handles the user registration activity.
 * 
 */
public class SignUpActivity extends FragmentActivity {

	// Constants
	private static final int SELECT_IMAGE = 1;
	private UiLifecycleHelper uiHelper;
	// Path
	private static String PATH_URL = "http://192.168.0.4:8080/ExerciseMotiator-web/webresources/JoinService";
	//private static String FILE_URL = "http://192.168.0.4:8080/NewExerciseMotivator-web/webresources/UploadFile";

	// For Facebook
	 private static final String PERMISSION = "publish_actions";
	private static final List<String> READ_PERMISSIONS = Arrays
			.asList("user_status");
	private static final List<String> PUBLISH_PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final List<String> PERMISSIONS = Arrays.asList(
			"user_status", "publish_actions");
	private PendingAction pendingAction = PendingAction.NONE;
	protected static final String TAG = null;
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	// UI
	private EditText newUserId;
	private EditText newPassword;
	private EditText newConfiPass;
	private ImageView faceImage;
	private Button selectPicButton;
	private Button registerButton;
	private GraphUser user;
	private Button backButton;

	private LoginButton authButton;

	private byte[] image;
	private String token;

	private Bitmap imageBitmap;
	private File imageFile;

	private Empeople personInfo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		uiHelper = new UiLifecycleHelper(this, null);
	    uiHelper.onCreate(savedInstanceState);
		personInfo = new Empeople();
		initControls();
		checkHashKey();
		Session.openActiveSession(this, true, statusCallback);
	}
	
	 @Override
	public View onCreateView(String name, @NonNull Context context,
			@NonNull AttributeSet attrs) {
		// TODO Auto-generated method stub
		// authButton.setFragment(this);
		return super.onCreateView(name, context, attrs);
	}

	private enum PendingAction {
	        NONE,
	        POST_PHOTO,
	        POST_STATUS_UPDATE
	    }
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	private void checkHashKey(){
		 try {
		        PackageInfo info = getPackageManager().getPackageInfo(
		                "com.example.signup", 
		                PackageManager.GET_SIGNATURES);
		        for (Signature signature : info.signatures) {
		            MessageDigest md = MessageDigest.getInstance("SHA");
		            md.update(signature.toByteArray());
		            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
		            }
		    } catch (NameNotFoundException e) {

		    } catch (NoSuchAlgorithmException e) {

		    }
	}
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Log.i("Facebook Activity", "Logged in...");
			String token = session.getAccessToken();
			personInfo.setToken(token);
			System.out.println("Token is " + session.getAccessToken());
		} else if (state.isClosed()) {
			if (Session.getActiveSession() != null) {
				Session.getActiveSession().closeAndClearTokenInformation();
			}

			Session.setActiveSession(null);
			Log.i("Facebook Activity", "Logged out...");
		}
		
		if (pendingPublishReauthorization && 
		        state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
		    pendingPublishReauthorization = false;
		    //publishStory();
		}
	}
	/**
	 * Set interface control
	 */
	private void initControls() {
		newUserId = (EditText) findViewById(R.id.nUserId);
		newPassword = (EditText) findViewById(R.id.nPassword);
		newConfiPass = (EditText) findViewById(R.id.nConfiPass);
		faceImage = (ImageView) findViewById(R.id.nFaceImage);
		selectPicButton = (Button) findViewById(R.id.nSeletPicture);
		registerButton = (Button) findViewById(R.id.nRegister);
		authButton = (LoginButton) findViewById(R.id.fbButton);
		backButton = (Button) findViewById(R.id.nBack);
		
		selectPicButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				doSelectImage();
			}
		});

		registerButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				registerMe();
			}
		});

		backButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				backToLogin();
				//publishStory();
			}
		});
		
		authButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Session currentSession = Session.getActiveSession();
				if (currentSession == null
						|| currentSession.getState().isClosed()) {
					Session session = new Session.Builder(SignUpActivity.this)
							.build();
					Session.setActiveSession(session);
					currentSession = session;
				}

				if (currentSession.isOpened()) {
					// Do whatever u want. User has logged in

				} else if (!currentSession.isOpened()) {
					// Ask for username and password
					OpenRequest op = new Session.OpenRequest(
							SignUpActivity.this);

					op.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
					op.setCallback(statusCallback);
					op.setPermissions(PERMISSIONS);

					Session session = new Session.Builder(SignUpActivity.this)
							.build();
					Session.setActiveSession(session);
					if (session.getPermissions() == null)
						session.openForPublish(op);
				}
			}
		});

		authButton.setUserInfoChangedCallback(new UserInfoChangedCallback() {

			@Override
			public void onUserInfoFetched(GraphUser user) {
				// TODO Auto-generated method stub
				SignUpActivity.this.user = user;
				if (user != null) {
					Log.d("Login?", "Hello, " + user.getName());
				} else {
					Log.d("Not login?", "You are not logged");
				}
			}
		});
	}

	/**
	 * Clear all forms
	 */

	

	private void clearForm() {
		newUserId.setText("");
		newPassword.setText("");
		newConfiPass.setText("");
	}

	/**
	 * Take user back to login.
	 */
	private void backToLogin() {
		finish();
	}
	 private boolean hasPublishPermission() {
	        Session session = Session.getActiveSession();
	        return session != null && session.getPermissions().contains("publish_actions");
	    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		 uiHelper.onResume();
		 Session session = Session.getActiveSession();
		    if (session!=null) {
		         //personInfo.setToken(session.getAccessToken());
		    	 onSessionStateChange(session, session.getState(), null);
		         pendingAction = PendingAction.POST_PHOTO;
		         if(hasPublishPermission()){
		        	 personInfo.setToken(session.getAccessToken());
		        	 //handlePhto()...
		         }else if(session.isOpened()){
		        	 session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
		         }
		    }
		    
		    
	}
	/**
	 * Select picture in your folder
	 */
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
	 * Handles the registration process.
	 * 
	 * @param v
	 */
	private void registerMe() {
		// Get user details.
		String userId = newUserId.getText().toString();
		String password = newPassword.getText().toString();
		String confirmpassword = newConfiPass.getText().toString();
		//String token = "Dfasdfasfsaf";
		
		personInfo.setIdname(userId);
		personInfo.setPw(password);
		//p.setToken(token);

		BitmapDrawable d = (BitmapDrawable) faceImage.getDrawable();
		Bitmap imageB = d.getBitmap();

		File fileDir = getApplicationContext().getFilesDir();

		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String fileName = "image.jpeg";
		File filechche = new File(fileDir.getAbsolutePath() + fileName);

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

		new GetTask().execute(personInfo);
		new PostFile().execute();
		
		// Check if all fields have been completed.
		// if (userId.equals("") || password.equals("") ||
		// confirmpassword.equals("")) {
		// Toast.makeText(getApplicationContext(),
		// "Please ensure all fields have been completed.",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		//
		// // Check password match.
		// if (!password.equals(confirmpassword)) {
		// Toast.makeText(getApplicationContext(),
		// "The password does not match.", Toast.LENGTH_SHORT).show();
		// return;
		// }

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		super.onActivityResult(requestCode, resultCode, intent);
		 Session.getActiveSession().onActivityResult(this, requestCode, resultCode, intent);
		  uiHelper.onActivityResult(requestCode, resultCode, intent, new FacebookDialog.Callback() {
		        @Override
		        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
		            Log.e("Activity", String.format("Error: %s", error.toString()));
		        }

		        @Override
		        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
		            Log.i("Activity", "Success!");
		        }
		    });
		token = intent.getStringExtra("token");
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
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
			
			
				break;
			}
			}
		}
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
	private String signUpInfoToJSON(Empeople signUpInfo) {
		Gson gson = new Gson();
		String json = gson.toJson(signUpInfo);
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
	class GetTask extends AsyncTask<Empeople, String, String> {

		@Override
		protected String doInBackground(Empeople... params) {
			// TODO Auto-generated method stub
			System.out.println("AsyncTask is doing");
			Empeople p = params[0];
			String data = signUpInfoToJSON(p);
			String fileName = personInfo.getIdname()+"face";
			personInfo.setFileName(fileName);
			HttpClient client = new DefaultHttpClient();
			personInfo.setFileName(personInfo.getIdname()+"face");
			String param = "?id=" + personInfo.getIdname() + "&pw="
					+ personInfo.getPw() + "&token=" +personInfo.getToken()+
					 "&fileName=" + personInfo.getFileName();
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
					personInfo.setFileName(data +"-1"+ ".jpg");
					System.out.println("Request sent successfully!");

				} else {
					
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
			System.out.println("AsyncTask is doing");
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(PATH_URL);
			
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
				StringBody comment = new StringBody("Filename: " + personInfo.getId() +"-1.pgm");
				MultipartEntity reqEntity = new MultipartEntity();
				reqEntity.addPart("file", fileContent);
				httppost.setEntity(reqEntity);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity resEntity = response.getEntity();
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
