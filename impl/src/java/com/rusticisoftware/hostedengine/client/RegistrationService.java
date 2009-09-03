package com.rusticisoftware.hostedengine.client;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.rusticisoftware.hostedengine.client.Enums.*;

public class RegistrationService
{
    private Configuration configuration = null;
    private ScormEngineService manager = null;

    /// <summary>
    /// Main constructor that provides necessary configuration information
    /// </summary>
    /// <param name="configuration">Application Configuration Data</param>
    public RegistrationService(Configuration configuration, ScormEngineService manager)
    {
        this.configuration = configuration;
        this.manager = manager;
    }

    /// <summary>
    /// Create a new Registration (Instance of a user taking a course)
    /// </summary>
    /// <param name="registrationId">Unique Identifier for the registration</param>
    /// <param name="courseId">Unique Identifier for the course</param>
    /// <param name="versionId">Optional versionID, if Int32.MinValue, latest course version is used.</param>
    /// <param name="learnerId">Unique Identifier for the learner</param>
    /// <param name="learnerFirstName">Learner's first name</param>
    /// <param name="learnerLastName">Learner's last name</param>
    /// <param name="resultsPostbackUrl">URL to which the server will post results back to</param>
    /// <param name="authType">Type of Authentication used at results postback time</param>
    /// <param name="postBackLoginName">If postback authentication is used, the logon name</param>
    /// <param name="postBackLoginPassword">If postback authentication is used, the password</param>
    /// <param name="resultsFormat">The Format of the results XML sent to the postback URL</param>
    public void CreateRegistration(String registrationId, String courseId, int versionId, String learnerId, 
        String learnerFirstName, String learnerLastName, String resultsPostbackUrl, 
        RegistrationResultsAuthType authType, String postBackLoginName, String postBackLoginPassword,
        RegistrationResultsFormat resultsFormat) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        request.getParameters().add("regid", registrationId);
        request.getParameters().add("courseid", courseId);
        request.getParameters().add("fname", learnerFirstName);
        request.getParameters().add("lname", learnerLastName);
        request.getParameters().add("learnerid", learnerId);
        
        // Required on this signature but not by the actual service
        request.getParameters().add("authtype", authType.toString().toLowerCase());
        request.getParameters().add("resultsformat", resultsFormat.toString().toLowerCase());

        // Optional:
        if (!Utils.isNullOrEmpty(resultsPostbackUrl))
            request.getParameters().add("postbackurl", resultsPostbackUrl);
        if (!Utils.isNullOrEmpty(postBackLoginName))
            request.getParameters().add("urlname", postBackLoginName);
        if (!Utils.isNullOrEmpty(postBackLoginPassword))
            request.getParameters().add("urlpass", postBackLoginPassword);
        if (versionId != Integer.MIN_VALUE)
            request.getParameters().add("versionid", versionId);

        request.callService("rustici.registration.createRegistration");
    }


    //TODO: Other overrides of createRegistration....

    /// <summary>
    /// Create a new Registration (Instance of a user taking a course)
    /// </summary>
    /// <param name="registrationId">Unique Identifier for the registration</param>
    /// <param name="courseId">Unique Identifier for the course</param>
    /// <param name="learnerId">Unique Identifier for the learner</param>
    /// <param name="learnerFirstName">Learner's first name</param>
    /// <param name="learnerLastName">Learner's last name</param>
    public void CreateRegistration(String registrationId, String courseId, String learnerId,
        String learnerFirstName, String learnerLastName) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        request.getParameters().add("regid", registrationId);
        request.getParameters().add("courseid", courseId);
        request.getParameters().add("fname", learnerFirstName);
        request.getParameters().add("lname", learnerLastName);
        request.getParameters().add("learnerid", learnerId);
        request.callService("rustici.registration.createRegistration");
    }
       
    /// <summary>
    /// Creates a new instance of an existing registration.  This essentially creates a
    /// fresh take of a course by the user. The latest version of the associated course
    /// will be used.
    /// </summary>
    /// <param name="registrationId">Unique Identifier for the registration</param>
    /// <returns>Instance ID of the newly created instance</returns>
//    public int CreateNewInstance (String registrationId) throws Exception
//    {
//        ServiceRequest request = new ServiceRequest(configuration);
//        request.getParameters().add("regid", registrationId);
//        Document response = request.callService("rustici.registration.createNewInstance");
//
//        NodeList successNodes = response.getElementsByTagName("success");
//        return Integer.parse(((Element)successNodes.item(0)).getAttribute("instanceid"));
//    }

    /// <summary>
    /// Return a registration summary object for the given registration
    /// </summary>
    /// <param name="registrationId">The unique identifier of the registration</param>
    /// <returns></returns>
    public RegistrationSummary GetRegistrationSummary(String registrationId) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        request.getParameters().add("regid", registrationId);
        request.getParameters().add("resultsformat", "course");
        request.getParameters().add("dataformat", "xml");
        Document response = request.callService("rustici.registration.getRegistrationResult");
        Element reportElem = (Element)response.getElementsByTagName("registrationreport").item(0);
        return new RegistrationSummary(reportElem);
    }

    /// <summary>
    /// Returns the current state of the registration, including completion
    /// and satisfaction type data.  Amount of detail depends on format parameter.
    /// </summary>
    /// <param name="registrationId">Unique Identifier for the registration</param>
    /// <returns>Registration data in XML Format</returns>
    public String GetRegistrationResult(String registrationId) throws Exception
    {
        return GetRegistrationResult(registrationId, RegistrationResultsFormat.COURSE_SUMMARY, DataFormat.XML);
    }
    
    /// <summary>
    /// Returns the current state of the registration, including completion
    /// and satisfaction type data.  Amount of detail depends on format parameter.
    /// </summary>
    /// <param name="registrationId">Unique Identifier for the registration</param>
    /// <param name="resultsFormat">Degree of detail to return</param>
    /// <returns>Registration data in XML Format</returns>
    public String GetRegistrationResult(String registrationId, RegistrationResultsFormat resultsFormat) throws Exception
    {
        return GetRegistrationResult(registrationId, resultsFormat, DataFormat.XML);
    }

    /// <summary>
    /// Returns the current state of the registration, including completion
    /// and satisfaction type data.  Amount of detail depends on format parameter.
    /// </summary>
    /// <param name="registrationId">Unique Identifier for the registration</param>
    /// <param name="resultsFormat">Degree of detail to return</param>
    /// <returns>Registration data in XML Format</returns>
    public String GetRegistrationResult(String registrationId, RegistrationResultsFormat resultsFormat, DataFormat dataFormat) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        request.getParameters().add("regid", registrationId);
        request.getParameters().add("resultsformat", resultsFormat.toString().toLowerCase());
        if (dataFormat == DataFormat.JSON)
            request.getParameters().add("dataformat", "json");
        Document response = request.callService("rustici.registration.getRegistrationResult");

        // Return the subset of the xml starting with the top <summary>
        if(dataFormat == DataFormat.XML){
            Node reportElem = response.getElementsByTagName("registrationreport").item(0);
            return Utils.getXmlString(reportElem);
        } else {
            return Utils.getNonXmlPayloadFromResponse(response);
        }
    }

    /// <summary>
    /// Returns a list of registration id's along with their associated course
    /// </summary>
    /// <param name="regIdFilterRegex">Optional registration id filter</param>
    /// <param name="courseIdFilterRegex">Option course id filter</param>
    /// <returns></returns>
    public List<RegistrationData> GetRegistrationList(String regIdFilterRegex, String courseIdFilterRegex) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        if (!Utils.isNullOrEmpty(regIdFilterRegex))
            request.getParameters().add("filter", regIdFilterRegex);
        if (!Utils.isNullOrEmpty(courseIdFilterRegex))
            request.getParameters().add("coursefilter", courseIdFilterRegex);
        Document response = request.callService("rustici.registration.getRegistrationList");

        // Return the subset of the xml starting with the top <summary>
        return RegistrationData.ConvertToRegistrationDataList(response);
    }

    /// <summary>
    /// Returns a list of all registration id's along with their associated course
    /// </summary>
    public List<RegistrationData> GetRegistrationList() throws Exception
    {
        return GetRegistrationList(null, null);
    }

    /// <summary>
    /// Delete the specified registration
    /// </summary>
    /// <param name="registrationId">Unique Identifier for the registration</param>
    /// <param name="deleteLatestInstanceOnly">If false, all instances are deleted</param>
    public void DeleteRegistration(String registrationId) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        request.getParameters().add("regid", registrationId);
        request.callService("rustici.registration.deleteRegistration");
    }


    /// <summary>
    /// Resets all status data regarding the specified registration -- essentially restarts the course
    /// </summary>
    /// <param name="registrationId">Unique Identifier for the registration</param>
    public void ResetRegistration(String registrationId) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        request.getParameters().add("regid", registrationId);
        request.callService("rustici.registration.resetRegistration");
    }


    /// <summary>
    /// Clears global objective data for the given registration
    /// </summary>
    /// <param name="registrationId">Unique Identifier for the registration</param>
    /// <param name="deleteLatestInstanceOnly">If false, all instances are deleted</param>
    public void ResetGlobalObjectives(String registrationId, boolean deleteLatestInstanceOnly) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        request.getParameters().add("regid", registrationId);
        if (deleteLatestInstanceOnly)
            request.getParameters().add("instanceid", "latest");
        request.callService("rustici.registration.resetGlobalObjectives");
    }

    /// <summary>
    /// Delete the specified instance of the registration
    /// </summary>
    /// <param name="registrationId">Unique Identifier for the registration</param>
    /// <param name="instanceId">Specific instance of the registration to delete</param>
    public void DeleteRegistrationInstance(String registrationId, int instanceId) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        request.getParameters().add("regid", registrationId);
        request.getParameters().add("instanceid", "instanceId");
        request.callService("rustici.registration.deleteRegistration");
    }

    /// <summary>
    /// Gets the url to directly launch/view the course registration in a browser
    /// </summary>
    /// <param name="registrationId">Unique Identifier for the registration</param>
    /// <returns>URL to launch</returns>
    public String GetLaunchUrl (String registrationId) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        request.getParameters().add("regid", registrationId);
        return request.constructUrl("rustici.registration.launch");
    }

    /// <summary>
    /// Gets the url to directly launch/view the course registration in a browser
    /// </summary>
    /// <param name="registrationId">Unique Identifier for the registration</param>
    /// <param name="redirectOnExitUrl">Upon exit, the url that the SCORM player will redirect to</param>
    /// <returns>URL to launch</returns>
    public String GetLaunchUrl(String registrationId, String redirectOnExitUrl) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        request.getParameters().add("regid", registrationId);
        if (!Utils.isNullOrEmpty(redirectOnExitUrl))
            request.getParameters().add("redirecturl", redirectOnExitUrl);
        return request.constructUrl("rustici.registration.launch");

    }

    /// <summary>
    /// Gets the url to directly launch/view the course registration in a browser
    /// </summary>
    /// <param name="registrationId">Unique Identifier for the registration</param>
    /// <param name="redirectOnExitUrl">Upon exit, the url that the SCORM player will redirect to</param>
    /// <returns>URL to launch</returns>
    /// <param name="debugLogPointerUrl">Url that the server will postback a "pointer" url regarding
    /// a saved debug log that resides on s3</param>
    public String GetLaunchUrl(String registrationId, String redirectOnExitUrl, String debugLogPointerUrl) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        request.getParameters().add("regid", registrationId);
        if (!Utils.isNullOrEmpty(redirectOnExitUrl))
            request.getParameters().add("redirecturl", redirectOnExitUrl);

        if (!Utils.isNullOrEmpty(debugLogPointerUrl))
            request.getParameters().add("saveDebugLogPointerUrl", debugLogPointerUrl);

        return request.constructUrl("rustici.registration.launch");
    }

    /// <summary>
    /// Returns list of launch info objects, each of which describe a particular launch,
    /// but note, does not include the actual history log for the launch. To get launch
    /// info including the log, use GetLaunchInfo
    /// </summary>
    /// <param name="registrationId"></param>
    /// <returns></returns>
    public List<LaunchInfo> GetLaunchHistory(String registrationId) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        request.getParameters().add("regid", registrationId);
        Document response = request.callService("rustici.registration.getLaunchHistory");
        Element launchHistory = ((Element)response.getElementsByTagName("launchhistory").item(0));
        return LaunchInfo.ConvertToLaunchInfoList(launchHistory);
    }

    /// <summary>
    /// Get the full launch information for the launch with the given launch id
    /// </summary>
    /// <param name="launchId"></param>
    /// <returns></returns>
    public LaunchInfo GetLaunchInfo(String launchId) throws Exception
    {
        ServiceRequest request = new ServiceRequest(configuration);
        request.getParameters().add("launchid", launchId);
        Document response = request.callService("rustici.registration.getLaunchInfo");
        Element launchInfoElem = ((Element)response.getElementsByTagName("launch").item(0));
        return new LaunchInfo(launchInfoElem);
    }
}
