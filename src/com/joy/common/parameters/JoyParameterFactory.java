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
package com.joy.common.parameters;

import com.joy.C;
import com.joy.JOY;
import com.joy.common.parameters.JoyParameter.ParameterType;
import com.joy.common.joyClassTemplate;
import com.joy.etl.MappingSignature;
import com.joy.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom2.Element;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyParameterFactory extends joyClassTemplate {
    private String applicationFolder;
    private boolean initialized;
    private String apiStartPath;
    private String bundledMessageFile;
    private String defaultDateFormat;
    private boolean noLogin;
    private String version;
    private List<String> entitiesConfigList;
    private List<MappingSignature> mappingConfigList;
    private int sessionTimeoutMin;
    private List<JoyParameter> applicationParameters;
    private String defaultLocalCountry;
    private String defaultLocalLanguage;
    private String applicationName;

    public boolean isNoLogin() {
        return noLogin;
    }
    
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
        noLogin = false;
        apiStartPath="";
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
    
    public String getAPIStartPath() {
        return apiStartPath;
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
            doc = JOY.OPEN_XML(xmlFileName);
            racine = doc.getRootElement();
            
            getLog().fine("Get the fixed Joy framework parameters");
            
            // Get the Joy mandatory PARAMETERS
            apiStartPath = racine.getChildText(C.PARAMETERS_TAG_API_START_PATH);
            getLog().fine("Default API URL start path: " + apiStartPath);
            
            noLogin =  racine.getChildText(C.PARAMETERS_NO_LOGIN).equalsIgnoreCase(C.YES);
            getLog().fine("No login: " + racine.getChildText(C.PARAMETERS_NO_LOGIN));
            
            bundledMessageFile = racine.getChildText(C.PARAMETERS_TAG_BUNDLEMSG);
            getLog().fine("Bundled files: " + bundledMessageFile);
            
            defaultDateFormat = racine.getChildText(C.PARAMETERS_TAG_DATEFORMAT);
            getLog().fine("Date Format: " + defaultDateFormat);
            
            version = racine.getChildText(C.PARAMETERS_TAG_VERSION);
            getLog().fine("Version: " + version);
            
            applicationName = racine.getChildText(C.PARAMETERS_TAG_APPNAME);
            getLog().fine("Application name: " + applicationName);
            
            defaultLocalCountry = racine.getChildText(C.PARAMETERS_TAG_LOCALE);
            getLog().fine("Version: " + defaultLocalCountry);
            
            defaultLocalLanguage = racine.getChildText(C.PARAMETERS_TAG_LOCALE_LANG);
            getLog().fine("Locale Language: " + defaultLocalLanguage);
            
            try {
                sessionTimeoutMin = Integer.valueOf(racine.getChildText(C.PARAMETERS_TAG_SESSION_TIMEOUT));
            } catch (Exception e) {
                sessionTimeoutMin = 10;
            }
            getLog().fine("Session Timeout: " + sessionTimeoutMin + " Minute(s)");
            
            // Get all the ENTITIES
            List entities = racine.getChild(C.PARAMETERS_TAG_ENTITIES).getChildren(C.PARAMETERS_TAG_ENTITY);
            if (entities != null) {
                Iterator entityIt = entities.iterator();
                while(entityIt.hasNext()) {
                    Element ent = (Element)entityIt.next();
                    entitiesConfigList.add(ent.getText());
                    getLog().fine("Add entity " + ent.getText() + " in the Joy scope");
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
                    getLog().fine("Add mapping " + signature.getName() + " which refers to " + signature.getConfigFile() + "  in the Joy scope");
                }
            }
            
            // Optional Application PARAMETERS
            getLog().fine("Collect all the Application parameters");
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
            getLog().severe( "ERROR : Exception=" + ex);
        }
        return initialized;
    }

    public JSONObject getJson() {
        JSONObject paramsObj = new JSONObject();
        
        // fixed & mandatory parameters
        paramsObj.put("appname", this.getApplicationName());
        paramsObj.put("country", this.getDefaultLocalCountry());
        paramsObj.put("lang", this.getDefaultLocalLanguage());
        paramsObj.put("dateformat", this.getJoyDefaultDateFormat());
        paramsObj.put("api", this.getAPIStartPath());
        paramsObj.put("version", this.getVersion());
        
        // Other parameters
        for (JoyParameter param : this.applicationParameters) {
            paramsObj.put(param.getName(), param.getValue());
        }
 
        return paramsObj;
    }
}
