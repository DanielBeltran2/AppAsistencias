package com.example.appasistencias;

import android.os.Parcel;
import android.os.Parcelable;

public class Clase implements Parcelable {
    private String rol;
    private String id;

    public Clase(String rol, String id) {
        this.rol = rol;
        this.id = id;
    }

    protected Clase(Parcel in) {
        rol = in.readString();
        id = in.readString();
    }

    public static final Creator<Clase> CREATOR = new Creator<Clase>() {
        @Override
        public Clase createFromParcel(Parcel in) {
            return new Clase(in);
        }

        @Override
        public Clase[] newArray(int size) {
            return new Clase[size];
        }
    };

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rol);
        dest.writeString(id);
    }
}
