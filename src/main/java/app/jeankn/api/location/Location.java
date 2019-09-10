package app.jeankn.api.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class Location {

    android.location.Location location;
    LocationManager locationManager;
    LocationListener listener;
    LocationChangedListener changedListener;

    public Location(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener();
    }

    public android.location.Location start(Context context, int interval, LocationChangedListener listener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            changedListener = listener;

            // Get last known location before GPS connects.
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // Ask GPS to connect
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this.listener);

            return location;
        }
        return null;
    }

    public android.location.Location start(Context context, LocationChangedListener listener) {
        return start(context, 1, listener);
    }

    public void stop(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(listener);
            listener = null;
        }
    }

    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(android.location.Location location) {
            Location.this.location = location;
            Log.d("Location", "onLocationChanged: " + location.getLatitude() + " " + location.getLongitude());
            changedListener.onChange(location.getLatitude(), location.getLongitude(), location.getBearing(), location.getSpeed());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d("Location", "onStatusChanged: " + s);
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.d("Location", "onProviderEnabled: " + s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.d("Location", "onProviderDisabled: " + s);
        }
    }

    public interface LocationChangedListener {
        void onChange(double latitude, double longitude, float bearing, float speed);
    }
}
