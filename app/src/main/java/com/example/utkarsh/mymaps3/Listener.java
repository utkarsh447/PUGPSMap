package com.example.utkarsh.mymaps3;

/**
 * Created by utkarsh on 1/7/16.
 */
import com.google.android.gms.maps.GoogleMap;

import java.util.HashMap;
import java.util.List;

public interface Listener {

    public void onSuccessfullRouteFetch(List<List<HashMap<String, String>>> result);
    public void onFail();

    void replaceMapFragment();

    GoogleMap.OnMyLocationChangeListener myLocationChangeListener();
}