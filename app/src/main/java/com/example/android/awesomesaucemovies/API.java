package com.example.android.awesomesaucemovies;

/**
 * Created by dev on 7/23/15.
 *
 *  I would prefer a different approach. However, for 'assessment'
 *  purposes, passing allows the Udacity reviewer to add the key 1 time in one place.
 *  Future projects will consider a different approach.
 */
public class API {


    //---------- API Key ------------------------------------------------------------
    //
    //    >>>>  Replace "new API().getAPI();" with API String  <<<<<
    //
    public final static String API_KEY = new API_STORE().getAPI();
    //
    //-------------------------------------------------------------------------------



    public API(){

    }

    public String getAPI() {
        return API_KEY;
    }

}
