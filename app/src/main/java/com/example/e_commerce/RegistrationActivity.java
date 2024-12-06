package com.example.e_commerce;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;

public class RegistrationActivity extends AppCompatActivity {


    private TextView birthdate;
    private FirebaseAuth auth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();


        EditText name = findViewById(R.id.name);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText address = findViewById(R.id.address);
        EditText number = findViewById(R.id.number);
        Button signUp = findViewById(R.id.sign_up_btn);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user = new User(name.getText().toString(), email.getText().toString(), password.getText().toString(),
                        address.getText().toString(), number.getText().toString(), birthdate.getText().toString());

                if(user.getName().isEmpty())
                    Toast.makeText(RegistrationActivity.this,
                            "Enter Your Name!", Toast.LENGTH_SHORT).show();

                else if(user.getEmail().isEmpty())
                    Toast.makeText(RegistrationActivity.this,
                            "Enter Your E-mail!", Toast.LENGTH_SHORT).show();

                else if(user.getPassword().isEmpty())
                    Toast.makeText(RegistrationActivity.this,
                            "Enter Your Password!", Toast.LENGTH_SHORT).show();

                else if(user.getPassword().length() < 6)
                    Toast.makeText(RegistrationActivity.this,
                            "Password Length Should Be >= 6", Toast.LENGTH_SHORT).show();

                else if(user.getAddress().isEmpty())
                    Toast.makeText(RegistrationActivity.this,
                            "Enter Your Address!", Toast.LENGTH_SHORT).show();

                else if(user.getNumber().isEmpty())
                    Toast.makeText(RegistrationActivity.this,
                            "Enter Your Number!", Toast.LENGTH_SHORT).show();

                else if(user.getBirthdate().isEmpty())
                    Toast.makeText(RegistrationActivity.this,
                            "Enter Your Birthdate!", Toast.LENGTH_SHORT).show();

                auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // User creation successful
                                db.collection("Users").add(user).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(RegistrationActivity.this, "Sign In Succeeded", Toast.LENGTH_SHORT).show();
                                    }

                                    else{
                                        Toast.makeText(RegistrationActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                                    }
                                } );
                                //Toast.makeText(RegistrationActivity.this, "Sign In Succeeded", Toast.LENGTH_SHORT).show();
                            } else {
                                // User creation failed
                                Toast.makeText(RegistrationActivity.this, "Sign In Failed2", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        birthdate = findViewById(R.id.birthdate);
        ImageButton calendar_btn = findViewById(R.id.calendar_btn);

        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });


    }

    private void openDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                birthdate.setText(String.valueOf(day)+"/"+String.valueOf(month+1)
                        +"/"+String.valueOf(year));
            }
        }, 2003, 0, 15);

        dialog.show();
    }

    public void login(View view) {
        Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(i);
    }

}