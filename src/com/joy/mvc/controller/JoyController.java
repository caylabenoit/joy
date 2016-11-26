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
package com.joy.mvc.controller;

import com.joy.mvc.actionTypes.ActionTypeREST;
import com.joy.mvc.actionTypes.ActionTypeForm;
import com.joy.C;
import com.joy.Joy;
import com.joy.bo.BOFactory;
import com.joy.mvc.Action;
import com.joy.mvc.ActionRedirect;
import com.joy.mvc.ActionRequest;
import com.joy.mvc.actionTypes.ActionTypeLogin;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*
* Classe principale du framework --> Controleur/Handler de toutes les request http
*   ./action?
*
* Cette classe redistribue les actions au classes dérivées.
*       - ActionTypeForm : pour une Action de type formulaire
*           dans ce cas, dans Action-tags.xml on doit avoir la référence : <objecttype>form</objecttype>
*       - ActionTypeCmdLine : pour une Action de type lancement de commande externe (avec ou sans wait)
*           dans ce cas, dans Action-tags.xml on doit avoir la référence : <objecttype>command</objecttype>
*       - ActionTypeREST : pour une Action de type REST
*           dans ce cas, dans Action-tags.xml on doit avoir la référence : <objecttype>rest</objecttype>
*
* Parametres du framework MVC (Action) :
*
*   -> object (./action?object[objectname]) : référence au fichier joy-action-tags.xml. Permet de retrouver la classe 
*                                de traitement et les redirections :
*                                <actionRequest>
*                                    <object>[objectname]</object>
*           si [objectname] n'est pas trouvé dans les <actionRequest>, le framework pointe directement sur 
*           <redirects_no_action> et recherche avec le tag [objectname] dans les <redirect> proposés :
*                                <redirects_no_action>
*                                    <redirect tag="[objectname]">[URL]</redirect>
*
*   -> actiontype (./action?object[objectname]&actiontype=[actiontypename]) : selon le tag proposé permet d'appeler 
*               une méthode spécifique de traitement de l'Action.
*               Les actionstypes sont si <objecttype>form</objecttype> :
*                             "EDIT": Appelle par exemple ActionTypeForm.Edit()
*                             "ADD": 
*                             "DELETE": 
*                             "UPDATE": 
*                             "LIST": 
*                             "DISPLAY": 
*                             "OTHER": (actiontype par défaut)
*               Les actionstypes ne sont pas utilisés si <objecttype>command</objecttype>.
* 
* object disponibles :
*   - C.ACTION_TAG_OBJECTTYPE_COMMAND : lancement de commande line
 * @author Benoit CAYLA (benoit@famillecayla.fr)
*/

public class JoyController extends HttpServlet {

    private void loginfo(String myLog) {
        try {
            Joy.LOG().error( myLog);
        } catch (Exception e) {
            Joy.SYSTEM_LOG(myLog);
        }
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
            Input request PARAMETERS :
            object : specify the <object> tag in the Action.xml file
                * if object="command" -> command to execute, nowait parameter specify a wait page or not
            actiontype : Action to do, callback to the designed Action in the Action class
            Command launch URL sample
            http://localhost:18180/GovManagementTool/Action?object=cmdwf&wait=yes&workflow=wf_Full_Delta_SC
     */
    protected void processRequest(HttpServletRequest request, 
                                  HttpServletResponse response)
            throws ServletException, IOException {
        
        String redirect = C.PAGE_404;
        ControllerConfiguration joySrvCfg = null;
        
        try {
            joySrvCfg = checkInit(request.getServletContext());
            joySrvCfg.checks();
            Joy.LOG().debug("New HTTP Request initialization, URL=" + request.getRequestURL() + " | QueryString=" + request.getQueryString());
            
            ActionRequest req = joySrvCfg.getActionConfig().getActionRequest(getAttributeOrParameter(request, C.ACTION_TAG_OBJECT));

            Joy.LOG().debug("HTTP Session management");
            // Session management
            HttpSession mySession = request.getSession();
            if (mySession.isNew()) {
                Joy.LOG().debug("This session has never been initialized, Configure a new session");
                mySession = request.getSession();
                mySession.setMaxInactiveInterval(Joy.PARAMETERS().getSessionItemout()*60);
                mySession.setAttribute("Anonymous", "true");
            } else 
                Joy.LOG().debug("Session Already configured");

            // Request management (depending of the object type)
            Joy.LOG().debug("Request=" + request.getQueryString());
            if (req.getIsInitialized()) {
                String objectType = req.getObjectType();

                switch (objectType) {
                    //////////////////////////////////////////////////////////////////////////////////////////
                    case C.ACTION_TAG_OBJECTTYPE_LOGIN:
                        // http://localhost:18180/GovManagementTool/action?object=login --> redirect to login page
                        // http://localhost:18180/GovManagementTool/action?object=login/actiontype=connect --> Validate credentials
                        redirect = manageLoginRequest(mySession, request, req);
                        break;
                        
                    //////////////////////////////////////////////////////////////////////////////////////////
                    case C.ACTION_TAG_OBJECTTYPE_REST: 
                        // http://localhost:18180/GovManagementTool/action?object=[tag] / objecttype=rest
                        // http://localhost:18180/GovManagementTool/rest/[tag]/P1/P2/P3/...
                        manageRESTRequest(mySession, request, response, req, joySrvCfg);
                        joySrvCfg.getEntities().End();
                        return;

                    //////////////////////////////////////////////////////////////////////////////////////////
                    case C.ACTION_TAG_OBJECTTYPE_TASK: 
                        // http://localhost:18180/GovManagementTool/action?object=[tag] / objecttype=task
                        // http://localhost:18180/GovManagementTool/task/[tag]/P1/P2/P3/...
                        manageTASKRequest(mySession, request, response, req, joySrvCfg);
                        //joySrvCfg.getBOFactory().End();
                        return;
                        
                    //////////////////////////////////////////////////////////////////////////////////////////
                    case C.ACTION_TAG_OBJECTTYPE_FORM: 
                        // http://localhost:18180/GovManagementTool/action?object=[tag] / objecttype=form
                        redirect = manageFormRequest(mySession, request, req, joySrvCfg);
                        break;
                        
                    default:
                        redirect = C.PAGE_404;
                        break;
                }
                    
            } else { 
                // http://localhost:18180/GovManagementTool/action?object=[tag]
                redirect = manageRedirectionOnlyRequest(mySession, request, req, joySrvCfg);
            }
            Joy.LOG().debug( "URL redirection> " + redirect);
            
        } catch (Exception ex) {
            try {
                Joy.LOG().error( "Exception=" + ex);
            } catch (Exception e) {
                Joy.SYSTEM_LOG( "Exception=" + ex);
            }
        } 
        
        if (redirect.isEmpty())
            redirect = C.PAGE_ERROR;
        
        Joy.LOG().debug("End of HTTP request treatment");
        if (joySrvCfg.getEntities() != null)
            joySrvCfg.getEntities().End(); // Close the connection attached to the request
        
        String encodedURL = response.encodeRedirectURL(redirect);
        RequestDispatcher rd = request.getRequestDispatcher(encodedURL); 
        rd.forward(request, response);
    }

    /**
     * Simple redirection without actions
     * http://localhost:18180/GovManagementTool/action?object=[tag]
     * @param mySession
     * @param request
     * @param req
     * @return 
     */
    private String manageRedirectionOnlyRequest(HttpSession mySession, 
                                                HttpServletRequest request,
                                                ActionRequest req,
                                                ControllerConfiguration joySrvConfig) {  
        Joy.LOG().debug( "Redirection without action requested.");
        Action act = new Action();
        act.setMessageBundle(joySrvConfig.getMessageBundle());
        act.init(mySession, request);
        request.setAttribute(C.ACTION_FORM_BEAN, act);
        return joySrvConfig.getActionConfig().getRedirectNoAction(getAttributeOrParameter(request, C.ACTION_TAG_OBJECT));
    }
    
    /**
     * Manage login/logout or login request 
     * http://localhost:18180/GovManagementTool/action?object=login --> redirect to login page
     * http://localhost:18180/GovManagementTool/action?object=login/actiontype=connect --> Validate credentials
     * @param mySession
     * @param request
     * @param req
     * @return 
     */
    private String manageLoginRequest(HttpSession mySession, 
                                      HttpServletRequest request,
                                      ActionRequest req) {  
        String redirect = C.PAGE_404;
        
        try {
            Joy.LOG().debug("Authentication action requested");
            ActionTypeLogin actionLogin = (ActionTypeLogin) Class.forName(req.getJavaclass()).newInstance();
            actionLogin.init(mySession, request);
            String redirectTag = C.ACTION_TAG_LOGIN_REQUEST;

            String loginType = getAttributeOrParameter(request, C.ACTION_TYPE_TAG).toUpperCase();
            if (loginType.equalsIgnoreCase(C.ATYPE_LOGIN)) { // effective login
                Joy.LOG().debug("Login requested");
                redirectTag = actionLogin.login();
                if (redirectTag.equalsIgnoreCase(C.ACTION_SUCCESS)) 
                    mySession.setAttribute(C.JOY_ANONYMOUS_LOGIN, "false");
                redirectTag = C.ACTION_TAG_LOGIN_AFTERLOGIN;

            } else if (loginType.equalsIgnoreCase(C.ATYPE_LOGOUT)) { // effective logout
                Joy.LOG().debug("Logout requested");
                redirectTag = actionLogin.logout();
                if (redirectTag.equalsIgnoreCase(C.ACTION_SUCCESS)) {
                    // remove all session attributes
                    removeAllSessionAttributes(mySession);
                    mySession.setAttribute(C.JOY_ANONYMOUS_LOGIN, "true");
                }
                redirectTag = C.ACTION_TAG_LOGIN_AFTERLOGOUT;

            } else { // login form requested
                Joy.LOG().debug("Login form requested");
                redirectTag = actionLogin.request();
            }

            ActionRedirect redLog = req.getRedirectFromTag(redirectTag);
            if (redLog == null) {
                Joy.LOG().error( "Redirection Tag error, impossible to match the result tag with a valid URL.");
                redirect = C.PAGE_ERROR;
            } else 
                redirect = redLog.getUrl();

            Joy.LOG().debug( "Tag redirection> " + redirect);
            request.setAttribute(C.ACTION_FORM_BEAN, actionLogin);
        } catch (ClassNotFoundException ex) {
            Joy.LOG().error( "ClassNotFoundException=" + ex);
        } catch (InstantiationException ex) {
            Joy.LOG().error( "InstantiationException=" + ex);
        } catch (IllegalAccessException ex) {
            Joy.LOG().error( "IllegalAccessException=" + ex);
        }
        
        return redirect;
    }
    
    /**
     * Give to the Action object all the session attributes
     */
    private void removeAllSessionAttributes(HttpSession mySession) {
        // applique tous les parametres de la session
        Enumeration<String> sessionNames = mySession.getAttributeNames();
        while (sessionNames.hasMoreElements()) {
            String argEnum = sessionNames.nextElement();
            if (!argEnum.equalsIgnoreCase("mode"))
                mySession.removeAttribute(argEnum);
        }
    }
    
    /**
     * Manage a REST call request
     * http://localhost:18180/GovManagementTool/action?object=[tag] / objecttype=rest
     * http://localhost:18180/GovManagementTool/rest/[tag]/P1/P2/P3/...
     * @param mySession
     * @param request
     * @param req
     */
    private void manageRESTRequest(HttpSession mySession, 
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   ActionRequest req,
                                   ControllerConfiguration joySrvConfig) {
        try {
            Joy.LOG().debug("REST action requested");
            ActionTypeREST actionRestObject = (ActionTypeREST) Class.forName(req.getJavaclass()).newInstance();
            actionRestObject.init(mySession, request);
            actionRestObject.setEntities(joySrvConfig.getEntities());
            String resultREST = actionRestObject.restGet();
            
            if (req.getFlowType().equalsIgnoreCase(C.FLOWTYPE_FILE)) {
                // Force a file download
                response.setContentType ("unknown/unknown");
                response.setHeader ("Content-Disposition", "attachment; filename=\"\"");
                ServletOutputStream outs = response.getOutputStream();
                outs.print(resultREST);
                outs.flush();
                outs.close();
                
            } else {
                // Return classic flow (in json)
                PrintWriter out = response.getWriter();
                out.print( resultREST );
                out.close();
            }
            actionRestObject.endOfWork();
            
        } catch (ClassNotFoundException ex) {
            Joy.LOG().error( "ClassNotFoundException=" + ex);
        } catch (InstantiationException ex) {
            Joy.LOG().error( "InstantiationException=" + ex);
        } catch (IllegalAccessException ex) {
            Joy.LOG().error( "IllegalAccessException=" + ex);
        } catch (IOException ex) {
            Joy.LOG().error( "IOException=" + ex);
        }
    }
    

    /**
     * Manage a TASK call request
     * http://localhost:18180/GovManagementTool/action?object=[tag] / objecttype=task
     * http://localhost:18180/GovManagementTool/task/name/P1/P2/P3/...
     * @param mySession
     * @param request
     * @param req
     */
    private void manageTASKRequest(HttpSession mySession, 
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   ActionRequest req,
                                   ControllerConfiguration joySrvConfig) {
        
        PrintWriter out = null;
        try {
            String object = request.getParameter(C.ACTION_TAG_OBJECT);
            boolean result;
            // create a new task
            result = Joy.TASKS().newTask(object,
                                               request.getParameter("P1"), 
                                               req.getJavaclass(),
                                               mySession, 
                                               request,
                                               joySrvConfig.getEntities());
            out = response.getWriter();
            
            out.print( (result ? "{\"result\":\"Success\"}" : "{\"result\":\"Failed\"}") );
            out.close();

        } catch (IOException ex) {
            Joy.LOG().error(ex + "IOException=");
        } finally {
            try { out.close(); }catch (Exception e) {}
        }
    }
    
    
    /**
     * Manage a form call request
     * @param mySession
     * @param request
     * @param req
     * @return 
     */
    private String manageFormRequest(HttpSession mySession, 
                                     HttpServletRequest request,
                                     ActionRequest req,
                                     ControllerConfiguration joySrvConfig) {
        String redirect = C.PAGE_404;
        String redirectTag = "";
        
        try {
            //////////////////////////////////////////////////////////////////////////////////////////
            // http://localhost:18180/GovManagementTool/action?object=[tag] / objecttype=form
            
            Joy.LOG().debug("Display Form action requested");
            ActionTypeForm actionFormObject = (ActionTypeForm) Class.forName(req.getJavaclass()).newInstance();
            actionFormObject.init(mySession, request);
            actionFormObject.collectAttachedFiles(request);
            actionFormObject.setMessageBundle(joySrvConfig.getMessageBundle());
            actionFormObject.setEntities(joySrvConfig.getEntities());
                    
            String actionType = getAttributeOrParameter(request, C.ACTION_TYPE_TAG).toUpperCase();
            switch (actionType) {
                case C.ATYPE_FORM_EDIT : redirectTag = actionFormObject.edit(); break;
                case C.ATYPE_FORM_ADD : redirectTag = actionFormObject.add(); break;
                case C.ATYPE_FORM_DELETE : redirectTag = actionFormObject.delete(); break;
                case C.ATYPE_FORM_UPDATE : redirectTag = actionFormObject.update(); break;
                case C.ATYPE_FORM_LIST : redirectTag = actionFormObject.list(); break;
                case C.ATYPE_FORM_DISPLAY : redirectTag = actionFormObject.display(); break;
                case C.ATYPE_FORM_OTHER :  redirectTag = actionFormObject.other(); break;
                case C.ATYPE_FORM_SEARCH: redirectTag = actionFormObject.search(); break;
                case C.ATYPE_FORM_NODATA: redirectTag = actionFormObject.nodata(); break;
                default:
                    redirectTag = actionFormObject.execute(actionType);
            }
            Joy.LOG().debug( "Tag redirection> " + redirectTag);
            
            // convertit la tag en redirection effective :
            if (!redirectTag.equals("")) {
                ActionRedirect red = req.getRedirectFromTag(redirectTag);
                if (red == null) {
                    Joy.LOG().error( "Redirection Tag error, impossible to match the result tag with a valid URL.");
                    redirect = C.PAGE_ERROR;
                } else {
                    redirect = red.getUrl();
                }
            }
            request.setAttribute(C.ACTION_FORM_BEAN, actionFormObject);
            
        } catch (ClassNotFoundException ex) {
            Joy.LOG().error( "ClassNotFoundException=" + ex);
        } catch (InstantiationException ex) {
            Joy.LOG().error( "InstantiationException=" + ex);
        } catch (IllegalAccessException ex) {
            Joy.LOG().error( "IllegalAccessException=" + ex);
        }
        
        return redirect;
    }
    
    
    /**
     * Return servlet parameter initialisation context (coming from web.xml)
     * @param ParamName
     * @return 
     */
    public String getContextParamFromWebXml(String ParamName) {
        try {
            return getServletContext().getInitParameter(ParamName);
        } catch (Exception e) {
            Joy.LOG().error(e);
            return "";
        }
    }
    
    /**
     * Check Initialization features
     * @param sce servlet context
     */
    private ControllerConfiguration checkInit(ServletContext sce) {
        
        // Initialisation du framework (base de données, logs et entités)
        Joy.INIT(sce);
        ControllerConfiguration srvConfig = new ControllerConfiguration();
        
        // Set new DB connection
        srvConfig.setEntities((BOFactory)Joy.ENTITIES().clone()); 
        
        // Initialisation du fichier action
        if (!srvConfig.getActionConfig().isInitialized())
            srvConfig.getActionConfig().init(getContextParamFromWebXml("joy-actions")); 
        
        // Initialisation des fichiers de traduction
        if (!srvConfig.getMessageBundle().isInitilized()) {
            Joy.LOG().debug ( "Initialize locales language and country.");
            srvConfig.getMessageBundle().setCountry(Joy.PARAMETERS().getDefaultLocalCountry());
            srvConfig.getMessageBundle().setLanguage(Joy.PARAMETERS().getDefaultLocalLanguage());
            srvConfig.getMessageBundle().init();
        }
        return srvConfig;
    }
    
    public JoyController() {}

    /**
     * Return a request parameter or attribute value
     * @param request http request
     * @param Name parameter name
     * @return 
     */
    private String getAttributeOrParameter(HttpServletRequest request, 
                                           String Name) {
        try {
            String param = request.getParameter(Name);
            if (param == null)
                param = request.getAttribute(Name).toString();
            return param;

        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    @Override
    public void init(ServletConfig config) throws ServletException {        
        super.init(config); //To change body of generated methods, choose Tools | Templates.
    }

}
