package com.example.cropmonitor.location;

import java.util.ArrayList;

public interface LocationCallback {
    void onSuccess(ArrayList<ModelLocation> fetchedLocations);
}
