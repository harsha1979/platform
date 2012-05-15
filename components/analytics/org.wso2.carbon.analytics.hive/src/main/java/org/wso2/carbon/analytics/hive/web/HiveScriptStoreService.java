/**
 * Copyright (c) 2009, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wso2.carbon.analytics.hive.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.hive.HiveConstants;
import org.wso2.carbon.analytics.hive.ServiceHolder;
import org.wso2.carbon.analytics.hive.exception.HiveScriptStoreException;
import org.wso2.carbon.analytics.hive.persistence.HiveScriptPersistenceManager;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.ntask.common.TaskException;
import org.wso2.carbon.ntask.core.TaskInfo;
import org.wso2.carbon.ntask.core.TaskManager;

import java.util.HashMap;
import java.util.Map;

public class HiveScriptStoreService {
    private static final Log log = LogFactory.getLog(HiveScriptStoreService.class);

    public String retrieveHiveScript(String scriptName) throws HiveScriptStoreException {
        scriptName = validateScriptName(scriptName);
        if (null != scriptName) {
            HiveScriptPersistenceManager manager = HiveScriptPersistenceManager.getInstance();
            return manager.retrieveScript(scriptName);
        } else {
            log.error("Script name is empty. Please provide a valid script name!");
            throw new HiveScriptStoreException("Script name is empty. Please provide a valid" +
                                               " script name!");
        }
    }

    public void saveHiveScript(String scriptName, String scriptContent)
            throws HiveScriptStoreException {
        scriptName = validateScriptName(scriptName);
        if (null != scriptName) {
            HiveScriptPersistenceManager manager = HiveScriptPersistenceManager.getInstance();
            manager.saveScript(scriptName, scriptContent);
        } else {
            log.error("Script name is empty. Please provide a valid script name!");
            throw new HiveScriptStoreException("Script name is empty. Please provide a valid" +
                                               " script name!");
        }

        String cron = HiveConstants.DEFAULT_TRIGGER_CRON;
        if (cron != null) {
            TaskInfo.TriggerInfo triggerInfo = new TaskInfo.TriggerInfo();
            //triggerInfo.setRepeatCount(sequence.getCount());
            //triggerInfo.setIntervalMillis(sequence.getInterval());
            triggerInfo.setCronExpression(cron);

            TaskInfo info = new TaskInfo();
            info.setName(scriptName);
            info.setTriggerInfo(triggerInfo);
            info.setTaskClass(HiveConstants.HIVE_DEFAULT_TASK_CLASS);

            Map<String, String> properties = new HashMap<String, String>();
            properties.put(HiveConstants.HIVE_SCRIPT_NAME, scriptName);

            info.setProperties(properties);

            int tenantId = CarbonContext.getCurrentContext().getTenantId();
            try {
                ServiceHolder.getTaskManager().registerTask(info);
            } catch (TaskException e) {
                log.error("Error while scheduling script : " + info.getName() + " for tenant : " +
                          tenantId + "..", e);
                throw new HiveScriptStoreException("Error while scheduling script : " +
                                                   info.getName() + " for tenant : " + tenantId +
                                                   "..", e);
            }

            if (log.isDebugEnabled()) {
                log.debug("Registered script execution task : " + info.getName() +
                          " for tenant : " + tenantId);
            }
        }

    }

    public void editHiveScript(String scriptName, String scriptContent)
            throws HiveScriptStoreException {
        deleteScript(scriptName);
        saveHiveScript(scriptName, scriptContent);
    }

    public String[] getAllScriptNames() throws HiveScriptStoreException {
        try {
            HiveScriptPersistenceManager manager = HiveScriptPersistenceManager.getInstance();
            return manager.getAllHiveScriptNames();
        } catch (HiveScriptStoreException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public void deleteScript(String scriptName) throws HiveScriptStoreException {
        scriptName = validateScriptName(scriptName);
        if (null != scriptName) {
            HiveScriptPersistenceManager manager = HiveScriptPersistenceManager.getInstance();
            manager.deleteScript(scriptName);
        } else {
            log.error("Script name is empty. Please provide a valid script name!");
            throw new HiveScriptStoreException("Script name is empty. Please provide a valid" +
                                               " script name!");
        }

        TaskManager.TaskState taskState = null;
        TaskInfo info = null;
        TaskManager manager = ServiceHolder.getTaskManager();
        try {
            info = manager.getTask(scriptName);
            taskState = manager.getTaskState(scriptName);
        } catch (TaskException ignored) {
            //
        }

        if (info != null && taskState != null) {
            try {
                manager.deleteTask(scriptName);
            } catch (TaskException e) {
                log.error("Error while unscheduling task : " + scriptName + "..", e);
                throw new HiveScriptStoreException("Error while unscheduling task : " + scriptName +
                                                   "..", e);
            }
        }
    }

    private String validateScriptName(String scriptName) {
        if (scriptName.endsWith(HiveConstants.HIVE_SCRIPT_EXT)) {
            scriptName = scriptName.substring(0, scriptName.length() -
                                                 HiveConstants.HIVE_SCRIPT_EXT.length());
        }
        return scriptName;
    }
}
