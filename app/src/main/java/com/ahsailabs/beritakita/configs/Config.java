package com.ahsailabs.beritakita.configs;

/**
 * Created by ahmad s on 2019-09-26.
 */
public class Config {
    private static final String BASE_URL = "https://api.ahsailabs.com/cijou/";
    public static final String API_KEY = "qwerty123456";
    public static final String GROUP_CODE = "ABJAL1";

    public static final String getLoginUrl(){
        return BASE_URL+"login";
    }
    public static final String getNewsListUrl(){
        return BASE_URL+"news/all";
    }
    public static final String getNewsDetailUrl(){
        return BASE_URL+"news/detail/{id}";
    }
    public static final String getAddNewsUrl(){
        return BASE_URL+"news/add";
    }

    public static final String APP_PREFERENCES = "beritakita_preferences";
    public static final String DATA_TOKEN = "data_token";
    public static final String DATA_NAME = "data_name";
    public static final String DATA_USERNAME = "data_username";
    public static final String DATA_ISLOGGEDIN = "data_isloggedin";

}
