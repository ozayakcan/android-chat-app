package com.ozayakcan.chat.Fragment;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Adapter.KisiAdapter;
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Ozellik.Izinler;
import com.ozayakcan.chat.Ozellik.SharedPreference;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;

import java.util.ArrayList;
import java.util.List;

public class KisilerFragment extends Fragment {

    private Izinler izinler;
    private SharedPreference sharedPreference;
    private View view;
    private FirebaseUser firebaseUser;
    private RecyclerView kisilerRW;
    private KisiAdapter kisiAdapter;
    private List<Kullanici> kullaniciList;
    private MainActivity mainActivity;
    public KisilerFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kisiler, container, false);
        izinler = new Izinler(getContext());
        sharedPreference = new SharedPreference(getContext());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        kisilerRW = view.findViewById(R.id.kisilerRW);
        kisilerRW.setHasFixedSize(true);
        kisilerRW.setLayoutManager(new LinearLayoutManager(getActivity()));

        kullaniciList = new ArrayList<>();
        if (izinler.KontrolEt(Manifest.permission.READ_CONTACTS)){
            KisileriBul();
        }else{
            izinler.Sor(Manifest.permission.READ_CONTACTS, kisiIzniResultLauncher);
        }
        return view;
    }

    ActivityResultLauncher<String> kisiIzniResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result){
                    KisileriBul();
                }else{
                    KisiIzniUyariKutusu();
                }
            });

    private void KisiIzniUyariKutusu() {
        izinler.ZorunluIzinUyariKutusu(Manifest.permission.READ_CONTACTS, kisiIzniResultLauncher);
    }
    private void KisileriBul(){
        //Kişiler veritabanından çekiliyor
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu+"/"+firebaseUser.getPhoneNumber()+"/"+Veritabani.KisiTablosu);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                KisileriGuncelle(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void KisileriGuncelle(DataSnapshot veriler){
        kullaniciList.clear();
        for (DataSnapshot verilerSnapshot : veriler.getChildren()){
            Kullanici kullanici = verilerSnapshot.getValue(Kullanici.class);
            if (kullanici != null){
                kullaniciList.add(kullanici);
            }
        }
        if (mainActivity != null){
            kisiAdapter = new KisiAdapter(kullaniciList, mainActivity);
        }
        kisilerRW.setAdapter(kisiAdapter);
    }
}