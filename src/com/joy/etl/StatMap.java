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
package com.joy.etl;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class StatMap {
    private int rowsUpdated;
    private int rowsInserted;
    private boolean fatalError;

    public boolean isFatalError() {
        return fatalError;
    }

    public void setFatalError(boolean FatalError) {
        this.fatalError = FatalError;
    }
    
    public StatMap() {
        rowsUpdated=0;
        rowsInserted=0;
        fatalError=false;
    }

    public int getRowsUpdated() {
        return rowsUpdated;
    }

    public int getRowsInserted() {
        return rowsInserted;
    }

    public void incRowsUpdated(int nb) {
        rowsUpdated += nb;
    }
    
    public void incRowsInserted(int nb) {
        rowsInserted += nb;
    }
    
}
