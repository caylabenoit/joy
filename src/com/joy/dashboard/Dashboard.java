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

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class Dashboard {
    private String id;
    private String title;
    private String name;
    private List<DashboardRow> rows;

    public Dashboard() {
        rows = new ArrayList<>();
    }
    
    public void addRow(DashboardRow row) {
        rows.add(row);
    }
    
    public DashboardRow getRow(int index) {
        return rows.get(index);
    }
    
    public int rowCount() {
        return rows.size();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }
    
    public String getID() {
        return id;
    }

    public void setID(String ID) {
        this.id = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String Title) {
        this.title = Title;
    }
    
}
