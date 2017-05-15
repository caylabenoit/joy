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

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class C {
    public static final String JOY_VERSION = "2.0.0.1";
    
    public static final String YES = "yes";
    public static final String NO = "no";
    public static final String ID = "id";
    
    // Application folders
    public static final String APPDIR_PARAMETER = "joy-appdir";
    public static final String CONFIG_DIR_FROM_APPDIR = "config";
    public static final String LOG_DIR_FROM_APPDIR = "log";
    public static final String LANG_DIR_FROM_APPDIR = "lang";
    
    ////////////////////////
    // Framework  MVC
    ////////////////////////
    public static final String ENCODING = "UTF-8";
    public static final String PAGE_404 = "./404.jsp";
    public static final String PAGE_ERROR = "./error.jsp";
    public static final String FLOWTYPE_STREAM = "Stream";
    public static final String FLOWTYPE_FILE = "File";
    public static final String ACTION_FORM_BEAN = "ACTIONOBJECT";   // référence à l'objet Action contenu dans la requete pour les JSP
    // actions tags keywords
    public static final String ACTION_TAG_OBJECT = "object";
    public static final String ACTION_TAG_OBJECT_REPORTING = "reporting";
    public static final String ACTION_REPORTING_ID = "ID";
    public static final String ACTION_REST_PARAM_PREFIX = "P";
    public static final String ACTION_TAG_OBJECTTYPE_LOGIN = "login";
    public static final String ACTION_TAG_LOGIN_REQUEST = "REQUEST";
    public static final String ACTION_TAG_LOGIN_AFTERLOGIN = "AFTERLOGIN";
    public static final String ACTION_TAG_LOGIN_AFTERLOGOUT = "AFTERLOGOUT";
    public static final String ACTION_TAG_OBJECTTYPE_ASYNCTASK = "asynchronous";
    public static final String ACTION_TAG_OBJECTTYPE = "objecttype";
    public static final String ACTION_TAG_REQUEST = "actionRequest";
    public static final String ACTION_TAG_ACTION = "action";
    public static final String ACTION_TAG_REDIRECTS = "redirects";
    public static final String ACTION_TAG_REDIRECT = "redirect";
    public static final String ACTION_TAG_FLOWTYPE = "flowtype";
    public static final String ACTION_TAG_ATTRIB_TAG = "tag";
    public static final String ACTION_TAG_REDIRECT_NO_ACTION = "redirects_no_action";
    public static final String ACTION_TAG_OBJECTTYPE_FORM = "form";
    public static final String ACTION_TAG_OBJECTTYPE_REST = "rest";
    public static final String ACTION_TAG_OBJECTTYPE_TASK = "task";
    public static final String ACTION_TYPE_TAG = "actiontype";
    public static final String ACTION_SUCCESS = "SUCCESS";
    public static final String ACTION_ERROR = "ERROR";
    public static final int EXEC_SUCCESS = 1;
    public static final int EXEC_ERROR = 0;  
    // ActionsTypes list pour form
    public static final String ATYPE_FORM_EDIT = "EDIT";
    public static final String ATYPE_FORM_ADD = "ADD";
    public static final String ATYPE_FORM_DELETE = "DELETE";
    public static final String ATYPE_FORM_UPDATE = "UPDATE";
    public static final String ATYPE_FORM_LIST = "LIST";
    public static final String ATYPE_FORM_DISPLAY = "DISPLAY";
    public static final String ATYPE_FORM_OTHER = "OTHER";
    public static final String ATYPE_FORM_SEARCH = "SEARCH";
    public static final String ATYPE_FORM_NODATA = "NODATA";
    // Login
    public static final String ATYPE_LOGIN = "LOGIN";
    public static final String ATYPE_LOGOUT = "LOGOUT";
    
    ///////////////////////////////////////
    // XML Configuration tags for entities
    ///////////////////////////////////////
    
    // DB Connection tags
    public static final String ENTITIES_JOY_QUERY_INIT ="joy-query-init";
    public static final String ENTITIES_JOY_JDBC_USER ="joy-jdbc-user";
    public static final String ENTITIES_JOY_JDBC_PWD ="joy-jdbc-pwd";
    public static final String ENTITIES_JOY_JDBC_URL ="joy-jdbc-url";
    public static final String ENTITIES_JOY_JDBC_DRIVER ="joy-jdbc-driver";
    public static final String ENTITIES_JOY_DATASOURCE_TAG ="joy-datasource";
    public static final String ENTITYFIELD_COUNT_FIELD ="NEWID";
    public static final String ENTITIES_JOY_FIELDLABEL_TAG ="joy-field-label";
    public static final String ENTITIES_JOY_ENTITY_TAG ="joy-entity";
    public static final String ENTITIES_NAME_ATTRIBUTE ="name";
    public static final String ENTITIES_LABEL_ATTRIBUTE ="label";
    // Composite objects
    public static final String ENTITIES_COMPOSITE_ENTITY ="joy-entity";
    public static final String ENTITIES_COMPOSITE_ATTR_DISTINCT ="distinct";
    public static final String ENTITIES_COMPOSITE_ATTR_ALIAS ="alias";
    public static final String ENTITIES_COMPOSITE_ATTR_AS ="as";
    public static final String ENTITIES_COMPOSITE_FIELD ="joy-field";
    public static final String ENTITIES_COMPOSITE_GROUP ="joy-group";
    public static final String ENTITIES_COMPOSITE_JOIN="joy-join";
    public static final String ENTITIES_COMPOSITE_ATTR_TYPE ="type";
    public static final String ENTITIES_COMPOSITE_ATTR_DESC = "desc";
    public static final String ENTITIES_COMPOSITE_JOINKEY="joy-join-key";
    public static final String ENTITIES_COMPOSITE_ATTR_MASTER ="master";
    public static final String ENTITIES_COMPOSITE_ATTR_SLAVE ="slave";
    public static final String ENTITIES_COMPOSITE_FILTER ="joy-filter";
    public static final String ENTITIES_COMPOSITE_ATTR_OPERATOR ="operator";
    public static final String ENTITIES_COMPOSITE_ATTR_FIELD ="field";
    public static final String ENTITIES_COMPOSITE_SORT="joy-sort";

    ///////////////////////////////////////
    // XML Configuration tags Parameters
    ///////////////////////////////////////    
    public static final String PARAMETERS_TAG_API_START_PATH = "api-start-path";
    public static final String PARAMETERS_TAG_BUNDLEMSG = "bundle-message";
    public static final String PARAMETERS_TAG_DATEFORMAT = "default-dateformat";
    public static final String PARAMETERS_TAG_VERSION = "joy-version";
    public static final String PARAMETERS_TAG_APPNAME = "joy-app-name";
    public static final String PARAMETERS_TAG_LOCALE = "joy-localecountry";
    public static final String PARAMETERS_TAG_LOCALE_LANG = "joy-localelanguage";
    public static final String PARAMETERS_TAG_SESSION_TIMEOUT = "joy-session-timeout";
    public static final String PARAMETERS_TAG_ENTITIES = "joy-entities";
    public static final String PARAMETERS_TAG_ENTITY = "joy-entity";
    public static final String PARAMETERS_TAG_MAPPINGS = "joy-mappings";
    public static final String PARAMETERS_TAG_MAPPING = "joy-mapping";
    public static final String PARAMETERS_SINGLEPARAM = "app-parameter";
    public static final String PARAMETERS_SP_ATTR_TYPE = "type";
    public static final String PARAMETERS_SP_ATTR_VALUE = "value";
    public static final String PARAMETERS_SP_ATTR_NAME = "name";
    public static final String PARAMETERS_SP_ATTR_LIST = "list";
    public static final String PARAMETERS_SP_PARAMVAL = "app_parameter-value";
    public static final String PARAMETERS_NO_LOGIN = "no-login";
    
    ////////
    // Tags
    ////////
    public static final String JOTYAGLIB_ROWATTRIBUTE = "JOYFORMMATRIXROW";
    public static final String JOTYAGLIB_ROWINDEX = "JOYFORMMATRIXROWINDEX";

    /////////
    // Logs
    /////////
    public static final String JOTYLOG_TAG_FILENAME = "joy-logfilename";
    public static final String JOTYLOG_TAGATTR_DATEPREFIX = "dateprefix";
    public static final String JOTYLOG_TAGATTR_VERSIONPREFIX = "versionprefix";
    public static final String JOTYLOG_TAG_DIRECTORY = "joy-logdirectory";
    public static final String JOTYLOG_TAG_LEVELS = "joy-loglevels";
    public static final String JOTYLOG_TAG_LEVEL = "joy-loglevel";
    public static final String JOTYLOG_TAGATTR_ACTIVE = "active";
    public static final String JOTYLOG_DEBUG = "DEBUG";
    public static final String JOTYLOG_INFO = "INFO";
    public static final String JOTYLOG_WARNING = "WARNING";
    public static final String JOTYLOG_ERROR = "ERROR";
    public static final String JOTYLOG_FATAL = "FATAL";

    ///////////
    // Mappings
    ///////////
    public static final String JOTYMAP_TAG_ENTITYFROM = "entity-from";
    public static final String JOTYMAP_TAG_ENTITYTO = "entity-to";
    public static final String JOTYMAP_TAG_MAP = "map";
    public static final String JOTYMAP_TAGATTR_MAP_FROM = "from";
    public static final String JOTYMAP_TAGATTR_NAME = "name";
    public static final String JOTYMAP_TAGATTR_MAP_TO = "to";
    public static final String JOTYMAP_TAGATTR_MAP_KEY = "key";
    public static final String JOTYMAP_TAGATTR_MAP_CHECKEX = "check-if-exist";
    public static final String JOTYMAP_TAGATTR_MAP_DEFNULL = "default-if-null";
    public static final String JOTYMAP_TAGATTR_MAP_VALUE = "value";
    public static final String JOTYMAP_TAGATTR_MAP_TYPE = "type";
    public static final String JOTYMAP_TAG_LOOKUP = "map-lookup";
    public static final String JOTYMAP_TAGATTR_FILTER = "filter";
    public static final String JOTYMAP_TAG_ENTITYLOOKUP = "entity-lookup";
    public static final String JOTYMAP_TAG_ENTITYLOOKUP_KEY = "entity-lookup-key";
    public static final String JOTYMAP_TAG_LOOKUP_COND = "lookup-condition";
    public static final String JOTYMAP_TAG_LOOKUP_KEY = "lookup-key";
    
    // Menus
    public static final String JOYMENU_TAG = "joy-menu";
    public static final String JOYMENUATTR_NAME = "name";
    public static final String JOYMENUATTR_ID = "id";
    public static final String JOYMENUATTR_CLASS = "class";
    public static final String JOYMENUATTR_GLYPHE = "glyphe";
    public static final String JOYMENUATTR_URL = "url";
    public static final String JOYMENU_TAG_BUTTON = "joy-button";
    public static final String JOYMENUATTR_SHORTCUT = "shortcut";
    
    // Nothing
    public static final String JSON_EMPTY = "{\"title\": \"Nothing\"}";
    
    public static final String RESTFUL_NOT_FOUND = "JOY_RESTFUL_NOT_FOUND";
    public static final String RESTFUL_ALREADY_EXIST = "JOY_RESTFUL_ALREADY_EXIST";
    public static final String RESTFUL_NO_CONTENT = "JOY_RESTFUL_NO_CONTENT";
    public static final String RESTFUL_OK = "JOY_RESTFUL_OK";
    
    ///////////
    // Authentication
    ///////////
    public static final String DEFAULT_USER = "Anonymous";
    public static final String TOKEN_PKEY_TAG = "publickey";
    public static final String REQ_PARAM_USER_TAG = "user";
    public static final String REQ_PARAM_PWD_TAG = "password";
    public static final String TOKEN_STATUS_TAG = "status";
    public static final String TOKEN_TOKEN_TAG = "token";
    public static final int TOKEN_STATUS_KO = 0;
    public static final int TOKEN_STATUS_OK = 1;
    public static final String TOKEN_EMPTY = "";
    
}
