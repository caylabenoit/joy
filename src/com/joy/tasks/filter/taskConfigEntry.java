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
package com.joy.tasks.filter;

import com.joy.json.JSONArray;
import com.joy.json.JSONObject;

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class taskConfigEntry {
        private String name;
        private String className;
        private boolean secure;
        
        public taskConfigEntry(String name, JSONObject json) {
            JSONArray services = json.getJSONArray("services");
            for (Object tag : services) {
                JSONObject item = (JSONObject)tag;
                if (name.equalsIgnoreCase(item.getString("name"))) {
                    this.className = item.getString("class");
                    this.name = item.getString("name");
                    String secured;
                    try {
                        secured = item.getString("secure");
                    } catch (Exception e) { secured = "yes"; }
                    this.secure = (secured.equalsIgnoreCase("yes"));
                    return;
                }
            }
        }
        
        public boolean isSecure() {
            return secure;
        }
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

}
