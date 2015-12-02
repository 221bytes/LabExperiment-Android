package alexandre.nakatani.rits.experimentlab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import alexandre.nakatani.rits.experimentlab.models.CustomLatLngs;
import alexandre.nakatani.rits.experimentlab.models.Event;
import alexandre.nakatani.rits.experimentlab.models.GeoJson;
import alexandre.nakatani.rits.experimentlab.models.Pictogram;
import alexandre.nakatani.rits.experimentlab.models.Sound;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback
{

    private GoogleMap mMap;
    private MarkerOptions mMarker;
    private Marker test;
    private ArrayList<Pictogram> mPictograms = new ArrayList<>();
    private ArrayList<Sound> mSounds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mMarker = new MarkerOptions().position(new LatLng(0, 0)).title("");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                GeoJson geoJson = new GeoJson();
                LatLng latLng = mMarker.getPosition();
                geoJson.setType(GeoJson.Type.POINT);
                ArrayList<CustomLatLngs> latLngs = new ArrayList<CustomLatLngs>();
                latLngs.add(new CustomLatLngs(latLng));
                geoJson.setLatLng(latLngs);
                GetRequestService getRequestService = ServiceGenerator.createService(GetRequestService.class, "http://10.0.3.2:5000/todo/api/v1/");
                Event event = new Event();
                event.setGeoJson(geoJson);
                ArrayList<Pictogram> pictograms = new ArrayList<Pictogram>();
                pictograms.add(mPictograms.get(0));
                event.setPictograms(pictograms);
                getRequestService.postEvent(event, new Callback<Event>()
                {
                    @Override
                    public void success(Event event, Response response)
                    {
                        Event event1 = event;
                    }

                    @Override
                    public void failure(RetrofitError error)
                    {
                        RetrofitError retrofitError = error;
                    }
                });
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    PicassoMarker mPicassoMarker = null;

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                if (test != null) test.remove();
                mMarker.position(latLng);
                test = mMap.addMarker(mMarker);
                test.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });

        final GetRequestService getRequestService = ServiceGenerator.createService(GetRequestService.class, "http://10.0.3.2:5000/todo/api/v1/");
        getRequestService.getPictograms(new Callback<ArrayList<Pictogram>>()
        {
            @Override
            public void success(final ArrayList<Pictogram> pictograms, Response response)
            {
                mPictograms = pictograms;

                final ArrayList<ImageView> imageViews = new ArrayList<ImageView>();
                getRequestService.getEvent(new Callback<ArrayList<Event>>()
                {
                    @Override
                    public void success(ArrayList<Event> events, Response response)
                    {
                        for (Event event : events)
                        {
                            CustomLatLngs customLatLngs = event.getGeoJson().getLatLng().get(0);
                            LatLng latLng = new LatLng(customLatLngs.getLatitude(), customLatLngs.getLongitude());
                            Marker test = mMap.addMarker(new MarkerOptions().position(latLng));
                            mPicassoMarker = new PicassoMarker(test);
                            String URL = "http://10.0.3.2:5000/" + pictograms.get(0).getPath();
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.common_full_open_on_phone);
                            test.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        }
                    }

                    @Override
                    public void failure(RetrofitError error)
                    {
                        RetrofitError retrofitError = error;
                    }
                });
            }

            @Override
            public void failure(RetrofitError error)
            {
                RetrofitError retrofitError = error;
            }
        });
        getRequestService.getSounds(new Callback<ArrayList<Sound>>()
        {
            @Override
            public void success(ArrayList<Sound> sounds, Response response)
            {
                mSounds = sounds;
            }

            @Override
            public void failure(RetrofitError error)
            {
                RetrofitError retrofitError = error;
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara)
        {
            // Handle the camera action
        } else if (id == R.id.nav_gallery)
        {

        } else if (id == R.id.nav_slideshow)
        {

        } else if (id == R.id.nav_manage)
        {

        } else if (id == R.id.nav_share)
        {

        } else if (id == R.id.nav_send)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
