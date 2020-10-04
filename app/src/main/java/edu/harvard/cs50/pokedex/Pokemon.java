package edu.harvard.cs50.pokedex;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Pokemon implements Parcelable {
    private String name;
    private String url;
    private boolean caught;

    Pokemon(String name, String url) {
        this.name = name;
        this.url = url;
        this.caught = false;
    }

    protected Pokemon(Parcel in) {
        name = in.readString();
        url = in.readString();
        caught = in.readByte() != 0;
    }

    public static final Creator<Pokemon> CREATOR = new Creator<Pokemon>() {
        @Override
        public Pokemon createFromParcel(Parcel in) {
            return new Pokemon(in);
        }

        @Override
        public Pokemon[] newArray(int size) {
            return new Pokemon[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public boolean isCaught() { return caught; }

    public void setCaught(boolean caught) {
        this.caught = caught;
    }

    public void toggleCaught() {
        Log.d("Toggle Caught", this.caught + " to " + !this.caught);
        this.caught = !this.caught;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(url);
        parcel.writeByte((byte) (caught ? 1 : 0));
    }
}
