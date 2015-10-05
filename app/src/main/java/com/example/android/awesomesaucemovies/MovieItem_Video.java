package com.example.android.awesomesaucemovies;

/**
 * Created by dev on 10/4/15.
 */
public class MovieItem_Video {

    private String vid_id; //: "533ec654c3a36854480003eb",
    private String vid_language; // "iso_639_1": "en",
    private String vid_key;  //"key": "SUXWAEX2jlg",
    private String vid_name; //"name": "Trailer 1",
    private String vid_site; //"site": "YouTube",
    private Double vid_size; //"size": 720,
    private String vid_type; //"type": "Trailer"


    public MovieItem_Video(String vid_id) {
        this.vid_id = vid_id;
    }


    public String getVid_id() {
        return vid_id;
    }

    public void setVid_id(String vid_id) {
        this.vid_id = vid_id;
    }

    public String getVid_language() {
        return vid_language;
    }

    public void setVid_language(String vid_language) {
        this.vid_language = vid_language;
    }

    public String getVid_key() {
        return vid_key;
    }

    public void setVid_key(String vid_key) {
        this.vid_key = vid_key;
    }

    public String getVid_name() {
        return vid_name;
    }

    public void setVid_name(String vid_name) {
        this.vid_name = vid_name;
    }

    public String getVid_site() {
        return vid_site;
    }

    public void setVid_site(String vid_site) {
        this.vid_site = vid_site;
    }

    public Double getVid_size() {
        return vid_size;
    }

    public void setVid_size(Double vid_size) {
        this.vid_size = vid_size;
    }

    public String getVid_type() {
        return vid_type;
    }

    public void setVid_type(String vid_type) {
        this.vid_type = vid_type;
    }
}
