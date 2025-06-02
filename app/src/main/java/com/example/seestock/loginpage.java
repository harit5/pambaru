// loginpage.java
package com.example.seestock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences; // Import SharedPreferences
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class loginpage extends AppCompatActivity {

    Button btnLogin;
    EditText etEmail, etPassword;
    MyDatabaseHelper dbHelper;

    TextView register;

    public static final String PREF_USER_ID = "pref_user_id"; // Kunci untuk SharedPreferences
    public static final String PREF_NAME = "MyLoginPrefs"; // Nama file SharedPreferences


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loginpage);

        btnLogin = findViewById(R.id.button4);
        etEmail = findViewById(R.id.editTextText);
        etPassword = findViewById(R.id.editTextText2);
        dbHelper = new MyDatabaseHelper(this);
        register = findViewById(R.id.textView2);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent register = new Intent(loginpage.this, MainActivity.class);
            startActivity(register);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = etEmail.getText().toString().trim();
                String inputPassword = etPassword.getText().toString().trim();

                if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                    Toast.makeText(loginpage.this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
                    return;
                }

                Cursor cursor = dbHelper.getUserByEmail(inputEmail);

                if (cursor != null && cursor.moveToFirst()) {
                    String dbPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id")); // Ambil userId
                    if (inputPassword.equals(dbPassword)) {
                        // Login berhasil
                        Toast.makeText(loginpage.this, "Login berhasil", Toast.LENGTH_SHORT).show();

                        // *** Simpan userId ke SharedPreferences ***
                        SharedPreferences sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(PREF_USER_ID, userId);
                        editor.apply(); // Gunakan apply() untuk menyimpan secara asynchronous

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