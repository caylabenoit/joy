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
package com.joy.bo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class BOQueryExecution {
    private List<BOField> values;
    private String sql;

    /**
     *
     * @return
     */
    public List<BOField> getValues() {
        return values;
    }

    /**
     *
     * @return
     */
    public String getSQL() {
        return sql;
    }

    /**
     *
     * @param SQL
     */
    public void setSQL(String SQL) {
        this.sql = SQL;
    }

    /**
     *
     */
    public BOQueryExecution() {
        sql = "";
        values = new ArrayList();
    }
    
    /**
     *
     * @param o
     */
    public void addValue(BOField o) {
        values.add(o);
    }
}
