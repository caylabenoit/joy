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
package com.joy.log;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */

public interface Ilog {
    public  boolean isInitialized();
    public void info(Object message);
    public void warn(Object message);
    public void error(Object message);
    public void fatal(Object message);
    public void debug(Object message);
    public boolean init(String log4jFile);
}
