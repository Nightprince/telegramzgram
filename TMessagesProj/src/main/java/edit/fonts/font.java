package edit.fonts;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class font implements Parcelable {
    public static final Creator<font> CREATOR = new C09451();
    String address;
    String name;

    static class C09451 implements Creator<font> {
        C09451() {
        }

        public font createFromParcel(Parcel in) {
            return new font(in);
        }

        public font[] newArray(int size) {
            return new font[size];
        }
    }

    public font(String name, String address) {
        this.name = name;
        this.address = address;
    }

    protected font(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.address);
    }
}
