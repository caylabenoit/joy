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

public class JoyFormBean {
    private List<JoyFormCommonInputs> m_formAttributes;
    
    public JoyFormBean() {
        m_formAttributes = new ArrayList();
    }

    public void addVector(String Name, 
                          List<JoyFormSingleEntry> Values,
                          String SelectedValue) {
        JoyFormVectorEntry input = new JoyFormVectorEntry(Name, Values, SelectedValue);
        m_formAttributes.add(input);
    }

    public void addVector(String Name, 
                          JoyFormVectorEntry Value) {
        Value.setName(Name);
        m_formAttributes.add(Value);
    }
    
    public void addVector(JoyFormVectorEntry Value) {
        m_formAttributes.add(Value);
    }
    
    public void addMatrix(JoyFormMatrixEntry Value) {
        m_formAttributes.add(Value);
    }
    
    public void addSingle(String Name, 
                          String Value) {
        JoyFormSingleEntry input = new JoyFormSingleEntry(Name, Value);
        m_formAttributes.add(input);
    }
    
    public void addSingle(String Name, 
                          Object Value) {
        JoyFormSingleEntry input = new JoyFormSingleEntry(Name, Value);
        m_formAttributes.add(input);
    }
    
    public void addSingle(JoyFormSingleEntry Value) {
        m_formAttributes.add(Value);
    }
    
    public JoyFormSingleEntry getSingle(String Name) {
        for (JoyFormCommonInputs input : m_formAttributes) {
            if (input.getName().equalsIgnoreCase(Name) && input.getInputType() == JoyFormInputTypes.Single) {
                JoyFormSingleEntry obj = (JoyFormSingleEntry)input;
                return obj;
            }
        }
        return new JoyFormSingleEntry();
    }
    
    public JoyFormVectorEntry getVector(String Name) {
        for (JoyFormCommonInputs input : m_formAttributes) {
            if (input.getName().equalsIgnoreCase(Name) && input.getInputType() == JoyFormInputTypes.Vector) {
                JoyFormVectorEntry obj = (JoyFormVectorEntry)input;
                return obj;
            }
        }
        return new JoyFormVectorEntry();
    }
    
    public JoyFormMatrixEntry getMatrix(String Name) {
        for (JoyFormCommonInputs input : m_formAttributes) {
            if (input.getName().equalsIgnoreCase(Name) && input.getInputType() == JoyFormInputTypes.Matrix) {
                JoyFormMatrixEntry obj = (JoyFormMatrixEntry)input;
                return obj;
            }
        }
        return new JoyFormMatrixEntry();
    }
    
}
