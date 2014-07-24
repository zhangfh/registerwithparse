package com.burns.android.registerwithparse;




import java.util.List;






import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.Parse;
import com.parse.ParseAnalytics;







import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
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
	  
	  private List<ParseObject>  mLogings;
	  private Context mContext;
	  private Dialog  mprogressDialog ;
	  
	  private String mUsername;
	  private Editor mEditor;
	  
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
	          String password = mPasswordField.getText().toString();

	          if (mUsername.length() == 0) {
	            showToast(R.string.ui_no_username_toast);
	          } else if (password.length() == 0) {
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
	          String password = mPasswordField.getText().toString();

	          if (mUsername.length() == 0) {
	            showToast(R.string.ui_no_username_toast);
	          } else if (password.length() == 0) {
	            showToast(R.string.ui_no_password_toast);
	          } else {
	        	  //
	  			new RemoteDataTask() {
					protected Void doInBackground(Void... params) {
						ParseObject login = new ParseObject("NewLogin");
						login.put("name", mUsername);
						try {
							login.save();
						} catch (ParseException e) {
						}

						super.doInBackground();
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
					Log.i(TAG,"doInBackground");
					ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("NewLogin");
					//query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
					//Log.i(TAG,"get cachepolicy:" + query.getCachePolicy());
					//ParseQuery<ParseObject> query = ParseQuery.getQuery("NewLogin");
					query.orderByDescending("_created_at");

					try {
						mLogings = query.find();

						Log.i(TAG,"lenghth:" + mLogings.size());
					} catch (ParseException e) {
						Log.i(TAG,"execption:" + e);
					}
								
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
		            if (mLogings != null) {
		            	Log.i(TAG,"mLogings is not null");
		            	Log.i(TAG,"lenght:" + mLogings.size());
		                for (ParseObject loging : mLogings) {
		                	Log.i(TAG,"name:" + loging.get("name"));
		                	Log.i(TAG, "mUsername:" + mUsername);
		                  if(mUsername.equals(  (String)(loging.get("name"))))
		                  {
		                	Log.i(TAG,"in here");
		                	mEditor=mContext.getSharedPreferences(MainActivity.PREFS_NAME, 0).edit();
		                	mEditor.putInt(MainActivity.RegisterStateKey, 1);
		                	mEditor.commit();
		            		startNewActivity();
		            		break;
		                  }
		                }
		            }
	
					mprogressDialog.dismiss();

			}
	}
}
