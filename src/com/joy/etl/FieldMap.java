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
public class FieldMap {
    private String fieldNameFrom;
    private String fieldNameTo;
    private boolean key;
    private boolean checkIfExist;
    private String value;
    private String defaultIfNull;
    private String etltype;

    /**
     *
     * @return
     */
    public String getEtlType() {
        return etltype;
    }
    
    /**
     *
     * @return
     */
    public String getDefaultIfNull() {
        return defaultIfNull;
    }

    /**
     *
     * @param defaultIfNull
     */
    public void setDefaultIfNull(String defaultIfNull) {
        this.defaultIfNull = defaultIfNull;
    }
    
    /**
     *
     * @return
     */
    public boolean isCheckIfExist() {
        return checkIfExist;
    }

    /**
     *
     * @param CheckIfExist
     */
    public void setCheckIfExist(boolean CheckIfExist) {
        this.checkIfExist = CheckIfExist;
    }

    /**
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     *
     */
    public FieldMap() {
        fieldNameFrom="";
        fieldNameTo="";
        value="";
        defaultIfNull="";
        key = false;
        etltype="";
    }

    /**
     *
     * @param FieldNameFrom
     * @param FieldNameTo
     * @param key
     * @param CheckIfExist
     * @param value
     * @param defaultifnull
     * @param etltype
     */
    public FieldMap(String FieldNameFrom, String FieldNameTo, boolean key, boolean CheckIfExist, String value, String defaultifnull, String etltype) {
        this.fieldNameFrom = FieldNameFrom;
        this.fieldNameTo = FieldNameTo;
        this.key = key;
        this.checkIfExist = CheckIfExist;
        this.value = value;
        this.defaultIfNull = defaultifnull;
        this.etltype = etltype;
    }
    
    /**
     *
     * @param FieldNameFrom
     * @param FieldNameTo
     * @param key
     * @param CheckIfExist
     */
    public FieldMap(String FieldNameFrom, String FieldNameTo, boolean key, boolean CheckIfExist) {
        this.fieldNameFrom = FieldNameFrom;
        this.fieldNameTo = FieldNameTo;
        this.key = key;
        this.checkIfExist = CheckIfExist;
        this.value = "";
        this.defaultIfNull = "";
    }
    
    /**
     *
     * @return
     */
    public String getFieldNameFrom() {
        return fieldNameFrom;
    }

    /**
     *
     * @param FieldNameFrom
     */
    public void setFieldNameFrom(String FieldNameFrom) {
        this.fieldNameFrom = FieldNameFrom;
    }

    /**
     *
     * @return
     */
    public String getFieldNameTo() {
        return fieldNameTo;
    }

    /**
     *
     * @param FieldNameTo
     */
    public void setFieldNameTo(String FieldNameTo) {
        this.fieldNameTo = FieldNameTo;
    }

    /**
     *
     * @return
     */
    public boolean isKey() {
        return key;
    }

    /**
     *
     * @param key
     */
    public void setKey(boolean key) {
        this.key = key;
    }
    
    
}
