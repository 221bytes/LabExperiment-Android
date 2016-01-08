package alexandre.nakatani.rits.experimentlab;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import alexandre.nakatani.rits.experimentlab.models.CustomLatLngs;
import alexandre.nakatani.rits.experimentlab.models.Event;

class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{

    private ImageLoader mImageLoader;
    Context mContext;
    // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
    // "title" and "snippet".
    private final View mWindow;

    private final View mContents;
    private ArrayList<Event> mEvents;

    CustomInfoWindowAdapter(Context context, ArrayList<Event> events)
    {
        mImageLoader = MySingleton.getInstance(context).getImageLoader();

        mEvents = events;
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWindow = inflater.inflate(R.layout.custom_info_window, null);
        mContents = inflater.inflate(R.layout.custom_info_contents, null);
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker)
    {

        render(marker, mContents);
        return mContents;
    }

    private void render(Marker marker, View view)
    {
        String path = "";
        if (mEvents == null) return;
        for (Event event : mEvents)
        {
            CustomLatLngs customLatLngs = event.getGeoJson().getLatLng().get(0);
            LatLng latLng = new LatLng(customLatLngs.getLatitude(), customLatLngs.getLongitude());
            if (marker.getPosition().equals(latLng))
            {
                path = event.getPictograms().get(0).getPath();
                String[] split = path.split("/");
                break;
            }
        }

        NetworkImageView imageView = ((NetworkImageView) view.findViewById(R.id.badge));
        mContext.getResources().getString(R.string.server_url);
        String url = mContext.getResources().getString(R.string.server_url) + path;
        imageView.setImageUrl(url, mImageLoader);
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
