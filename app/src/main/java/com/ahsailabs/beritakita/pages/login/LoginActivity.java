package com.ahsailabs.beritakita.pages.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ahsailabs.beritakita.configs.Config;
import com.ahsailabs.beritakita.pages.home.HomeActivity;
import com.ahsailabs.beritakita.pages.login.models.LoginData;
import com.ahsailabs.beritakita.pages.login.models.LoginResponse;
import com.ahsailabs.beritakita.utils.HttpUtil;
import com.ahsailabs.beritakita.utils.InfoUtil;
import com.ahsailabs.beritakita.utils.SessionUtil;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ahsailabs.beritakita.R;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private LinearLayout llLoadingPanel;
    private ProgressBar pbLoadingIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        loadView();
        registerClickListener();

        //check if isLogined
        if(SessionUtil.isLoggedIn(this)){
            HomeActivity.start(LoginActivity.this);
            finish();
        }
    }


    private void loadView() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        llLoadingPanel = findViewById(R.id.llLoadingPanel);
        pbLoadingIndicator = findViewById(R.id.pbLoadingIndicator);
    }

    private void registerClickListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all data
                String txtUsername = etUsername.getText().toString();
                String txtPassword = etPassword.getText().toString();


                //validation
                if(TextUtils.isEmpty(txtUsername)){
                    etUsername.setError("Username wajib diisi");
                    return;
                }

                if(TextUtils.isEmpty(txtPassword)){
                    etPassword.setError("Password wajib diisi");
                    return;
                }


                //postdata
                postData(txtUsername, txtPassword);
            }
        });
    }

    private void postData(String username, String password) {
        showLoading();
        AndroidNetworking.post(Config.getLoginUrl())
                .setOkHttpClient(HttpUtil.getCLient(LoginActivity.this))
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .setTag("login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(LoginResponse.class, new ParsedRequestListener<LoginResponse>() {
                    @Override
                    public void onResponse(LoginResponse response) {
                        hideLoading();
                        if (response.getStatus() == 1) {
                            LoginData loginData = response.getData();

                            SessionUtil.login(LoginActivity.this, loginData);

                            finish();
                        } else {
                            InfoUtil.showToast(LoginActivity.this, response.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideLoading();
                        InfoUtil.showToast(LoginActivity.this, anError.getMessage());
                    }
                });
    }

    private void showLoading(){
        btnLogin.setVisibility(View.GONE);
        llLoadingPanel.setVisibility(View.VISIBLE);
        pbLoadingIndicator.setProgress(50);
    }

    private void hideLoading(){
        pbLoadingIndicator.setProgress(0);
        btnLogin.setVisibility(View.VISIBLE);
        llLoadingPanel.setVisibility(View.GONE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void start(Context context){
        Intent loginIntent = new Intent(context, LoginActivity.class);
        context.startActivity(loginIntent);
    }

}
