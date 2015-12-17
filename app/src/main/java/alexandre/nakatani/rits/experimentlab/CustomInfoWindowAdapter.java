package alexandre.nakatani.rits.experimentlab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import alexandre.nakatani.rits.experimentlab.models.CustomLatLngs;
import alexandre.nakatani.rits.experimentlab.models.Event;

/**
 * Created by Alex on 15/12/03.
 */
class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{

    private final ImageLoader mImageLoader;
    Context mContext;
    // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
    // "title" and "snippet".
    private final View mWindow;

    private final View mContents;
    private ArrayList<Event> mEvents;

    CustomInfoWindowAdapter(Context context, ArrayList<Event> events)
    {

        mImageLoader = ImageLoader.getInstance();

        mEvents = events;
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWindow = inflater.inflate(R.layout.custom_info_window, null);
        mContents = inflater.inflate(R.layout.custom_info_contents, null);
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
//        render(marker, mWindow);
//        return mWindow;
        return null;
    }

    @Override
    public View getInfoContents(Marker marker)
    {

        render(marker, mContents);
        return mContents;
    }

    private void render(Marker marker, View view)
    {
        String name = "";
        if (mEvents == null) return;
        for (Event event : mEvents)
        {
            CustomLatLngs customLatLngs = event.getGeoJson().getLatLng().get(0);
            LatLng latLng = new LatLng(customLatLngs.getLatitude(), customLatLngs.getLongitude());
            if (marker.getPosition().equals(latLng))
            {
                String path = event.getPictograms().get(0).getPath();
                String[] split = path.split("/");
                name = split[split.length - 1];
                break;
            }
        }
        String path = mContext.getFilesDir().getAbsolutePath();

        ImageView imageView = ((ImageView) view.findViewById(R.id.badge));
        ImageAware imageAware = new ImageViewAware(imageView, false);
        mImageLoader.displayImage("http://10.0.3.2:5000/images/" + name, imageAware, new ImageLoadingListener()
        {
            @Override
            public void onLoadingStarted(String imageUri, View view)
            {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason)
            {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
            {

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view)
            {

            }
        });
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(path + "/" + name + ".jpg", options);
//        imageView.setImageBitmap(bitmap);
        String title = "";
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null)
        {
            // Spannable string allows us to edit the formatting of the text.
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
            titleUi.setText(titleText);
        } else
        {
            titleUi.setText("");
        }

        String snippet = "";
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null && snippet.length() > 12)
        {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
            snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
            snippetUi.setText(snippetText);
        } else
        {
            snippetUi.setText("");
        }
    }
}
