/*
 * Copyright (C) 2017 benoit
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

import com.joy.C;
import com.joy.JOY;
import com.joy.tasks.JoyTaskManager;
import com.joy.bo.BOFactory;
import com.joy.common.JoyParameterFactory;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author benoit
 */
public class JoyState extends JoyStateConfig {
    private JoyParameterFactory parameters;          // Global PARAMETERS from xml file
    private List<BOFactory> bofactories;     // Data ENTITIES      // LOG
    private JoyTaskManager taskManager;
    private HttpServletRequest currentRequest;
    
    public HttpServletRequest getCurrentRequest() {
        return currentRequest;
    }
    
    public JoyState() {
        super();
    }

    public JoyParameterFactory getParameters() {
        return parameters;
    }

    public BOFactory getBOFactory(int index) {
        return bofactories.get(index);
    }
    
    public BOFactory getBOFactory() {
        return bofactories.get(0);
    }
    
    public List<BOFactory> getBOFactories() {
        return bofactories;
    }
    
    public JoyTaskManager getTaskManager() {
        return taskManager;
    }
    
    public boolean init(ServletContext sce, HttpServletRequest _request) {
        boolean entityInit = false;
        boolean logParam;

        this.currentRequest = _request;
        getLog().info("********************************************************************");
        getLog().finest("Log initialized");

        // 1 - Get the appdir parameter first
        parameters = new JoyParameterFactory();
        String appdir = sce.getInitParameter(C.APPDIR_PARAMETER);
        if (appdir == null) appdir = "";
        parameters.setApplicationFolder(appdir);
        getLog().info("Application directory = " + (appdir.isEmpty() ? "Not defined" : appdir));

        // 2 - Initialisation des parametres
        String paramFile = parameters.getConfigFolder() + sce.getInitParameter("joy-parameters");
        getLog().info("Initialize joy parameters with parameter file : " + paramFile);
        logParam = parameters.init(paramFile);
        getLog().info("Joy Parameters Initialization : " + (logParam ? "OK" : "KO"));

        // 3 - Initialisation des entités & DB
        bofactories = new ArrayList();
        for (String entitiesName : parameters.getEntities()) {
            String entityFile = parameters.getConfigFolder() + entitiesName;
            BOFactory entities = new BOFactory();
            entities.init(entityFile);
            bofactories.add(entities);
            entityInit = entities.isInitialized();
        }
        getLog().info("Entities Initialization : " + (entityInit ? "OK" : "KO"));

        // 5 - Taks Manager Initalization
        JOY.INIT();
        getLog().info("Task Manager Initialized");
        
        getLog().info("Joy RestFul state initialized successfully :-)");
        getLog().info("********************************************************************");

        return (entityInit && logParam);
    }
    
    public void end() {
        for (BOFactory factory : bofactories) {
            try {
                factory.End();
            } catch (Exception e) {
                getLog().warning("Impossible to close the connection: " + e);
            }
        }
    }
    
    
}
