package com.example.seestock;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText etUsername, etEmail, etPassword;
    Button btnRegister;
    TextView textView;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        etUsername = findViewById(R.id.editTextText);
        etEmail = findViewById(R.id.editTextTextEmailAddress);
        etPassword = findViewById(R.id.editTextTextPassword);
        btnRegister = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        dbHelper = new MyDatabaseHelper(this);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(MainActivity.this, loginpage.class);
                startActivity(login);
            }
        });

        //buat nunjukin nama user setelah register
//        Cursor cursor = dbHelper.getUser(1); // ambil user dengan id 1
//        if (cursor != null && cursor.moveToFirst()) {
//            String userName = cursor.getString(cursor.getColumnIndexOrThrow("userName"));
//            textView.setText("User: " + userName);
//            cursor.close();
//        } else {
//            textView.setText("User tidak ditemukan");
//        }

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();

            } else {
                dbHelper.registerUser(username, password, email);
                Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                Intent tologin = new Intent(MainActivity.this, loginpage.class);
                startActivity(tologin);
                finish(); // atau redirect ke login
            }
        });
    }
}