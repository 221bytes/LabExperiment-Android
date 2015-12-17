package alexandre.nakatani.rits.experimentlab.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Enumeration;

public class Event
{


    @SerializedName("_id")
    @Expose
    private alexandre.nakatani.rits.experimentlab.models.Id Id;
    @SerializedName("last_update")
    @Expose
    private String lastUpdate;
    @SerializedName("pictograms")
    @Expose
    private ArrayList<Pictogram> mPictograms;
    @SerializedName("sounds")
    @Expose
    private ArrayList<Sound> mSounds;
    @SerializedName("geojson")
    @Expose
    private GeoJson mGeoJson;
    @SerializedName("importance")
    @Expose
    private int mImportance;


    /**
     * @return The Id
     */
    public alexandre.nakatani.rits.experimentlab.models.Id getId()
    {
        return Id;
    }

    /**
     * @param Id The _id
     */
    public void setId(alexandre.nakatani.rits.experimentlab.models.Id Id)
    {
        this.Id = Id;
    }

    /**
     * @return The lastUpdate
     */
    public String getLastUpdate()
    {
        return lastUpdate;
    }

    /**
     * @param lastUpdate The last_update
     */
    public void setLastUpdate(String lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @return List of Pictograms
     */

    public ArrayList<Pictogram> getPictograms()
    {
        return mPictograms;
    }
    /**
     *
     * @param pictograms
     *     The pictograms
     */
    public void setPictograms(ArrayList<Pictogram> pictograms)
    {
        mPictograms = pictograms;
    }

    /**
     * @return List of Sounds
     */
    public ArrayList<Sound> getSounds()
    {
        return mSounds;
    }

    /**
     * @param sounds The sounds
     */
    public void setSounds(ArrayList<Sound> sounds)
    {
        mSounds = sounds;
    }

    /**
     * @return Geojson
     */
    public GeoJson getGeoJson()
    {
        return mGeoJson;
    }

    /**
     * @param geoJson
     */
    public void setGeoJson(GeoJson geoJson)
    {
        mGeoJson = geoJson;
    }

    /**
     * @return Importance
     */
    public int getImportance()
    {
        return mImportance;
    }

    /**
     * @param importance
     */
    public void setImportance(int importance)
    {
        mImportance = importance;
    }
}