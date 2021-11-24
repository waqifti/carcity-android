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
    public static String URL_PROFILE_INFO_CUSTOMER = BASE_URL+"/Authenticated/Customer/getProfileInfo";

    //Service Provider URLs
    public static String URL_Update_Location = BASE_URL+"/Authenticated/ServiceProvider/UpdateLocation";
    public static String URL_PROFILE_INFO_SERVICE_PROVIDER = BASE_URL+"/Authenticated/ServiceProvider/getProfileInfo";

    //Admin URLs
    public static String URL_ALL_SERVICE_PROVIDERS = BASE_URL+"/Authenticated/AdminPortal/GetAllServiceProviders";
    public static String URL_SERVICE_PROVIDERS_RECORDED_LOCATIONS = BASE_URL+"/Authenticated/AdminPortal/GetUsersRecordedLocations";
    public static String URL_PROFILE_INFO_ADMIN = BASE_URL+"/Authenticated/AdminPortal/getProfileInfo";
}
