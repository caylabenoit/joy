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

    /**
     *
     */
    public LookupMap() {
        lookupConditions = new ArrayList();
    }

    /**
     *
     * @return
     */
    public String getTo() {
        return entityTo;
    }

    /**
     *
     * @param To
     */
    public void setTo(String To) {
        this.entityTo = To;
    }

    /**
     *
     * @return
     */
    public String getEntityLookup() {
        return entityLookup;
    }

    /**
     *
     * @param EntityLookup
     */
    public void setEntityLookup(String EntityLookup) {
        this.entityLookup = EntityLookup;
    }

    /**
     *
     * @return
     */
    public String getEntityLookupKey() {
        return entityLookupKey;
    }

    /**
     *
     * @param EntityLookupKey
     */
    public void setEntityLookupKey(String EntityLookupKey) {
        this.entityLookupKey = EntityLookupKey;
    }
    
    /**
     *
     * @param From
     * @param To
     */
    public void addCondition (String From, String To) {
        lookupConditions.add(new FieldMap(From, To, false, false));
    }

    /**
     *
     * @return
     */
    public List<FieldMap> getLookupConditions() {
        return lookupConditions;
    }
    
}
