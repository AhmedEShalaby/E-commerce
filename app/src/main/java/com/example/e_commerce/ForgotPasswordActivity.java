package com.example.e_commerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button reset_btn;
    EditText edtEmail;
    //ProgressBar progressBar;
    String strEmail;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        reset_btn = findViewById(R.id.reset_btn);
        edtEmail = findViewById(R.id.email_forgot);


        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strEmail = edtEmail.getText().toString().trim();

                if(!strEmail.isEmpty()){
                    ResetPassword();
                }
                else {
                    Toast.makeText(ForgotPasswordActivity.this, "Email field can't be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void ResetPassword() {

        /*progressBar.setVisibility(View.VISIBLE);
        reset_btn.setVisibility(View.INVISIBLE);*/

        auth.sendPasswordResetEmail(strEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(ForgotPasswordActivity.this, "Reset Password Link has been sent to the registered Email", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPasswordActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

       /* progressBar.setVisibility(View.INVISIBLE);
        reset_btn.setVisibility(View.VISIBLE);*/

    }
}