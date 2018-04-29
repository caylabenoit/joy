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
package com.joy.listener;

import com.joy.common.JoyClassTemplate;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ListenerBasic extends JoyClassTemplate implements ServletContextListener {

    /**
     *
     * @param context
     * @param ParamName
     * @return
     */
    public String getContextParamFromWebXml(ServletContext context, String ParamName) {
        try {
            return context.getInitParameter(ParamName);
        } catch (Exception e) {
            //JOY.LOG().error(e);
            return "";
        }
    }
    
    /**
     *
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        joyInit(sce);
        init(sce);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        end(sce);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Initialisation des entités et autre données propre au fmk
     * @param sce servlet context
     */
    public void joyInit(ServletContextEvent sce) {
        // Initialisation des entités
        
    }
    
    /**
     *
     * @param sce
     */
    public void init(ServletContextEvent sce) {}
    
    /**
     *
     * @param sce
     */
    public void end(ServletContextEvent sce) {}
}
