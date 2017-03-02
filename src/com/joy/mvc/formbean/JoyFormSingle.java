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
package com.joy.mvc.formbean;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyFormSingle extends JoyFormCommonInputs {
    private Object value;

    JoyFormSingle() {
        value = "";
    }

    public JoyFormSingle(String Name, String Value) {
        super(Name, JoyFormInputTypes.Single);
        this.setID(Value);
        this.setValue(Value);
    }
    
    public JoyFormSingle(String Name, Object Value) {
        super(Name, JoyFormInputTypes.Single);
        this.setID(Name);
        this.setValue(Value);
    }
    
    public JoyFormSingle(String Name, String ID, Object Value) {
        super(Name, JoyFormInputTypes.Single);
        this.setID(ID);
        this.setValue(Value);
    }
    
    public Object getValue() {
        return value;
    }
    
    public String getStrValue() {
        return (value != null ? value.toString() : "");
    }
    
    public void setValue(String Value) {
        this.value = Value;
    }
    
    public void setValue(Object Value) {
        this.value = Value;
    }

}
