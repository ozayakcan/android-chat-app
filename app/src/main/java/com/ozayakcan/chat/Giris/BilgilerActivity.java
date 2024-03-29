package com.ozayakcan.chat.Giris;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozayakcan.chat.Ayarlar.ProfilActivity;
import com.ozayakcan.chat.MainActivity;
import com.ozayakcan.chat.Model.Kullanici;
import com.ozayakcan.chat.Ozellik.Izinler;
import com.ozayakcan.chat.Resim.ResimlerClass;
import com.ozayakcan.chat.Ozellik.SharedPreference;
import com.ozayakcan.chat.Ozellik.Veritabani;
import com.ozayakcan.chat.R;
import com.ozayakcan.chat.SSActivity;
import com.yalantis.ucrop.UCrop;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class BilgilerActivity extends AppCompatActivity {

    private LinearLayout progressBarLayout;
    private CircleImageView profilResmi;
    private EditText isimET;
    private TextView isimHata;
    private Button bitirBtn;
    FirebaseUser firebaseUser;
    private String resimBaglantisi = Veritabani.VarsayilanDeger;

    String profilResmiString, isimString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bilgiler);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        progressBarLayout = findViewById(R.id.progressBarLayout);
        profilResmi = findViewById(R.id.profilResmi);
        CircleImageView kamera = findViewById(R.id.kamera);
        isimET = findViewById(R.id.isimET);
        Intent intent = getIntent();
        profilResmiString = intent.getStringExtra(Veritabani.ProfilResmiKey);
        isimString = intent.getStringExtra(Veritabani.IsimKey);
        isimHata = findViewById(R.id.isimHata);
        if (isimString.equals("")){
            BilgileriGetir();
        }else{
            ResimlerClass.getInstance(BilgilerActivity.this).ResimGoster(profilResmiString, profilResmi, R.drawable.ic_profil_resmi);
            resimBaglantisi = profilResmiString;
            isimET.setText(isimString);
            isimET.setSelection(isimET.getText().length());
        }
        bitirBtn = findViewById(R.id.bitirBtn);
        profilResmi.setOnClickListener(v -> ResimlerClass.getInstance(BilgilerActivity.this).ProfilResmiGoruntule("", resimBaglantisi));
        kamera.setOnClickListener(v -> ProfilResmiDegistir());
        isimET.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                Kaydet();
            }
            return false;
        });
        isimET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isimHata.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bitirBtn.setOnClickListener(v -> Kaydet());
    }

    private void BilgileriGetir() {
        DatabaseReference bilgilerReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber());
        bilgilerReference.keepSynced(true);
        bilgilerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                if (kullanici != null){
                    ResimlerClass.getInstance(BilgilerActivity.this).ResimGoster(kullanici.getProfilResmi(), profilResmi, R.drawable.ic_profil_resmi);
                    resimBaglantisi = kullanici.getProfilResmi();
                    isimET.setText(kullanici.getIsim());
                    isimET.setSelection(isimET.getText().length());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ProfilResmiDegistir() {
        ResimlerClass.getInstance(BilgilerActivity.this).ProfilResmiDegistir(firebaseUser, resimBaglantisi, profilResmi, resimYukleActivityResult, kameraIzniResultLauncher, dosyaIzniResultLauncher);
    }

    ActivityResultLauncher<String> kameraIzniResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result){
                    KameraIzniVerildi();
                }else{
                    KameraIzniVerilmedi();
                }
            });

    private void KameraIzniVerildi(){
        ResimlerClass.getInstance(BilgilerActivity.this).KameradanYukle(resimYukleActivityResult);
    }
    private void KameraIzniVerilmedi(){
        Izinler.getInstance(BilgilerActivity.this).ZorunluIzinUyariKutusu(Manifest.permission.CAMERA, kameraIzniResultLauncher);
    }
    ActivityResultLauncher<String> dosyaIzniResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result){
                    DosyaIzniVerildi();
                }else{
                    DosyaIzniVerilmedi();
                }
            });
    private void DosyaIzniVerildi(){
        ResimlerClass.getInstance(BilgilerActivity.this).GaleridenYukle(resimYukleActivityResult);
    }
    private void DosyaIzniVerilmedi(){
        Izinler.getInstance(BilgilerActivity.this).ZorunluIzinUyariKutusu(Manifest.permission.READ_EXTERNAL_STORAGE, dosyaIzniResultLauncher);
    }
    ActivityResultLauncher<Intent> resimYukleActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null){
                        Uri sonuc = intent.getData();
                        ResimlerClass.getInstance(BilgilerActivity.this).ResimKirp(sonuc);
                    }else{
                        Toast.makeText(BilgilerActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (data != null){
                Uri resimUri = UCrop.getOutput(data);
                ResimlerClass.getInstance(BilgilerActivity.this).ResimYukle(resimUri, firebaseUser.getUid() + "/" + Veritabani.ProfilResmiDosyaAdi + ResimlerClass.VarsayilanResimUzantisi, progressBarLayout, new ResimlerClass.ResimYukleSonuc() {
                    @Override
                    public void Basarili(String resimUrl) {
                        resimBaglantisi = resimUrl;
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber()).child(Veritabani.ProfilResmiKey);
                        reference.keepSynced(true);
                        reference.setValue(resimBaglantisi);
                        if (profilResmi != null){
                            ResimlerClass.getInstance(BilgilerActivity.this).ResimGoster(resimBaglantisi, profilResmi, R.drawable.ic_profil_resmi);
                        }
                    }

                    @Override
                    public void Basarisiz(String hata) {
                        Log.e("Resim", hata);
                        Toast.makeText(BilgilerActivity.this, getString(R.string.could_not_update_profile_photo), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void Kaydet(){
        String isim = isimET.getText().toString();
        if (isim.isEmpty()){
            isimHata.setVisibility(View.VISIBLE);
        }else{
            bitirBtn.setEnabled(false);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Veritabani.KullaniciTablosu).child(firebaseUser.getPhoneNumber());
			databaseReference.keepSynced(true);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("DB Kullanıcı Kaydet", snapshot.toString());
                    Kullanici kullanici = snapshot.getValue(Kullanici.class);
                    Kullanici kullaniciEkle = new Kullanici(firebaseUser.getUid(), isimET.getText().toString(), firebaseUser.getPhoneNumber(), Veritabani.VarsayilanHakkimdaYazisi(BilgilerActivity.this), true);
                    if (kullanici != null){
                        kullaniciEkle.setBildirimDurumu(kullanici.isBildirimDurumu());
                        SharedPreference.getInstance(BilgilerActivity.this).KaydetBoolean(Veritabani.BildirimDurumuKey, kullanici.isBildirimDurumu());

                        kullaniciEkle.setBildirimSesi(kullanici.isBildirimSesi());
                        SharedPreference.getInstance(BilgilerActivity.this).KaydetBoolean(Veritabani.BildirimSesiKey, kullanici.isBildirimSesi());

                        kullaniciEkle.setBildirimOncelik(kullanici.isBildirimOncelik());
                        SharedPreference.getInstance(BilgilerActivity.this).KaydetBoolean(Veritabani.BildirimOncelikKey, kullanici.isBildirimOncelik());

                        kullaniciEkle.setBildirimTitresim(kullanici.getBildirimTitresim());
                        SharedPreference.getInstance(BilgilerActivity.this).KaydetLong(Veritabani.BildirimTitresimKey, kullanici.getBildirimTitresim());

                        kullaniciEkle.setBildirimIsigi(kullanici.getBildirimIsigi());
                        SharedPreference.getInstance(BilgilerActivity.this).KaydetLong(Veritabani.BildirimIsigiKey, kullanici.getBildirimIsigi());
                    }
                    HashMap<String, Object> map = Veritabani.getInstance(BilgilerActivity.this).KayitHashMap(kullaniciEkle, kullanici == null);
                    databaseReference.updateChildren(map, (error, ref) -> {
                        if (error == null){
                            Tamamlandi();
                        }else{
                            Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            bitirBtn.setEnabled(true);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Veritabanı Hatası", error.getMessage());
                    Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    bitirBtn.setEnabled(true);
                }
            });
        }
    }

    private void Tamamlandi() {
        SharedPreference.getInstance(BilgilerActivity.this).KaydetBoolean(SharedPreference.kullaniciKaydedildi, true);
        startActivity(new Intent(BilgilerActivity.this, SSActivity.class));
        finish();
        overridePendingTransition(0,0);
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return false;
        }
        else{
            return super.onKeyDown(keyCode, event);
        }
    }
}