package com.cuan.plugincore.servicemanager;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by genglei-cuan on 16-9-18.
 */

/**
 * 对service Ibinder的跨进程传输
 */
public class ParcelBinder implements Parcelable {

    private IBinder binder;

    public ParcelBinder(IBinder binder) {
        this.binder = binder;
    }

    private ParcelBinder(Parcel parcel) {
        readFromParcel(parcel);
    }

    public IBinder getBinder() {
        return binder;
    }

    public void setBinder(IBinder binder) {
        this.binder = binder;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(binder);
    }

    public static final Creator<ParcelBinder> CREATOR = new Creator<ParcelBinder>() {
        @Override
        public ParcelBinder createFromParcel(Parcel source) {
            return new ParcelBinder(source);
        }

        @Override
        public ParcelBinder[] newArray(int size) {
            return new ParcelBinder[size];
        }
    };

    private void readFromParcel(Parcel parcel) {
        this.binder = parcel.readStrongBinder();
    }
}
