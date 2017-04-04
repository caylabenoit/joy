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

import com.joy.common.state.JoyState;
import com.joy.common.joyClassTemplate;
import java.io.IOException;
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
public class FilterCommon extends joyClassTemplate implements Filter {
    private JoyState joyState;

    public FilterCommon() {
        joyState = null;
    }

    public JoyState getState() {
        return joyState;
    }
    
    @Override
    public void init(FilterConfig fc) throws ServletException {}

    @Override
    public void destroy() {}
    
    @Override
    public void doFilter(ServletRequest request, 
                         ServletResponse response, 
                         FilterChain chain)
    {
        try {
            HttpServletRequest _request = (HttpServletRequest)request;
            HttpServletResponse _response = (HttpServletResponse)response;
            
            // Initialization
            joyState = joyInitialize(request.getServletContext(), _request);
            joyState.checks();
            joyState.getLog().log(Level.FINE,  "New HTTP Request initialization, URL=" + _request.getRequestURL() + " | QueryString=" + _request.getQueryString());
        
            // Request
            process(_request, _response);
            
            // Finalize
            joyFinalize();
            
        } catch (Exception t) {
            // Finalize
            joyFinalize();
            try {
                chain.doFilter(request, response);
            } catch (IOException | ServletException  ex) {}
        } 
        

    }
    
    protected void process(HttpServletRequest request, HttpServletResponse response) {}
    
    /**
     * Check Initialization features
     * @param sce servlet context
     * @return 
     */
    protected JoyState joyInitialize(ServletContext sce, 
                                     HttpServletRequest request) {
        
        // Initialisation du framework
        JoyState srvConfig = new JoyState();
        srvConfig.init(sce,request);
        
        // Rest configuration
        if (srvConfig.getRestConfiguration() == null)
            srvConfig.setRestConfiguration(sce.getInitParameter("joy-rest")); 
        
        // Rest configuration
        if (srvConfig.getTaskConfiguration()== null)
            srvConfig.setTaskConfiguration(sce.getInitParameter("joy-task")); 
        
        // Initialisation des fichiers de traduction
        if (!srvConfig.getMessageBundle().isInitilized()) {
            getLog().fine("Initialize locales language and country.");
            srvConfig.getMessageBundle().setCountry(srvConfig.getParameters().getDefaultLocalCountry());
            srvConfig.getMessageBundle().setLanguage(srvConfig.getParameters().getDefaultLocalLanguage());
            srvConfig.getMessageBundle().init(srvConfig.getParameters().getJoyBundledMessageFile());
        }
        return srvConfig;
    }
    
    protected void joyFinalize() {
        getState().end();
    }
    
}
