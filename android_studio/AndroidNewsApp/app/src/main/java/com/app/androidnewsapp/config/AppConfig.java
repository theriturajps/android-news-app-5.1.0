package com.app.androidnewsapp.config;

public class AppConfig {

    //your Server Key obtained from the admin panel
    public static final String SERVER_KEY = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

    //your Rest API Key obtained from the admin panel
    public static final String REST_API_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    //you can turn the app to old design by set false value
    public static final boolean ENABLE_NEW_APP_DESIGN = true;

    //show one latest news as header view
    public static final boolean SHOW_HEADER_VIEW = true;

    //show news list in large view
    public static final boolean BIG_NEWS_LIST_STYLE = false;

    //show short description in the news list
    public static final boolean SHOW_EXCERPT_IN_POST_LIST = true;

    //show total news in each category
    public static final boolean SHOW_POST_COUNT_IN_CATEGORY = false;

    //video player orientation
    public static final boolean FORCE_VIDEO_PLAYER_TO_LANDSCAPE = false;

    //date display configuration
    public static final boolean SHOW_POST_DATE = true;
    public static final boolean SHOW_DATE_DISPLAY_AS_TIME_AGO = false;

    //enable view count in the news description
    public static final boolean SHOW_POST_VIEW_COUNT = true;

    //display alert dialog when user want to close the app
    public static final boolean ENABLE_EXIT_DIALOG = true;

    //set false to disable copy text in the news description
    public static final boolean ENABLE_TEXT_SELECTION = false;

    //open link in the news description using external web browser
    public static final boolean OPEN_LINK_INSIDE_APP = true;

    //GDPR EU Consent
    public static final boolean ENABLE_GDPR_UMP_SDK = true;

    //load more for next news list
    public static final int LOAD_MORE = 10;

    //if you use RTL Language e.g : Arabic Language or other, set true
    public static final boolean ENABLE_RTL_MODE = false;

    public static final boolean SET_CATEGORY_AS_MAIN_PAGE = false;

    //Enable it with true value if want to the app will force to display open ads first before start the main menu
    //Longer duration to start the app may occur depending on internet connection or open ad response time itself
    public static final boolean FORCE_TO_SHOW_APP_OPEN_AD_ON_START = false;

    //Delay splash screen
    public static final int DELAY_SPLASH_SCREEN = 1500;

}