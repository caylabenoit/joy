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
package com.joy.common.parameters;

import java.util.List;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyParameter {
    private String name;
    private Object value;
    private ParameterType type;

    public enum ParameterType {
        LIST, VALUE
    };

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType Type) {
        this.type = Type;
    }
        
    public JoyParameter(String Name, Object Value, ParameterType type) {
        this.name = Name;
        this.value = Value;
        this.type = type; 
    }    
    
    public JoyParameter() {
        name = "";
        value = null;
        this.type = ParameterType.VALUE;
    }

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    public Object getValue() {
        return value;
    }
    
    public List<JoyParameter> getList() {
        if (this.type == ParameterType.LIST)
            return (List<JoyParameter>)value;
        else
            return null;
    }
    
    /**
     * Return the goo parameter in the list or itself
     * @param Name parameter name requested
     * @return Parameters informations
     */
    public JoyParameter get(String Name) {
        if (this.type == ParameterType.LIST) {
             List<JoyParameter> list = (List<JoyParameter>)value;
             for (JoyParameter param : list) {
                 if (param.getName().equalsIgnoreCase(Name))
                     return param;
             }
        } else
            return (JoyParameter)value;
        return null;
    }
    
    public void setValue(Object Value) {
        this.value = Value;
    }
    
    
    
}
