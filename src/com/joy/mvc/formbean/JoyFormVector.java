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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyFormVector extends JoyFormCommonInputs {
    private List<JoyFormSingle> vector;
    private String selected;

    public JoyFormVector() {
        vector = new ArrayList();
        selected = "";
        this.setInputType(JoyFormInputTypes.Vector);
    }
    
    public String getSelected() {
        return selected;
    }

    public void setSelected(String Selected) {
        this.selected = Selected;
    }
    
    public List<JoyFormSingle> getVector() {
        return vector;
    }
    
    public Object getVectorObject(String Name) {
        for (JoyFormSingle val : this.vector) {
            if (val.getName().equalsIgnoreCase(Name)) {
                return val.getValue();
            }
        }
        return "";
    }
    
    public String getVectorValue(String Name) {
        for (JoyFormSingle val : this.vector) {
            if (val.getName().equalsIgnoreCase(Name)) {
                return val.getStrValue();
            }
        }
        return "";
    }
    
    public String getVectorID(String Name) {
        for (JoyFormSingle val : this.vector) {
            if (val.getName().equalsIgnoreCase(Name)) {
                return ((val.getID()==null ? val.getStrValue() : val.getID()));
            }
        }
        return "";
    }
    
    public void addItem(String Name, String Value) {
        this.vector.add(new JoyFormSingle(Name, Value));
    }

    public void addItem(String Name, Object object) {
        this.vector.add(new JoyFormSingle(Name, object));
    }
    
    public void addItem(String Name, String ID, String Value) {
        JoyFormSingle val = new JoyFormSingle( Name, Value);
        val.setID(ID);
        this.vector.add(val);
    }
    
    public void addItem(String Name, int Value) {
        this.vector.add(new JoyFormSingle(Name, String.valueOf(Value)));
    }
    
    public void setVector(List<JoyFormSingle> List) {
        this.vector = List;
    }

    public JoyFormVector(String Name, 
                              List<JoyFormSingle> Value) {
        super(Name, JoyFormInputTypes.Vector);
        this.setVector(Value);
        selected = "";
    }
    
    public JoyFormVector(String Name) {
        super(Name, JoyFormInputTypes.Vector);
        this.vector = new ArrayList();
        selected = "";
    }
    
    public JoyFormVector(String Name, 
                              List<JoyFormSingle> Value,
                              String SelectedValue) {
        super(Name, JoyFormInputTypes.Vector);
        this.setVector(Value);
        selected = SelectedValue;
    }


                  
}
