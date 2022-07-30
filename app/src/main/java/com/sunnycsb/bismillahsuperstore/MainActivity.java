package com.sunnycsb.bismillahsuperstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sunnycsb.bismillahsuperstore.Model.Users;
import com.sunnycsb.bismillahsuperstore.Prevalent.Prevalent;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button signUpButton, logInButton;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Paper.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUpButton = (Button) findViewById(R.id.welcome_button_signup);
        logInButton = (Button) findViewById(R.id.welcome_button_login);

        loadingBar = new ProgressDialog(this);

        /*Setting Login Button OnClick Listener*/
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        /*Setting Sign Up Button OnClick Listener*/
        signUpButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        }));

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if(UserPasswordKey != "" && UserPhoneKey != "") {
            if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {

                AllowAccess(UserPhoneKey, UserPasswordKey);

                loadingBar.setTitle("Automatically Logging In");
                loadingBar.setMessage("Please wait while we verify your credentials.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    private void AllowAccess(final String phone,final String password) {

        final com.google.firebase.database.DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(phone).exists()) {
                    Users usersData = snapshot.child("Users").child(phone).getValue(Users.class);

                    if(usersData.getPhone().equals(phone)) {
                        Log.d("Phone", "Phone passed!");
                        if(usersData.getPassword().equals(Paper.book().read(Prevalent.UserPasswordKey))) {
                            Log.d("Password", "Password passed!");
                            Toast.makeText(MainActivity.this, "Welcome " + usersData.getName(), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d("Password", "Password failed!");
                            Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    } else {
                        Log.d("Phone", "Phone failed!");
                        Toast.makeText(MainActivity.this, "Phone number is incorrect", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                } else {
                    Log.d("Total", "Total failed!");
                    Toast.makeText(MainActivity.this, "The Phone Number and/or Password are incorrect", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}