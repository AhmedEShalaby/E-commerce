package com.example.e_commerce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String REMEMBER_ME_KEY = "rememberMe";
    public static final String TIMESTAMP_KEY = "timestamp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();

        EditText email_login = findViewById(R.id.email_login);
        EditText password_login = findViewById(R.id.password_login);
        CheckBox remember_cb = findViewById(R.id.remember_cb);
        Button login_btn = findViewById(R.id.login_btn);

        if (isRememberMeValid()) {
            proceedToMainActivity();
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* User user = new User(null, email_login.getText().toString().trim(), password_login.getText().toString().trim(),
                        null, null, null, );*/

                if (email_login.getText().toString().isEmpty())
                    Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                else if (password_login.getText().toString().isEmpty())
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                else {
                    auth.signInWithEmailAndPassword(email_login.getText().toString().trim(), password_login.getText().toString().trim())
                            .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            if (remember_cb.isChecked()) {
                                saveRememberMeState(true);
                            }

                            Toast.makeText(LoginActivity.this, "Login Succeeded", Toast.LENGTH_SHORT).show();
                            proceedToMainActivity();

                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void saveRememberMeState(boolean rememberMe) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(REMEMBER_ME_KEY, rememberMe);
        editor.putLong(TIMESTAMP_KEY, System.currentTimeMillis());
        editor.apply();
    }

    private boolean isRememberMeValid() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean(REMEMBER_ME_KEY, false);
        long timestamp = sharedPreferences.getLong(TIMESTAMP_KEY, 0);

        if (rememberMe) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - timestamp;
            if (elapsedTime > 10 * 1000) { // 15 minutes in milliseconds
                saveRememberMeState(false); // Reset remember me state
                return false;
            }
            return true;
        }
        return false;
    }

    private void proceedToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void SignUp(View view) {
        Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(i);
    }
}
