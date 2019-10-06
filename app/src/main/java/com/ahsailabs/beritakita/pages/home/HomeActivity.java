package com.ahsailabs.beritakita.pages.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.ahsailabs.beritakita.R;
import com.ahsailabs.beritakita.bases.BaseRecyclerViewAdapter;
import com.ahsailabs.beritakita.configs.Config;
import com.ahsailabs.beritakita.pages.detail.DetailActivity;
import com.ahsailabs.beritakita.pages.home.adapters.NewsAdapter;
import com.ahsailabs.beritakita.pages.home.models.News;
import com.ahsailabs.beritakita.pages.home.models.NewsListResponse;
import com.ahsailabs.beritakita.pages.login.LoginActivity;
import com.ahsailabs.beritakita.pages.login.models.LoginData;
import com.ahsailabs.beritakita.pages.submission.AddNewsActivity;
import com.ahsailabs.beritakita.utils.HttpUtil;
import com.ahsailabs.beritakita.utils.InfoUtil;
import com.ahsailabs.beritakita.utils.SessionUtil;
import com.ahsailabs.beritakita.utils.SwipeRefreshLayoutUtil;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener {
    private RecyclerView rvList;
    private TextView tvEmpty;
    private List<News> newsList;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private SwipeRefreshLayoutUtil swipeRefreshLayoutUtil;


    private LinearLayout llLoadingPanel;
    private ProgressBar pbLoadingIndicator;

    private String keyword="";

    private int selectedContextPosition;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        loadView();
        setupView();
        setupListener();

        swipeRefreshLayoutUtil.refreshNow();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                keyword = query;
                swipeRefreshLayoutUtil.refreshNow();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                keyword = "";
                swipeRefreshLayoutUtil.refreshNow();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            swipeRefreshLayoutUtil.refreshNow();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_submission){
            AddNewsActivity.start(this);
        } else if (id == R.id.nav_login) {
            LoginActivity.start(this);
        } else if (id == R.id.nav_logout) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Logout confirmation")
                    .setMessage("Are you sure?")
                    .setPositiveButton("logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SessionUtil.logout(HomeActivity.this);
                            refreshDrawer();
                        }
                    })
                    .setNegativeButton("cancel", null)
                    .show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadView() {
        rvList = findViewById(R.id.rvList);
        tvEmpty = findViewById(R.id.tvEmpty);

        llLoadingPanel = findViewById(R.id.llLoadingPanel);
        pbLoadingIndicator = findViewById(R.id.pbLoadingIndicator);

        swipeRefreshLayout = findViewById(R.id.srlView);

    }

    private void setupView() {
        rvList.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        //rvList.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(newsList);
        rvList.setAdapter(newsAdapter);
    }


    private void setupListener() {
        newsAdapter.setOnChildViewClickListener(new BaseRecyclerViewAdapter.OnChildViewClickListener<News>() {
            @Override
            public void onClick(View view, News dataModel, int position) {
                DetailActivity.start(HomeActivity.this, dataModel.getId());
            }

            @Override
            public void onLongClick(View view, News dataModel, int position) {
                PopupMenu popupMenu = new PopupMenu(HomeActivity.this,view);
                popupMenu.getMenuInflater().inflate(R.menu.home_context,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(HomeActivity.this);
                selectedContextPosition = position;
                popupMenu.show();
            }
        });

        swipeRefreshLayoutUtil = SwipeRefreshLayoutUtil.init(swipeRefreshLayout, new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        });
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.action_delete){
            newsList.remove(selectedContextPosition);
            newsAdapter.notifyDataSetChanged();
            showContent();
            return true;
        }
        return false;
    }

    private void loadData() {
        showLoading();
        AndroidNetworking.post(Config.getNewsListUrl())
                .setOkHttpClient(HttpUtil.getCLient(HomeActivity.this))
                .addBodyParameter("groupcode", Config.GROUP_CODE)
                .addBodyParameter("keyword", keyword)
                .setTag("newslist")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(NewsListResponse.class, new ParsedRequestListener<NewsListResponse>() {
                    @Override
                    public void onResponse(NewsListResponse response) {
                        hideLoading();
                        if (response.getStatus() == 1) {
                            List<News> resultList = response.getData();

                            newsList.clear();
                            newsList.addAll(resultList);
                            newsAdapter.notifyDataSetChanged();
                            showContent();
                        } else {
                            InfoUtil.showToast(HomeActivity.this, response.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideLoading();
                        InfoUtil.showToast(HomeActivity.this, anError.getMessage());
                    }
                });
    }

    private void showLoading(){
        rvList.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        llLoadingPanel.setVisibility(View.VISIBLE);
        pbLoadingIndicator.setProgress(50);
    }

    private void hideLoading(){
        pbLoadingIndicator.setProgress(0);
        tvEmpty.setVisibility(View.GONE);
        llLoadingPanel.setVisibility(View.GONE);

        swipeRefreshLayoutUtil.refreshDone();
    }

    private void showContent(){
        if(newsList.size() > 0){
            rvList.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            rvList.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeRefreshLayoutUtil.refreshNow();

        refreshDrawer();
    }


    private void refreshDrawer(){
        MenuItem navLogin = navigationView.getMenu().findItem(R.id.nav_login);
        MenuItem navLogout = navigationView.getMenu().findItem(R.id.nav_logout);
        LoginData loginData = new LoginData();
        if(SessionUtil.isLoggedIn(this)){
            navLogin.setVisible(false);
            navLogout.setVisible(true);
            loginData = SessionUtil.getLoginData(this);
        } else {
            navLogin.setVisible(true);
            navLogout.setVisible(false);

            loginData.setName("anonymous");
            loginData.setUsername("anonymous");
        }



        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tvName);
        TextView tvUserName = headerView.findViewById(R.id.tvUserName);

        tvName.setText(loginData.getName());
        tvUserName.setText(String.format("@%s", loginData.getUsername()));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static void start(Context context){
        Intent homeIntent = new Intent(context,HomeActivity.class);
        context.startActivity(homeIntent);
    }

}
