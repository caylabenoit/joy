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
package com.joy.etl;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class MappingSignature {
    private String Name;
    private String ConfigFile;

    /**
     *
     * @param Name
     * @param ConfigFile
     */
    public MappingSignature(String Name, String ConfigFile) {
        this.Name = Name;
        this.ConfigFile = ConfigFile;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return Name;
    }

    /**
     *
     * @param Name
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     *
     * @return
     */
    public String getConfigFile() {
        return ConfigFile;
    }

    /**
     *
     * @param ConfigFile
     */
    public void setConfigFile(String ConfigFile) {
        this.ConfigFile = ConfigFile;
    }
    
    
}
