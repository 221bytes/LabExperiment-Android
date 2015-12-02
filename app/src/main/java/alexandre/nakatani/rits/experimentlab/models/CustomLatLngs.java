package alexandre.nakatani.rits.experimentlab.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by necocityhunters on 15/12/02.
 */
public class CustomLatLngs
{
    @SerializedName("latitude")
    @Expose
    private double mLatitude;

    @SerializedName("longitude")
    @Expose
    private double mLongitude;


    public CustomLatLngs(double latitude, double longitude)
    {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public CustomLatLngs(LatLng latLng)
    {
        mLatitude = latLng.latitude;
        mLongitude = latLng.longitude;
    }

    public double getLatitude()
    {
        return mLatitude;
    }

    public void setLatitude(double latitude)
    {
        mLatitude = latitude;
    }

    public double getLongitude()
    {
        return mLongitude;
    }

    public void setLongitude(double longitude)
    {
        mLongitude = longitude;
    }
}
