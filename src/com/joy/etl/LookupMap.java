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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class LookupMap {
    private String entityTo;
    private String entityLookup;
    private String entityLookupKey;
    private List<FieldMap> lookupConditions;

    public LookupMap() {
        lookupConditions = new ArrayList();
    }

    public String getTo() {
        return entityTo;
    }

    public void setTo(String To) {
        this.entityTo = To;
    }

    public String getEntityLookup() {
        return entityLookup;
    }

    public void setEntityLookup(String EntityLookup) {
        this.entityLookup = EntityLookup;
    }

    public String getEntityLookupKey() {
        return entityLookupKey;
    }

    public void setEntityLookupKey(String EntityLookupKey) {
        this.entityLookupKey = EntityLookupKey;
    }
    
    public void addCondition (String From, String To) {
        lookupConditions.add(new FieldMap(From, To, false, false));
    }

    public List<FieldMap> getLookupConditions() {
        return lookupConditions;
    }
    
}
