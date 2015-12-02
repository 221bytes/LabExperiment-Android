package alexandre.nakatani.rits.experimentlab.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pictogram {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("_id")
    @Expose
    private alexandre.nakatani.rits.experimentlab.models.Id Id;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("last_update")
    @Expose
    private String lastUpdate;

    /**
     *
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The Id
     */
    public alexandre.nakatani.rits.experimentlab.models.Id getId() {
        return Id;
    }

    /**
     *
     * @param Id
     *     The _id
     */
    public void setId(alexandre.nakatani.rits.experimentlab.models.Id Id) {
        this.Id = Id;
    }

    /**
     *
     * @return
     *     The path
     */
    public String getPath() {
        return path;
    }

    /**
     *
     * @param path
     *     The path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     *
     * @return
     *     The lastUpdate
     */
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     *
     * @param lastUpdate
     *     The last_update
     */
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}