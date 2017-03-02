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
package com.joy.mvc;

import com.joy.Joy;
import com.joy.mvc.formbean.JoyFormBean;
import com.joy.mvc.formbean.JoyFormMatrix;
import com.joy.mvc.formbean.JoyFormSingle;
import com.joy.mvc.formbean.JoyFormVector;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionForm extends Action {
    private JoyFormBean m_FormDataBean;

    public ActionForm() {
        m_FormDataBean = new JoyFormBean();
    }

    public void addVector(String Name, 
                                   JoyFormVector vector) {
        m_FormDataBean.addVector(Name, vector);
    }
    
    public JoyFormVector getVector(String Name) {
        return m_FormDataBean.getVector(Name);
    }
    
    public void addSingle(String Name,  String Value) {
        m_FormDataBean.addSingle(Name, Value);
    }
    
    public void addSingle(String Name,  Object Value) {
        m_FormDataBean.addSingle(Name, Value);
    }
    
    public void addSingle(String Name,  int Value) {
        m_FormDataBean.addSingle(Name, String.valueOf(Value));
    }
     
    public JoyFormSingle getSingle(String Name) {
        return m_FormDataBean.getSingle(Name);
    }
    
    public void addMatrix(String Name, JoyFormMatrix obj) {
        obj.setName(Name);
        m_FormDataBean.addMatrix(obj);
    }
    
    public JoyFormMatrix getMatrix(String Name) {
        return m_FormDataBean.getMatrix(Name);
    }
    
    /**
     * Direct load a matrix into the result form from a resultset
     * @param rs    resultset
     * @param FormTagName  tag name for the form
     * @return false if error
     */
    public boolean loadMatrix(ResultSet rs, 
                              String FormTagName) {
        JoyFormMatrix matrix = new JoyFormMatrix();
        List<String> FieldNames = new ArrayList();
        int i;
        boolean hasNoError = true;
        
        try {
            // Get the ResultSet names list first
            ResultSetMetaData rsmd = rs.getMetaData(); 
            for(i=1; i <= rsmd.getColumnCount(); i++){ 
                FieldNames.add(rsmd.getColumnName(i));
            }

            // Load the matrix
            while (rs.next()) {
                JoyFormVector columns = new JoyFormVector();
                for(i=0; i < FieldNames.size(); i++){ 
                    columns.addItem(FieldNames.get(i), rs.getObject(FieldNames.get(i)));
                }
                matrix.addRow(columns);
            }
            
        } catch (SQLException e) {
            Joy.LOG().error(e);
            hasNoError = false;
        } 
        this.addMatrix(FormTagName, matrix );
        return hasNoError;
    }
    
    /**
     * Load the resultset directly as fields into the form. Each DB field become the tag for the form
     * @param rs resultset
     * @return 
     */
    public boolean loadSingle(ResultSet rs) {
        List<String> FieldNames = new ArrayList();
        int i;
        boolean hasNoError = true;
        
        try {
            // Get the ResultSet names list first
            ResultSetMetaData rsmd = rs.getMetaData(); 
            for(i=1; i <= rsmd.getColumnCount(); i++){ 
                FieldNames.add(rsmd.getColumnName(i));
            }

            // Load the resultset directly as fields into the form
            if (rs.next()) {
                for(i=0; i < FieldNames.size(); i++){ 
                    this.addSingle(FieldNames.get(i), rs.getObject(FieldNames.get(i)));
                }
            }
        } catch (SQLException e) {
            Joy.LOG().error(e);
            hasNoError= false;
        } 
        return hasNoError;
    }
    
    /**
     * Load a vector into the result form
     * @param rs    resultset
     * @param FieldKey  Key field for the items into the vector
     * @param FieldName Name field for the items into the vector
     * @param FormTagName   Tag for the form to get this vector
     * @param SelectedFieldKeyValue for combobox loading, specify the selected value
     * @return trus if no pb
     */
    public boolean loadVector(ResultSet rs, 
                              String FieldKey, 
                              String FieldName,
                              String FormTagName,
                              String SelectedFieldKeyValue) {
        boolean hasNoError = true;
        JoyFormVector columns = new JoyFormVector();
        if (SelectedFieldKeyValue != null)
            columns.setSelected(String.valueOf(SelectedFieldKeyValue));
        try {
            while (rs.next()) {
                columns.addItem(FieldKey, 
                                 rs.getString(FieldKey), 
                                 rs.getString(FieldName));
            }

        } catch (SQLException e) {
            Joy.LOG().error(e);
            hasNoError = false;
        }
        this.addVector(FormTagName, columns);
        return hasNoError;
    }
    
}
