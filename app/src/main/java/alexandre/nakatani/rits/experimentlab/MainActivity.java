package alexandre.nakatani.rits.experimentlab;

import android.Manifest;
import android.animation.Animator;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;

import alexandre.nakatani.rits.experimentlab.models.CustomLatLngs;
import alexandre.nakatani.rits.experimentlab.models.Event;
import alexandre.nakatani.rits.experimentlab.models.GeoJson;
import alexandre.nakatani.rits.experimentlab.models.Pictogram;
import alexandre.nakatani.rits.experimentlab.models.Sound;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

//TODO implement this https://developers.google.com/maps/documentation/android-api/utility/

public class MainActivity extends AppCompatActivity implements MyAdapter.ClickOnPictogram, NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, PictogramManager.PictogramsLoading
{
    private GoogleMap mMap;
    private MarkerOptions mMarkerOptions;
    private Marker mCurrentMarker;
    private ArrayList<Marker> mAreaMarkers = new ArrayList<>();
    private ArrayList<Pictogram> mPictograms = new ArrayList<>();
    private ArrayList<Sound> mSounds = new ArrayList<>();
    private ArrayList<Event> mEvents = new ArrayList<>();
    private ArrayList<Polygon> mPolygons = new ArrayList<>();
    private ArrayList<Polyline> mPolylines = new ArrayList<>();

    private SupportMapFragment mSupportMapFragment;
    private ProgressBar mProgressBar;
    private View mChosePictogram;
    private View mChoseColor;
    private View mContentMain;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private MainActivity mMainActivity;
    private Pictogram mPictogramSelected;
    private int mImportance;
    private GeoJson.Type mType = GeoJson.Type.POINT;
    private GetRequestService mGetRequestService;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mMainActivity = this;
        requestPerm();

        String res = getResources().getString(R.string.server_url);
        String url = res + "/todo/api/v1/";
        mGetRequestService = ServiceGenerator.createService(GetRequestService.class, url);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContentMain = findViewById(R.id.content_main);
        mChosePictogram = findViewById(R.id.chose_pictogram);
        mChoseColor = findViewById(R.id.chose_color);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FloatingActionButton floatingActionButton = (FloatingActionButton) v;
                switch (mType)
                {
                    case POINT:
                        floatingActionButton.setImageResource(R.drawable.map_marker_radius);
                        mType = GeoJson.Type.POLYGON;
                        if (mCurrentMarker != null) mCurrentMarker.remove();
                        break;
                    case POLYGON:
                        floatingActionButton.setImageResource(R.drawable.map_lines);
                        mType = GeoJson.Type.POLYLINE;
                        for (Marker marker : mAreaMarkers)
                        {
                            marker.remove();
                        }
                        mAreaMarkers.clear();
                        break;
                    case POLYLINE:
                        floatingActionButton.setImageResource(R.drawable.ic_place_black_24dp);
                        mType = GeoJson.Type.POLYLINE;
                        for (Marker marker : mAreaMarkers)
                        {
                            marker.remove();
                        }
                        mAreaMarkers.clear();
                        break;
                }
            }
        });
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        setSupportActionBar(toolbar);
        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        mMarkerOptions = new MarkerOptions().position(new LatLng(0, 0)).title("");
        mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_black_24dp));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        PictogramManager pictogramManager = new PictogramManager(this);
        pictogramManager.addListener(this);

    }

    private void requestPerm()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else
            {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 3:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else
                {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private GoogleMap.OnMarkerClickListener mOnMarkerClickListener = new GoogleMap.OnMarkerClickListener()
    {
        @Override
        public boolean onMarkerClick(Marker marker)
        {
            if (marker.equals(mCurrentMarker))
            {
                Projection projection = mMap.getProjection();
                LatLng markerLocation = marker.getPosition();
                Point screenPosition = projection.toScreenLocation(markerLocation);

                int cx = screenPosition.x;
                int cy = screenPosition.y;

                int finalRadius = Math.max(mChosePictogram.getWidth(), mChosePictogram.getHeight());

                Animator anim = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    anim = ViewAnimationUtils.createCircularReveal(mChosePictogram, cx, cy, 0, finalRadius);
                }

                mContentMain.setVisibility(View.GONE);
                mChoseColor.setVisibility(View.GONE);
                mChosePictogram.setVisibility(View.VISIBLE);
                if (anim != null) anim.start();

            } else
            {
                if (mAreaMarkers.indexOf(marker) > -1)
                {
                    mContentMain.setVisibility(View.GONE);
                    mChoseColor.setVisibility(View.GONE);
                    mChosePictogram.setVisibility(View.VISIBLE);

                } else
                {
                    marker.showInfoWindow();
                }
            }
            return true;
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else mMap.setMyLocationEnabled(true);


        mMap.setOnMarkerClickListener(mOnMarkerClickListener);


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                MarkerOptions markerOptions;
                for (Polygon polygon : mPolygons)
                {
                    if (PolyUtil.containsLocation(latLng, polygon.getPoints(), true))
                    {
                        moveCurrentMarker(latLng);
                        mCurrentMarker.showInfoWindow();
                        return;
                    }
                } for (Polyline polyline : mPolylines)
            {
                if (PolyUtil.isLocationOnPath(latLng, polyline.getPoints(), false, 5))
                {
                    polyline.setColor(getResources().getColor(R.color.colorPrimary));
                    return;
                }
            }
                switch (mType)
                {
                    case POINT:
                        moveCurrentMarker(latLng);
                        break;
                    case POLYGON:
                        markerOptions = new MarkerOptions().position(latLng).title("");
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp));
                        mAreaMarkers.add(mMap.addMarker(markerOptions));
                        break;
                    case POLYLINE:
                        markerOptions = new MarkerOptions().position(latLng).title("");
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp));
                        mAreaMarkers.add(mMap.addMarker(markerOptions));
                        break;
                }

            }
        });
        CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(this, mEvents);
        mMap.setInfoWindowAdapter(customInfoWindowAdapter);


        mGetRequestService.getPictograms(new Callback<ArrayList<Pictogram>>()
        {
            @Override
            public void success(ArrayList<Pictogram> pictograms, Response response)
            {
                mPictograms = pictograms;
                mAdapter = new MyAdapter(mPictograms, getApplicationContext());
                mAdapter.addListener(mMainActivity);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void failure(RetrofitError error)
            {

            }
        });
        mGetRequestService.getEvent(new Callback<ArrayList<Event>>()
        {
            @Override
            public void success(ArrayList<Event> events, Response response)
            {
                mEvents = events;
                CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(getApplicationContext(), mEvents);
                mMap.setInfoWindowAdapter(customInfoWindowAdapter);
                for (Event event : events)
                {
                    switch (event.getGeoJson().getType())
                    {
                        case POINT:
                            addMarkerToMap(event);
                            break;
                        case POLYGON:
                            addPolygonToMap(event);
                            break;
                        case POLYLINE:
                            addPolylineToMap(event);
                            break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                RetrofitError retrofitError = error;
            }
        });
        mGetRequestService.getSounds(new Callback<ArrayList<Sound>>()
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

    private void addPolylineToMap(Event event)
    {
        PolylineOptions polylineOptions = new PolylineOptions();
        ArrayList<CustomLatLngs> customLatLngses = event.getGeoJson().getLatLng();
        for (CustomLatLngs customLatLngs : customLatLngses)
        {
            LatLng latLng = new LatLng(customLatLngs.getLatitude(), customLatLngs.getLongitude());
            polylineOptions.add(latLng);
        }
        switch (event.getImportance())
        {
            case 0:
                polylineOptions.color(ContextCompat.getColor(this, R.color.greenMarker));
                break;
            case 1:
                polylineOptions.color(ContextCompat.getColor(this, R.color.yellowMarker));
                break;
            case 2:
                polylineOptions.color(ContextCompat.getColor(this, R.color.orangeMarker));
                break;
            case 3:
                polylineOptions.color(ContextCompat.getColor(this, R.color.redMarker));
                break;
            default:
                polylineOptions.color(ContextCompat.getColor(this, R.color.greenMarker));
                break;
        }
        Polyline polyline = mMap.addPolyline(polylineOptions);
        mPolylines.add(polyline);
    }

    private void addPolygonToMap(Event event)
    {
        PolygonOptions polygonOptions = new PolygonOptions();
        ArrayList<CustomLatLngs> customLatLngses = event.getGeoJson().getLatLng();
        for (CustomLatLngs customLatLngs : customLatLngses)
        {
            LatLng latLng = new LatLng(customLatLngs.getLatitude(), customLatLngs.getLongitude());
            polygonOptions.add(latLng);
        }
        switch (event.getImportance())
        {
            case 0:
                polygonOptions.fillColor(ContextCompat.getColor(this, R.color.greenMarker));
                break;
            case 1:
                polygonOptions.fillColor(ContextCompat.getColor(this, R.color.yellowMarker));
                break;
            case 2:
                polygonOptions.fillColor(ContextCompat.getColor(this, R.color.orangeMarker));
                break;
            case 3:
                polygonOptions.fillColor(ContextCompat.getColor(this, R.color.redMarker));
                break;
            default:
                polygonOptions.fillColor(ContextCompat.getColor(this, R.color.greenMarker));
                break;
        }
        Polygon polygon = mMap.addPolygon(polygonOptions);
        mPolygons.add(polygon);
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

    @Override
    public void onPictgramsUpdated(ArrayList<Pictogram> pictograms)
    {
        mPictograms = pictograms;
        mSupportMapFragment.getMapAsync(this);
        mProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void onPictogramsClick(View view)
    {
        int itemPosition = mRecyclerView.getChildAdapterPosition(view);
        mPictogramSelected = mPictograms.get(itemPosition);
        mContentMain.setVisibility(View.GONE);
        mChoseColor.setVisibility(View.VISIBLE);
        mChosePictogram.setVisibility(View.GONE);
    }

    public void onColorClick(View view)
    {

        switch (view.getId())
        {
            case R.id.imageButtonGreen:
                mImportance = 0;
                break;
            case R.id.imageButtonYellow:
                mImportance = 1;
                break;
            case R.id.imageButtonOrange:
                mImportance = 2;
                break;
            case R.id.imageButtonRed:
                mImportance = 3;
                break;
            default:
                mImportance = 0;
        }

        postEvent();
        mContentMain.setVisibility(View.VISIBLE);
        mChoseColor.setVisibility(View.GONE);
        mChosePictogram.setVisibility(View.GONE);
    }

    private void postEvent()
    {
        Event event = new Event();
        switch (mType)
        {
            case POINT:
                event = postPoint();
                break;
            case POLYGON:
                event = postPolygon();
                break;
            case POLYLINE:
                event = PostPolyline();
                break;
        }

        mGetRequestService.postEvent(event, new Callback<Event>()
        {
            @Override
            public void success(Event event, Response response)
            {
                switch (mType)
                {
                    case POINT:
                        addMarkerToMap(event);
                        mCurrentMarker.remove();
                        break;
                    case POLYGON:
                        addPolygonToMap(event);
                        break;
                    case POLYLINE:
                        addPolylineToMap(event);
                        break;
                }

            }

            @Override
            public void failure(RetrofitError error)
            {
                RetrofitError retrofitError = error;
            }
        });
    }

    private Event postPoint()
    {
        GeoJson geoJson = new GeoJson();
        LatLng latLng = mMarkerOptions.getPosition();
        geoJson.setType(GeoJson.Type.POINT);
        ArrayList<CustomLatLngs> latLngs = new ArrayList<CustomLatLngs>();
        latLngs.add(new CustomLatLngs(latLng));
        geoJson.setLatLng(latLngs);
        Event event = new Event();
        event.setGeoJson(geoJson);
        ArrayList<Pictogram> pictograms = new ArrayList<Pictogram>();
        pictograms.add(mPictograms.get(0));
        ArrayList<Pictogram> pictogramArrayList = new ArrayList<>();
        pictogramArrayList.add(mPictogramSelected);
        event.setPictograms(pictogramArrayList);
        event.setImportance(mImportance);
        mEvents.add(event);
        return event;
    }

    private Event postPolygon()
    {
        GeoJson geoJson = new GeoJson();

        geoJson.setType(GeoJson.Type.POLYGON);
        ArrayList<CustomLatLngs> latLngs = new ArrayList<CustomLatLngs>();
        for (Marker marker : mAreaMarkers)
        {
            latLngs.add(new CustomLatLngs(marker.getPosition()));
        }
        geoJson.setLatLng(latLngs);
        Event event = new Event();
        event.setGeoJson(geoJson);
        ArrayList<Pictogram> pictograms = new ArrayList<Pictogram>();
        pictograms.add(mPictograms.get(0));
        ArrayList<Pictogram> pictogramArrayList = new ArrayList<>();
        pictogramArrayList.add(mPictogramSelected);
        event.setPictograms(pictogramArrayList);
        event.setImportance(mImportance);
        mEvents.add(event);
        return event;
    }

    private Event PostPolyline()
    {
        GeoJson geoJson = new GeoJson();

        geoJson.setType(GeoJson.Type.POLYLINE);
        ArrayList<CustomLatLngs> latLngs = new ArrayList<CustomLatLngs>();
        for (Marker marker : mAreaMarkers)
        {
            latLngs.add(new CustomLatLngs(marker.getPosition()));
        }
        geoJson.setLatLng(latLngs);
        Event event = new Event();
        event.setGeoJson(geoJson);
        ArrayList<Pictogram> pictogramArrayList = new ArrayList<>();
        pictogramArrayList.add(mPictogramSelected);
        event.setPictograms(pictogramArrayList);
        event.setImportance(mImportance);
        mEvents.add(event);
        return event;
    }

    private void addMarkerToMap(Event event)
    {
        CustomLatLngs customLatLngs = event.getGeoJson().getLatLng().get(0);
        LatLng latLng = new LatLng(customLatLngs.getLatitude(), customLatLngs.getLongitude());
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));

        BitmapDescriptor bitmapDescriptor;
        switch (event.getImportance())
        {
            case 0:
                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                break;
            case 1:
                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                break;
            case 2:
                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                break;
            case 3:
                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                break;
            default:
                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                break;
        }
        marker.setIcon(bitmapDescriptor);
    }

    private void moveCurrentMarker(LatLng latLng)
    {
        if (mCurrentMarker != null) mCurrentMarker.remove();
        mMarkerOptions.position(latLng);
        mCurrentMarker = mMap.addMarker(mMarkerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}
