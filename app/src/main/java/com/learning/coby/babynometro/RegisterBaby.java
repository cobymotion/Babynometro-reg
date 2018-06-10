package com.learning.coby.babynometro;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegisterBaby extends AppCompatActivity {

    private static final int CAMERA_CODE = 801;
    public static final int REQUEST_PERMISSION_CAMERA = 900;
    public static final int REQUEST_PERMISSION_NET = 901;
     Uri fileUri;

    private static final String IMAGE_DIRECTORY_NAME = "babynometro";

    @BindView(R.id.thumbnail)
    CircleImageView thumbnail;
    @BindView(R.id.txtName)
    EditText txtName;
    @BindView(R.id.txtPhone)
    EditText txtPhone;
    @BindView(R.id.txtLife)
    EditText txtLife;
    @BindView(R.id.imgSave)
    ImageButton btnSave;


    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_baby);
        ButterKnife.bind(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    public void capturePhoto(View view){
        Log.e("Capture Photo", "Initialization.... ");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA, Manifest.permission.MEDIA_CONTENT_CONTROL},
                    REQUEST_PERMISSION_CAMERA);
            return;
        }
        Intent photoIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri();
        photoIntent.putExtra("camera", true);
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.putExtra("camera", false);
        List<Intent> intents = new ArrayList<>();

        intents.add(pickIntent);
        intents.add(photoIntent);

        Intent chooserIntent = Intent.createChooser(intents.get(0),
                getString(R.string.title_chooser));
        intents.remove(0);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[intents.size()]));
        startActivityForResult(chooserIntent,CAMERA_CODE);

    }

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        Log.e("DIRECTORY PHOTOS", mediaStorageDir.getAbsolutePath());
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdir()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Algo fallo "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK){

            boolean isCamara = true;
            if(data != null )
                isCamara = data.getBooleanExtra("camera",false);
            Bitmap bitmap ;
            BitmapFactory.Options options = new BitmapFactory.Options();
            if(isCamara) {
                bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                        options);
                thumbnail.setImageBitmap(bitmap);
            } else {
                fileUri = data.getData();
                thumbnail.setImageURI(fileUri);
            }
         }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.imgSave)
    public void saveBaby(){

        if(!MainActivity.isPermissionNet(this,  REQUEST_PERMISSION_NET))
           return;

        dialog = ProgressDialog.show(this,"",getString(R.string.message_save),true);

        PornStar pornStar = new PornStar();
        pornStar.setName(txtName.getText().toString());
        pornStar.setPhone(txtPhone.getText().toString());
        pornStar.setLife(txtLife.getText().toString());

        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pornStar.setPhoto(getBytePhoto(bmp));
        Log.e("IMAGE", Arrays.toString(pornStar.getPhoto()));

        Retrofit retrofit = new Retrofit.Builder().baseUrl(MainActivity.urlShared(this)).addConverterFactory(GsonConverterFactory.create()).build();
        BabynometroWs request = retrofit.create(BabynometroWs.class);

        Call<Boolean> requestData  = request.saveStar(pornStar);

        requestData.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(!response.isSuccessful())
                {
                    messageSnackBar(getString(R.string.error_request));
                    return;
                }
                Boolean statusClass = response.body();
                dialog.dismiss();
                if(statusClass)
                    closeRegister();
                else
                    messageSnackBar(getString(R.string.error_save));

            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                dialog.dismiss();
                Log.e("RETROFIT",t.getMessage());
                t.printStackTrace();
                messageSnackBar(getString(R.string.error_server));
            }
        });




    }


    private byte[] getBytePhoto(Bitmap bmp)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();
        return byteArray;
    }


    private void messageSnackBar(String msg)
    {
        Snackbar.make(btnSave,msg, Snackbar.LENGTH_LONG).show();
    }

    private void closeRegister() {
        finish();
    }

}
