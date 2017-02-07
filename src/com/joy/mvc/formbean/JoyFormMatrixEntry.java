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
public class JoyFormMatrixEntry extends JoyFormCommonInputs {
    private List<JoyFormVectorEntry> rows;

    public JoyFormMatrixEntry() {
        this.rows = new ArrayList();
        this.setName("DUMMY");
        this.setInputType(JoyFormInputTypes.Matrix);
    }

    public int getRowNumber() {
        if (this.rows != null) 
            return rows.size();
        else
            return 0;
    }
    
    public List<JoyFormVectorEntry> getMatrix() {
        return this.rows;
    }

    public void addRow(JoyFormVectorEntry Row) {
        this.rows.add(Row);
    }

    public JoyFormMatrixEntry(String Name) {
        super(Name, JoyFormInputTypes.Matrix);
    }
                               
}
