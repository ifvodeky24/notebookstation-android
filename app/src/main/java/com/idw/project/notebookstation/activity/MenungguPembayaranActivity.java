package com.idw.project.notebookstation.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.idw.project.notebookstation.R;
import com.idw.project.notebookstation.adapter.TransaksiAdapter;
import com.idw.project.notebookstation.model.Pesanan;
import com.idw.project.notebookstation.response.MenungguPembayaranResponse;
import com.idw.project.notebookstation.rest.ApiClient;
import com.idw.project.notebookstation.rest.ApiInterface;
import com.idw.project.notebookstation.util.SessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenungguPembayaranActivity extends AppCompatActivity {
    private RecyclerView recylerView;
    private TransaksiAdapter transaksiAdapter;
    private ArrayList<Pesanan> pesananArrayList = new ArrayList<>();

    private ApiInterface apiInterface;
    private SessionManager sessionManager;
    private LinearLayout ll_data_pesanan_kosong;

    String id_konsumen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menunggu_pembayaran);

        recylerView = findViewById(R.id.recylerView1);
        ll_data_pesanan_kosong = findViewById(R.id.ll_data_pesanan_kosong);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new SessionManager(this);

        getData();
    }

    private void getData() {
        id_konsumen = sessionManager.getLoginDetail().get(SessionManager.ID_KONSUMEN);

        apiInterface.menungguPembayaran(id_konsumen).enqueue(new Callback<MenungguPembayaranResponse>() {
            @Override
            public void onResponse(Call<MenungguPembayaranResponse> call, Response<MenungguPembayaranResponse> response) {
                System.out.println("responya"+response);
                if (response.isSuccessful()){
                    if(response.body().getMaster().size()>0){
                        ll_data_pesanan_kosong.setVisibility(View.GONE);
                        pesananArrayList.addAll(response.body().getMaster());
                        System.out.println(response.body().getMaster().get(0).getIdPesanan());

                        LinearLayoutManager manager = new LinearLayoutManager(MenungguPembayaranActivity.this);
                        recylerView.setLayoutManager(manager);
                        recylerView.setHasFixedSize(true);
                        transaksiAdapter = new TransaksiAdapter(MenungguPembayaranActivity.this, pesananArrayList);
                        recylerView.setAdapter(transaksiAdapter);

                    }else {
//                        Toast.makeText(getApplicationContext(), "Data Menunggu Pembayaran Kosong", Toast.LENGTH_SHORT).show();
                        ll_data_pesanan_kosong.setVisibility(View.VISIBLE);
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MenungguPembayaranResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
