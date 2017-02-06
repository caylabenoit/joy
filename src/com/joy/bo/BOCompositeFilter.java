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

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class BOCompositeFilter {
    private String alias;
    private String name;
    private String operator;
    private String value;

    public BOCompositeFilter(String alias, String name, String operator, String value) {
        this.alias = alias;
        this.name = name;
        this.operator = (operator == null ? "=" : operator);
        this.value = (value == null ? "NULL" : value);
    }

    public String getFullName() {
        return (alias == null ? name : alias + "." + name);
    }

    public String getOperator() {
        return (operator.isEmpty() ? "=" : operator);
    }

    public String getValue() {
        return (value.isEmpty() ? "NULL" : value);
    }

}
