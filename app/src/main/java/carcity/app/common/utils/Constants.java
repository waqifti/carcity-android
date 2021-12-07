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
    public static final int TIME_INTERVAL = 5000;

    //Common URLs
    public static String URL_LOGIN = BASE_URL+"/Login";
    public static String URL_LOGOUT = BASE_URL+"/Logout";
    public static String URL_GET_JOB_TYPES = BASE_URL+"/GetJobTypes";

    //Customer urls
    public static String URL_PROFILE_INFO_CUSTOMER = BASE_URL+"/Authenticated/Customer/getProfileInfo";
    public static String URL_PROFILE_UPDATE_CUSTOMER = BASE_URL+"/Authenticated/Customer/updateProfileInfo";
    public static String URL_GET_JOB_DETAILS_CUSTOMER = BASE_URL+"/Authenticated/Customer/getJobDetails";
    public static String URL_CREATE_JOB_CUSTOMER = BASE_URL+"/Authenticated/Customer/createJobRequest";
    public static String URL_CANCEL_ALL_JOBS_CUSTOMER = BASE_URL+"/Authenticated/Customer/cancelAllMyJobs";

    //Service Provider URLs
    public static String URL_Update_Location = BASE_URL+"/Authenticated/ServiceProvider/UpdateLocation";
    public static String URL_PROFILE_INFO_SERVICE_PROVIDER = BASE_URL+"/Authenticated/ServiceProvider/getProfileInfo";
    public static String URL_PROFILE_UPDATE_SERVICE_PROVIDER = BASE_URL+"/Authenticated/ServiceProvider/updateProfileInfo";
    public static String URL_ACTIVATE_JOB_SEARCH = BASE_URL+"/Authenticated/ServiceProvider/activateJobSearch";
    public static String URL_DEACTIVATE_JOB_SEARCH = BASE_URL+"/Authenticated/ServiceProvider/deactivateJobSearch";
    public static String URL_GET_JOB_DETAILS_SP = BASE_URL+"/Authenticated/ServiceProvider/getAssignedJobDetails";

    //Admin URLs
    public static String URL_ALL_SERVICE_PROVIDERS = BASE_URL+"/Authenticated/AdminPortal/GetAllServiceProviders";
    public static String URL_SERVICE_PROVIDERS_RECORDED_LOCATIONS = BASE_URL+"/Authenticated/AdminPortal/GetUsersRecordedLocations";
    public static String URL_PROFILE_INFO_ADMIN = BASE_URL+"/Authenticated/AdminPortal/getProfileInfo";
    public static String URL_PROFILE_UPDATE_ADMIN = BASE_URL+"/Authenticated/AdminPortal/updateProfileInfo";
    public static String URL_GET_ALL_JOBS_ADMIN = BASE_URL+"/Authenticated/AdminPortal/getAllJobs";
}
