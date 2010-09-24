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

package com.rusticisoftware.scormcloud.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;

import com.rusticisoftware.scormcloud.dao.ScormCloudDao;

/**
 * Implementations of any specialized DAO methods from the specialized DAO 
 * that allows the developer to extend the functionality of the generic dao package
 * @author Sakai App Builder -AZ
 */
public class ScormCloudDaoImpl 
   extends HibernateGeneralGenericDao 
      implements ScormCloudDao {

   private static Log log = LogFactory.getLog(ScormCloudDaoImpl.class);

   public void init() {
      log.debug("init");
   }

}
