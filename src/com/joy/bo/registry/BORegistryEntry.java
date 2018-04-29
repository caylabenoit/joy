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
package com.joy.bo.registry;

import com.joy.json.JSONObject;
import org.jdom2.Element;

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class BORegistryEntry {
    private String name;
    private String entityFile;
    private BOEntityType entityType;

    /**
     *
     * @param myEntity
     */
    public BORegistryEntry(JSONObject myEntity) {
        try {
            name = myEntity.getString("name");
            entityFile = myEntity.getString("file");
            switch (myEntity.getString("type").toUpperCase()) {
                case "TABLE" : entityType = BOEntityType.boTable; break;
                case "COMPOSITE": entityType = BOEntityType.boComposite; break;
                case "QUERY": entityType = BOEntityType.boQuery; break;
                default : entityType = BOEntityType.boNothing;
            }
        } catch (Exception e) {}
    }

    /**
     *
     * @param myEntity
     */
    public BORegistryEntry(Element myEntity) {
        try {
            name = myEntity.getAttributeValue("name");
            entityFile = myEntity.getAttributeValue("file");
            switch (myEntity.getAttributeValue("type").toUpperCase()) {
                case "TABLE" : entityType = BOEntityType.boTable; break;
                case "COMPOSITE": entityType = BOEntityType.boComposite; break;
                case "QUERY": entityType = BOEntityType.boQuery; break;
                default : entityType = BOEntityType.boNothing;
            }
        } catch (Exception e) {}
    }
    
    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public String getEntityFile() {
        return entityFile;
    }

    /**
     *
     * @return
     */
    public BOEntityType getEntityType() {
        return entityType;
    }

}
