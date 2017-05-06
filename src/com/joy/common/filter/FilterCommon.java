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
package com.joy.common.filter;

import com.joy.C;
import com.joy.common.state.JoyState;
import com.joy.common.JoyClassTemplate;
import java.util.logging.Level;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class FilterCommon extends JoyClassTemplate implements Filter {
    
    @Override
    public void init(FilterConfig fc) throws ServletException {}

    @Override
    public void destroy() {}
    
    @Override
    public void doFilter(ServletRequest request, 
                         ServletResponse response, 
                         FilterChain chain)
    {
        JoyState joyState = null;
        
        try {
            HttpServletRequest _request = (HttpServletRequest)request;
            HttpServletResponse _response = (HttpServletResponse)response;
            
            // Check authorization here
            String headerAuth = _request.getHeader("Authorization");
            this.getLog().log(Level.WARNING, "Authorization header> {0}", headerAuth);
            
            // Initialization
            joyState = joyInitialize(request.getServletContext(), _request, _response);
            joyState.getLog().log(Level.FINEST, "New HTTP Request initialization, URL={0} | QueryString={1}", new Object[]{_request.getRequestURL(), _request.getQueryString()});
        
            // Request
            process(joyState);

        } catch (Exception t) {
            this.getLog().log(Level.WARNING, "FilterCommon.doFilter|Exception> {0}", t.toString());
        } 
        
        joyFinalize(joyState);
        joyState = null;
    }
    
    protected void process(JoyState state) {}
    
    /**
     * Check Initialization features
     * @param sce servlet context
     * @param request
     * @param response
     * @return 
     */
    protected JoyState joyInitialize(ServletContext sce, 
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        
        getLog().log(Level.FINE, "------ [JOY v{0}] Start Request Treatment ------", C.JOY_VERSION);

        // Initialisation du framework
        JoyState srvConfig = new JoyState();
        srvConfig.init(sce,request,response);
        
        // Rest configuration
        if (srvConfig.getRestConfiguration() == null)
            srvConfig.setRestConfiguration(sce.getInitParameter("joy-rest")); 
        
        // Rest configuration
        if (srvConfig.getTaskConfiguration()== null)
            srvConfig.setTaskConfiguration(sce.getInitParameter("joy-task")); 
        
        // Initialisation des fichiers de traduction
        if (!srvConfig.getMessageBundle().isInitilized()) {
            getLog().fine("[JOY] Initialize locales language and country.");
            srvConfig.getMessageBundle().setCountry(srvConfig.getAppParameters().getDefaultLocalCountry());
            srvConfig.getMessageBundle().setLanguage(srvConfig.getAppParameters().getDefaultLocalLanguage());
            srvConfig.getMessageBundle().init(srvConfig.getAppParameters().getJoyBundledMessageFile());
        }
        return srvConfig;
    }
    
    protected void joyFinalize(JoyState state) {
        try {
            getLog().log(Level.FINE, "------ [JOY v{0}]  End Of Request Treatment ------", C.JOY_VERSION);
            getLog().log(Level.FINE, "[JOY] Number of Entities cached : {0}", state.getBOFactory().cacheSize());
            getLog().log(Level.FINE, "[JOY] Treatment duration : {0} ms", state.getDuration());
            if (state != null)
                state.end();
        } catch (Exception e) {}
    }
    
}
