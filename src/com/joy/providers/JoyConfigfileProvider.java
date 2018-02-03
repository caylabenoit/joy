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
package com.joy.providers;

import com.joy.common.JoyClassTemplate;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyConfigfileProvider extends JoyClassTemplate {
    
    private Properties m_properties;
    
    private void init() {
        try {
            if (m_properties == null) {
                InputStream myProperties = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
                m_properties = new Properties();
                m_properties.load(myProperties);
            }
            
        } catch (IOException ex) {
            getLog().warning(ex.toString());
        }
    }
    
    public String get(String _name) {
        init();
        try {
            return m_properties.getProperty(_name);
        } catch (Exception e) {
            return null;
        }
    }
}
