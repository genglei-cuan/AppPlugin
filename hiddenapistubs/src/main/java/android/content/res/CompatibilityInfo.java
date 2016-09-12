package android.content.res;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by genglei-cuan on 16-9-12.
 */

public class CompatibilityInfo  implements Parcelable {
    @Override
    public int describeContents() {
        throw new UnsupportedOperationException("STUB");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        throw new UnsupportedOperationException("STUB");
    }

    public static final Parcelable.Creator<CompatibilityInfo> CREATOR = null;
}
