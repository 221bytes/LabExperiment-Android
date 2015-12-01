package alexandre.nakatani.rits.experimentlab;


import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Alexandre Catalano aka 221Bytes on 6/1/15.
 */
public interface GetRequestService
{
    @GET("/task/565dbc45e4623033348bb366")
    void getMe(Callback<Task> cb);
}
