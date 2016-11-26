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
package com.joy.mvc.filters;

import com.joy.C;
import com.joy.Joy;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TASK HTTP filter
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class FilterTASK implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
 
    @Override
    public void destroy() {}

    // réecriture de http://localhost:18180/GovManagementTool/task/[object]
    // en http://localhost:18180/GovManagementTool/action?object=[object]
    
    @Override
    public void doFilter(ServletRequest request, 
                         ServletResponse response, 
                         FilterChain chain)
    {
        try {
            HttpServletRequest _request = (HttpServletRequest)request;
            HttpServletResponse _response = (HttpServletResponse)response;
        
            String uri = _request.getRequestURI();
            String[] uriParts = uri.split("/");
            int i;
            String urlFinal;
            
            Joy.LOG().debug("URI Request=" + uri);
            // si l'URL est de la forme ./task/[tag]/param1/param2/ etc.
            // on met les parametres dans la request
            urlFinal = "/" + uriParts[1] + Joy.PARAMETERS().getJoyDefaultURLPattern() + "?" + C.ACTION_TAG_OBJECT + "=" + uriParts[3];
            for (i=4; i < uriParts.length; i++) {
                urlFinal += "&" + C.ACTION_REST_PARAM_PREFIX + String.valueOf(i-3) + "=" + uriParts[i];
            }
            _response.sendRedirect(urlFinal);
            
        } catch (Exception t) {
            try {
                Joy.LOG().warn("TASK Error: " + t);
                chain.doFilter(request, response);
            } catch (IOException | ServletException ex) {
                Logger.getLogger(FilterTASK.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
        
    }

}