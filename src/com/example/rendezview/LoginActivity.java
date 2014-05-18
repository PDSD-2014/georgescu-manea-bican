package com.example.rendezview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	TryToLogin tryToLogin = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.login);
 
        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
        
        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
        
        Button loginButton = (Button) findViewById(R.id.btnLogin);
        final TextView emailView = (TextView) findViewById(R.id.log_email);
        final TextView passwordView = (TextView) findViewById(R.id.log_password);
        
        loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {				
				String email = emailView.getText().toString();
				String password = passwordView.getText().toString();
				
				if (email == null || email.equals("")) {
					Toast.makeText(LoginActivity.this.getApplication(), "Please enter your name!", Toast.LENGTH_SHORT).show();
				} else if (password == null || password.equals("")) {
					Toast.makeText(LoginActivity.this.getApplication(), "Please enter your password!", Toast.LENGTH_SHORT).show();
				} else {
					// Check validity for user and password
					if (tryToLogin == null)
						tryToLogin = new TryToLogin();
					
					tryToLogin.execute(email, password);
				}							
			}
		});
    }
		
	// Background thread that checks validity of user and password
	protected class TryToLogin extends AsyncTask<String, Void, String> {
	
	    private final static String TAG = "LoginActivity.TryToLogin";
	
	    protected ProgressDialog progressDialog;
	
	    private String email, password; 
	    
	    @Override
	    protected void onPreExecute()
	    {
	        super.onPreExecute();
	        Log.d(TAG, "Se executa onPreaExecute!");
	        progressDialog = ProgressDialog.show(LoginActivity.this, "Authenticating", "Trying to reach server. Please wait few seconds.", true, false);
	    }
		
	    @SuppressWarnings("unchecked")
	    @Override
	    protected String doInBackground(String... nameAndPassword) {
	    	Log.d(TAG, "Se executa doInBackground!");
	        
	    	email = nameAndPassword[0]; password = nameAndPassword[1];	        	       		                  
	
	    	String result = null;
	    		        
	    	// Replace this with actual result from server
	    	// Server has to respond with name 
	    	result = "passed";
	    	
	    	int contor = 0;
	    	while (true) {
	    		contor++;
	    		if (contor == 1000)
	    			break;
	    	}
	    	
	        return result;
	    }
	
	    @Override
	    protected void onPostExecute(String result)
	    {
	        super.onPostExecute(result);
	
	        if (result == null) {
	        	progressDialog.dismiss();
	            Toast.makeText(LoginActivity.this.getApplication(), "Server did not respond! Please try againg or check your internet connection.", Toast.LENGTH_LONG).show();
	            return;
	        } else if (result.equals("")) {
	            progressDialog.dismiss();
	            Toast.makeText(LoginActivity.this.getApplication(), "Wrong name or password!", Toast.LENGTH_SHORT).show();
	            return;
	        } else {	
	        	Log.d(TAG, "Se executa onPostExecute! Faza de login e completa.");
	        	progressDialog.dismiss();
	            Toast.makeText(LoginActivity.this.getApplication(), "Succes! !", Toast.LENGTH_SHORT).show();
	            
	            // Set the name to userInfo var from MainActivity
	            UserInfo user = MainActivity.getUserInfo();
				user.setUserName(result);
				MainActivity.setUserInfo(user);
				finish();
	            
				return;
	        }		       	       		      
	    }	
	}
}
