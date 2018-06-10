package com.learning.coby.babynometro;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final int REGISTER_ACTIVITY = 800;
    public static final int REQUEST_PERMISSION_NET=901;



    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler)
    RecyclerView rv;

    List<PornStar> datos;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        pedirDatos();
    }

    private void pedirDatos() {
        if(!MainActivity.isPermissionNet(this,  REQUEST_PERMISSION_NET))
            return;
        dialog = ProgressDialog.show(this,"",getString(R.string.message_save),true);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(MainActivity.urlShared(this)).addConverterFactory(GsonConverterFactory.create()).build();
        BabynometroWs request = retrofit.create(BabynometroWs.class);

        Call<List<PornStar>> requestData = request.getRegistros();

        requestData.enqueue(new Callback<List<PornStar>>() {
            @Override
            public void onResponse(Call<List<PornStar>> call, Response<List<PornStar>> response) {
                if(!response.isSuccessful())
                {
                    //messageSnackBar(getString(R.string.error_request));
                    return;
                }
                datos = response.body();
                dialog.dismiss();
                llenarRecycler();
            }

            @Override
            public void onFailure(Call<List<PornStar>> call, Throwable t) {

            }
        });

    }

    public void llenarRecycler() {
        if(datos!=null)
        {
            AdapterRecycler ar = new AdapterRecycler(this,datos);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            rv.setLayoutManager(llm);
            rv.setAdapter(ar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.ic_menu_add:
                Intent i  = new Intent(this, RegisterBaby.class);
                startActivityForResult(i, REGISTER_ACTIVITY);
        }
        return super.onOptionsItemSelected(item);
    }




    public static String urlShared(Activity c){
        SharedPreferences sp = c.getPreferences(c.MODE_PRIVATE);
        String cad = sp.getString("url", "http://192.168.1.71:4567/");
        return cad;
    }


    public static boolean isPermissionNet(Context context, int code)
    {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE},code
                    );
            return false;
        }
        return true;
    }


}
