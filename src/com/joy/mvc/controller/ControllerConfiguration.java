/*
 * Copyright (C) 2016 Benoit CAYLA (benoit@famillecayla.fr)
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
package com.joy.mvc.controller;

import com.joy.Joy;
import com.joy.bo.BOFactory;
import com.joy.mvc.ActionLocaleMgnt;
import com.joy.providers.JoyDBProvider;

/**
 * This class persists the global informations needed for all the application life
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ControllerConfiguration {
    private  ActionConfiguration actionConfig;  // Configuration des actions
    private  ActionLocaleMgnt messageBundle; // Configuration des fichiers de traduction     
    private  BOFactory entities;

    public ActionConfiguration getActionConfig() {
        return actionConfig;
    }

    public void setActionConfig(ActionConfiguration ActionConfig) {
        this.actionConfig = ActionConfig;
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

    public ControllerConfiguration() {
        actionConfig = new ActionConfiguration();
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
        Joy.LOG().debug("ActionConfig initialized: " + messageBundle +
                        ", MessageBundle initialized: " + messageBundle +
                        ", Entities initialized: " + entities +
                        ", DB Connection initialized: " + connectionCheck);
    }
}
