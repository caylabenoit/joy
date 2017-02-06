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
package com.joy.dashboard;

import com.joy.Joy;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Element;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class Dashboards {
    private List<Dashboard> dashboards;
    private List<DashboardTemplate> templates;
    
    public Dashboards() {
        dashboards = new ArrayList<>();
        templates = new ArrayList<>();
    }
    
    public List<Dashboard> getList() {
        return dashboards;
    }
    
    public Dashboard getDefaultDashboard() {
        if (dashboards.isEmpty())  
            return null;
        return dashboards.get(0);
    }
    
    public Dashboard getDashboard(String ID) {
        for (Dashboard ds : dashboards) {
            if (ds.getID().equalsIgnoreCase(ID)) 
                return ds;
        }
        return null;
    }
    
    /**
     * Initialize the dashboards with the configuration XML file
     * @param dashbInitializationFile XML file
     * @return 
     */
    public boolean init(String dashbInitializationFile) {
        try {
            if (dashbInitializationFile.isEmpty())  return false; 

            // go through the dashboards
            org.jdom2.Document document;
            Joy.LOG().info("Open Dashboard configuration file: " + dashbInitializationFile);
            document = Joy.OPEN_XML(dashbInitializationFile);
            Element xmlRoot = document.getRootElement();
            
            // Get the templates first
            List<Element> eltTemplates = xmlRoot.getChildren("joy-dashboard-template");
            for (Element elTpl : eltTemplates) {
                DashboardTemplate template = new DashboardTemplate(elTpl.getAttribute("type").getValue());
                String body = elTpl.getChildText("body");
                String afterBody = elTpl.getChildText("after-body");
                template.setBodyContent((body != null ? body : ""));
                template.setAfterBodyContent((afterBody != null ? afterBody : ""));
                templates.add(template);
            }
            
            // Per dashboard afterwards
            for (Element eltDashb : xmlRoot.getChildren("joy-dashboard")) {
                Dashboard db = new Dashboard();
                db.setID(eltDashb.getAttribute("id").getValue());
                db.setName(eltDashb.getAttribute("name").getValue());
                db.setTitle(eltDashb.getAttribute("name").getValue());

                // Go to the rows now
                List<Element> eltRows = eltDashb.getChildren("joy-dashboard-row");
                for (Element eltRow : eltRows) {
                    DashboardRow dbRow = new DashboardRow();

                    // Go to the columns now
                    List<Element> eltCols = eltRow.getChildren("joy-dashboard-bloc");
                    for (Element eltCol : eltCols) {
                        DashboardBloc dbColumn = new DashboardBloc(); 
                        dbColumn.setID(eltCol.getAttribute("id").getValue());
                        dbColumn.setSize(eltCol.getAttribute("size").getValue());
                        dbColumn.setBlocType(eltCol.getAttribute("type").getValue());
                        dbColumn.setTitle(eltCol.getAttribute("title").getValue());
                        dbColumn.setTemplate(getTemplateByName(dbColumn.getBlocType()));
                        
                        // Get the PARAMETERS
                        List<Element> eltParams = eltCol.getChildren("joy-param");
                        for (Element eltParam : eltParams) {
                            DashboardBlocParameter param = new DashboardBlocParameter();
                            String paramName = eltParam.getAttribute("name").getValue();
                            boolean isJoyParameter = false;
                            try {
                                isJoyParameter = eltParam.getAttribute("joy").getValue().equalsIgnoreCase("yes");
                            } catch (Exception e) {}
                            if (isJoyParameter) {
                                param.setName(paramName);
                                param.setValue(Joy.PARAMETERS().getParameter(paramName).getValue().toString());
                            } else {
                                param.setName(paramName);
                                param.setValue(eltParam.getText());
                            }
                            dbColumn.addParameter(param);
                        }

                        dbRow.addColumn(dbColumn);
                    }
                    db.addRow(dbRow);
                }
                dashboards.add(db);
            }
            
        } catch (Exception e) {
            Joy.LOG().error(e);
            return false;
        }
        return true;
    }
    
    private DashboardTemplate getTemplateByName (String Name) {
        for (DashboardTemplate template : this.templates) {
            if (template.getName().equalsIgnoreCase(Name))
                return template;
        }
        return null;
    }
}
