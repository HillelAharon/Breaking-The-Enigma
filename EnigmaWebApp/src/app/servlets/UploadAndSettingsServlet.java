package app.servlets;

//taken from: http://www.servletworld.com/servlet-tutorials/servlet3/multipartconfig-file-upload-example.html
// and http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html

import app.utils.MessagesUtils;
import app.utils.ServletUtils;
import app.utils.SessionUtils;
import com.google.gson.Gson;
import common.SecretAndMsgInfo;
import engine.machineRelated.components.machine.api.EnigmaMachine;
import engine.manager.Manager;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

@WebServlet(name = "UploadAndSettingsServlet",urlPatterns = "/pages/loadXML/uploadAndSetting")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadAndSettingsServlet extends HttpServlet {
    private static final String UBOAT_BATTLEFIELD_URL = "/Enigma/pages/uboatBattlefield/UboatBattlefield.html";
    private static final String DATA = "data";
    private static final String CHECK = "check";
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException,IOException {
        response.setContentType("application/json");
        String username = SessionUtils.getUsername(request);
        Manager manager = ServletUtils.getManager(getServletContext());
        String jsonResponse = "";
        Map<String, String[]> paramMap = request.getParameterMap();
        if(paramMap.size() != 0) {
            switch (paramMap.entrySet().iterator().next().getKey()) {


                case DATA:
                    String res = request.getParameter("data");
                    SecretAndMsgInfo secretAndMsg = new Gson().fromJson(res, SecretAndMsgInfo.class);

                    if (res != null) {
                        secretAndMsg.setMsg(secretAndMsg.getMsg().toUpperCase());
                        if (manager.setUboatSecretAndMsgFromJS(username, secretAndMsg)) {
                            manager.setUboatReady(username, true);
                            manager.addUserUpdateUrl(username,UBOAT_BATTLEFIELD_URL);
                            jsonResponse = new Gson().toJson(new MessagesUtils(UBOAT_BATTLEFIELD_URL, true));
                        } else {
                            jsonResponse = new Gson().toJson(new MessagesUtils("Message not in dictionary", false));
                        }
                    } else
                        jsonResponse = new Gson().toJson(new MessagesUtils("Please select secret and message first", false));
                    break;
                case CHECK:
                    EnigmaMachine.MachineInfo machineInfo = manager.getUboatMachineInfo(username);
                    if (machineInfo != null)
                        jsonResponse = new Gson().toJson(new MessagesUtils(machineInfo, true));
                    else
                        jsonResponse = new Gson().toJson(new MessagesUtils(null, false));
                    break;

            }
        }
        else if(request.getParts() != null){
            Collection<Part> parts = request.getParts();
            Part part = parts.iterator().next();
            if (part.getContentType().compareTo("text/xml") != 0) {
                jsonResponse = new Gson().toJson(new MessagesUtils("Error: File type most be xml", false));
            } else {
                try {
                    jsonResponse = new Gson().toJson(new MessagesUtils(manager.xmlToBuilders(username, part.getInputStream()), true));
                } catch (Exception e) {
                    jsonResponse = new Gson().toJson(new MessagesUtils(e.getMessage(), false));
                }
            }
        }

        try (PrintWriter out = response.getWriter()) {
            out.println(jsonResponse);
            out.flush();
        }



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

}