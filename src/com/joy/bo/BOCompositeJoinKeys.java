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
package com.joy.bo;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class BOCompositeJoinKeys {
    private String masterField;
    private String slaveField;

    /**
     *
     */
    public BOCompositeJoinKeys() {
        this.masterField = "";
        this.slaveField = "";
    }

    /**
     *
     * @param masterField
     * @param slaveField
     */
    public BOCompositeJoinKeys(String masterField, String slaveField) {
        this.masterField = masterField;
        this.slaveField = slaveField;
    }

    /**
     *
     * @return
     */
    public String getMasterKey() {
        return masterField;
    }

    /**
     *
     * @param LeftField
     */
    public void setMasterKey(String LeftField) {
        this.masterField = LeftField;
    }

    /**
     *
     * @return
     */
    public String getSlaveKey() {
        return slaveField;
    }

    /**
     *
     * @param RightField
     */
    public void setSlaveKey(String RightField) {
        this.slaveField = RightField;
    }
    
    
}
