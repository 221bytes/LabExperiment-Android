package alexandre.nakatani.rits.experimentlab.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by necocityhunters on 15/12/02.
 */

public class GeoJson
{
    @SerializedName("type")
    @Expose
    private Type mType;

    @SerializedName("coordinates")
    @Expose
    private ArrayList<CustomLatLngs> mLatLng;

    /**
     * @return The Type
     */
    public Type getType()
    {
        return mType;
    }

    /**
     * @param type
     */
    public void setType(Type type)
    {
        mType = type;
    }

    /**
     * @return The arraylist of LatLng
     */
    public ArrayList<CustomLatLngs> getLatLng()
    {
        return mLatLng;
    }

    /**
     * @param latLng
     */
    public void setLatLng(ArrayList<CustomLatLngs> latLng)
    {
        mLatLng = latLng;
    }

    public enum Type
    {
        POINT, POLYGON, POLYLINE
    }

    public LatLng getClassicLatLng()
    {
        LatLng latLng = new LatLng(mLatLng.get(0).getLatitude(), mLatLng.get(0).getLongitude());
        return latLng;
    }

}
