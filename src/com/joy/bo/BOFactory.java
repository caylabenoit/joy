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
import com.joy.JOY;
import com.joy.bo.init.BOInitRecord;
import com.joy.common.joyClassTemplate;
import com.joy.providers.JoyConnectionDetail;
import com.joy.providers.JoyDBProvider;
import com.joy.providers.JoyInitQuery;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom2.Element;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr) 
 */
public class BOFactory extends joyClassTemplate {
    protected JoyDBProvider dbConnection;
    protected JoyConnectionDetail connectionDetail;
    protected boolean initialized;
    protected List<IEntity> cacheEntities;
    private boolean poolManagementActive;
    private List<BOPlugin> plugins;
    private BOEntityRegistry registry;
    
    public List<IEntity> getAll() {
        return this.cacheEntities;
    }

    /**
     * Get and return the requested entity
     * @param Name Entity name
     * @return BOEntityReadWrite, BOView or BOEntityCustom
     */
    public IEntity getEntity(String Name) {
        for (IEntity entity : this.cacheEntities) 
            if (entity.getLabel().equalsIgnoreCase(Name) || entity.getName().equalsIgnoreCase(Name)) {
                return entity;
            }
        return null;
    }
    
    public boolean isInitialized() {
        return this.initialized;
    }

    public BOFactory() {
        this.cacheEntities = new ArrayList();
        this.plugins = new ArrayList();
        this.poolManagementActive = true;
    }
    
    /**
     * Build a table with all the field technical name & label mappings
     * @param entityElement
     * @return 
     */
    private List<BOMapFieldLabel> getMappedFields(Element entityElement) {
        List myEntities = entityElement.getChildren(C.ENTITIES_JOY_FIELDLABEL_TAG);
        Iterator iremaps = myEntities.iterator();
        List<BOMapFieldLabel> retValue = new ArrayList();
        
        while(iremaps.hasNext()) {   // get all the fields remaps
            Element field = (Element)iremaps.next();
            retValue.add(new BOMapFieldLabel(field.getAttributeValue("name"), field.getText()));
        }
        return retValue;
    }
    
    /**
     * Initialize the tables myEntities only
     * @param entities
     * @param root
     * @param db 
     */
    private void initTables(String group,
                            Element root, 
                            JoyDBProvider db) {
        List tables = root.getChildren(C.ENTITIES_JOY_TABLE_TAG);
        Iterator i1 = tables.iterator();
        
        while(i1.hasNext()) {   // parcours toutes les entités/tables
            Element entityXml = (Element)i1.next();

            // Get all the fields remaps
            List<BOMapFieldLabel> remapsField = getMappedFields(entityXml); 
            IEntity entity = getEntityPlugin(db);
            entity.setGroup(group);
            entity.setName(entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE));
            entity.setBoType(BOEntityType.boReadWrite);
            entity.setDB(db);
            db.getMetadataFromDB(entity, null, remapsField); 
            
            // Get all the INIT values if any
            List<Element> inits = entityXml.getChildren("joy-init-record");
            for (Element initRec : inits) {
                BOInitRecord rec = new BOInitRecord();
                List<Element> fields = initRec.getChildren("joy-field");
                for (Element initField : fields) {
                    rec.addField(initField.getAttributeValue("name"), initField.getText());
                }
                entity.addDefaultRecord(rec);
            }
            
            // Label assignment
            String label = (entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE) == null ? "" : entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE));
            label = (!label.isEmpty() ? label : entity.getName());
            entity.setLabel(label);

            // Set and add BO
            cacheEntities.add(entity);
            if (entity.isInitialized())
                getLog().fine("Table " + entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE) + " Added successfully");
            else
                getLog().severe("Table " + entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE) + " has not been Added successfully !!!");
        }
    }
    
    /**
     * Get the SQL myEntities
     * @param entities
     * @param root 
     */
    private void initQueries(String group,
                            Element root, 
                            JoyDBProvider db) {
        List eltentities = root.getChildren(C.ENTITIES_JOY_QUERY_TAG);
        Iterator i1 = eltentities.iterator();
        while(i1.hasNext()) {   // parcours toutes les queries
            Element entityXml = (Element)i1.next();
            String queryContent = "";
            
            // get the DB engine specific query if exists
            Element childDBSpecific = entityXml.getChild(db.getDBProvider().toUpperCase());
            if (childDBSpecific != null) 
                queryContent = childDBSpecific.getText().replace("\n", "");
            else
                queryContent = entityXml.getText().replace("\n", "");

            // get all the fields remaps
            List<BOMapFieldLabel> remapsField = getMappedFields(entityXml); 
            IEntity entity = getEntityPlugin(db);
            entity.setGroup(group);
            entity.setName(entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE));
            entity.setBoType(BOEntityType.boReadOnly);
            entity.setQuery(queryContent);
            entity.setDB(db);
            db.getMetadataFromDB(entity, queryContent, remapsField);
            
            // Label assignment
            String label = (entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE) == null ? "" : entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE));
            label = (!label.isEmpty() ? label : entity.getName());
            entity.setLabel(label);

            // Set and add BO
            cacheEntities.add(entity);
            if (entity.isInitialized())
                getLog().info("Custom Entity " + entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE) + " Added successfully");
            else
                getLog().severe("Custom Entity " + entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE) + " has not been Added successfully !!!");
        }
    }

    /**
     * Instantiate the good DB plugin
     * @param db DB connection
     * @return Entity for DB
     */
    private IEntity getEntityPlugin(JoyDBProvider db) {
        String currentProvider = db.getDBProvider().toUpperCase();
        IEntity retEntity = null;
        for (BOPlugin plugin : this.plugins) {
            if (plugin.getName().equalsIgnoreCase(currentProvider)) {
                try {
                    String myClass = plugin.getClassName();
                    retEntity = (IEntity)Class.forName(myClass).newInstance();
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                    getLog().severe(ex.toString());
                }
            }
        }
        return (retEntity == null ? new BOEntityReadWrite() : retEntity);
    }
    
    /**
     * Initialize all the Composite queries
     * @param entities
     * @param root 
     */
    private void initComposites(String group,
                                Element root, 
                                JoyDBProvider db) {
        List eltentities = root.getChildren(C.ENTITIES_JOY_COMPOSITE_TAG);
        Iterator i1 = eltentities.iterator();
        
        while(i1.hasNext()) {   // parcours toutes les composites
            Element entityXml = (Element)i1.next();
            String EntityName = entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE);
            
            // Build the Composite query
            BOEntityComposite myEntityFactory = new BOEntityComposite(this);
            myEntityFactory.init(entityXml);
            
            // Get all the fields remaps
            List<BOMapFieldLabel> remapsField = getMappedFields(entityXml); 
            //IEntity entity = new BOEntityReadWrite();
            IEntity entity = getEntityPlugin(db);
            entity.setGroup(group);
            entity.setName(entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE));
            entity.setBoType(BOEntityType.boReadOnly);
            String myQuery =  myEntityFactory.getQuery();
            db.getMetadataFromDB(entity, myQuery, remapsField);
            entity.setQuery(myQuery);
            entity.setDB(db);
            getLog().info("Composite Entity SQL Query :" + myQuery);
            
            // Label assignment
            String label = (entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE) == null ? "" : entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE));
            label = (!label.isEmpty() ? label : entity.getName());
            entity.setLabel(label);

            // Set and add BO
            cacheEntities.add(entity);
            if (entity.isInitialized())
                getLog().info("Composite Entity " + EntityName + " Added successfully");
            else
                getLog().severe("Composite Entity " + EntityName + " has not been Added successfully !!!");
        }
    }
    
    /**
     * Initialize all the myEntities
     * @param FileConfigEntity
     * @return 
     */
    public boolean init(String FileConfigEntity) {
        try {
            org.jdom2.Document document;
            getLog().info("Open Global Entity configuration file: " + FileConfigEntity);
            document = JOY.OPEN_XML(FileConfigEntity);
            Element root = document.getRootElement();

            // attach initalization queries if any here
            List<Element> queryinits =  root.getChild(C.ENTITIES_JOY_QUERY_INIT).getChildren(); 
            List<JoyInitQuery> queries = new ArrayList();
            for (Element query : queryinits) {
                queries.add(new JoyInitQuery(query.getName().toUpperCase(), query.getText()));
            }
            
            // dbConnection to the DB, get the main Datasource name first
            connectionDetail = new JoyConnectionDetail();
            connectionDetail.init(root.getChild(C.ENTITIES_JOY_DATASOURCE_TAG).getText(),
                                                    root.getChild(C.ENTITIES_JOY_JDBC_USER).getText(), 
                                                    root.getChild(C.ENTITIES_JOY_JDBC_PWD).getText(),
                                                    root.getChild(C.ENTITIES_JOY_JDBC_URL).getText(),
                                                    root.getChild(C.ENTITIES_JOY_JDBC_DRIVER).getText(),
                                                    queries);
            this.dbConnection = connectionDetail.getDB();
            
            // Gather tables, views and queries from all the configuration files
            Element fileList = root.getChild(C.ENTITIES_JOY_FILES);
            if (fileList == null) return true;
            List eltfiles = fileList.getChildren(C.ENTITIES_JOY_FILE);
            
            // Collect plugin list
            List<Element> pluginList = root.getChild("joy-db-plugins").getChildren("joy-db-plugin");
            for (Element plugin : pluginList) {
                this.plugins.add(new BOPlugin(plugin.getAttributeValue("name"), plugin.getText()));
            }
            
            // Initialize the ENTITIES by loading the different files
            Iterator ilist = eltfiles.iterator();
            while(ilist.hasNext()) {   // parcours toutes les queries
                Element fileRoot = (Element)ilist.next();
                String filename = fileRoot.getText();
                String group = fileRoot.getAttributeValue("group");
                getLog().info("Open Entity configuration file: " + filename + " for the group: " + group);
                Element rootfile = JOY.OPEN_XML(filename).getRootElement();
                // Get the Tables myEntities only
                initTables(group, rootfile, this.dbConnection);
                // Get the Custom Query myEntities only
                initQueries(group, rootfile, this.dbConnection);
                // Build the composites queries
                initComposites(group, rootfile, this.dbConnection);
            }
            
            initialized = true;
            getLog().info("Entities and Queries have been configured.");
            
        } catch (Exception e) {
            getLog().severe(e.toString());
            initialized = false;
            return false;
        }

        return true;
    }
    
    public void End() {
        if (poolManagementActive) {
            getLog().info("Close Connection [" + dbConnection + "]");
            if (dbConnection != null)
                dbConnection.end();
        }
    }
    
    public void setDB(JoyDBProvider db) {
        dbConnection = db;
    }
    
    public JoyDBProvider getDB() {
        return dbConnection;
    }

    public void closeResultSet(ResultSet rs) {
        getDB().closeResultSet(rs);
    }
}
