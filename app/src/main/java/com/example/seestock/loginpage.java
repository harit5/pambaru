package com.example.seestock;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class loginpage extends AppCompatActivity {

    Button btnLogin;
    EditText etEmail, etPassword;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loginpage);

        btnLogin = findViewById(R.id.button4);
        etEmail = findViewById(R.id.editTextText);
        etPassword = findViewById(R.id.editTextText2);
        dbHelper = new MyDatabaseHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = etEmail.getText().toString().trim();
                String inputPassword = etPassword.getText().toString().trim();

                if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                    Toast.makeText(loginpage.this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
                    return;
                }

                

                Cursor cursor = dbHelper.getUserByEmail(inputEmail); // method ini perlu dibuat di MyDatabaseHelper

                if (cursor != null && cursor.moveToFirst()) {
                    String dbPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                    if (inputPassword.equals(dbPassword)) {
                        Toast.makeText(loginpage.this, "Login berhasil", Toast.LENGTH_SHORT).show();
                        Intent login = new Intent(loginpage.this, homepage.class);
                        startActivity(login);
                        finish();
                    } else {
                        Toast.makeText(loginpage.this, "Password salah", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                } else {
                    Toast.makeText(loginpage.this, "Email tidak ditemukan", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
