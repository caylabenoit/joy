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
import com.joy.common.JoyClassTemplate;
import com.joy.json.JSONObject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class persists the global informations needed for all the application life
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyStateMinimum extends JoyClassTemplate {
    private ActionLocaleMgnt messageBundle; // Configuration des fichiers de traduction     
    private JSONObject restConfiguration;
    private JSONObject taskConfiguration;
    private HttpServletResponse currentResponse;
    
    /**
     *
     * @return
     */
    public HttpServletResponse getCurrentResponse() {
        return currentResponse;
    }
    
    /**
     *
     * @return
     */
    public JSONObject getTaskConfiguration() {
        return taskConfiguration;
    }

    /**
     *
     * @param taskConfiguration
     */
    public void setTaskConfiguration(String taskConfiguration) {
        this.taskConfiguration = new JSONObject(JOY.FILE_TO_STRING(taskConfiguration));
    }
    
    /**
     *
     * @return
     */
    public JSONObject getRestConfiguration() {
        return restConfiguration;
    }

    /**
     *
     * @param restConfigurationFile
     */
    public void setRestConfiguration(String restConfigurationFile) {
        restConfiguration = new JSONObject(JOY.FILE_TO_STRING(restConfigurationFile));
    }

    /**
     *
     * @return
     */
    public ActionLocaleMgnt getMessageBundle() {
        return messageBundle;
    }

    /**
     *
     * @param MessageBundle
     */
    public void setMessageBundle(ActionLocaleMgnt MessageBundle) {
        this.messageBundle = MessageBundle;
    }

    /**
     *
     */
    public JoyStateMinimum() {
        super();
        messageBundle = new ActionLocaleMgnt();
    }
    
    /**
     *
     * @param sce
     * @param _request
     * @param _response
     * @return
     */
    public boolean init(ServletContext sce, HttpServletRequest _request, HttpServletResponse _response) {
        this.currentResponse = _response;
        return true;
    }
}
