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

import com.joy.C;
import static com.joy.bo.BOEntityType.*;
import com.joy.common.joyClassTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom2.Element;

/**
 * Build a dynamic query considering an XML description like this :
 * 
 * Sample :
 * <joy-composite name = "Composite name" distinct="yes/no">
 *   <joy-entity alias="[Left Table Alias]" as="[As in Select/To rename the selected field]">...</joy-entity>
 * 
 *   <joy-field alias="[Table Alias for selected field]">[Left IEntity]</joy-field>
 *   <joy-field ...></joy-field>
 * 
 *   <joy-join type="INNER/LEFT/RIGHT [Single JOIN by default]">
 *       <joy-entity alias="[Right Table Alias]">[Right IEntity]</joy-entity>
 *       <joy-join-key master="[Left field Key]" slave="[Right field Key]" />
 *   </joy-join>
 *   <joy-join type="...">
 *       ...
 *   </joy-join>
 * 
 *   <joy-filter alias="[Table Alias for filter field]" field="[Filter field]" operator="&lt;&gt;[Or anything else]">[Filter]</joy-filter>
 *   <joy-filter>...</joy-filter>
 * 
 *   <joy-sort alias="[Alias Sort field]" field="[Sort field]" desc="[no/yes]" />
 *   <joy-sort ... />
 * 
 *   <joy-group alias="[Table Alias for grouped field]">[Group Field]</joy-group>
 * 
 * </joy-composite>
 * 
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class BOEntityComposite extends joyClassTemplate  {
    
    private String  name;
    private String  masterEntity; 
    private String  masterEntityAlias; 
    private boolean masterIsAQuery;
    
    private final List<BOCompositeJoin>     joins;      // joins
    private final List<BOCompositeField>    fields;    // fields select
    private final List<BOCompositeFilter>   filters;  // where 
    private final List<BOCompositeSorter>   sorts;    // Order By 
    private final List<BOCompositeField>    groups;    // Order By 
    
    private boolean             distinct;
    private final BOFactory    entities;

    public String getName() {
        return name;
    }
    
    public void addJoin(BOCompositeJoin join) {
        joins.add(join);
    }
    
    public void addField(String name, String alias, String as) {
        fields.add(new BOCompositeField(name, alias, as));
    }
    
    public void addFilter(String Alias, String Name, String Operator, String Value) {
        filters.add(new BOCompositeFilter(Alias, Name, Operator, Value));
    }
    
    public void addSort(String Alias, String Name, boolean desc) {
        sorts.add(new BOCompositeSorter(Name, Alias, desc));
    }
    
    public void addGroup(String Alias, String Name) {
        groups.add(new BOCompositeField(Name, Alias, null));
    }
    
    /**
     * returns the Table name or the query
     * @return 
     */
    private String getEntityForQuery(boolean isQuery, String Entity) {
        if (isQuery) {
            try {
                IEntity query = entities.getEntity(Entity);
                return "(" + query.getQuery() + ")";
            } catch (Exception ex) { 
                getLog().warning(ex.toString());
                return  Entity; 
            }
        } else
            return  Entity;
    }

    /**
     * Returns the Order By part of the query
     * @return 
     */
    private String getSort() {
        if (this.sorts.isEmpty())
            return "";
        String mySorter = " ORDER BY ";
        
        for (BOCompositeSorter filter : this.sorts) {
            mySorter += filter.getSort() + ",";
        }
        
        return mySorter.substring(0, mySorter.length()-1);
    }
    
    /**
     * Returns the Where/and part of the query
     * @return 
     */
    private String getFilter() {
        String myFilter = "";
        
        myFilter +=" WHERE 1=1 ";
        for (BOCompositeFilter filter : this.filters) {
            myFilter += " AND " + filter.getFullName() + " " + filter.getOperator() + " " + filter.getValue() + " ";
        }
        return myFilter;
    }
    
    /**
     * Returns the Select part of the query
     * @return 
     */
    private String getSelect() {
        // Select part
        String mySelect = " SELECT " + (this.distinct ? "DISTINCT " : "");
        
        if (fields.isEmpty()) {
            mySelect += masterEntityAlias + ".*" + ",";
            for (BOCompositeJoin join : joins) {
                mySelect += join.getSlaveEntityAlias()+ ".*" + ",";
            }
        } else {
            for (BOCompositeField field : this.fields) {
                mySelect += field.getSelectField()  +",";
            }
        }
        return mySelect.substring(0, mySelect.length()-1) + " ";
    }

    /**
     * Returns the Select part of the query
     * @return 
     */
    private String getGroup() {
        // Select part
        String myGroupBy = " GROUP BY ";
        
        if (!groups.isEmpty()) {
            for (BOCompositeField group : groups) {
                myGroupBy += group.getSelectField() + ",";
            }
        } else
            return "";
        return myGroupBy.substring(0, myGroupBy.length()-1) + " ";
    }
    
    /**
     * Returns the From part of the query
     * @return 
     */
    private String getFrom() {
        String myFrom = " FROM " + getEntityForQuery(this.masterIsAQuery, this.masterEntity) + " " + this.masterEntityAlias + " ";
        for (BOCompositeJoin join : joins) { // for each join
            myFrom += join.getJoinType() + " JOIN " + getEntityForQuery(join.isSlaveAQuery(), join.getSlaveEntity()) + " " + join.getSlaveEntityAlias();
            myFrom += " ON (";
            boolean first = true;
            for (BOCompositeJoinKeys keys : join.getKeys()) {
                if (!first) myFrom += " AND ";
                myFrom += this.masterEntityAlias + "." + keys.getMasterKey();
                myFrom += " = ";
                myFrom += join.getSlaveEntityAlias() + "." + keys.getSlaveKey();
                first = false;
            }
            myFrom += " ) ";
        }
        return myFrom;
    }
    
    /**
     * Dynamically build the query
     * @return 
     */
    public String getQuery() {
        String generatedQuery = "";
        try {
            generatedQuery = this.getSelect() + this.getFrom() + this.getFilter() + this.getGroup() + this.getSort();
        } catch (Exception ex) {
            getLog().severe("Impossible to generate query for Entity " + this.name + ", Error: " + ex + " | Resisual: " + generatedQuery);
        }
        return generatedQuery;
    }

    public BOEntityComposite(BOFactory Entities) {
        super();
        this.joins = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.filters = new ArrayList<>();
        this.sorts = new ArrayList<>(); 
        this.groups = new ArrayList<>(); 
        this.masterEntity = "";
        this.name = "";
        this.masterIsAQuery = false;
        this.masterEntityAlias = "";
        this.entities = Entities;
        this.distinct = false;
    }

    /**
     * Return true if the entity is a query
     * @param Name entity name
     * @return 
     */
    private boolean isEntityAQuery(String Name) {
        IEntity entity = entities.getEntity(Name);
        if (entity != null) {
            return (entity.getBOType() != boReadWrite);
        } else // if null consider as a table
            return false;
    }    
    
    /**
     * Initialize the Composite entity
     * @param entityXml XML node with all the composite informations
     */
    private void initEntity(Element entityXml) {
        // Build the composite query
        this.masterEntity = entityXml.getChild(C.ENTITIES_COMPOSITE_ENTITY).getText();
        this.name = entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE);
        this.masterEntityAlias = entityXml.getChild(C.ENTITIES_COMPOSITE_ENTITY).getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_ALIAS);
        this.masterIsAQuery = isEntityAQuery(this.masterEntity);
        
        String localDistinct = (entityXml.getAttributeValue(
                                C.ENTITIES_COMPOSITE_ATTR_DISTINCT) == null ? 
                                C.NO : 
                                entityXml.getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_DISTINCT));
        this.distinct = localDistinct.equalsIgnoreCase(C.YES);
    }
    
    /**
     * Initialize the select fields
     * @param entityXml XML node with all the composite informations
     */
    private void initSelectFields(Element entityXml) {
        // Go through the select fields
        List eltFields = entityXml.getChildren(C.ENTITIES_COMPOSITE_FIELD);
        Iterator i1 = eltFields.iterator();
        while(i1.hasNext()) { 
            Element jnXml = (Element)i1.next();
            String myName = jnXml.getText();
            String alias = jnXml.getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_ALIAS);
            String as = jnXml.getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_AS);
            this.addField(myName, alias, as);
        }
    }
    
    /**
     * Initialize the joins
     * @param entityXml XML node with all the composite informations
     */
    private void initJoins(Element entityXml) {
        // Go through the joins
        List eltJoins = entityXml.getChildren(C.ENTITIES_COMPOSITE_JOIN);
        Iterator i2 = eltJoins.iterator();
        while(i2.hasNext()) { 
            Element jnXml = (Element)i2.next();
            String slaveJoin = jnXml.getChild(C.ENTITIES_COMPOSITE_ENTITY).getText();
            String slaveAlias = jnXml.getChild(C.ENTITIES_COMPOSITE_ENTITY).getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_ALIAS);
            String typeJoin = jnXml.getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_TYPE);
            BOCompositeJoin join = new BOCompositeJoin(slaveJoin, 
                                                       typeJoin, 
                                                       isEntityAQuery(slaveJoin),
                                                       slaveAlias);

            List eltKeys = jnXml.getChildren(C.ENTITIES_COMPOSITE_JOINKEY);
            Iterator i3 = eltKeys.iterator();
            while(i3.hasNext()) { // go through the join keys
                Element keyXml = (Element)i3.next();
                String slaveKey = keyXml.getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_SLAVE);
                String masterKey = keyXml.getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_MASTER);
                join.addKeys(masterKey, slaveKey);
            }
            this.addJoin(join);
        }
    }
    
    /**
     * Initialize the filters
     * @param entityXml XML node with all the composite informations
     */
    private void initFilters(Element entityXml) {
        // Go through the filter fields
        List eltFilters = entityXml.getChildren(C.ENTITIES_COMPOSITE_FILTER);
        Iterator i3 = eltFilters.iterator();
        while(i3.hasNext()) { 
            Element jnXml = (Element)i3.next();
            String value = jnXml.getText();
            String alias = jnXml.getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_ALIAS);
            String operator = jnXml.getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_OPERATOR);
            String myName = jnXml.getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_FIELD);
            this.addFilter(alias, myName, operator, value);
        }
    }
    
    /**
     * Initialize the sorts
     * @param entityXml XML node with all the composite informations
     */
    private void initSorters(Element entityXml) {
        // Go through the sorts fields
        List eltSorters = entityXml.getChildren(C.ENTITIES_COMPOSITE_SORT);
        Iterator i4 = eltSorters.iterator();
        while(i4.hasNext()) { 
            Element jnXml = (Element)i4.next();
            String alias = jnXml.getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_ALIAS);
            String desc = jnXml.getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_DESC);
            if (desc == null) desc = C.NO;
            String myName = jnXml.getText();
            this.addSort(alias, myName, desc.equalsIgnoreCase(C.YES));
        }
    }
    
    /**
     * Initialize the group by fields
     * @param entityXml XML node with all the composite informations
     */
    private void initGroups(Element entityXml) {
        // Go through the select fields
        List eltFields = entityXml.getChildren(C.ENTITIES_COMPOSITE_GROUP);
        Iterator i1 = eltFields.iterator();
        while(i1.hasNext()) { 
            Element jnXml = (Element)i1.next();
            String myName = jnXml.getText();
            String alias = jnXml.getAttributeValue(C.ENTITIES_COMPOSITE_ATTR_ALIAS);
            this.addGroup(alias, myName);
        }
    }
    
    /**
     * Initialize a composite bloc
     * @param entityXml XML bloc (left + right only ... no more levels)
     */
    public void init(Element entityXml) {
        try {
            initEntity(entityXml);
            initSelectFields(entityXml);
            initJoins(entityXml);
            initFilters(entityXml);
            initSorters(entityXml);
            initGroups(entityXml);
            
        } catch (Exception ex) {
            getLog().severe("Entity " + this.name + " cannot be initialized, Error: " + ex);
        }
    }
    
}
