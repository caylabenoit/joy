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
import com.joy.bo.BOFactory;
import com.joy.mvc.formbean.JoyFormBean;
import com.joy.mvc.formbean.JoyFormMatrixEntry;
import com.joy.mvc.formbean.JoyFormSingleEntry;
import com.joy.mvc.formbean.JoyFormVectorEntry;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionForm {
    private JoyFormBean m_FormDataBean;
    private BOFactory m_Entities;

    public BOFactory getBOFactory() {
        return m_Entities;
    }

    public void setEntities(BOFactory m_Entities) {
        this.m_Entities = m_Entities;
    }
    
    public ActionForm() {
        m_FormDataBean = new JoyFormBean();
        this.m_Entities = null;
    }

    public void addFormVectorEntry(String Name, 
                                   JoyFormVectorEntry vector) {
        m_FormDataBean.addVector(Name, vector);
    }
    
    public JoyFormVectorEntry getFormVectorEntry(String Name) {
        return m_FormDataBean.getVector(Name);
    }
    
    public void addFormSingleEntry(String Name,  String Value) {
        m_FormDataBean.addSingle(Name, Value);
    }
    
    public void addFormSingleEntry(String Name,  Object Value) {
        m_FormDataBean.addSingle(Name, Value);
    }
    
    public void addFormSingleEntry(String Name,  int Value) {
        m_FormDataBean.addSingle(Name, String.valueOf(Value));
    }
     
    public JoyFormSingleEntry getFormSingleEntry(String Name) {
        return m_FormDataBean.getSingle(Name);
    }
    
    public void addFormMatrixEntry(String Name, JoyFormMatrixEntry obj) {
        obj.setName(Name);
        m_FormDataBean.addMatrix(obj);
    }
    
    public JoyFormMatrixEntry getFormMatrixEntry(String Name) {
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
        JoyFormMatrixEntry matrix = new JoyFormMatrixEntry();
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
                JoyFormVectorEntry columns = new JoyFormVectorEntry();
                for(i=0; i < FieldNames.size(); i++){ 
                    columns.addValue(FieldNames.get(i), rs.getObject(FieldNames.get(i)));
                }
                matrix.addRow(columns);
            }
            
        } catch (SQLException e) {
            Joy.log().error(e);
            hasNoError = false;
        } 
        this.addFormMatrixEntry(FormTagName, matrix );
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
                    this.addFormSingleEntry(FieldNames.get(i), rs.getObject(FieldNames.get(i)));
                }
            }
        } catch (SQLException e) {
            Joy.log().error(e);
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
        JoyFormVectorEntry columns = new JoyFormVectorEntry();
        if (SelectedFieldKeyValue != null)
            columns.setSelected(String.valueOf(SelectedFieldKeyValue));
        try {
            while (rs.next()) {
                columns.addValue(FieldKey, 
                                 rs.getString(FieldKey), 
                                 rs.getString(FieldName));
            }

        } catch (SQLException e) {
            Joy.log().error(e);
            hasNoError = false;
        }
        this.addFormVectorEntry(FormTagName, columns);
        return hasNoError;
    }
    
}
