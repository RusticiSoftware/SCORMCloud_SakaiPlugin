package com.rusticisoftware.hostedengine.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rusticisoftware.hostedengine.client.CourseData;
import com.rusticisoftware.hostedengine.client.RegistrationData;


public class ClientTester {
	private static String _appId = "dave";
	private static String _secretKey = "y7bxwHapEWuOz3ODfZJ2je0DPJaFP8kHqDrQ4Bld";
	private static String _serviceUrl = "http://dev.cloud.scorm.com/EngineWebServices";
	//private static String _serviceUrl = "http://localhost/EngineWebServices";
	private static String _testFile = "/test/file/path.zip";
	private static String _testCourseId = "client321";
	private static String _testRegistrationId = "clientreg1";
	
	static {
	    ScormCloud.setConfiguration(new Configuration(_serviceUrl, _appId, _secretKey));
	}
	
	public static void main(String[] args) throws Exception {
		//testImportCourse();
		//testVersionCourse();
		//testUpdateAssets();
		//testDeleteCourse();
	    //testDeleteAllCourses();
		//testCourseList();
		//testGetAttributes();
		//testUpdateAttributes();
		//testGetMetadata();
		//testDeleteFiles();
		//testListImportDelete();
		
		//testCreateRegistration();
		//testDeleteRegistration();
		//testCreateNewInstance();
		//testRegistrationList();
		//testGetRegistrationResult();
		//testGetLaunchUrl();
	}
	
	
	private static boolean testDeleteAllCourses() throws Exception {
        List<CourseData> courseList = ScormCloud.getCourseService().GetCourseList();
        for (CourseData course : courseList){
            ScormCloud.getCourseService().DeleteCourse(course.getCourseId());
        }
        return true;
    }


    public static void testImportCourse() throws Exception {
		List<ImportResult> results = ScormCloud.getCourseService().ImportCourse("client321", _testFile);
		for (ImportResult result : results){
		    System.out.println("Import Result:");
		    System.out.println("\tTitle:" + result.getTitle());
		    System.out.println("\tSuccessful:" + result.getWasSuccessful());
		    System.out.println("\tMessage:" + result.getMessage());
		}
	}
	
	public static void testDeleteCourse() throws Exception {
		ScormCloud.getCourseService().DeleteCourse("client321");
	}
	
	public static void testCourseList() throws Exception {
		List<CourseData> courses = ScormCloud.getCourseService().GetCourseList();
		for(CourseData course : courses){
			System.out.println("Course id=" + course.getCourseId() + ", title=" + course.getTitle());
		}
	}
	
	public static void testVersionCourse() throws Exception {
		ScormCloud.getCourseService().VersionCourse("client321", _testFile);
	}
	
	public static void testUpdateAttributes() throws Exception {
		HashMap<String, String> attrs = new HashMap<String, String>();
		attrs.put("showNavBar", "false");
		Map<String, String> changedAttrs = ScormCloud.getCourseService().UpdateAttributes(_testCourseId, attrs);
		for(String key : changedAttrs.keySet()){
			System.out.println("Attribute name=" + key + ", val=" + attrs.get(key));
		}
	}
	
	public static void testUpdateAssets() throws Exception {
		ScormCloud.getCourseService().UpdateAssets(_testCourseId, _testFile);
	}
	
	public static void testGetAttributes() throws Exception {
		Map<String, String> attrs = ScormCloud.getCourseService().GetAttributes(_testCourseId);
		for(String key : attrs.keySet()){
			System.out.println("Attribute name=" + key + ", val=" + attrs.get(key));
		}
	}
	
	public static void testGetMetadata() throws Exception {
		String data = ScormCloud.getCourseService().GetMetadata(_testCourseId, Enums.MetadataScope.COURSE,  Enums.MetadataFormat.SUMMARY);
		System.out.println("Metadata = " + data);
//		System.out.println("Metadata id=" + data.getId() + " title=" + data.getTitle() + " duration=" + data.getDuration());
//		for(CourseMetadata childData : data.getChildren()){
//			System.out.println("Metadata id=" + childData.getId() + " title=" + childData.getTitle() + " duration=" + childData.getDuration());
//		}
	}
	
	public static void testDeleteFiles() throws Exception {
		String[] paths = {"images/birdfeeder.jpg", "images/doesnotexist.jpg" };
		List<String> pathList = Arrays.asList(paths);
		Map<String, Boolean> successMap = ScormCloud.getCourseService().DeleteFiles(_testCourseId, pathList);
		for(String path : successMap.keySet()){
			System.out.println("Path=" + path + " deleted=" + successMap.get(path));
		}
	}
	
	public static void testListImportDelete() throws Exception {
		List<CourseData> list = ScormCloud.getCourseService().GetCourseList();
		
		assert !isCourseIdInList(_testCourseId, list)  : _testCourseId;
		
		ScormCloud.getCourseService().ImportCourse(_testCourseId, _testFile);
		
		assert isCourseIdInList(_testCourseId, list)  : _testCourseId;
		
		ScormCloud.getCourseService().DeleteCourse(_testCourseId);
		
		assert !isCourseIdInList(_testCourseId, list) : _testCourseId;
	}
	
	protected static boolean isCourseIdInList (String courseId, List<CourseData> courses){
		for(CourseData data : courses){
			if(data.getCourseId().equalsIgnoreCase(_testCourseId)){
				return true;
			}
		}
		return false;
	}
	
	public static void testCreateRegistration() throws Exception {
		ScormCloud.getRegistrationService().CreateRegistration(_testRegistrationId, _testCourseId, "client001", "Another", "Client");
	}
	public static void testDeleteRegistration() throws Exception {
		ScormCloud.getRegistrationService().DeleteRegistration(_testRegistrationId);
	}
	public static void testRegistrationList() throws Exception {
		List<RegistrationData> regList = ScormCloud.getRegistrationService().GetRegistrationList();
		for(RegistrationData reg : regList){
			System.out.println("Registration id=" + reg.getRegistrationId() + " courseid=" + reg.getCourseId() + " instances=" + reg.getInstances().size());
		}
	}
	public static void testGetRegistrationResult() throws Exception {
		String xmlDoc = ScormCloud.getRegistrationService().GetRegistrationResult(_testRegistrationId);
		System.out.println(xmlDoc);
	}
	public static void testGetLaunchUrl() throws Exception {
		String launchUrl = ScormCloud.getRegistrationService().GetLaunchUrl(_testRegistrationId);
		System.out.println(launchUrl);
	}
}
