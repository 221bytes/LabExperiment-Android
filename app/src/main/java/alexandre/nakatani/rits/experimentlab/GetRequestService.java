package alexandre.nakatani.rits.experimentlab;

import java.util.ArrayList;

import alexandre.nakatani.rits.experimentlab.models.Event;
import alexandre.nakatani.rits.experimentlab.models.Pictogram;
import alexandre.nakatani.rits.experimentlab.models.Sound;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by Alexandre Catalano aka 221Bytes on 6/1/15.
 */
public interface GetRequestService
{
    @GET("/pictograms")
    void getPictograms(Callback<ArrayList<Pictogram>> cb);
    @GET("/sounds")
    void getSounds(Callback<ArrayList<Sound>> cb);

    @POST("/events")
    void postEvent(@Body Event event, Callback<Event> cb);
    @GET("/events")
    void getEvent(Callback<ArrayList<Event>> cb);


}
