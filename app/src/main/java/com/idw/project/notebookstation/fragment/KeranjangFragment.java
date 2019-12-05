package com.idw.project.notebookstation.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.idw.project.notebookstation.R;
import com.idw.project.notebookstation.activity.MainActivity;
import com.idw.project.notebookstation.adapter.KeranjangAdapter;
import com.idw.project.notebookstation.model.Keranjang;
import com.idw.project.notebookstation.response.KeranjangDetailResponse;
import com.idw.project.notebookstation.rest.ApiClient;
import com.idw.project.notebookstation.rest.ApiInterface;
import com.idw.project.notebookstation.util.SessionManager;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class KeranjangFragment extends Fragment {
    private RecyclerView recylerView;
    private KeranjangAdapter keranjangAdapter;
    private ArrayList<Keranjang> keranjangArrayList = new ArrayList<>();
    LinearLayout ll_label, ll_data_keranjang_kosong;
    private Button btn_belanja_sekarang, btn_beli;

    private ApiInterface apiInterface;
    private SessionManager sessionManager;

    String id_konsumen;


    public KeranjangFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_keranjang, container, false);

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
        getActivity().setTitle("Keranjang");

        recylerView = view.findViewById(R.id.recylerView1);
        ll_label = view.findViewById(R.id.ll_label);
        ll_data_keranjang_kosong = view.findViewById(R.id.ll_data_keranjang_kosong);
        btn_belanja_sekarang = view.findViewById(R.id.btn_belanja_sekarang);
        btn_beli = view.findViewById(R.id.btn_beli);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new SessionManager(getActivity());

//        Fragment frg = null;
//        frg = getFragmentManager().findFragmentByTag("Your_Fragment_TAG");
//        final FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(frg);
//        ft.attach(frg);
//        ft.commit();

        btn_belanja_sekarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getActivity().startActivity(intent);
            }
        });

        btn_beli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Klik beli", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        keranjangArrayList.clear();
        getKeranjang();

    }

    private void getKeranjang() {
        id_konsumen = sessionManager.getLoginDetail().get(SessionManager.ID_KONSUMEN);

        apiInterface.keranjangDetail(id_konsumen).enqueue(new Callback<KeranjangDetailResponse>() {
            @Override
            public void onResponse(Call<KeranjangDetailResponse> call, Response<KeranjangDetailResponse> response) {
                System.out.println("responnya"+response);
                if (response.isSuccessful()){
                    if(response.body().getMaster().size()>0){
                        ll_data_keranjang_kosong.setVisibility(View.GONE);
                        ll_label.setVisibility(View.VISIBLE);keranjangArrayList.addAll(response.body().getMaster());
                        System
                        .out.println(response.body().getMaster().get(0).getNamaProduk());

                        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                        recylerView.setLayoutManager(manager);
                        recylerView.setHasFixedSize(true);
                        keranjangAdapter = new KeranjangAdapter(getActivity(), keranjangArrayList);
                        recylerView.setAdapter(keranjangAdapter);

                    }else {
                        ll_label.setVisibility(View.GONE);
                        ll_data_keranjang_kosong.setVisibility(View.VISIBLE);
                    }
                }else {
                    Toast.makeText(getActivity(), "terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KeranjangDetailResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
