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
import com.joy.api.JoyApiRequest;
import com.joy.bo.BOFactory;
import com.joy.common.parameters.JoyParameterFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author benoit
 */
public class JoyState extends JoyStateMinimum {
    private JoyParameterFactory parameters;          // Application parameters
    private List<BOFactory> bofactories;             // Datas
    private long startTime;
    private JoyApiRequest request;
    private String httpAuthToken;

    /**
     *
     * @return
     */
    public String getHttpAuthToken() {
        return httpAuthToken;
    }

    /**
     *
     * @param httpAuthToken
     */
    public void setHttpAuthToken(String httpAuthToken) {
        this.httpAuthToken = httpAuthToken;
    }
    
    /**
     *
     * @return
     */
    public JoyApiRequest getAPIRequest() {
        return request;
    }
    
    /**
     *
     */
    public void start() {
        startTime = System.currentTimeMillis();
    }
    
    /**
     *
     * @return
     */
    public long getDuration() {
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     *
     */
    public JoyState() {
        super();
    }

    /**
     *
     * @return
     */
    public JoyParameterFactory getAppParameters() {
        return parameters;
    }

    /**
     *
     * @param index
     * @return
     */
    public BOFactory getBOFactory(int index) {
        return bofactories.get(index);
    }
    
    /**
     *
     * @return
     */
    public BOFactory getBOFactory() {
        return bofactories.get(0);
    }
    
    /**
     *
     * @return
     */
    public List<BOFactory> getBOFactories() {
        return bofactories;
    }
    
    /**
     *
     * @param sce
     * @param _request
     * @param _response
     * @return
     */
    @Override
    public boolean init(ServletContext sce, 
                        HttpServletRequest _request, 
                        HttpServletResponse _response) {
        boolean entityInit = false;
        boolean logParam;

        this.start();
        super.init (sce, _request, _response);

        // 1 - Get the appdir parameter first
        parameters = new JoyParameterFactory();
        String appdir = sce.getInitParameter(C.APPDIR_PARAMETER);
        if (appdir == null) appdir = "";
        parameters.setApplicationFolder(appdir);
        getLog().log(Level.FINE, "Application directory = {0}", appdir.isEmpty() ? "Not defined" : appdir);
                
        // 2 - Initialisation des parametres
        String paramFile = parameters.getConfigFolder() + sce.getInitParameter("joy-parameters");
        getLog().log(Level.FINE, "Initialize joy parameters with parameter file : {0}", paramFile);
        logParam = parameters.init(paramFile);
        getLog().log(Level.FINE, "Joy Parameters Initialization : {0}", logParam ? "OK" : "KO");
        
        // 2bis - parse the request to get parameters & api infos
        request = new JoyApiRequest(_request, this.getAppParameters().getAPIStartPath());
        // 3 - Initialisation des entités & DB
        bofactories = new ArrayList();
        for (String entitiesName : parameters.getEntities()) {
            String entityFile = parameters.getConfigFolder() + entitiesName;
            BOFactory entities = new BOFactory();
            entities.init(entityFile);
            bofactories.add(entities);
            entityInit = entities.isInitialized();
        }
        getLog().log(Level.FINE, "Entities Initialization : {0}", entityInit ? "OK" : "KO");

        // 5 - Taks Manager Initalization
        JOY.INIT();
        getLog().info("Task Manager Initialized");
        
        getLog().info("Joy RestFul state initialized successfully :-)");

        return (entityInit && logParam);
    }
    
    /**
     *
     */
    public void end() {
        for (BOFactory factory : bofactories) {
            try {
                factory.end();
            } catch (Exception e) {
                getLog().warning("JoyState.end()> Impossible to close the connection: " + e);
            }
        }
    }
    
    
}
