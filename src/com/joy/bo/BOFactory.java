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

import com.joy.bo.registry.BORegistry;
import com.joy.C;
import com.joy.JOY;
import com.joy.bo.init.BOInitRecord;
import com.joy.bo.registry.BOEntityType;
import com.joy.bo.registry.BORegistryEntry;
import com.joy.common.JoyClassTemplate;
import com.joy.providers.JoyConnectionDetail;
import com.joy.providers.JoyDBProvider;
import com.joy.providers.JoyInitQuery;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.jdom2.Element;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr) 
 */
public class BOFactory extends JoyClassTemplate {

    /**
     * DB Connection
     */
    protected JoyDBProvider dbConnection;

    /**
     * Connection details (not the connection but all the infos to connect)
     */
    protected JoyConnectionDetail connectionDetail;

    /**
     * Factory initialized ?
     */
    protected boolean initialized;

    /**
     * Entities in the cache
     */
    protected List<IEntity> cacheEntities;
    
    private final List<BOPlugin> plugins;
    
    private final BORegistry registry;
    
    /**
     * returns all the cached entities
     * @return
     */
    public List<IEntity> getCachedEntities() {
        return this.cacheEntities;
    }

    /**
     * return the number of entities in the cache
     * @return
     */
    public int cacheSize() {
        return cacheEntities.size();
    }
    
    /**
     * Check if an entity is already cached or not
     * @param Name Entity name
     * @return true if entity in cache (already loaded)
     */
    public boolean isEntityInCache(String Name) {
        // search in the cache first
        for (IEntity entity : this.cacheEntities) 
            if (entity.getLabel().equalsIgnoreCase(Name) || entity.getName().equalsIgnoreCase(Name))
                return true;
        // entity not loaded in the cache, load a new one
        return false;
    }    
    
    /**
     * Get and return the requested entity
     * @param Name Entity name
     * @return IEntity
     */
    public IEntity getEntity(String Name) {
        // search in the cache first
        for (IEntity entity : this.cacheEntities) 
            if (entity.getLabel().equalsIgnoreCase(Name) || entity.getName().equalsIgnoreCase(Name))
                return entity;
        // entity not loaded in the cache, load a new one
        return loadEntityFromRegistry(Name);
    }
    
    /**
     * Get and load entity from registry infos
     * @param name Entity name
     * @return IEntity
     */
    private IEntity loadEntityFromRegistry(String name) {
        try {
            BORegistryEntry entityReg = registry.getRegistryEntry(name);
            if (entityReg != null) {
                return cacheEntityFromRegistry(entityReg);
            }
        } catch (Exception e) {
            this.getLog().severe(e.toString());
        }
        return null;
    }
    
    /**
     * return true if the Factory has been successfully initialized
     * @return 
     */
    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * factory initialization/constructor
     */
    public BOFactory() {
        this.cacheEntities = new ArrayList();
        this.plugins = new ArrayList();
        this.registry = new BORegistry();
    }
    
    /**
     * Build a table with all the field technical name & label mappings
     * @param entityElement
     * @return 
     */
    private List<BOMapFieldLabel> getMappedFields(List fieldsRemap) {
        Iterator iremaps = fieldsRemap.iterator();
        List<BOMapFieldLabel> retValue = new ArrayList();
        
        while(iremaps.hasNext()) {   // get all the fields remaps
            Element field = (Element)iremaps.next();
            retValue.add(new BOMapFieldLabel(field.getAttributeValue("name"), field.getText()));
        }
        return retValue;
    }
    
    /**
     * Initialize an table entity
     * @param entityXml
     * @return IEntity
     */
    private IEntity initEntityTable(Element entityXml) {
        try {
            // Get all the fields remaps
            List<BOMapFieldLabel> remapsField = getMappedFields(entityXml.getChildren(C.ENTITIES_JOY_FIELDLABEL_TAG)); 
            IEntity entity = getEntityPlugin(getConnection());
            entity.setName(entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE));
            entity.setBoType(BOEntityRWType.boReadWrite);
            
            entity.setDB(getConnection());
            getConnection().getMetadataFromDB(entity, null, remapsField); 
            
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
            
            return entity;
        } catch (Exception e) {
            this.getLog().severe(e.toString());
            return null;
        }
    }

    /**
     * Initialize an Query entity
     * @param db
     * @param entityXml
     * @return 
     */
    private IEntity initEntityQuery(Element entityXml) {

        try {
            String queryContent = "";
            
            // get the DB engine specific query if exists
            Element childDBSpecific = entityXml.getChild(getConnection().getDBProvider().toUpperCase());
            if (childDBSpecific != null) 
                queryContent = childDBSpecific.getText().replace("\n", "");
            else
                queryContent = entityXml.getText().replace("\n", "");

            // get all the fields remaps
            List<BOMapFieldLabel> remapsField = getMappedFields(entityXml.getChildren(C.ENTITIES_JOY_FIELDLABEL_TAG)); 
            IEntity entity = getEntityPlugin(getConnection());
            entity.setName(entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE));
            entity.setBoType(BOEntityRWType.boReadOnly);
            entity.setQuery(queryContent);
            entity.setDB(getConnection());
            getConnection().getMetadataFromDB(entity, queryContent, remapsField);
            
            // Label assignment
            String label = (entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE) == null ? "" : entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE));
            label = (!label.isEmpty() ? label : entity.getName());
            entity.setLabel(label);
            
            return entity;
        } catch (Exception e) {
            this.getLog().severe(e.toString());
            return null;
        }
    }    

    /**
     * Initialize an Query entity
     * @param entityXml
     * @return 
     */
    private IEntity initEntityComposite(Element entityXml) {

        try {
            // Build the Composite query
            BOEntityComposite myEntityFactory = new BOEntityComposite(this);
            myEntityFactory.init(entityXml);
            
            // Get all the fields remaps
            List<BOMapFieldLabel> remapsField = getMappedFields(entityXml.getChildren(C.ENTITIES_JOY_FIELDLABEL_TAG)); 
            //IEntity entity = new BOEntityReadWrite();
            IEntity entity = getEntityPlugin(getConnection());
            entity.setName(entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE));
            entity.setBoType(BOEntityRWType.boReadOnly);
            String myQuery =  myEntityFactory.getQuery();
            getConnection().getMetadataFromDB(entity, myQuery, remapsField);
            entity.setQuery(myQuery);
            entity.setDB(getConnection());
            getLog().info("Composite Entity SQL Query :" + myQuery);

            // Label assignment
            String label = (entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE) == null ? "" : entityXml.getAttributeValue(C.ENTITIES_LABEL_ATTRIBUTE));
            label = (!label.isEmpty() ? label : entity.getName());
            entity.setLabel(label);
            
            return entity;
        } catch (Exception e) {
            this.getLog().severe(e.toString());
            return null;
        }
    }        
    
    /**
     * Load and cache the entity
     * @param regEntry       Registry entry
     * @param db             DB connection   
     */
    private IEntity cacheEntityFromRegistry(BORegistryEntry regEntry) {

        try {
            if (isEntityInCache(regEntry.getName()))
                return this.getEntity(regEntry.getName());
            
            getLog().log(Level.FINE, "Add new Entity {0}", regEntry.getName());
            
            // Retrieve the xml configuration
            Element entityXml = getXmlEntityConfig(regEntry);
            
            // Check the dependencies first ...
            cacheAllDependendies(regEntry, entityXml);
            
            // Cache the entity
            if (entityXml != null) {
                IEntity entity = null;
                switch (regEntry.getEntityType()) {
                    case boTable : entity = initEntityTable(entityXml); break;
                    case boQuery : entity = initEntityQuery(entityXml); break;
                    case boComposite : entity = initEntityComposite(entityXml); break;
                }
                if (entity != null) {
                    cacheEntities.add(entity);
                    if (entity.isInitialized())
                        getLog().log(Level.FINE, "Entity {0} Added successfully", entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE));
                    else
                        getLog().log(Level.SEVERE, "Entity {0} has not been Added successfully !!!", entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE));
                    return entity;
                }
            }
            
        } catch (Exception e) {
            this.getLog().severe(e.toString());
        }
        return null;
    }    
    
    /**
     * recursively cache all the dependencies of the entity
     * @param regEntry
     * @param entityXml 
     */
    private void cacheAllDependendies(BORegistryEntry regEntry, 
                                      Element entityXml) {
        if (regEntry.getEntityType() == BOEntityType.boComposite) {
            try {
                // 1st level <joy-entity>
                List<Element> entity1stLevels = entityXml.getChildren(C.ENTITIES_JOY_ENTITY_TAG);
                for (Element entity1st : entity1stLevels) {
                    BORegistryEntry entity1stReg = registry.getRegistryEntry(entity1st.getText());
                    this.cacheEntityFromRegistry(entity1stReg);
                }
            } catch (Exception e) {
                getLog().log(Level.WARNING, "Error while caching main entity dependency for {0}", regEntry.getName());
            }
            
            try {
                // 2nd level (after <joy-join>
                List<Element> entity2ndLevels = entityXml.getChildren(C.ENTITIES_COMPOSITE_JOIN);
                for (Element entity2nd : entity2ndLevels) {
                    Element entity2ndElt = entity2nd.getChild(C.ENTITIES_JOY_ENTITY_TAG);
                    this.cacheEntityFromRegistry(registry.getRegistryEntry(entity2ndElt.getText()));
                }
            } catch (Exception e) {
                getLog().log(Level.WARNING, "Error while caching joined entity dependency for {0}", regEntry.getName());
            }
        }
    }
    
    /**
     * Retrieve the xml configuration of the entity into its configuration file
     * @param regEntry
     * @return 
     */
    private Element getXmlEntityConfig(BORegistryEntry regEntry) {
        
        try {
            org.jdom2.Document document;
            document = JOY.OPEN_XML(regEntry.getEntityFile());
            
            List<Element> entities = document.getRootElement().getChildren(C.ENTITIES_JOY_ENTITY_TAG);
            for (Element entityXml : entities) 
                if (entityXml.getAttributeValue(C.ENTITIES_NAME_ATTRIBUTE).equalsIgnoreCase(regEntry.getName())) 
                    return entityXml;
            this.getLog().log(Level.WARNING, "Entity {0} not retrieved, it may have an entity registry configuration error.", regEntry.getName());
            
        } catch (Exception e) {
            this.getLog().severe(e.toString());
        }
        return null;
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
     * Initialize the entity factory
     * @param FileConfigEntity
     * @return 
     */
    public boolean init(String FileConfigEntity) {
        try {
            org.jdom2.Document document;
            getLog().log(Level.INFO, "Open Global Entity configuration file: {0}", FileConfigEntity);
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
            this.setConnection(connectionDetail.getDB());
            
            // Collect plugin list
            List<Element> pluginList = root.getChild("joy-db-plugins").getChildren("joy-db-plugin");
            for (Element plugin : pluginList) {
                this.plugins.add(new BOPlugin(plugin.getAttributeValue("name"), plugin.getText()));
            }
            
            // load registry
            registry.loadXML(FileConfigEntity);
            
            initialized = true;
            getLog().fine("BO Factory has been initialized.");
            
        } catch (Exception e) {
            getLog().severe(e.toString());
            initialized = false;
            return false;
        }

        return true;
    }
    
    /**
     * Close the connection
     */
    public void end() {
        getLog().log(Level.INFO, "Close Connection [{0}]", dbConnection);
        if (getConnection() != null)
            getConnection().end();
    }
    
    /**
     * Set the DB connection
     * @param db 
     */
    public void setConnection(JoyDBProvider db) {
        dbConnection = db;
    }
    
    /**
     * returns the DB connection
     * @return 
     */
    public JoyDBProvider getConnection() {
        return dbConnection;
    }

    /**
     * Close a recordset
     * @param rs  recordset
     */
    public void closeResultSet(ResultSet rs) {
        getConnection().closeResultSet(rs);
    }
    
    /**
     * Cache all the entities in the registry
     */
    public void cacheAllRegistry() {
        for (BORegistryEntry regEntry : registry.getAllEntries()) {
            cacheEntityFromRegistry(regEntry);
        }
    }
}
