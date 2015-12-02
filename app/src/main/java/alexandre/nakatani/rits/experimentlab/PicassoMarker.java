package alexandre.nakatani.rits.experimentlab;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by necocityhunters on 15/12/02.
 */
public class PicassoMarker implements Target
{
    Marker mMarker;

    PicassoMarker(Marker marker)
    {
        mMarker = marker;
    }

    @Override
    public int hashCode()
    {
        return mMarker.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof PicassoMarker)
        {
            Marker marker = ((PicassoMarker) o).mMarker;
            return mMarker.equals(marker);
        } else
        {
            return false;
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
    {
        mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable)
    {
        Drawable drawable = errorDrawable;
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable)
    {
    }
}