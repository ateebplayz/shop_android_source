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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class SignUpActivity<DatabaseReference> extends AppCompatActivity
{
    private Button CreateAccountButton;
    private EditText InputName, InputNumber, InputPassword, InputConfirmPassword;
    private ProgressDialog loadingbar;
    private Calendar FirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        CreateAccountButton = (Button) findViewById(R.id.signup_button_login);
        InputName = (EditText) findViewById(R.id.signup_edittxt_username);
        InputNumber = (EditText) findViewById(R.id.signup_edittxt_phonenumber);
        InputPassword = (EditText) findViewById(R.id.signup_edittxt_password);
        InputConfirmPassword = (EditText) findViewById(R.id.signup_edittxt_confirmpassword);
        loadingbar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount()
    {
        String name = InputName.getText().toString();
        String number = InputNumber.getText().toString();
        String password = InputPassword.getText().toString();
        String confirmPassword = InputConfirmPassword.getText().toString();
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Username Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return;
        } else if(TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Phone Number Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return;
        } else if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return;
        } else if(name.length() < 3) {
            Toast.makeText(this, "Name Must be longer than 3 Characters", Toast.LENGTH_SHORT).show();
            return;
        } else if(!(number.length() == 10)) {
            Toast.makeText(this, "Number Must be in the format 0000000000 (10 numbers)", Toast.LENGTH_SHORT).show();
            return;
        } else if(password.length() < 8) {
            Toast.makeText(this, "Password Must be greater than 7 characters", Toast.LENGTH_SHORT).show();
            return;
        } else if((number.contains(" "))) {
            Toast.makeText(this, "Phone number Must not contain spaces", Toast.LENGTH_SHORT).show();
            return;
        } else if(!(password.equals(confirmPassword))) {
            Log.d("Pass is", password);
            Log.d("confirm pass is ", confirmPassword);
            Toast.makeText(this, "Passwords Are Not The Same", Toast.LENGTH_SHORT).show();
            return;
        } else {
            loadingbar.setTitle("Sign Up");
            loadingbar.setMessage("Please wait while we finish the process.");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            ValidatephoneNumber(name, number, password);
        }
    }

    private void ValidatephoneNumber(String name, String number, String password) {
        final com.google.firebase.database.DatabaseReference RootRef;
        RootRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child("Users").child(number).exists())) {
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("phone", number);
                    userMap.put("password", password);
                    RootRef.child("Users").child(number).updateChildren(userMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "Account has been created! code 0x02222", Toast.LENGTH_SHORT).show();
                                            loadingbar.dismiss();
                                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        } else {
                                            loadingbar.dismiss();
                                            Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                                            Toast.makeText(SignUpActivity.this, "Error 0x00001 | Try Again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                } else {
                    Toast.makeText(SignUpActivity.this, "This " + number + "Already Exists.", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(SignUpActivity.this, "Try using the login feature instead", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}