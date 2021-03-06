package com.idw.project.notebookstation.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.idw.project.notebookstation.R;
import com.idw.project.notebookstation.config.ServerConfig;
import com.idw.project.notebookstation.response.PesananDetailResponse;
import com.idw.project.notebookstation.response.ProdukDetailResponse;
import com.idw.project.notebookstation.rest.ApiClient;
import com.idw.project.notebookstation.rest.ApiInterface;
import com.idw.project.notebookstation.util.SessionManager;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTransaksiActivity extends AppCompatActivity {
    TextView tv_status, tv_kode_pesanan, tv_tanggal_pesanan, tv_nama_produk, tv_merk, tv_harga;
    TextView tv_nama_toko, tv_alamat, tv_harga_produk, tv_ongkos_kirim, tv_total_pembayaran, tv_jumlah;
    ImageView iv_foto;

    ApiInterface apiInterface;
    SessionManager sessionManager;

    String kode_pesanan, total_tagihan, id_produk, id_konsumen, alamat_pengiriman, status, tanggal_pesanan;
    String nama_produk, nama_toko, harga, merk, total_pembayaran, jumlah;

    DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);
        setTitle("Detail Transaksi");

        tv_status = findViewById(R.id.tv_status);
        tv_kode_pesanan = findViewById(R.id.tv_kode_pesanan);
        tv_tanggal_pesanan = findViewById(R.id.tv_tanggal_pesanan);
        tv_nama_produk = findViewById(R.id.tv_nama_produk);
        tv_merk = findViewById(R.id.tv_merk);
        tv_harga = findViewById(R.id.tv_harga);
        tv_nama_toko = findViewById(R.id.tv_nama_toko);
        tv_alamat = findViewById(R.id.tv_alamat);
        iv_foto = findViewById(R.id.iv_foto);
        tv_harga_produk = findViewById(R.id.tv_harga_produk);
        tv_ongkos_kirim = findViewById(R.id.tv_ongkos_kirim);
        tv_total_pembayaran = findViewById(R.id.tv_total_pembayaran);
        tv_jumlah = findViewById(R.id.tv_jumlah);

        sessionManager = new SessionManager(this);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        df = new DecimalFormat( "#,###");

        if (sessionManager.isLoggedIn()){

            Intent intent = getIntent();
            Bundle extras = intent.getExtras();

            if (extras != null){
                kode_pesanan = extras.getString("kode_pesanan");
                total_tagihan = extras.getString("total_tagihan");
                id_produk = extras.getString("id_produk");

                tv_kode_pesanan.setText(kode_pesanan);

                id_konsumen = sessionManager.getLoginDetail().get(SessionManager.ID_KONSUMEN);

                apiInterface.pesananDetail(id_konsumen, kode_pesanan).enqueue(new Callback<PesananDetailResponse>() {
                    @Override
                    public void onResponse(Call<PesananDetailResponse> call, Response<PesananDetailResponse> response) {
                        if (response.isSuccessful()){
                            if (response.body() != null) {
                                alamat_pengiriman = response.body().getMaster().get(0).getAlamatLengkap();
                                status = response.body().getMaster().get(0).getStatus();
                                tanggal_pesanan = response.body().getMaster().get(0).getCreatedAt();
                                total_pembayaran = response.body().getMaster().get(0).getTotalTagihan();
                                jumlah = response.body().getMaster().get(0).getJumlah();


                                tv_alamat.setText(alamat_pengiriman);
                                tv_status.setText(status);
                                tv_tanggal_pesanan.setText(tanggal_pesanan);
                                tv_jumlah.setText(jumlah+" Pcs");
                                tv_total_pembayaran.setText("Rp."+df.format(Double.valueOf(total_pembayaran)));

                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PesananDetailResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Gagal Terhubung ke Server", Toast.LENGTH_SHORT).show();
                    }
                });

                apiInterface.produkById(id_produk).enqueue(new Callback<ProdukDetailResponse>() {
                    @Override
                    public void onResponse(Call<ProdukDetailResponse> call, Response<ProdukDetailResponse> response) {
                        if (response.isSuccessful()){
                            if (response.body() != null && response.body().getMaster().size() > 0) {
                                nama_produk = response.body().getMaster().get(0).getNamaProduk();
                                nama_toko = response.body().getMaster().get(0).getNamaToko();
                                harga = response.body().getMaster().get(0).getHarga();
                                merk = response.body().getMaster().get(0).getMerkProduk();

                                tv_nama_produk.setText(nama_produk);
                                tv_nama_toko.setText(nama_toko);
                                tv_harga.setText("Rp."+df.format(Double.valueOf(harga)));
                                tv_merk.setText(merk);
                                tv_harga_produk.setText("Rp."+df.format(Double.valueOf(harga)));

                                Picasso.with(getApplicationContext())
                                        .load(ServerConfig.PRODUK_IMAGE+ response.body().getMaster().get(0).getFoto1())
                                        .fit()
                                        .centerCrop()
                                        .into(iv_foto);
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ProdukDetailResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Gagal Terhubung ke Server", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
