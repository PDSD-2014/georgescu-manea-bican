package com.example.rendezview;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
	    protected void onPreExecute() {
	        super.onPreExecute();
	        Log.d(TAG, "Se executa onPreExecute!");
	        progressDialog = ProgressDialog.show(LoginActivity.this, "Authenticating", "Trying to reach server. Please wait few seconds.", true, false);
	    }
		
	    @SuppressWarnings("unchecked")
	    @Override
	    protected String doInBackground(String... nameAndPassword) {
	    	Log.d(TAG, "Se executa doInBackground!");
	        
	    	email = nameAndPassword[0]; password = nameAndPassword[1];	        	       		                  
	
	    	String serverResult = null;	    	
	    	
	    	String messageForServer = "4" + " " + email + " " + password + "\n";
	    	
//	    	Log.d(TAG, "*****************************" + messageForServer);

	    	try {	    		
	    		Socket clientSocket = new Socket(InetAddress.getByName("projects.rosedu.org"), 9000);
				
	    		BufferedWriter messageSender = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	    		BufferedReader responseReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
	    		messageSender.write(messageForServer);
	    		messageSender.flush();
	    		
				serverResult = responseReader.readLine();
				
				clientSocket.close();
			} catch (UnknownHostException e) {
				Log.e(TAG, "Eroare la contactare server! " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(TAG, "Eroare la conectare cu socketul! " + e.getMessage());
				e.printStackTrace();
			}
	    	
	        return serverResult;
	    }
	
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	
	        if (result == null) {
	        	progressDialog.dismiss();
	            Toast.makeText(LoginActivity.this.getApplication(), "Server did not respond! Please try again or check your internet connection.", Toast.LENGTH_LONG).show();
	            return;	        
	        } else {
	        	Log.d(TAG, "Se executa onPostExecute! Faza de login e completa.");
	        	String[] resultParts = result.split(" ");  		       
	        	progressDialog.dismiss();
	        	
	        	if (resultParts[0].equals("4")) {	        			        			        		        		      		        		        		        			            
		            // Succcesfull login		            
		            if (resultParts[1].equals("0")) {
		            	// Set the name to userInfo var from MainActivity	            		            	
		            	UserInfo user = MainActivity.getUserInfo();
			            if (user != null) {
			            	if (resultParts.length == 5) {
			            		if (user.getUserName() == null )
			            			user.setUserName(resultParts[3] + " " + resultParts[4]);
			            		if (user.getUserId() == -1)
			            			user.setUserId(Integer.valueOf(resultParts[2]));
			            		MainActivity.setUserInfo(user);     		
			            	}
			            }
		            	finish();
		            } // User does not exist 
		            else if (resultParts[1].equals("1")) {
		            	Toast.makeText(LoginActivity.this.getApplication(), "User does not exist! Please register!", Toast.LENGTH_SHORT).show();
		            } // Incorrect password
		            else if (resultParts[1].equals("2")) {
		            	Toast.makeText(LoginActivity.this.getApplication(), "Incorrect password! Try again!", Toast.LENGTH_SHORT).show();
		            }		            		           
	        	} else {
	        		Toast.makeText(LoginActivity.this.getApplication(), TAG + "Incorrect result from server", Toast.LENGTH_SHORT).show();
	        	}	        		        				
	            
				return;
	        }		       	       		      
	    }	
	}
}
