package alexandre.nakatani.rits.experimentlab;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import alexandre.nakatani.rits.experimentlab.models.Pictogram;

/**
 * Created by Alex on 15/12/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{

    ArrayList<ClickOnPictogram> mClickOnPictograms = new ArrayList<>();


    public interface ClickOnPictogram
    {
        void onPictogramsClick(View view);
    }

    public void addListener(ClickOnPictogram listener)
    {
        mClickOnPictograms.add(listener);
    }

    private void fireOnPictogramsUpdate(View view)
    {
        for (ClickOnPictogram clickOnPictogram : mClickOnPictograms)
        {
            clickOnPictogram.onPictogramsClick(view);
        }
    }

    private ArrayList<Pictogram> mDataset;
    private final ImageLoader mImageLoader;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        private View mView;
        private ImageView mImageView;

        public ViewHolder(View v)
        {
            super(v);
            mView = v;
            mImageView = (ImageView) v.findViewById(R.id.PictogramSelectedImageButton);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<Pictogram> myDataset)
    {
        mDataset = myDataset;
        mImageLoader = ImageLoader.getInstance();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_image_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        v.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fireOnPictogramsUpdate(v);
            }
        });
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String path = mDataset.get(position).getPath();
        ImageView imageView = holder.mImageView;
        ImageAware imageAware = new ImageViewAware(imageView, false);
        mImageLoader.displayImage("http://10.0.3.2:5000" + path, imageAware, new ImageLoadingListener()
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
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }
}