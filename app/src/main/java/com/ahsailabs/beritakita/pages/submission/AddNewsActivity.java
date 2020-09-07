package com.ahsailabs.beritakita.pages.submission;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.ahsailabs.beritakita.configs.Config;
import com.ahsailabs.beritakita.pages.submission.models.AddNewsResponse;
import com.ahsailabs.beritakita.pages.login.LoginActivity;
import com.ahsailabs.beritakita.utils.HttpUtil;
import com.ahsailabs.beritakita.utils.InfoUtil;
import com.ahsailabs.beritakita.utils.PermissionUtil;
import com.ahsailabs.beritakita.utils.SessionUtil;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.ahsailabs.beritakita.R;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

public class AddNewsActivity extends AppCompatActivity {
    private TextInputEditText tietTitle;
    private TextInputEditText tietSummary;
    private TextInputEditText tietBody;
    private MaterialButton mbtnPhoto;
    private ImageView ivPhoto;
    private ExtendedFloatingActionButton fab;

    private EasyImage easyImage;
    private MediaFile mediaFile;

    private PermissionUtil permissionUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

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

        easyImage = new EasyImage.Builder(this)
                .setChooserTitle("How do you get image?")
                .setChooserType(ChooserType.CAMERA_AND_DOCUMENTS)
                .build();

        loadView();
    }



    private void loadView() {
        tietTitle = findViewById(R.id.tietTitle);
        tietSummary = findViewById(R.id.tietSummary);
        tietBody = findViewById(R.id.tietBody);
        mbtnPhoto = findViewById(R.id.mbtnPhoto);
        ivPhoto = findViewById(R.id.ivPhoto);

        mbtnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] options = {"Camera","Gallery"};
                AlertDialog. Builder builder = new AlertDialog.Builder(AddNewsActivity.this);
                builder.setTitle("How do you get the image?");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(options[which].equals("Camera")){
                            permissionUtil = PermissionUtil.checkPermissionAndGo(AddNewsActivity.this, 10,new Runnable() {
                                @Override
                                public void run() {
                                    easyImage.openCameraForImage(AddNewsActivity.this);
                                }
                            }, Manifest.permission.CAMERA);
                        }else if(options[which].equals("Gallery")){
                            easyImage.openDocuments(AddNewsActivity.this);
                        }
                    }
                });
                builder.create().show();

                //MaterialAlertDialog
                /*
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(AddNewsActivity.this);
                materialAlertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                materialAlertDialogBuilder.create().show();
                 */
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(permissionUtil != null){
            permissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        easyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Throwable throwable, MediaSource mediaSource) {

            }

            @Override
            public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                mediaFile = mediaFiles[0];
                Picasso.get().load(mediaFile.getFile()).into(ivPhoto);
            }

            @Override
            public void onCanceled(MediaSource mediaSource) {

            }
        });
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

        if(mediaFile == null){
            InfoUtil.showToast(this, "photo cannot be empty");
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
        AndroidNetworking.upload(Config.getAddNewsUrl())
                .setOkHttpClient(HttpUtil.getCLient(AddNewsActivity.this))
                .addMultipartFile("photo", mediaFile.getFile())
                .addMultipartParameter("groupcode", Config.GROUP_CODE)
                .addMultipartParameter("title", txtTitle)
                .addMultipartParameter("summary", txtSummary)
                .addMultipartParameter("body", txtBody)
                .setTag("addnews")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {

                    }
                })
                .getAsObject(AddNewsResponse.class, new ParsedRequestListener<AddNewsResponse>() {
                    @Override
                    public void onResponse(AddNewsResponse response) {
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
