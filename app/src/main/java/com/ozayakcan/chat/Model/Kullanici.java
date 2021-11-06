package com.ozayakcan.chat.Model;

import com.ozayakcan.chat.Ozellik.Veritabani;

public class Kullanici {

    private String ID = "";

    private String isim = "";

    private boolean onlineDurumu = false;

    private String profilResmi = Veritabani.VarsayilanDeger;

    private String telefon = "";

    private String hakkimda = "";

    private long kayitZamani = 0;

    private long sonGorulme = 0;

    private String fcmToken = "0";

    public Kullanici() {
        kayitZamani = System.currentTimeMillis();
        sonGorulme = System.currentTimeMillis();
    }

    public Kullanici(String ID, String isim, String telefon, String hakkimda, boolean onlineDurumu) {
        this.ID = ID;
        this.isim = isim;
        this.telefon = telefon;
        this.hakkimda = hakkimda;
        kayitZamani = System.currentTimeMillis();
        this.onlineDurumu = onlineDurumu;
    }
    public Kullanici(String ID, String isim, String telefon, String hakkimda, long kayitZamani, boolean onlineDurumu) {
        this.ID = ID;
        this.isim = isim;
        this.telefon = telefon;
        this.hakkimda = hakkimda;
        this.kayitZamani = kayitZamani;
        this.onlineDurumu = onlineDurumu;
    }

    public Kullanici(String ID, String isim, String profilResmi, String telefon, String hakkimda, long kayitZamani, boolean onlineDurumu, long sonGorulme, String fcmToken) {
        this.ID = ID;
        this.isim = isim;
        this.profilResmi = profilResmi;
        this.telefon = telefon;
        this.hakkimda = hakkimda;
        this.kayitZamani = kayitZamani;
        this.onlineDurumu = onlineDurumu;
        this.sonGorulme = sonGorulme;
        this.fcmToken = fcmToken;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public boolean getOnlineDurumu() {
        return onlineDurumu;
    }

    public void setOnlineDurumu(boolean onlineDurumu) {
        this.onlineDurumu = onlineDurumu;
    }

    public String getProfilResmi() {
        return profilResmi;
    }

    public void setProfilResmi(String profilResmi) {
        this.profilResmi = profilResmi;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getHakkimda() {
        return hakkimda;
    }

    public void setHakkimda(String hakkimda) {
        this.hakkimda = hakkimda;
    }

    public long getKayitZamani() {
        return kayitZamani;
    }

    public void setKayitZamani(long kayitZamani) {
        this.kayitZamani = kayitZamani;
    }

    public long getSonGorulme() {
        return sonGorulme;
    }

    public void setSonGorulme(long sonGorulme) {
        this.sonGorulme = sonGorulme;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}