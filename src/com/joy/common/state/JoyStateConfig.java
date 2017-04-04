/*
 * Copyright (C) 2017 Benoit Cayla (benoit@famillecayla.fr)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.joy.common.state;

import com.joy.JOY;
import com.joy.api.ActionLocaleMgnt;
import com.joy.bo.BOFactory;
import com.joy.common.joyClassTemplate;
import com.joy.json.JSONObject;
import com.joy.providers.JoyDBProvider;

/**
 * This class persists the global informations needed for all the application life
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyStateConfig extends joyClassTemplate {
    private ActionLocaleMgnt messageBundle; // Configuration des fichiers de traduction     
    private BOFactory entities;
    private JSONObject restConfiguration;
    private JSONObject taskConfiguration;

    public JSONObject getTaskConfiguration() {
        return taskConfiguration;
    }

    public void setTaskConfiguration(String taskConfiguration) {
        this.taskConfiguration = new JSONObject(JOY.FILE_TO_STRING(taskConfiguration));
    }
    
    public JSONObject getRestConfiguration() {
        return restConfiguration;
    }

    public void setRestConfiguration(String restConfigurationFile) {
        restConfiguration = new JSONObject(JOY.FILE_TO_STRING(restConfigurationFile));
    }

    public ActionLocaleMgnt getMessageBundle() {
        return messageBundle;
    }

    public void setMessageBundle(ActionLocaleMgnt MessageBundle) {
        this.messageBundle = MessageBundle;
    }

    public BOFactory getEntities() {
        return entities;
    }

    public void setEntities(BOFactory Entities) {
        this.entities = Entities;
    }

    public JoyStateConfig() {
        super();
        messageBundle = new ActionLocaleMgnt();
        entities = null;
    }
    
    /**
     * display check informations in the logs
     */
    public void checks() {
        JoyDBProvider connectionCheck = null;
        if (entities != null)
            connectionCheck = entities.getDB();
        getLog().fine("ActionConfig initialized: " + messageBundle +
                        ", MessageBundle initialized: " + messageBundle +
                        ", Entities initialized: " + entities +
                        ", DB Connection initialized: " + connectionCheck);
    }
}
