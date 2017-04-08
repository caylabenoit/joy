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
package com.joy.bo.registry;

import com.joy.JOY;
import com.joy.common.joyClassTemplate;
import com.joy.json.JSONArray;
import com.joy.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Element;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class BORegistry extends joyClassTemplate {
    public List<BORegistryEntry> registry;
    
    public BORegistry() {
        super();
        registry = new ArrayList();
    }
    
    public List<BORegistryEntry> getAllEntries() {
        return registry;
    }
    
    public boolean loadXML(String regFile) {
        try {
            // read the registry config file
            org.jdom2.Document document;
            document = JOY.OPEN_XML(regFile);
            
            // Populate the registry in the cache
            List<Element> entities = document.getRootElement().getChild("joy-registry").getChildren("joy-entity");
            for (int j=0; j < entities.size(); j++) {
                registry.add(new BORegistryEntry(entities.get(j)));
            }
            
            return true;
        } catch (Exception e) {
            this.getLog().severe(e.toString());
            registry = null;
            return false;
        }
    }
    
    public BORegistryEntry getRegistryEntry(String name) {
        for (int i=0; i < registry.size(); i++) {
            if (registry.get(i).getName().equalsIgnoreCase(name)) {
                return registry.get(i);
            }
        }
        return null;
    }
}
