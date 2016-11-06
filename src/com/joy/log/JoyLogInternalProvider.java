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

import com.joy.C;
import com.joy.Joy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Element;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyLogInternalProvider implements Ilog {
    private boolean initialized;
    private PrintStream logStream;  // current stream
    private String currentStreamFile;
    private JoyLogDetails logDetails;
    
    private boolean activeDebug;
    private boolean activeFatal;
    private boolean activeInfo;
    private boolean activeWarning;
    private boolean activeError;

    public boolean isActiveDebug() {
        return activeDebug;
    }

    public boolean isActiveFatal() {
        return activeFatal;
    }

    public boolean isActiveInfo() {
        return activeInfo;
    }

    public boolean isActiveWarning() {
        return activeWarning;
    }

    public boolean isActiveError() {
        return activeError;
    }
    
    public JoyLogInternalProvider() {
        initialized=false;
        logDetails = new JoyLogDetails();
        currentStreamFile="";
        activeDebug = true;
        activeFatal = true;
        activeInfo = true;
        activeWarning = true;
        activeError = true;
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void info(Object message) {
        if (activeInfo)
            this.write(C.JOTYLOG_INFO, message.toString());
    }

    @Override
    public void warn(Object message) {
        if (activeWarning)
            this.write(C.JOTYLOG_WARNING, message.toString());
    }

    @Override
    public void error(Object message) {
        if (activeError)
            this.write(C.JOTYLOG_ERROR, message.toString());
    }

    @Override
    public void fatal(Object message) {
        if (activeFatal)
            this.write(C.JOTYLOG_FATAL, message.toString());
    }

    @Override
    public void debug(Object message) {
        if (activeDebug)
            this.write(C.JOTYLOG_DEBUG, message.toString());
    }

    /**
     * Initialisation from xml config file
     * @param logConfigFile
     * @return 
     */
    private void initConfig(String logConfigFile) {
        Element racine;  
        
        try {
            org.jdom2.Document doc;
            doc = Joy.openXMLConfig(logConfigFile);
            racine = doc.getRootElement();
            
            // Get the file
            Element elt = racine.getChild(C.JOTYLOG_TAG_FILENAME);
            logDetails.setFilename(elt.getText());
            // Get the date prefix
            String datePref = elt.getAttributeValue(C.JOTYLOG_TAGATTR_DATEPREFIX);
            if (datePref != null)
                logDetails.setPrefixDate(datePref.equalsIgnoreCase(C.YES));
            // Get the directory
            logDetails.setDirectory(racine.getChildText(C.JOTYLOG_TAG_DIRECTORY));
            
            // Get the log activation
            List<Element> levels = racine.getChild(C.JOTYLOG_TAG_LEVELS).getChildren(C.JOTYLOG_TAG_LEVEL);
            for (Element level : levels) {
                String lev = level.getText();
                String active = level.getAttributeValue(C.JOTYLOG_TAGATTR_ACTIVE);
                switch (lev) {
                    case C.JOTYLOG_DEBUG: this.activeDebug = (active != null ? active.equalsIgnoreCase(C.YES) : true); break;
                    case C.JOTYLOG_INFO:   this.activeInfo = (active != null ? active.equalsIgnoreCase(C.YES) : true); break;
                    case C.JOTYLOG_WARNING:  this.activeWarning = (active != null ? active.equalsIgnoreCase(C.YES) : true); break;
                    case C.JOTYLOG_ERROR:  this.activeError = (active != null ? active.equalsIgnoreCase(C.YES) : true); break;
                    case C.JOTYLOG_FATAL:  this.activeFatal = (active != null ? active.equalsIgnoreCase(C.YES) : true); break;
                }
            }
            
        } catch (Exception e) {
            Logger.getLogger(JoyLogInternalProvider.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Initialize the internal log
     * @param logConfigFile
     * @return 
     */
    @Override
    public boolean init(String logConfigFile) {
        try {
            initConfig(logConfigFile); 
            openNewStream();
            if (logStream == null)
                logStream = System.out;
            initialized = (logStream != null);
            
        } catch (Exception ex) {
            Logger.getLogger(JoyLogInternalProvider.class.getName()).log(Level.SEVERE, null, ex);
            initialized = false;
        }
        return initialized;
    }
    
    /**
     * Print the log line
     * @param level
     * @param line 
     */
    private void write(String level, String line) {
        try { 
            PrintStream myStream;
            
            Exception e = new Exception();
            String callername = ((e.getStackTrace())[2]).getClassName()  + "." + ((e.getStackTrace())[2]).getMethodName();
            String myLog = Joy.getCurrentDate() + "\t" + level + "\t[" + callername + "]\t" +  line;

            if (logStream != null) {
                if (!currentStreamFile.equalsIgnoreCase(logDetails.getFile())) 
                    openNewStream();
                logStream.println(myLog);
                logStream.flush();
            } else {
                System.out.println(myLog);
                System.out.flush();
            }
            
        } catch (Exception ex) {
            Logger.getLogger(JoyLogInternalProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Open the output stream
     * @return 
     */
    private boolean openNewStream() {
        try {
            if (logStream != null) {
                logStream.flush();
                logStream.close();
            }
            File LogFile = new File(logDetails.getCompleteFilename());
            logStream = new PrintStream(logDetails.getCompleteFilename());
            currentStreamFile = logDetails.getFile();
            return true;
            
        } catch (FileNotFoundException ex) {
            currentStreamFile = "";
            logStream = null;
            Logger.getLogger(JoyLogInternalProvider.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
