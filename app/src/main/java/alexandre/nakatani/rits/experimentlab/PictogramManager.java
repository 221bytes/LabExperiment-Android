package alexandre.nakatani.rits.experimentlab;

import android.content.Context;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import alexandre.nakatani.rits.experimentlab.models.Pictogram;
import retrofit.RetrofitError;

/**
 * Created by Alex on 15/12/03.
 */
public class PictogramManager
{

    private boolean mPictogramsListHasBeenUpdated;

    private final OkHttpClient client = new OkHttpClient();
    private Context mContext;
    private ArrayList<Pictogram> mPictograms;
    private ArrayList<Pictogram> mPictogramsInDownload = new ArrayList<>();

    public interface PictogramsLoading
    {
        void onPictgramsUpdated(ArrayList<Pictogram> pictograms);
    }

    ArrayList<PictogramsLoading> mPictogramsLoadings = new ArrayList<>();

    public void addListener(PictogramsLoading listener)
    {
        mPictogramsLoadings.add(listener);
        fireOnPictogramsUpdate();
    }

    private void fireOnPictogramsUpdate()
    {
        for (PictogramsLoading pictogramsLoading : mPictogramsLoadings)
        {
            pictogramsLoading.onPictgramsUpdated(mPictograms);
        }
    }

    private void writeJsonFile(String data)
    {
        String filename = "/pictograms.json";
        String path = mContext.getFilesDir().getAbsolutePath();

        FileOutputStream outputStream;
        try
        {
            outputStream = mContext.openFileOutput(path + filename, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String convertStreamToString(InputStream is) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public String getStringFromFile() throws Exception
    {
        String filename = "/pictograms.json";
        String path = mContext.getFilesDir().getAbsolutePath();

        File fl = new File(path + filename);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public PictogramManager(final Context context)
    {
        mContext = context;

        final GetRequestService getRequestService = ServiceGenerator.createService(GetRequestService.class, "http://10.0.3.2:5000/todo/api/v1/");
        getRequestService.getPictograms(new retrofit.Callback<ArrayList<Pictogram>>()
        {
            @Override
            public void success(final ArrayList<Pictogram> pictograms, retrofit.client.Response response)
            {
                try
                {
                    String jsonFileString = getStringFromFile();
                    ArrayList<Pictogram> localPictograms = getArrayListFromString(jsonFileString);
                    getAllPictograms(localPictograms, pictograms);
                } catch (Exception e)
                {
                    mPictograms = pictograms;
                    String json = new Gson().toJson(pictograms);
                    writeJsonFile(json);
                }
                if (mPictogramsListHasBeenUpdated)
                {

                }

//                for (Pictogram pictogram : pictograms)
//                {
//                    String url = pictogram.getPath();
//                    try
//                    {
//                        run(url);
//                    } catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//                }

            }

            @Override
            public void failure(RetrofitError error)
            {
            }
        });


    }

    private void getAllPictograms(ArrayList<Pictogram> localPictograms, ArrayList<Pictogram> newPictograms)
    {
        for (Pictogram upToDatePictogram : newPictograms)
        {
            String url = "";
            boolean isUpToDate = false;
            for (Pictogram oldPictogram : localPictograms)
            {
                if (oldPictogram.getId().get$oid().equals(upToDatePictogram.getId().get$oid()))
                {
                    if (oldPictogram.getLastUpdate().equals(upToDatePictogram.getLastUpdate()))
                    {
                        isUpToDate = true;
                        break;
                    } else break;
                }
            }
            if (!isUpToDate)
            {
                try
                {
                    mPictogramsInDownload.add(upToDatePictogram);
                    mPictogramsListHasBeenUpdated = true;
                    run(upToDatePictogram);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }
    }


    private ArrayList<Pictogram> getArrayListFromString(String jsonFileString) throws JSONException
    {
        Gson gson = new Gson();
        ArrayList<Pictogram> pictograms = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonFileString);
        for (int i = 0; i < jsonArray.length(); i++)
        {
            Pictogram pictogram = gson.fromJson(jsonArray.getString(i), Pictogram.class);
            pictograms.add(pictogram);
        }
        return pictograms;
    }


    class CustomCallback implements Callback
    {

        public void setPictogram(Pictogram pictogram)
        {
            mPictogram = pictogram;
        }

        private Pictogram mPictogram;


        @Override
        public void onFailure(Request request, IOException throwable)
        {
            throwable.printStackTrace();
        }

        @Override
        public void onResponse(Response response) throws IOException
        {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String[] split = response.request().url().getPath().split("/");
            String name = split[split.length - 1];
            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++)
            {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }

            InputStream is = response.body().byteStream();
            String path = mContext.getFilesDir().getAbsolutePath();

            BufferedInputStream input = new BufferedInputStream(is);
            OutputStream output = new FileOutputStream(path + "/" + name);

            byte[] data = new byte[1024];

            long total = 0;
            int count;
            while ((count = input.read(data)) != -1)
            {
                total += count;
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            endDownloadPictograms(mPictogram);
//                System.out.println(response.body().string());
        }
    }

    private void endDownloadPictograms(Pictogram pictograms)
    {

        try
        {
            mPictograms.add(pictograms);
            mPictogramsInDownload.remove(pictograms);
            if (mPictogramsInDownload.isEmpty())
            {

                String json = new Gson().toJson(pictograms);
                writeJsonFile(json);
            }
        } catch (Exception e)
        {

        }

    }

    public void run(Pictogram pictogram) throws Exception
    {
        String url = pictogram.getPath();
        Request request = new Request.Builder().url("http://10.0.3.2:5000" + url).build();
        CustomCallback customCallback = new CustomCallback();
        customCallback.setPictogram(pictogram);
        client.newCall(request).enqueue(customCallback);
    }
}
