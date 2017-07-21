package org.odk.collect.android.location;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * An interface for classes that allow monitoring and retrieving the User's Location.
 * Currently there are only two implementations:
 * - {@link GoogleLocationClient}: A LocationClient using Google Play Services.
 * - {@link AndroidLocationClient}: A LocationClient using Android's existing Location Services.
 */
public interface LocationClient {
    /**
     * Prepares the LocationClient for use. This method must be called prior
     * to {@link LocationClient#requestLocationUpdates(LocationListener)}
     * or {@link LocationClient#getLastLocation()}.
     */
    void start();

    /**
     * Stops the LocationClient, ending any current connections and allowing
     * resources to be reclaimed.
     *
     * Implementations should call {@link LocationClient#stopLocationUpdates()} if
     * they have been previously requested.
     */
    void stop();

    /**
     * Begins requesting Location updates with the provided {@link LocationListener}
     * @param locationListener The LocationListener to pass location updates to.
     */
    void requestLocationUpdates(@NonNull LocationListener locationListener);

    /**
     * Ends Location updates for the previously provided LocationListener.
     * Implementations should call this from within {@link LocationClient#stop()}.
     */
    void stopLocationUpdates();

    /**
     * Sets the {@link LocationClientListener} which will receive status updates
     * for the LocationClient.
     *
     * @param locationClientListener The new {@link LocationClientListener}.
     */
    void setListener(@Nullable LocationClientListener locationClientListener);

    /**
     * Sets the LocationClient's {@link Priority} which will be used to determine
     * which Provider (GPS, Network, etc.) will be used to retrieve the User's location.
     *
     * If the LocationClient is already receiving updates, the new Priority will not
     * take effect until the next time Location updates are requested.
     *
     * @param priority The new Priorty.
     */
    void setPriority(@NonNull Priority priority);

    /**
     * Retrieves the most recent known Location, or null if none is available.
     * This method may block if start was not called before hand.
     * @return The most recent Location.
     */
    @Nullable Location getLastLocation();

    /**
     * An interface for listening to status changes on a LocaitonClient.
     */
    interface LocationClientListener {
        /**
         * Called after the LocationClient has been successfully started.
         */
        void onStart();

        /**
         * Called if any issue ocurred during LocationClient start-up.
         */
        void onStartFailure();

        /**
         * Called after the LocationClient has been stopped, either by calling
         * {@link LocationClient#stop()} or because it was stopped by another process.
         */
        void onStop();
    }

    /**
     * Enumerates the options for preferring certain Location Providers over others.
     */
    enum Priority {
        /**
         * Preferred: GPS
         * Backup: Network
         */
        PRIORITY_HIGH_ACCURACY(LocationRequest.PRIORITY_HIGH_ACCURACY),

        /**
         * Preferred: Network
         * Backup: GPS
         */
        PRIORITY_BALANCED_POWER_ACCURACY(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY),

        /**
         * Preferred: Network
         * Backup: GPS (Play Services), Passive (Android)
         */
        PRIORITY_LOW_POWER(LocationRequest.PRIORITY_LOW_POWER),

        /**
         * Preferred: Passive (only receives updates if another Application requests them).
         * Backup: N/A
         */
        PRIORITY_NO_POWER(LocationRequest.PRIORITY_NO_POWER);

        private final int value;

        Priority(int value) {
            this.value = value;
        }

        /**
         * The numeric value of the Priority;
         * LocationServices uses integer constants.
         *
         * @return The integer constant value for the Priority.
         *
         */
        public int getValue() {
            return value;
        }
    }
}
