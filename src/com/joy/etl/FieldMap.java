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

    public String getEtlType() {
        return etltype;
    }
    
    public String getDefaultIfNull() {
        return defaultIfNull;
    }

    public void setDefaultIfNull(String defaultIfNull) {
        this.defaultIfNull = defaultIfNull;
    }
    
    public boolean isCheckIfExist() {
        return checkIfExist;
    }

    public void setCheckIfExist(boolean CheckIfExist) {
        this.checkIfExist = CheckIfExist;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public FieldMap() {
        fieldNameFrom="";
        fieldNameTo="";
        value="";
        defaultIfNull="";
        key = false;
        etltype="";
    }

    public FieldMap(String FieldNameFrom, String FieldNameTo, boolean key, boolean CheckIfExist, String value, String defaultifnull, String etltype) {
        this.fieldNameFrom = FieldNameFrom;
        this.fieldNameTo = FieldNameTo;
        this.key = key;
        this.checkIfExist = CheckIfExist;
        this.value = value;
        this.defaultIfNull = defaultifnull;
        this.etltype = etltype;
    }
    
    public FieldMap(String FieldNameFrom, String FieldNameTo, boolean key, boolean CheckIfExist) {
        this.fieldNameFrom = FieldNameFrom;
        this.fieldNameTo = FieldNameTo;
        this.key = key;
        this.checkIfExist = CheckIfExist;
        this.value = "";
        this.defaultIfNull = "";
    }
    
    public String getFieldNameFrom() {
        return fieldNameFrom;
    }

    public void setFieldNameFrom(String FieldNameFrom) {
        this.fieldNameFrom = FieldNameFrom;
    }

    public String getFieldNameTo() {
        return fieldNameTo;
    }

    public void setFieldNameTo(String FieldNameTo) {
        this.fieldNameTo = FieldNameTo;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }
    
    
}
