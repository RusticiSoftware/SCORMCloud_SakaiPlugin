/*
 *   Copyright 2009-2010 Rustici Software. Licensed under the
 *   Educational Community License, Version 2.0 (the "License"); you may
 *   not use this file except in compliance with the License. You may
 *   obtain a copy of the License at
 *   
 *   http://www.osedu.org/licenses/ECL-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an "AS IS"
 *   BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *   or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package com.rusticisoftware.scormcloud.logic;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.rusticisoftware.scormcloud.model.ScormCloudConfiguration;
import com.rusticisoftware.scormcloud.model.ScormCloudPackage;
import com.rusticisoftware.scormcloud.model.ScormCloudRegistration;

/**
 * This is the interface for the app Logic, 
 * @author Sakai App Builder -AZ
 */
public interface ScormCloudLogic {
   
   public void saveScormCloudConfiguration(ScormCloudConfiguration config) throws Exception;
   public ScormCloudConfiguration getScormCloudConfiguration() throws Exception;
   
   public ScormCloudPackage getPackageById(String id);
   public List<ScormCloudPackage> getAllSitePackages();
   public void addNewPackage(ScormCloudPackage pkg, File packageZip) throws Exception;
   public void updatePackage(ScormCloudPackage pkg);
   public void removePackage(ScormCloudPackage pkg);
   public String getPackagePropertiesUrl(ScormCloudPackage pkg, String styleSheetUrl);
   public String getPackagePreviewUrl(ScormCloudPackage pkg, String redirectOnExitUrl);
   
   public ScormCloudRegistration getRegistrationById(String id);
   public List<ScormCloudRegistration> getRegistrationsByPackageId(String pkgId);
   public List<ScormCloudRegistration> getRegistrationsWherePropertiesEqual(Map<String, Object> propertyMap);
   public List<ScormCloudRegistration> getRegistrationsWherePropertiesLike(Map<String, Object> propertyMap);
   public ScormCloudRegistration findRegistrationFor(ScormCloudPackage pkg, String userId, String assignmentKey);
   public ScormCloudRegistration addNewRegistration(ScormCloudPackage pkg, String userId, String assignmentKey);
   public void updateRegistrationResultsFromCloud(ScormCloudRegistration reg);
   public void removeRegistration(ScormCloudRegistration reg);
   public void resetRegistration(ScormCloudRegistration reg);
   public String getLaunchUrl(ScormCloudRegistration reg, String redirectOnExitUrl);
   public String getLaunchHistoryReport(ScormCloudRegistration reg);
   public String getLaunchInfoXml(ScormCloudRegistration reg, String launchId);
   public Document getRegistrationReport(ScormCloudRegistration reg);
   
   public String getReportageAuth(String navPermission, boolean isAdmin);
   public String getReportUrl(String reportageAuth, String reportUrl);
   
   public boolean isCurrentUserSakaiAdmin();
   public boolean isCurrentUserPluginAdmin();
   public boolean canConfigurePlugin();
   public boolean isPluginConfigured();
}
