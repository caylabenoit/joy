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
package com.joy.common;

import com.joy.C;
import com.joy.Joy;
import com.joy.common.JoyParameter.ParameterType;
import com.joy.etl.MappingSignature;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom2.Element;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyParameterFactory {
    private String applicationFolder;
    private boolean initialized;
    private String defaultURLPattern;
    private String bundledMessageFile;
    private String defaultDateFormat;
    private String version;
    private List<String> entitiesConfigList;
    private List<MappingSignature> mappingConfigList;
    private int sessionTimeoutMin;
    private List<JoyParameter> applicationParameters;
    private String defaultLocalCountry;
    private String defaultLocalLanguage;
    private String applicationName;
    
    public String getApplicationName() {
        return applicationName;
    }
    
    public String getDefaultLocalCountry() {
        return (defaultLocalCountry.isEmpty() ? "US" : defaultLocalCountry);
    }

    public String getDefaultLocalLanguage() {
        return (defaultLocalLanguage.isEmpty() ? "en" : defaultLocalLanguage);
    }
    
    public String getConfigFolder() {
        return (applicationFolder.isEmpty() ? "" : applicationFolder + "/" + C.CONFIG_DIR_FROM_APPDIR + "/");
    }
    
    public String getLanguageFolder() {
        return (applicationFolder.isEmpty() ? "" : applicationFolder + "/" + C.LANG_DIR_FROM_APPDIR + "/");
    }
    
    public String getApplicationFolder() {
        return applicationFolder;
    }

    public void setApplicationFolder(String m_ApplicationFolder) {
        this.applicationFolder = m_ApplicationFolder;
    }
    
    public String getMapping(String name) {
        for (MappingSignature sig :  mappingConfigList) 
            if (sig.getName().equalsIgnoreCase(name))
                return sig.getConfigFile();
        return "";
    }
    
    public int getSessionItemout() {
        return sessionTimeoutMin;
    }
    
    public JoyParameter getParameter(String Name) {
        for (JoyParameter param: applicationParameters) 
            if (param.getName().equalsIgnoreCase(Name))
                return param;
        return null;
    }

    public JoyParameterFactory() {
        initialized = false;
        defaultURLPattern="";
        applicationParameters = new ArrayList();
        entitiesConfigList = new ArrayList();
        sessionTimeoutMin = 10;
        mappingConfigList = new ArrayList();
    }

    public boolean isInitialized() {
        return initialized;
    }
    
    public String getJoyDefaultDateFormat() {
        return defaultDateFormat;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getJoyBundledMessageFile() {
        return bundledMessageFile;
    }
    
    public String getJoyDefaultURLPattern() {
        return defaultURLPattern;
    }
    
    public String getJoyDefaultActionName() {
        // just remove the first /
        if (defaultURLPattern.substring(0, 1).equalsIgnoreCase("/"))
            return defaultURLPattern.substring(1, defaultURLPattern.length());
        else
            return defaultURLPattern;
    }
    
    
    public List<String> getEntities() {
        return entitiesConfigList;
    }
    
    /**
     * Initialize all the Joy PARAMETERS here
     * @param xmlFileName pathname of the joy-PARAMETERS.xml file
     * @return true if all have been initialized successfully
     */
    public boolean init(String xmlFileName) {
        initialized = false;
        Element racine = null;
        org.jdom2.Document doc;
        
        try {
            doc = Joy.OPEN_XML(xmlFileName);
            racine = doc.getRootElement();
            
            Joy.SYSTEM_LOG("Get the fixed Joy framework parameters");
            // Get the Joy mandatory PARAMETERS
            defaultURLPattern = racine.getChildText(C.PARAMETERS_TAG_URLPATTERN);
            Joy.SYSTEM_LOG("Default URL pattern: " + defaultURLPattern);
            
            bundledMessageFile = racine.getChildText(C.PARAMETERS_TAG_BUNDLEMSG);
            Joy.SYSTEM_LOG("Bundled files: " + bundledMessageFile);
            
            defaultDateFormat = racine.getChildText(C.PARAMETERS_TAG_DATEFORMAT);
            Joy.SYSTEM_LOG("Date Format: " + defaultDateFormat);
            
            version = racine.getChildText(C.PARAMETERS_TAG_VERSION);
            Joy.SYSTEM_LOG("Version: " + version);
            
            applicationName = racine.getChildText(C.PARAMETERS_TAG_APPNAME);
            Joy.SYSTEM_LOG("Application name: " + applicationName);
            
            defaultLocalCountry = racine.getChildText(C.PARAMETERS_TAG_LOCALE);
            Joy.SYSTEM_LOG("Version: " + defaultLocalCountry);
            
            defaultLocalLanguage = racine.getChildText(C.PARAMETERS_TAG_LOCALE_LANG);
            Joy.SYSTEM_LOG("Locale Language: " + defaultLocalLanguage);
            
            try {
                sessionTimeoutMin = Integer.valueOf(racine.getChildText(C.PARAMETERS_TAG_SESSION_TIMEOUT));
            } catch (Exception e) {
                sessionTimeoutMin = 10;
            }
            Joy.SYSTEM_LOG("Session Timeout: " + sessionTimeoutMin + " Minute(s)");
            
            // Get all the ENTITIES
            List entities = racine.getChild(C.PARAMETERS_TAG_ENTITIES).getChildren(C.PARAMETERS_TAG_ENTITY);
            if (entities != null) {
                Iterator entityIt = entities.iterator();
                while(entityIt.hasNext()) {
                    Element ent = (Element)entityIt.next();
                    entitiesConfigList.add(ent.getText());
                    Joy.SYSTEM_LOG("Add entity " + ent.getText() + " in the Joy scope");
                }
            }
            
            // Get all the Mappings
            List mappings = racine.getChild(C.PARAMETERS_TAG_MAPPINGS).getChildren(C.PARAMETERS_TAG_MAPPING);
            if (mappings != null) {
                Iterator mappingIt = mappings.iterator();
                while(mappingIt.hasNext()) {
                    Element map = (Element)mappingIt.next();
                    MappingSignature signature = new MappingSignature(map.getAttribute(C.JOTYMAP_TAGATTR_NAME).getValue(), 
                                                            (applicationFolder.isEmpty() ? 
                                                             map.getText() : 
                                                             this.getConfigFolder() +  map.getText()));
                    mappingConfigList.add(signature);
                    Joy.SYSTEM_LOG("Add mapping " + signature.getName() + " which refers to " + signature.getConfigFile() + "  in the Joy scope");
                }
            }
            
            // Optional Application PARAMETERS
            Joy.SYSTEM_LOG("Collect all the Application parameters");
            List parameters = racine.getChildren(C.PARAMETERS_SINGLEPARAM);
            if (parameters != null) {
                Iterator paramIt = parameters.iterator();
                while(paramIt.hasNext()) {
                    Element param = (Element)paramIt.next();
                    String pType = param.getAttributeValue(C.PARAMETERS_SP_ATTR_TYPE);
                    JoyParameter myParam = null;
                    if (pType.equalsIgnoreCase(C.PARAMETERS_SP_ATTR_VALUE)) { 
                        // ajout d'un parametre simple
                        myParam = new JoyParameter(param.getAttributeValue(C.PARAMETERS_SP_ATTR_NAME), param.getText(), ParameterType.VALUE);
                        applicationParameters.add(myParam);
                        
                    } else if (pType.equalsIgnoreCase(C.PARAMETERS_SP_ATTR_LIST)) {
                        // ajout d'un parametre liste
                        List paramValueList = param.getChildren(C.PARAMETERS_SP_PARAMVAL);
                        if (paramValueList != null) {
                            List myItemList = new ArrayList();
                            Iterator paramValueIt = paramValueList.iterator();
                            while(paramValueIt.hasNext()) { // parcours les éléments de la liste
                                Element item = (Element)paramValueIt.next();
                                myItemList.add(new JoyParameter(item.getAttributeValue(C.PARAMETERS_SP_ATTR_NAME), item.getText(), ParameterType.VALUE));
                            }
                            myParam = new JoyParameter(param.getAttributeValue(C.PARAMETERS_SP_ATTR_NAME), myItemList, ParameterType.LIST);
                        } else {
                            myParam = new JoyParameter(param.getAttributeValue(C.PARAMETERS_SP_ATTR_NAME), null, ParameterType.LIST);
                        }
                        applicationParameters.add(myParam);
                    }
                }
            }
            initialized = true;
            
        } catch (Exception ex) {
            Joy.SYSTEM_LOG( "ERROR : Exception=" + ex);
        }
        return initialized;
    }
}
