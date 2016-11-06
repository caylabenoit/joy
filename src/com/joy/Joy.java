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
package com.joy;

import com.joy.bo.BOFactory;
import com.joy.common.JoyParameterFactory;
import com.joy.common.JoyReadStream;
import com.joy.log.JoyLogInternalProvider;
import com.joy.mvc.Action;
import com.joy.providers.JoyConfigfileProvider;
import com.joy.tasks.JoyTaskManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import org.apache.commons.io.IOUtils;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import com.joy.log.Ilog;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr) CAYLA
 */
public class Joy {

    private static JoyParameterFactory s_Params;          // Global parameters from xml file
    private static List<BOFactory> s_BOEntities;     // Data entities
    private static Ilog s_Log;            // log
    private static boolean s_joyIsInitialized = false;
    private static JoyTaskManager s_taskManager;

    public static Ilog log() {
        return s_Log;
    }

    public static BOFactory entities() {
        return s_BOEntities.get(0);
    }

    public static BOFactory entities(int index) {
        return s_BOEntities.get(index);
    }

    public static JoyParameterFactory parameters() {
        return s_Params;
    }

    public static JoyTaskManager taskManager() {
        return s_taskManager;
    }

    /**
     * Renvoit le path du répertoire WEB-INF
     *
     * @return répertoire WEB-INF
     */
    public static String getWebInfPath() {
        //className= package/subPackage/subsubPackage/../ClassName.class
        String className = JoyConfigfileProvider.class.getName().replaceAll("\\.", "/") + ".class";
        //Use the ClassLoader to find the absolute path to this file.
        URL classPath = JoyConfigfileProvider.class.getClassLoader().getResource(className);
        //create a new File and go from file to parent file to find the
        //WEB-INF directory
        File f = new File(classPath.getPath());
        while (f != null && !f.getName().equals("WEB-INF")) {
            f = f.getParentFile();
        }
        //if the root is reached without finding the WEB-INF directory
        //WEB-INF will equal null
        if (f != null) {
            return f.getPath();
        } else {
            return "";
        }
    }

    /**
     * Exécute une ligne de commande en mode synchrone
     * @param line ligne de commande
     * @return résultat de l'éxécution
     */
    public static String executeCommandLine(String line) {
        String retValue = "";

        try {
            s_Log.debug("Action : execute command line: " + line);

            if (!line.equalsIgnoreCase("")) {
                Process p = Runtime.getRuntime().exec(line);
                try {
                    JoyReadStream s1 = new JoyReadStream("stdin", p.getInputStream());
                    //ReadStream s2 = new JoyReadStream("stderr", p.getErrorStream ());
                    s1.start();
                    //s2.start ();
                    s_Log.debug("Waiting ...");
                    p.waitFor();
                    s_Log.debug("Returning -> " + s1.getCmdreturn());
                    retValue = s1.getCmdreturn();

                } catch (InterruptedException e) {
                    s_Log.error(e.toString());
                    retValue = e.toString();

                } finally {
                    if (p != null) {
                        p.destroy();
                    }
                }
            }
            s_Log.debug("End of execution !");
        } catch (IOException ex) {
            s_Log.error(ex);
            retValue = ex.toString();
        }
        return retValue;
    }

    /**
     * Convert/copy an input Stream into a string variable
     *
     * @param in input stream
     * @return String variable
     */
    public static String convertInputStreamToString(InputStream in) {
        try {
            StringWriter writer = new StringWriter();
            Charset encoding = null;
            IOUtils.copy(in, writer, encoding);
            return writer.toString();

        } catch (IOException ex) {
            return "";
        }
    }

    /**
     * Initiliaze all the Joy entities, Joy parameters, Joy connection and Joy
     * logs
     *
     * @param sce Contexte de l'application
     * @return Initialisation OK
     */
    public static boolean init(ServletContext sce) {
        boolean logInit;
        boolean entityInit = false;
        boolean logParam;

        if (!s_joyIsInitialized) {
            s_Params = new JoyParameterFactory();

            // 1 - Get the appdir parameter first
            String appdir = sce.getInitParameter(C.APPDIR_PARAMETER);
            if (appdir == null) {
                appdir = "";
            }
            s_Params.setApplicationFolder(appdir);
            System.out.println("Joy: Application directory = " + (appdir.isEmpty() ? "Not defined" : appdir));

            // 2 - Initialisation des logs
            System.out.println("Joy: Configuration folder : " + s_Params.getConfigFolder());
            String lojInternalFile = s_Params.getConfigFolder() + sce.getInitParameter("joy-log");
            System.out.println("Joy: Log configuration file : " + lojInternalFile);
            s_Log = new JoyLogInternalProvider();
            s_Log.init(lojInternalFile);
            logInit = s_Log.isInitialized();
            System.out.println("Joy: Internal Log Initialization : " + (logInit ? "OK" : "KO"));

            // 3 - Initialisation des parametres
            String paramFile = s_Params.getConfigFolder() + sce.getInitParameter("joy-parameters");
            System.out.println("Joy: Initialize joy parameters with parameter file : " + paramFile);
            logParam = s_Params.init(paramFile);
            Joy.log().info("Joy: Joy Parameters Initialization : " + (logParam ? "OK" : "KO"));

            // 4 - Initialisation des entités & DB
            s_BOEntities = new ArrayList();
            for (String entitiesName : s_Params.getEntities()) {
                BOFactory entities = new BOFactory();
                String entityFile = s_Params.getConfigFolder() + entitiesName;
                entities.init(entityFile);
                s_BOEntities.add(entities);
                entityInit = entities.isInitialized();
            }

            // 5 - Taks Manager Initalization
            s_taskManager = new JoyTaskManager();
            s_taskManager.init();

            Joy.log().info("Joy: Joy Entities Initialization : " + (entityInit ? "OK" : "KO"));

            s_joyIsInitialized = true;

            return (logInit && entityInit && logParam);
        } else {
            return s_joyIsInitialized;
        }
    }

    /**
     * réduit une chaine a la taille et rajoute ...
     *
     * @param s Chaine à réduire
     * @param size Taille max de la chaine
     * @return chaine réduire
     */
    public static String shortenString(String s, int size) {
        if (s == null) {
            return "";
        } else {
            if (s.length() >= size) {
                return s.substring(0, size - 1) + " ...";
            } else {
                return s;
            }
        }
    }

    public static String basicURL(String _object,
            String _actiontype) {
        return url(_object, _actiontype);
    }

    public static String href(String _object,
            String _actiontype,
            String _Label,
            String... args) {
        return "<A HREF='" + url(_object, _actiontype, args) + "'>" + _Label + "</A>";
    }

    /**
     * Retourne l'url avec les éléments de base Joy
     *
     * @param _object élément Object / fichier joy-actions.xml
     * @param _actiontype élément ActionType / fichier joy-actions.xml
     * @param args dynamic list of parameters
     * @return Début d'url
     */
    public static String url(String _object,
                             String _actiontype,
                             String... args) {

        String urlRet = "." + Joy.parameters().getJoyDefaultURLPattern();
        String sepNext = "&";
        String sep = "?";
        
        if (_object != null) {
            urlRet += sep + C.ACTION_TAG_OBJECT + "=" + _object;
            sep = sepNext;
        }
        if (_actiontype != null) {
            urlRet += sep + C.ACTION_TYPE_TAG + "=" + _actiontype;
            sep = sepNext;
        }
        
        int i = 0;
        while (i < args.length) {
            try {
                String paramName = URLEncoder.encode(args[i++], C.ENCODING);
                String paramValue = URLEncoder.encode(args[i++], C.ENCODING);
                urlRet += sep + paramName + "=" + paramValue;
                sep = sepNext;
            } catch (UnsupportedEncodingException ex) {
                Joy.log().error("Error while encoding and/or creating URL, " + ex);
            }
        }
        //Joy.log().debug("Returning url --> " + urlRet);
        return urlRet;
    }

    public static String T(String value) {
        return (value == null ? "" : value);
    }

    public static String T(String value, String defaultValue) {
        return (value == null ? defaultValue : value);
    }

    /**
     * Get the current Joy Action Object
     * @param request HTTP request
     * @return Action Object
     */
    public static Action currentAction(HttpServletRequest request) {
        try {
            return (Action) request.getAttribute(C.ACTION_FORM_BEAN);
        } catch (Exception e) {
            Joy.log().debug("Impossible to retrieve current action, " + e);
            return null;
        }
    }

    /**
     * Get the current Joy Action Object
     * @param jsp JSP context
     * @return Action Object
     */
    public static Action currentAction(JspContext jsp) {
        try {
            return (Action) jsp.findAttribute(C.ACTION_FORM_BEAN);
        } catch (Exception e) {
            Joy.log().debug("Impossible to retrieve current action, " + e);
            return null;
        }
    }

    /** 
     * Get the current date in the dd/MM/yyyy HH:mm:ss format
     * @return current date
     */
    public static String getCurrentDate() {
        return getCurrentDate("dd/MM/yyyy HH:mm:ss");
    }
    
    /** 
     * Get the current date in the dd/MM/yyyy HH:mm:ss format
     * @param format date format (example "dd/MM/yyyy HH:mm:ss")
     * @return current date
     */
    public static String getCurrentDate(String format) {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(cal.getTime());
    }

    /** 
     * Get the current date
     * @return current date
     */
    public static Date getDate() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }
    
    /**
     * Format a date with the giver format
     * @param myDate date to format
     * @param format format (example: "dd/MM/yyyy HH:mm:ss")
     * @return formatted date
     */
    public static String formatDate(Date myDate, String format) {
        try {
            if (myDate == null)  return "";
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(myDate);
        } catch (Exception e) {
            return "";
        } 
    }
    
    /**
     * Format a date --> string (format=dd/MM/yyyy HH:mm:ss). 
     * Take the Joy format (in parameters.xml) or by default "dd/MM/yyyy HH:mm:ss"
     * @param myDate date to format
     * @return formatted date in string
     */
    public static String formatDate(Date myDate) {
        String format = "dd/MM/yyyy HH:mm:ss";

        try {
            if (myDate == null) return "";
            if (Joy.parameters().getJoyDefaultDateFormat() != null) 
                format = Joy.parameters().getJoyDefaultDateFormat();
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(myDate);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Open a Config file (xml)
     * @param xmlFileName xml file
     * @return dom document
     */
    public static org.jdom2.Document openXMLConfig(String xmlFileName) {
        SAXBuilder sxb = new SAXBuilder();
        org.jdom2.Document document = null;

        // Default opening
        try {
            document = sxb.build(Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlFileName));
        } catch (JDOMException | IOException ex) {
            Joy.log().info("Impossible to open XML file through through current context, " + ex);
        }

        // direct open
        if (document == null) {
            try {
                File file = new File(xmlFileName);
                document = sxb.build(file);
            } catch (JDOMException | IOException ex) {
                Joy.log().error("Exception (opening direct file) =" + ex);
            }
        }

        if (document != null) {
            return document;
        } else {
            return null;
        }
    }

    public static String rgba(String color, String transparency) {
        return "rgba(" + color + "," + transparency + ")";
    }

    /**
     * read a file and put it into a String variable
     * @param filename
     * @return
     */
    public static String readFileAsString(String filename) {
        BufferedReader reader = null;
        try {
            InputStream myFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            reader = new BufferedReader(new InputStreamReader(myFile));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            System.out.println(out.toString());   //Prints the string content read from input stream
            reader.close();
            return out.toString();

        } catch (IOException ex) {
            Joy.log().error(ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Joy.log().error(ex);
            }
        }
        return "";
    }
}
