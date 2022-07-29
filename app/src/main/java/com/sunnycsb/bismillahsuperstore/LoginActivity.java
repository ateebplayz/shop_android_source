package com.sunnycsb.bismillahsuperstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sunnycsb.bismillahsuperstore.Model.Users;

public class LoginActivity extends AppCompatActivity {

    private EditText InputNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;

    private String DBName = "Users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_button_login);
        InputPassword = (EditText) findViewById(R.id.login_edittxt_password);
        InputNumber = (EditText) findViewById(R.id.login_edittxt_phonenumber);
        loadingBar = new ProgressDialog(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginProcess();
            }
        });
    }
    private void LoginProcess() {
        String number = InputNumber.getText().toString();
        Log.d("Number", number.getClass().getSimpleName());
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Phone number Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return;
        } else if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return;
        } else {
            loadingBar.setTitle("Log In");
            loadingBar.setMessage("Please wait while we verify your credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateCredsLogin(number, password);
        }
    }
    private void ValidateCredsLogin(final String phone,final String password) {
        final com.google.firebase.database.DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(DBName).child(phone).exists()) {
                    Users usersData = snapshot.child("Users").child(phone).getValue(Users.class);

                    if(usersData.getPhone().equals(phone)) {
                        Log.d("Phone", "Phone passed!");
                        if(usersData.getPassword().equals(password)) {
                            Log.d("Password", "Password passed!");
                            Toast.makeText(LoginActivity.this, "Welcome " + usersData.getName(), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d("Password", "Password failed!");
                            Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    } else {
                        Log.d("Phone", "Phone failed!");
                        Toast.makeText(LoginActivity.this, "Phone number is incorrect", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                } else {
                    Log.d("Total", "Total failed!");
                    Toast.makeText(LoginActivity.this, "The Phone Number and/or Password are incorrect", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}