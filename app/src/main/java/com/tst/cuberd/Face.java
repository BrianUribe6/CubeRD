package com.tst.cuberd;

import android.os.Parcel;
import android.os.Parcelable;

public class Face implements Parcelable {
    private int faceId;
    private int faceColor;
    private int faceColorAbove;

    public Face(int faceId, int faceColor, int faceColorAbove) {
        this.faceId = faceId;
        this.faceColor = faceColor;
        this.faceColorAbove = faceColorAbove;
    }

    public int getColorAbove() {
        return faceColorAbove;
    }

    public int getPos() {
        return faceId;
    }

    public int getColor() {
        return faceColor;
    }

    protected Face(Parcel in) {
        faceId = in.readInt();
        faceColor = in.readInt();
        faceColorAbove = in.readInt();
    }

    public static final Creator<Face> CREATOR = new Creator<Face>() {
        @Override
        public Face createFromParcel(Parcel in) {
            return new Face(in);
        }

        @Override
        public Face[] newArray(int size) {
            return new Face[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(faceId);
        parcel.writeInt(faceColor);
        parcel.writeInt(faceColorAbove);
    }
}
