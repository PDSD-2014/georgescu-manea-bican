package com.example.rendezview;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity{
	
	private TryToRegister tryToRegister = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.register);
 
        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
        
        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View arg0) {
                // Closing registration screen
                // Switching to Login Screen/closing register screen
                finish();                               
            }
        });
        
        final TextView nameView = (TextView) findViewById(R.id.reg_name);
        final TextView surnameView = (TextView) findViewById(R.id.reg_surname);
        final TextView emailView = (TextView) findViewById(R.id.reg_email);
        final TextView passwordView = (TextView) findViewById(R.id.reg_password);
        final TextView reTypedPasswordView = (TextView) findViewById(R.id.reg_re_type_password);
        
        Button registerButton = (Button) findViewById(R.id.btnRegister);
        
        registerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				String name = nameView.getText().toString();
				String surname = surnameView.getText().toString();
				String email = emailView.getText().toString();
				String password = passwordView.getText().toString();
				String reTypedPassword = reTypedPasswordView.getText().toString();
				
				Pattern regEx = Pattern.compile(".+@.+\\.[a-z]+");
                Matcher matcher = regEx.matcher(email);                
				
				if (name == null || name.equals("")) {
					Toast.makeText(RegisterActivity.this.getApplication(), "Please enter your name!", Toast.LENGTH_SHORT).show();
				} if (surname == null || surname.equals("")) {
					Toast.makeText(RegisterActivity.this.getApplication(), "Please enter your surname!", Toast.LENGTH_SHORT).show();
				} else if (email == null || email.equals("")) {					
					Toast.makeText(RegisterActivity.this.getApplication(), "Please enter your email!", Toast.LENGTH_SHORT).show();
				} else if (!matcher.matches()) {                    
					Toast.makeText(RegisterActivity.this.getApplication(), "Email not well formed!", Toast.LENGTH_SHORT).show();
				} else if (password == null || password.equals("")) {
					Toast.makeText(RegisterActivity.this.getApplication(), "Please enter your password!", Toast.LENGTH_SHORT).show();
				} else if (reTypedPassword == null || reTypedPassword.equals("")) {
					Toast.makeText(RegisterActivity.this.getApplication(), "Please re-type your password!", Toast.LENGTH_SHORT).show();
				} else if (!password.equals(reTypedPassword)) {
					Toast.makeText(RegisterActivity.this.getApplication(), "Passwords don't match!", Toast.LENGTH_SHORT).show();
				} else {
					if (tryToRegister == null)
						tryToRegister = new TryToRegister();
					
					tryToRegister.execute(name, surname, email, password);
				}											
			}
		});
    }
	
	// Background thread that checks validity of user and password
	protected class TryToRegister extends AsyncTask<String, Void, String> {
	
	    private final static String TAG = "RegisterActivity.TryToRegister";
	
	    protected ProgressDialog progressDialog;
	
	    private String name, surname, email, password; 
	    
	    @Override
	    protected void onPreExecute()
	    {
	        super.onPreExecute();
	        Log.d(TAG, "Se executa onPreaExecute!");
	        progressDialog = ProgressDialog.show(RegisterActivity.this, "Registering", "Trying to reach server. Please wait few seconds.", true, false);
	    }
		
	    @SuppressWarnings("unchecked")
	    @Override
	    protected String doInBackground(String... nameAndPassword) {
	    	Log.d(TAG, "Se executa doInBackground!");
	        
	    	name = nameAndPassword[0]; surname = nameAndPassword[1]; email = nameAndPassword[2]; password = nameAndPassword[3]; 	        	       		                  
	
	    	String result = null;
	    		        
	    	// Replace this with actual result from server
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
	            Toast.makeText(RegisterActivity.this.getApplication(), "Server did not respond! Please try againg or check your internet connection.", Toast.LENGTH_LONG).show();
	            return;	        
	        } else {	
	        	Log.d(TAG, "Se executa onPostExecute! Faza de login e completa.");
	        	progressDialog.dismiss();
	            Toast.makeText(RegisterActivity.this.getApplication(), "Succes! !", Toast.LENGTH_SHORT).show();
	            
	            // Set the name to userInfo var from MainActivity
	            UserInfo user = MainActivity.getUserInfo();
				user.setUserName(name + surname);
				MainActivity.setUserInfo(user);
				
				finish();
					            
				return;
	        }		       	       		      
	    }
	}
}
