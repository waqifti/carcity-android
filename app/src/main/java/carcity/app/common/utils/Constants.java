package carcity.app.common.utils;

public class Constants {

    //system
    public static String SYSTEM_IP = "18.118.199.175";
    public static String SYSTEM_PORT = "9000";
    public static String BASE_URL = "http://"+SYSTEM_IP+":"+SYSTEM_PORT;

    //Common values
    public static final String UserTypeCustomer = "Customer";
    public static final String UserTypeServiceProvider = "ServiceProvider";
    public static final String UserTypeAdmin = "AdminPortal";

    //Common URLs
    public static String URL_LOGIN = BASE_URL+"/Login";

    //Service Provider URLs
    public static String URL_Update_Location = BASE_URL+"/Authenticated/ServiceProvider/UpdateLocation";

    //Admin URLs
    public static String URL_ALL_SERVICE_PROVIDERS = BASE_URL+"/Authenticated/AdminPortal/GetAllServiceProviders";
    public static String URL_SERVICE_PROVIDERS_RECORDED_LOCATIONS = BASE_URL+"/Authenticated/AdminPortal/GetUsersRecordedLocations";
}
