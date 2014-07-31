package com.burns.android.registerwithparse;




import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment {
	
	  protected static final String TAG = "LoginFragment";
	private Button   mLoginButton;
	  private Button   mSignupButton;
	  private EditText mUsernameField;
	  private EditText mPasswordField;
	  
	  //private List<ParseObject>  mLogings;
	  private Context mContext;
	  private Dialog  mprogressDialog ;
	  
	  private String mUsername;
	  private String mPassword;
	  private Editor mEditor;
	  
	  public static final int MESSAGE_LOGING_OK = 0;
	  public static final int MESSAGE_STATE_USER_EXIST = 1;
	    public static final int MESSAGE_WRONG_INPUT = 2;
	    public static final int MESSAGE_WRONG_WAY = 3;
	    public static final int MESSAGE_UNKOWN_WAY = 4;
	   public static final int MESSAGE_STATE_USER_NO_EXIST = 5;

	 public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    mContext = this.getActivity();
		    
	 }

	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup parent,
		                           Bundle savedInstanceState) {
		   
			 
		    View v = inflater.inflate(R.layout.login_fragment,
		        parent, false);

		    mLoginButton = (Button) v.findViewById(R.id.login_button);
		    mSignupButton = (Button) v.findViewById(R.id.signup_button);
		    mUsernameField = (EditText) v.findViewById(R.id.login_username_input);
		    mPasswordField = (EditText) v.findViewById(R.id.login_password_input);
		    setUpLoginAndSignup();
		    return v;
	 }
	 private void setUpLoginAndSignup() {
	
	    mLoginButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	          mUsername = mUsernameField.getText().toString();
	          Log.i(TAG,"mUsername:"+mUsername);
	          mPassword = mPasswordField.getText().toString();

	          if (mUsername.length() == 0) {
	            showToast(R.string.ui_no_username_toast);
	          } else if (mPassword.length() == 0) {
	            showToast(R.string.ui_no_password_toast);
	          } else {
	        	  new RemoteDataTask().execute();
	          }
	        }
	      });
	    
	    mSignupButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	          mUsername = mUsernameField.getText().toString();
	          mPassword = mPasswordField.getText().toString();

	          if (mUsername.length() == 0) {
	            showToast(R.string.ui_no_username_toast);
	          } else if (mPassword.length() == 0) {
	            showToast(R.string.ui_no_password_toast);
	          } else {
	        	  //
	  			new RemoteDataTask() {
					protected Void doInBackground(Void... params) {
						//do in here
						Log.i(TAG,"RemoteDataTask sign up");
						String restURL = "http://www.lonshinetech.cn/zfh/device/register_server.php" ;
						RestClient client = new RestClient(restURL);
						client.AddParam("submit", "1");
						client.AddParam("email", mUsername);
						client.AddParam("password1", mPassword);
						client.AddParam("password2", mPassword);
						try {
							client.Execute(RequestMethod.POST);
						} catch (Exception e) {
							e.printStackTrace();
						}

						String response = client.getResponse();
						Log.i(TAG,"response:" + response);
						
						int isSuccess = -1;
						int reason = 0 ;
						try {
						String jsonContent = response.substring(0, response.length());
						
						JSONObject json = new JSONObject(jsonContent);
						
						isSuccess  = json.getInt("success");
						reason  = json.getInt("reason");
						Log.i(TAG,"isSuccess:" + isSuccess + " reason: " + reason);
						} catch (JSONException e) {
							Log.e(TAG,"error:" + e);
						}
						if(isSuccess == 0){
							//continue to sign in
							super.doInBackground();
							return null;
						}
						if((isSuccess == -1) && (reason == 100))
							mHandler.obtainMessage(MESSAGE_STATE_USER_EXIST, -1, -1).sendToTarget();
						else if((isSuccess == -1) && (reason == 101))
							mHandler.obtainMessage(MESSAGE_WRONG_INPUT, -1, -1).sendToTarget();
						else if((isSuccess == -1) && (reason == 102))
							mHandler.obtainMessage(MESSAGE_WRONG_WAY, -1, -1).sendToTarget();
						else
							mHandler.obtainMessage(MESSAGE_UNKOWN_WAY, -1, -1).sendToTarget();
						return null;
					}
				}.execute();
	          }
	        }
	      });
	    
	}
	
	protected void showToast(int id) {
		    showToast(getString(id));
	}

    private  void startNewActivity()
	{
			Intent intent = new Intent(mContext,  MainActivity.class);


	        
			startActivity(intent);
			//((LoginActivity)mContext).finish();
			getActivity().finish();
	}
	protected void showToast(CharSequence text) {
		    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}
		  
	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
			private static final String TAG = "RemoteDataTask";

			// Override this method to do custom remote calls
			protected Void doInBackground(Void... params) {
					// Gets the current list of Login in sorted order
					Log.i(TAG,"sing in");
					String restURL = "http://www.lonshinetech.cn/zfh/device/singin.php" ;
					Log.e(TAG,"restURL: " + restURL);
					RestClient client = new RestClient(restURL);
					client.AddParam("submit", "1");
					client.AddParam("email", mUsername);
					client.AddParam("password1", mPassword);
					client.AddParam("password2", mPassword);
					try {
						client.Execute(RequestMethod.POST);
					} catch (Exception e) {
						e.printStackTrace();
					}

					String response = client.getResponse();
					Log.i(TAG,"response:" + response);
					int isSuccess = -1;
					int reason = 0 ;
					try {
					String jsonContent = response.substring(0, response.length());
					
					JSONObject json = new JSONObject(jsonContent);
					
					isSuccess  = json.getInt("success");
					reason  = json.getInt("reason");
					Log.i(TAG,"isSuccess:" + isSuccess + " reason: " + reason);
					} catch (JSONException e) {
						Log.e(TAG,"error:" + e);
					}
					if((isSuccess == 0) && (reason == 0)){
						mHandler.obtainMessage(MESSAGE_LOGING_OK, -1, -1).sendToTarget();
						return null;
					}
					if((isSuccess == -1) && (reason == 103))
						mHandler.obtainMessage(MESSAGE_STATE_USER_NO_EXIST, -1, -1).sendToTarget();
					else if((isSuccess == -1) && (reason == 101))
						mHandler.obtainMessage(MESSAGE_WRONG_INPUT, -1, -1).sendToTarget();
					else if((isSuccess == -1) && (reason == 102))
						mHandler.obtainMessage(MESSAGE_WRONG_WAY, -1, -1).sendToTarget();
					else
						mHandler.obtainMessage(MESSAGE_UNKOWN_WAY, -1, -1).sendToTarget();
					
					return null;
			}

			@Override
			protected void onPreExecute() {
					Log.i(TAG,"onPreExecute");
					// mprogressDialog = ((LoginActivity)mContext).getProgressDialog();
					 mprogressDialog = ((LoginActivity)getActivity()).getProgressDialog();
					 mprogressDialog = ProgressDialog.show(mContext, "","Loading...", true);
					 super.onPreExecute();
			}

			@Override
			protected void onProgressUpdate(Void... values) {
					super.onProgressUpdate(values);
			}

			@Override
			protected void onPostExecute(Void result) {
		            Log.i(TAG,"onPostExecute");
	
					mprogressDialog.dismiss();

			}
	}

	 private  final Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case MESSAGE_STATE_USER_EXIST:
	            	 showToast(R.string.ui_user_exist);
	            	break;
	            case MESSAGE_WRONG_INPUT:
	            	showToast(R.string.ui_input_wrong);
	            	break;
	            case MESSAGE_WRONG_WAY:
	            	showToast(R.string.ui_wrong_way);
	            	break;
	            case MESSAGE_UNKOWN_WAY:
	            	showToast(R.string.ui_unknown_error);
	            	break;
	            case MESSAGE_LOGING_OK:
	            	Log.i(TAG,"handler loging");
	    	        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0).edit();
	    	        editor.putInt(MainActivity.RegisterStateKey, 1);
	    	        editor.commit();
	            	startNewActivity();
	            	break;
	            case MESSAGE_STATE_USER_NO_EXIST:
	            	showToast(R.string.ui_user_no_exist);
	            	break;
	            default:
	            	break;
	            }
	        }
	 };
}
