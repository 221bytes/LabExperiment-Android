
package alexandre.nakatani.rits.experimentlab;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Task {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("_id")
    @Expose
    private alexandre.nakatani.rits.experimentlab.Id Id;

    /**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The Id
     */
    public alexandre.nakatani.rits.experimentlab.Id getId() {
        return Id;
    }

    /**
     * 
     * @param Id
     *     The _id
     */
    public void setId(alexandre.nakatani.rits.experimentlab.Id Id) {
        this.Id = Id;
    }

}
