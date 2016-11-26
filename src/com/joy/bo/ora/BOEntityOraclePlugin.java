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
package com.joy.bo.ora;

import com.joy.Joy;
import com.joy.bo.BOEntityReadWrite;
import com.joy.bo.BOQueryExecution;

/**
 * Entity plugin for Oracle
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class BOEntityOraclePlugin extends BOEntityReadWrite {

    
    /**
    * Build a Select SQL myQuery
    * @param filtering define a filtered myQuery based on the specified keys
    * @param From Table or Query 
    * @param sorted Define a myQuery with sort (order by)
    * @param countonly if true, define a Count(*) myQuery (do not use the select fields in that case)
    * @return SQL myQuery
    */
    @Override
    protected BOQueryExecution getSelectQuery(boolean filtering, 
                                              boolean sorted,
                                              boolean countonly,
                                              String From) {
        String sql = "";
        BOQueryExecution myQuery = new BOQueryExecution();
        
        sql += getSQLSelect(countonly);
        sql += getSQLFrom(From);
        sql += getSQLWhere(myQuery, filtering);
        
        // Limit records for Oracle Only
        if (limitRecords > 0) 
            sql += " AND ROWNUM <= " + limitRecords;
        
        sql += getSQLOrderBy(sorted);

        Joy.LOG().debug("SQL generated: " + sql);
        
        myQuery.setSQL(sql);
        return myQuery;
    }
    
}
