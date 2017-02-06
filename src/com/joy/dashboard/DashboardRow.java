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
public class DashboardRow {
    private int order;
    private List<DashboardBloc> columns;

    public List<DashboardBloc> columns() {
        return columns;
    }
    
    public DashboardRow() {
        columns = new ArrayList<>();
    }
    
    public void addColumn(DashboardBloc col) {
        columns.add(col);
    }
    
    public DashboardBloc getColumn(int index) {
        return columns.get(index);
    }
    
    public int columnCount() {
        return columns.size();
    }
    
    public int getOrder() {
        return order;
    }

    public void setOrder(int Order) {
        this.order = Order;
    }
    
    
}
