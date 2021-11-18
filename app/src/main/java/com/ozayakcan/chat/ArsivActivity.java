package com.ozayakcan.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ozayakcan.chat.Fragment.KisilerFragment;
import com.ozayakcan.chat.Fragment.MesajlarFragment;
import com.ozayakcan.chat.Fragment.VPAdapter;
import com.ozayakcan.chat.Model.Mesajlar;
import com.ozayakcan.chat.Ozellik.Veritabani;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArsivActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    Toolbar toolbar;
    FirebaseUser firebaseUser;
    MesajlarFragment mesajlarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arsiv);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.geri_butonu);
        toolbar.setNavigationOnClickListener(view -> Geri());
        viewPager = findViewById(R.id.viewPager);
        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), getLifecycle());
        mesajlarFragment = new MesajlarFragment(ArsivActivity.this);
        vpAdapter.fragmentEkle(mesajlarFragment, getString(R.string.messages));
        viewPager.setAdapter(vpAdapter);
        ArsivMenusu();
    }
    public void ArsivMenusu(){
        MesajBasiliTut(false);
        toolbar.getMenu().clear();
    }
    public boolean MesajSecildi = false;
    public void MesajBasiliTut(boolean secildi){
        if (secildi){
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.mesajlar_islev);
            toolbar.getMenu().findItem(R.id.menuArsivle).setTitle(getString(R.string.unarchive));
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menuSil || item.getItemId() == R.id.menuArsivle){
                    List<Mesajlar> mesajlarArrayList = new ArrayList<>();
                    for (int i = 0; i < mesajlarFragment.mesajlarList.size(); i++) {
                        Mesajlar mesajlar = mesajlarFragment.mesajlarList.get(i);
                        if (mesajlar.isSecildi()) {
                            mesajlarArrayList.add(mesajlar);
                        }
                    }
                    if(item.getItemId() == R.id.menuSil){
                        mesajlarFragment.MesajlariSil(mesajlarArrayList, true);
                    }else{
                        mesajlarFragment.MesajlariArsivle(mesajlarArrayList, false);
                    }
                }
                return false;
            });
        }
        MesajSecildi = secildi;
    }

    public void MesajGoster(String id, String isim, String telefon, String profilResmi){
        Intent intent = new Intent(ArsivActivity.this, MesajActivity.class);
        intent.putExtra(Veritabani.IDKey, id);
        intent.putExtra(Veritabani.IsimKey, isim);
        intent.putExtra(Veritabani.TelefonKey, telefon);
        intent.putExtra(Veritabani.ProfilResmiKey, profilResmi);
        intent.putExtra(Veritabani.MesajTablosu, Veritabani.ArsivTablosu);
        startActivity(intent);
        overridePendingTransition(R.anim.sagdan_sola_giris, R.anim.sagdan_sola_cikis);
        finish();
    }

    private void Geri() {
        if (MesajSecildi){
            ArsivMenusu();
        }else{
            startActivity(new Intent(ArsivActivity.this, MainActivity.class));
            overridePendingTransition(0,0);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Geri();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Geri();
            return false;
        }
        else{
            return super.onKeyDown(keyCode, event);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Veritabani.DurumGuncelle(firebaseUser, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Veritabani.DurumGuncelle(firebaseUser, false);
    }
}