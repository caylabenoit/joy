/*
 * Copyright (C) 2016 Benoit CAYLA (benoit@famillecayla.fr) remote
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

import com.joy.common.JoyReadStream;
import com.joy.json.JSONObject;
import com.joy.api.Action;
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
import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import org.apache.commons.io.IOUtils;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import java.text.ParseException;


/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr) CAYLA
 */
public class JOY {
    private static JoyTaskManager S_TASK_MANAGER;
    
    public static void SYSTEM_LOG(String log) {
        System.out.println("JOY> " + log);
    }
    
    public static JoyTaskManager TASKS() {
        return S_TASK_MANAGER;
    }
    
     public static void INIT() {
         if (S_TASK_MANAGER == null) {
            S_TASK_MANAGER = new JoyTaskManager();
            S_TASK_MANAGER.init();
         }
     }
     
    /**
     * Renvoit le path du répertoire WEB-INF
     *
     * @return répertoire WEB-INF
     */
    public static String WEBINF_PATH() {
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
    public static String EXECUTE_CMD(String line) {
        String retValue = "";

        try {

            if (!line.equalsIgnoreCase("")) {
                Process p = Runtime.getRuntime().exec(line);
                try {
                    JoyReadStream s1 = new JoyReadStream("stdin", p.getInputStream());
                    //ReadStream s2 = new JoyReadStream("stderr", p.getErrorStream ());
                    s1.start();
                    //s2.start ();
                    p.waitFor();
                    retValue = s1.getCmdreturn();

                } catch (InterruptedException e) {
                    retValue = e.toString();

                } finally {
                    if (p != null) {
                        p.destroy();
                    }
                }
            }
        } catch (IOException ex) {
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
    public static String STREAM_TO_STRING(InputStream in) {
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
     * réduit une chaine a la taille et rajoute ...
     *
     * @param s Chaine à réduire
     * @param size Taille max de la chaine
     * @return chaine réduire
     */
    public static String SHORTEN_STRING(String s, int size) {
        String after = " ...";
        if (s == null) {
            return "";
        } else {
            if (s.length() >= size) {
                return s.substring(0, size - after.length()) + after;
            } else {
                return s;
            }
        }
    }
    
    public static String HREF(String _object,
            String _actiontype,
            String _Label,
            String... args) {
        return "<A HREF='" + URL(_object, _actiontype, args) + "'>" + _Label + "</A>";
    }

    /**
     * Retourne l'URL avec les éléments de base Joy
     *
     * @param _object élément Object / fichier joy-actions.xml
     * @param _actiontype élément ActionType / fichier joy-actions.xml
     * @param args dynamic list of PARAMETERS
     * @return Début d'URL
     */
    public static String URL(String _object,
                             String _actiontype,
                             String... args) {

        String urlRet = "./joy";
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
                //JOY.LOG().error("Error while encoding and/or creating URL, " + ex);
            }
        }
        //Joy.LOG().debug("Returning URL --> " + urlRet);
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
    public static Action CURRENT_ACTION(HttpServletRequest request) {
        try {
            return (Action) request.getAttribute(C.ACTION_FORM_BEAN);
        } catch (Exception e) {
            //JOY.LOG().debug("Impossible to retrieve current action, " + e);
            return null;
        }
    }

    /**
     * Get the current Joy Action Object
     * @param jsp JSP context
     * @return Action Object
     */
    public static Action CURRENT_ACTION(JspContext jsp) {
        try {
            return (Action) jsp.findAttribute(C.ACTION_FORM_BEAN);
        } catch (Exception e) {
            //JOY.LOG().debug("Impossible to retrieve current action, " + e);
            return null;
        }
    }

    /** 
     * Get the current date in the dd/MM/yyyy HH:mm:ss format
     * @return current date
     */
    public static String CURRENT_STR_DATE() {
        return CURRENT_STR_DATE(C.DEFAULT_DATE_FORMAT);
    }
    
    /** 
     * Get the current date in the dd/MM/yyyy HH:mm:ss format
     * @param format date format (example "dd/MM/yyyy HH:mm:ss")
     * @return current date
     */
    public static String CURRENT_STR_DATE(String format) {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(cal.getTime());
    }

    public static String DATE_TO_STRING(Date myDate, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(myDate);
    }
    
    public static Date STRING_TO_DATE(String myDate, String format) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.parse(myDate);
        } catch (ParseException ex) {
            return null;
        }
    }
    
    /** 
     * Get the current date
     * @return current date
     */
    public static Date CURRENT_DATE() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }
    
    /**
     * Format a date with the giver format
     * @param myDate date to format
     * @param format format (example: "dd/MM/yyyy HH:mm:ss")
     * @return formatted date
     */
    public static String DATE_FORMAT(Date myDate, String format) {
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
     * Format a date to a string (format=dd/MM/yyyy HH:mm:ss). 
     * Take the Joy format (in PARAMETERS.xml) or by default "dd/MM/yyyy HH:mm:ss"
     * @param myDate date to format
     * @return formatted date in string
     */
    public static String DATE_FORMAT(Date myDate) {
        String format = "dd/MM/yyyy HH:mm:ss";

        try {
            if (myDate == null) return "";
            format = "yyyy-mm-dd HH:mm:ss";
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
    public static org.jdom2.Document OPEN_XML(String xmlFileName) {
        SAXBuilder sxb = new SAXBuilder();
        org.jdom2.Document document = null;

        // Default opening
        try {
            document = sxb.build(Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlFileName));
        } catch (JDOMException | IOException ex) {
            JOY.SYSTEM_LOG("Impossible to open XML file through current context, " + ex);
        }

        // direct open
        if (document == null) {
            try {
                JOY.SYSTEM_LOG("File Direct Open instead ...");
                File file = new File(xmlFileName);
                document = sxb.build(file);
            } catch (JDOMException | IOException ex) {
                JOY.SYSTEM_LOG("Exception (opening direct file) =" + ex);
            }
        }

        if (document != null) {
            return document;
        } else {
            return null;
        }
    }

    public static String RGBA(String color, String transparency) {
        return "rgba(" + color + "," + transparency + ")";
    }

    /**
     * read a file and put it into a String variable
     * @param filename
     * @return
     */
    public static String FILE_TO_STRING(String filename) {
        BufferedReader reader = null;
        try {
            InputStream myFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            reader = new BufferedReader(new InputStreamReader(myFile));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            reader.close();
            return out.toString();

        } catch (IOException ex) {
            //JOY.LOG().error(ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                //JOY.LOG().error(ex);
            }
        }
        return "";
    }
    
    /**
     * Return a JSON valueset in the Joy standard format
     * @param label field label
     * @param value field value
     * @return JSON Object
     */
    public static JSONObject GET_JSON_VALUESET(String label, Object value) {
        JSONObject column = new JSONObject();
        column.put("name", label);
        if (value == null)
            column.put("value", "<NULL>");
        else
            column.put("value", value);
        return column;
    }
}
