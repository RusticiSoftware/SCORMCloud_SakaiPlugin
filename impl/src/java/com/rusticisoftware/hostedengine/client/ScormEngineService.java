package com.rusticisoftware.hostedengine.client;

public class ScormEngineService
{
    private Configuration configuration = null;
    private CourseService courseService = null;
    private RegistrationService registrationService = null;
    private UploadService uploadService = null;
    private FtpService ftpService = null;

    /// <summary>
    /// SCORM Engine Service constructor that that takes the three required properties.
    /// </summary>
    /// <param name="scormEngineServiceUrl">URL to the service, ex: http://services.scorm.com/EngineWebServices</param>
    /// <param name="appId">The Application ID obtained by registering with the SCORM Engine Service</param>
    /// <param name="securityKey">The security key (password) linked to the application ID</param>
    public ScormEngineService(String scormEngineServiceUrl, String appId, String securityKey)
    {
        this(new Configuration(scormEngineServiceUrl, appId, securityKey));
    }

    /// <summary>
    /// SCORM Engine Service constructor that takes a single configuration parameter
    /// </summary>
    /// <param name="config">The Configuration object to be used to configure the Scorm Engine Service client</param>
    public ScormEngineService(Configuration config)
    {
        configuration = config;
        courseService = new CourseService(configuration, this);
        registrationService = new RegistrationService(configuration, this);
        uploadService = new UploadService(configuration, this);
        ftpService = new FtpService(configuration, this);
    }

    /// <summary>
    /// Contains all SCORM Engine Package-level (i.e., course) functionality.
    /// </summary>
    public CourseService getCourseService()
    {
        return courseService;
    }

    /// <summary>
    /// Contains all SCORM Engine Package-level (i.e., course) functionality.
    /// </summary>
    public RegistrationService getRegistrationService()
    {
        return registrationService;
    }


    /// <summary>
    /// Contains all SCORM Engine Upload/File Management functionality.
    /// </summary>
    public UploadService getUploadService()
    {
        return uploadService;
    }

    /// <summary>
    /// Contains all SCORM Engine FTP Management functionality.
    /// </summary>
    public FtpService getFtpService()
    {
        return ftpService;
    }

    /// <summary>
    /// The Application ID obtained by registering with the SCORM Engine Service
    /// </summary>
    public String getAppId()
    {
        return configuration.getAppId();
    }

    /// <summary>
    /// The security key (password) linked to the Application ID
    /// </summary>
    public String getSecurityKey()
    {
            return configuration.getSecurityKey();
    }

    /// <summary>
    /// URL to the service, ex: http://cloud.scorm.com/EngineWebServices
    /// </summary>
    public String getScormEngineServiceUrl()
    {
            return configuration.getScormEngineServiceUrl();
    }

    public ServiceRequest CreateNewRequest()
    {
        return new ServiceRequest(this.configuration);
    }
}
