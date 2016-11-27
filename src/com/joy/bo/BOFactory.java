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
import com.joy.Joy;
import com.joy.bo.init.BOInitRecord;
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
 * @author Benoit CAYLA (benoit@famillecayla.fr) CAYLA
 */
public class BOFactory implements Cloneable {
    protected JoyDBProvider dbConnection;
    protected JoyConnectionDetail connectionDetail;
    protected boolean initialized;
    protected List<IEntity> entities;
    private boolean poolManagementActive;
    private List<BOPlugin> plugins;
    
    @Override
    public Object clone()  {
        BOFactory myClone = null;
        try {
            myClone = (BOFactory)super.clone();
            if (this.poolManagementActive) 
                myClone.setDB(this.connectionDetail.getDB());
            else {
                if (this.dbConnection == null)
                    this.dbConnection = connectionDetail.getDB();
                myClone.setDB(dbConnection);
            }
            
            for (IEntity entity : this.entities) {
                entity.setDB(myClone.getDB());
            }
            
            Joy.LOG().debug("Clone new BOEntities with new connection [" + myClone.dbConnection + "]");
            
        } catch(CloneNotSupportedException cnse) {
            Joy.LOG().error(cnse);
        }
        return myClone;
    }
    
    public List<IEntity> getAll() {
        return this.entities;
    }

    /**
     * Get and return the requested entity
     * @param Name Entity name
     * @return BOEntityReadWrite, BOView or BOEntityCustom
     */
    private IEntity Entity(String Name) {
        //Joy.LOG().debug("Get Entity: " + name);
        for (IEntity entity : this.entities) 
            if (entity.getLabel().equalsIgnoreCase(Name) || entity.getName().equalsIgnoreCase(Name)) {
                IEntity myClone = (IEntity)entity.clone();
                myClone.setDB(this.getDB());
                myClone.reset();
                Joy.LOG().debug("Return Entity [" + Name + "] - " + myClone + " with connection: " + myClone.getDB());
                return myClone;
            }
        return null;
    }
    
    public IEntity getEntity(String Name) {
        return Entity(Name);
    }
    
    public boolean isInitialized() {
        return this.initialized;
    }

    public BOFactory() {
        this.entities = new ArrayList();
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
    private void initTables(Element root, 
                            JoyDBProvider db) {
        List tables = root.getChildren(C.ENTITIES_JOY_TABLE_TAG);
        Iterator i1 = tables.iterator();
        
        while(i1.hasNext()) {   // parcours toutes les entités/tables
            Element entityXml = (Element)i1.next();

            // Get all the fields remaps
            List<BOMapFieldLabel> remapsField = getMappedFields(entityXml); 
            IEntity entity = getEntityPlugin(db);
            entity.setName(entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE));
            entity.setBoType(BOEntityType.boReadWrite);
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
            entities.add(entity);
            if (entity.isInitialized())
                Joy.LOG().debug("Table " + entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE) + " Added successfully");
            else
                Joy.LOG().error("Table " + entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE) + " has not been Added successfully !!!");
        }
    }
    
    /**
     * Get the SQL myEntities
     * @param entities
     * @param root 
     */
    private void initQueries(Element root, JoyDBProvider db) {
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
            entity.setName(entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE));
            entity.setBoType(BOEntityType.boReadOnly);
            entity.setQuery(queryContent);
            db.getMetadataFromDB(entity, queryContent, remapsField);
            
            // Label assignment
            String label = (entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE) == null ? "" : entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE));
            label = (!label.isEmpty() ? label : entity.getName());
            entity.setLabel(label);

            // Set and add BO
            entities.add(entity);
            if (entity.isInitialized())
                Joy.LOG().debug("Custom Entity " + entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE) + " Added successfully");
            else
                Joy.LOG().error("Custom Entity " + entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE) + " has not been Added successfully !!!");
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
                    Joy.LOG().error(ex);
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
    private void initComposites(Element root, JoyDBProvider db) {
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
            entity.setName(entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE));
            entity.setBoType(BOEntityType.boReadOnly);
            String myQuery =  myEntityFactory.getQuery();
            db.getMetadataFromDB(entity, myQuery, remapsField);
            entity.setQuery(myQuery);
            Joy.LOG().debug("Composite Entity SQL Query :" + myQuery);
            
            // Label assignment
            String label = (entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE) == null ? "" : entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE));
            label = (!label.isEmpty() ? label : entity.getName());
            entity.setLabel(label);

            // Set and add BO
            entities.add(entity);
            if (entity.isInitialized())
                Joy.LOG().debug("Composite Entity " + EntityName + " Added successfully");
            else
                Joy.LOG().error("Composite Entity " + EntityName + " has not been Added successfully !!!");
        }
    }
    
    /**
     * Initialize all the myEntities
     * @param FileConfigEntity
     * @return 
     */
    public boolean init(String FileConfigEntity) {
        JoyDBProvider DbConn = null;
        try {
            org.jdom2.Document document;
            Joy.LOG().info("Open Global Entity configuration file: " + FileConfigEntity);
            document = Joy.OPEN_XML(FileConfigEntity);
            Element root = document.getRootElement();

            // attach initalization queries if any here
            //String myDB = connectionDetail.getDB().getDBProvider();
            List<Element> queryinits =  root.getChild(C.ENTITIES_JOY_QUERY_INIT).getChildren(); // .getChildren(myDB.toUpperCase());
            List<JoyInitQuery> queries = new ArrayList();
            for (Element query : queryinits) {
                //DbConn.executeSQL(query.getText());
                queries.add(new JoyInitQuery(query.getName().toUpperCase(), query.getText()));
            }
            
            // dbConnection to the DB, get the main Datasource name first
            connectionDetail = new JoyConnectionDetail(root.getChild(C.ENTITIES_JOY_DATASOURCE_TAG).getText(),
                                                root.getChild(C.ENTITIES_JOY_JDBC_USER).getText(), 
                                                root.getChild(C.ENTITIES_JOY_JDBC_PWD).getText(),
                                                root.getChild(C.ENTITIES_JOY_JDBC_URL).getText(),
                                                root.getChild(C.ENTITIES_JOY_JDBC_DRIVER).getText(),
                                                queries);
            DbConn = connectionDetail.getDB();
            
            // Gather tables, views and queries from all the configuration files
            Element fileList = root.getChild(C.ENTITIES_JOY_FILES);
            if (fileList == null) return true;
            List eltfiles = fileList.getChildren(C.ENTITIES_JOY_FILE);
            
            // Collect plugin list
            List<Element> pluginList = root.getChild("joy-db-plugins").getChildren("joy-db-plugin");
            for (Element plugin : pluginList) {
                this.plugins.add(new BOPlugin(plugin.getAttributeValue("name"), plugin.getText()));
            }
            
            // Initialize the ENTITIES
            Iterator ilist = eltfiles.iterator();
            while(ilist.hasNext()) {   // parcours toutes les queries
                Element fileRoot = (Element)ilist.next();
                String filename = fileRoot.getText();
                Joy.LOG().debug("Open Entity configuration file: " + filename);
                Element rootfile = Joy.OPEN_XML(filename).getRootElement();
                // Get the Tables myEntities only
                initTables(rootfile, DbConn);
                // Get the Custom Query myEntities only
                initQueries(rootfile, DbConn);
                // Build the composites queries
                initComposites(rootfile, DbConn);
            }
            
            DbConn.end();
            
            initialized = true;
            Joy.LOG().info("Entities and Queries have been configured.");
            
        } catch (Exception e) {
            Joy.LOG().error(e);
            initialized = false;
            return false;
        }
        if (DbConn != null) DbConn.end();
        return true;
    }
    
    public void End() {
        if (poolManagementActive) {
            Joy.LOG().debug("Close Connection [" + dbConnection + "]");
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
