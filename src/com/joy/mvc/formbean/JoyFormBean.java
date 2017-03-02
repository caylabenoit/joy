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
                          List<JoyFormSingle> Values,
                          String SelectedValue) {
        JoyFormVector input = new JoyFormVector(Name, Values, SelectedValue);
        m_formAttributes.add(input);
    }

    public void addVector(String Name, 
                          JoyFormVector Value) {
        Value.setName(Name);
        m_formAttributes.add(Value);
    }
    
    public void addVector(JoyFormVector Value) {
        m_formAttributes.add(Value);
    }
    
    public void addMatrix(JoyFormMatrix Value) {
        m_formAttributes.add(Value);
    }
    
    public void addSingle(String Name, 
                          String Value) {
        JoyFormSingle input = new JoyFormSingle(Name, Value);
        m_formAttributes.add(input);
    }
    
    public void addSingle(String Name, 
                          Object Value) {
        JoyFormSingle input = new JoyFormSingle(Name, Value);
        m_formAttributes.add(input);
    }
    
    public void addSingle(JoyFormSingle Value) {
        m_formAttributes.add(Value);
    }
    
    public JoyFormSingle getSingle(String Name) {
        for (JoyFormCommonInputs input : m_formAttributes) {
            if (input.getName().equalsIgnoreCase(Name) && input.getInputType() == JoyFormInputTypes.Single) {
                JoyFormSingle obj = (JoyFormSingle)input;
                return obj;
            }
        }
        return new JoyFormSingle();
    }
    
    public JoyFormVector getVector(String Name) {
        for (JoyFormCommonInputs input : m_formAttributes) {
            if (input.getName().equalsIgnoreCase(Name) && input.getInputType() == JoyFormInputTypes.Vector) {
                JoyFormVector obj = (JoyFormVector)input;
                return obj;
            }
        }
        return new JoyFormVector();
    }
    
    public JoyFormMatrix getMatrix(String Name) {
        for (JoyFormCommonInputs input : m_formAttributes) {
            if (input.getName().equalsIgnoreCase(Name) && input.getInputType() == JoyFormInputTypes.Matrix) {
                JoyFormMatrix obj = (JoyFormMatrix)input;
                return obj;
            }
        }
        return new JoyFormMatrix();
    }
    
}
