package Weather;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Roy on 12-Aug-16.
 */
public class WeatherData implements Parcelable {
    private int id;
    private String postcode;
    private String suburb;
    private float latitude;
    private float longitude;
    private int solarstation;
    private String solarname;
    private float solardistance;
    private float solarwet;
    private float solardry;
    private float solarmean;
    private int rainstation;
    private String rainname;
    private float raindistance;
    private float rainwet;
    private float raindry;
    private float rainmean;
    private int tempstation;
    private String tempname;
    private float tempdistance;
    private float tempmaxwet;
    private float tempmaxdry;
    private float tempmaxmean;
    private float tempminwet;
    private float tempmindry;
    private float tempminmean;

    public WeatherData(int id, String postcode, String suburb, float latitude, float longitude, int solarstation, String solarname, float solardistance, float solarwet, float solardry, float solarmean, int rainstation, String rainname, float raindistance, float rainwet, float raindry, float rainmean, int tempstation, String tempname, float tempdistance, float tempmaxwet, float tempmaxdry, float tempmaxmean, float tempminwet, float tempmindry, float tempminmean) {
        this.id = id;
        this.postcode = postcode;
        this.suburb = suburb;
        this.latitude = latitude;
        this.longitude = longitude;
        this.solarstation = solarstation;
        this.solarname = solarname;
        this.solardistance = solardistance;
        this.solarwet = solarwet;
        this.solardry = solardry;
        this.solarmean = solarmean;
        this.rainstation = rainstation;
        this.rainname = rainname;
        this.raindistance = raindistance;
        this.rainwet = rainwet;
        this.raindry = raindry;
        this.rainmean = rainmean;
        this.tempstation = tempstation;
        this.tempname = tempname;
        this.tempdistance = tempdistance;
        this.tempmaxwet = tempmaxwet;
        this.tempmaxdry = tempmaxdry;
        this.tempmaxmean = tempmaxmean;
        this.tempminwet = tempminwet;
        this.tempmindry = tempmindry;
        this.tempminmean = tempminmean;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getSolarstation() {
        return solarstation;
    }

    public void setSolarstation(int solarstation) {
        this.solarstation = solarstation;
    }

    public String getSolarname() {
        return solarname;
    }

    public void setSolarname(String solarname) {
        this.solarname = solarname;
    }

    public float getSolardistance() {
        return solardistance;
    }

    public void setSolardistance(float solardistance) {
        this.solardistance = solardistance;
    }

    public float getSolarwet() {
        return solarwet;
    }

    public void setSolarwet(float solarwet) {
        this.solarwet = solarwet;
    }

    public float getSolardry() {
        return solardry;
    }

    public void setSolardry(float solardry) {
        this.solardry = solardry;
    }

    public float getSolarmean() {
        return solarmean;
    }

    public void setSolarmean(float solarmean) {
        this.solarmean = solarmean;
    }

    public int getRainstation() {
        return rainstation;
    }

    public void setRainstation(int rainstation) {
        this.rainstation = rainstation;
    }

    public String getRainname() {
        return rainname;
    }

    public void setRainname(String rainname) {
        this.rainname = rainname;
    }

    public float getRaindistance() {
        return raindistance;
    }

    public void setRaindistance(float raindistance) {
        this.raindistance = raindistance;
    }

    public float getRainwet() {
        return rainwet;
    }

    public void setRainwet(float rainwet) {
        this.rainwet = rainwet;
    }

    public float getRaindry() {
        return raindry;
    }

    public void setRaindry(float raindry) {
        this.raindry = raindry;
    }

    public float getRainmean() {
        return rainmean;
    }

    public void setRainmean(float rainmean) {
        this.rainmean = rainmean;
    }

    public int getTempstation() {
        return tempstation;
    }

    public void setTempstation(int tempstation) {
        this.tempstation = tempstation;
    }

    public String getTempname() {
        return tempname;
    }

    public void setTempname(String tempname) {
        this.tempname = tempname;
    }

    public float getTempdistance() {
        return tempdistance;
    }

    public void setTempdistance(float tempdistance) {
        this.tempdistance = tempdistance;
    }

    public float getTempmaxwet() {
        return tempmaxwet;
    }

    public void setTempmaxwet(float tempmaxwet) {
        this.tempmaxwet = tempmaxwet;
    }

    public float getTempmaxdry() {
        return tempmaxdry;
    }

    public void setTempmaxdry(float tempmaxdry) {
        this.tempmaxdry = tempmaxdry;
    }

    public float getTempmaxmean() {
        return tempmaxmean;
    }

    public void setTempmaxmean(float tempmaxmean) {
        this.tempmaxmean = tempmaxmean;
    }

    public float getTempminwet() {
        return tempminwet;
    }

    public void setTempminwet(float tempminwet) {
        this.tempminwet = tempminwet;
    }

    public float getTempmindry() {
        return tempmindry;
    }

    public void setTempmindry(float tempmindry) {
        this.tempmindry = tempmindry;
    }

    public float getTempminmean() {
        return tempminmean;
    }

    public void setTempminmean(float tempminmean) {
        this.tempminmean = tempminmean;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeStringArray(new String[] {String.valueOf(this.id), this.postcode, this.suburb, String.valueOf(this.latitude), String.valueOf(this.longitude), String.valueOf(this.solarstation), this.solarname, String.valueOf(this.solardistance), String.valueOf(this.solarwet), String.valueOf(this.solardry), String.valueOf(this.solarmean), String.valueOf(this.rainstation), this.rainname, String.valueOf(this.raindistance), String.valueOf(this.rainwet), String.valueOf(this.raindry), String.valueOf(this.rainmean), String.valueOf(this.tempstation), this.tempname, String.valueOf(this.tempdistance), String.valueOf(this.tempmaxwet), String.valueOf(this.tempmaxdry), String.valueOf(this.tempmaxmean), String.valueOf(this.tempminwet), String.valueOf(this.tempmindry), String.valueOf(this.tempminmean)});
        dest.writeInt(this.id);
        dest.writeString(postcode);
        dest.writeString(suburb);
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
        dest.writeInt(solarstation);
        dest.writeString(solarname);
        dest.writeFloat(solardistance);
        dest.writeFloat(solarwet);
        dest.writeFloat(solardry);
        dest.writeFloat(solarmean);
        dest.writeFloat(rainstation);
        dest.writeString(rainname);
        dest.writeFloat(raindistance);
        dest.writeFloat(rainwet);
        dest.writeFloat(raindry);
        dest.writeFloat(rainmean);
        dest.writeInt(tempstation);
        dest.writeString(tempname);
        dest.writeFloat(tempdistance);
        dest.writeFloat(tempmaxwet);
        dest.writeFloat(tempmaxdry);
        dest.writeFloat(tempmaxmean);
        dest.writeFloat(tempminwet);
        dest.writeFloat(tempmindry);
        dest.writeFloat(tempminmean);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public WeatherData createFromParcel(Parcel in) {
            return new WeatherData(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[0];
        }

    };

    public WeatherData(Parcel parcel){
        this.id = parcel.readInt();
        this.postcode =  parcel.readString();
        this.suburb = parcel.readString();
        this.latitude = parcel.readFloat();
        this.longitude = parcel.readFloat();
        this.solarstation =  parcel.readInt();
        this.solarname = parcel.readString();
        this.solardistance = parcel.readFloat();
        this.solarwet = parcel.readFloat();
        this.solardry = parcel.readFloat();
        this.solarmean = parcel.readFloat();
        this.rainstation =  parcel.readInt();
        this.rainname = parcel.readString();
        this.raindistance = parcel.readFloat();
        this.rainwet = parcel.readFloat();
        this.raindry = parcel.readFloat();
        this.rainmean = parcel.readFloat();
        this.tempstation =  parcel.readInt();
        this.tempname = parcel.readString();
        this.tempdistance = parcel.readFloat();
        this.tempmaxwet = parcel.readFloat();
        this.tempmaxdry = parcel.readFloat();
        this.tempmaxmean = parcel.readFloat();
        this.tempminwet = parcel.readFloat();
        this.tempmindry = parcel.readFloat();
        this.tempminmean = parcel.readFloat();
    }
}
