package com.ahsailabs.beritakita.pages.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ahsailabs.beritakita.configs.Config;
import com.ahsailabs.beritakita.pages.detail.models.NewsDetail;
import com.ahsailabs.beritakita.pages.detail.models.NewsDetailResponse;
import com.ahsailabs.beritakita.utils.HttpUtil;
import com.ahsailabs.beritakita.utils.InfoUtil;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ahsailabs.beritakita.R;

public class DetailActivity extends AppCompatActivity {
    public static final String PARAM_NEWS_ID = "param_news_id";
    private TextView tvTitle;
    private TextView tvUser;
    private TextView tvDate;
    private TextView tvBody;
    private ScrollView svMain;

    private LinearLayout llLoadingPanel;
    private ProgressBar pbLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadView();
        loadData();
    }

    private void loadView() {
        tvTitle = findViewById(R.id.tvTitle);
        tvUser = findViewById(R.id.tvUser);
        tvDate = findViewById(R.id.tvDate);
        tvBody = findViewById(R.id.tvBody);

        svMain = findViewById(R.id.svMain);


        llLoadingPanel = findViewById(R.id.llLoadingPanel);
        pbLoadingIndicator = findViewById(R.id.pbLoadingIndicator);
    }



    private void loadData() {
        showLoading();
        String newsId = getIntent().getStringExtra(PARAM_NEWS_ID);
        AndroidNetworking.post(Config.getNewsDetailUrl().replace("{id}",newsId))
                .setOkHttpClient(HttpUtil.getCLient(DetailActivity.this))
                .setTag("newsdetail")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(NewsDetailResponse.class, new ParsedRequestListener<NewsDetailResponse>() {
                    @Override
                    public void onResponse(NewsDetailResponse response) {
                        hideLoading();
                        if (response.getStatus() == 1) {
                            NewsDetail newsDetail = response.getData();
                            tvTitle.setText(newsDetail.getTitle());
                            getSupportActionBar().setTitle(newsDetail.getTitle());
                            tvDate.setText(newsDetail.getCreatedAt());
                            tvUser.setText(newsDetail.getCreatedBy());
                            tvBody.setText(newsDetail.getBody());
                        } else {
                            InfoUtil.showToast(DetailActivity.this, response.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideLoading();
                        InfoUtil.showToast(DetailActivity.this, anError.getMessage());
                    }
                });
    }

    private void showLoading(){
        svMain.setVisibility(View.GONE);
        llLoadingPanel.setVisibility(View.VISIBLE);
        pbLoadingIndicator.setProgress(50);
    }

    private void hideLoading(){
        svMain.setVisibility(View.VISIBLE);
        pbLoadingIndicator.setProgress(0);
        llLoadingPanel.setVisibility(View.GONE);
    }

    public static void start(Context context, String newsId){
        Intent detailIntent = new Intent(context, DetailActivity.class);
        detailIntent.putExtra(PARAM_NEWS_ID, newsId);
        context.startActivity(detailIntent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
