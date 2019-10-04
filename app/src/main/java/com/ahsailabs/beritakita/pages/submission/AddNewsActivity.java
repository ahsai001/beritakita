package com.ahsailabs.beritakita.pages.submission;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ahsailabs.beritakita.configs.Config;
import com.ahsailabs.beritakita.pages.home.models.GeneralResponse;
import com.ahsailabs.beritakita.pages.login.LoginActivity;
import com.ahsailabs.beritakita.utils.HttpUtil;
import com.ahsailabs.beritakita.utils.InfoUtil;
import com.ahsailabs.beritakita.utils.SessionUtil;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.ahsailabs.beritakita.R;
import com.google.android.material.textfield.TextInputEditText;

public class AddNewsActivity extends AppCompatActivity {
    private TextInputEditText tietTitle;
    private TextInputEditText tietSummary;
    private TextInputEditText tietBody;
    private ExtendedFloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //check if isLogined

        if(!SessionUtil.isLoggedIn(this)){
            LoginActivity.start(AddNewsActivity.this);
            finish();
        }

        fab = findViewById(R.id.fab);
        fab.shrink();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fab.isExtended()){
                    Snackbar.make(view,R.string.please_wait,Snackbar.LENGTH_LONG).show();
                } else {
                    validateAndSendData();
                }
            }
        });

        loadView();
    }



    private void loadView() {
        tietTitle = findViewById(R.id.tietTitle);
        tietSummary = findViewById(R.id.tietSummary);
        tietBody = findViewById(R.id.tietBody);
    }


    private void validateAndSendData() {
        String txtTitle = tietTitle.getText().toString();
        String txtSummary = tietSummary.getText().toString();
        String txtBody = tietBody.getText().toString();

        //validation
        if(TextUtils.isEmpty(txtTitle)){
            tietTitle.setError("title cannot be empty");
            return;
        }

        if(TextUtils.isEmpty(txtSummary)){
            tietSummary.setError("summary cannot be empty");
            return;
        }

        if(TextUtils.isEmpty(txtBody)){
            tietBody.setError("body cannot be empty");
            return;
        }

        sendData(txtTitle, txtSummary, txtBody);
    }

    private void sendData(String txtTitle, String txtSummary, String txtBody) {
        showLoading();
        AndroidNetworking.post(Config.getAddNewsUrl())
                .setOkHttpClient(HttpUtil.getCLient(AddNewsActivity.this))
                .addBodyParameter("groupcode", Config.GROUP_CODE)
                .addBodyParameter("title", txtTitle)
                .addBodyParameter("summary", txtSummary)
                .addBodyParameter("body", txtBody)
                .setTag("addnews")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GeneralResponse.class, new ParsedRequestListener<GeneralResponse>() {
                    @Override
                    public void onResponse(GeneralResponse response) {
                        hideLoading();
                        if (response.getStatus() == 1) {
                            InfoUtil.showToast(AddNewsActivity.this, response.getMessage());
                            finish();
                        } else {
                            InfoUtil.showSnackBar(fab, response.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideLoading();
                        InfoUtil.showToast(AddNewsActivity.this, anError.getMessage());
                    }
                });
    }


    private void showLoading() {
        fab.extend();
    }

    private void hideLoading() {
        fab.shrink();
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
        Intent addNewsIntent = new Intent(context,AddNewsActivity.class);
        context.startActivity(addNewsIntent);
    }

}
