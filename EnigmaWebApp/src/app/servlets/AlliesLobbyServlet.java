package app.servlets;

import app.utils.ServletUtils;
import app.utils.SessionUtils;
import com.google.gson.Gson;
import engine.battlefield.BattlefieldInfo;
import engine.manager.Manager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


@WebServlet(name = "AlliesLobbyServlet", urlPatterns = {"/pages/alliesLobby/allieslobby"})
public class AlliesLobbyServlet extends HttpServlet {
    public static final String BATTLEFIELD_CHOICE_PARAMETER = "battlefieldList";
    public static final String INFO = "info";
    public static final String APPLIED = "applied";
    public static final String CHOICE = "choice";
    private static final String ALLIES_BATTLEFIELD_URL = "/Enigma/pages/alliesBattlefield/alliesBattlefield.html";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //todo -> update url
        //todo -> check paramMap
        response.setContentType("application/json");
        String username = SessionUtils.getUsername(request);
        Manager manager = ServletUtils.getManager(getServletContext());

        String jsonResponse = "";
        Map<String, String[]> paramMap = request.getParameterMap();
        String key = paramMap.entrySet().iterator().next().getKey();

        switch (key) {
            case BATTLEFIELD_CHOICE_PARAMETER:
                List<BattlefieldInfo> battlefieldsInfoList = manager.getBattlefieldsInfoList();
                jsonResponse = new Gson().toJson(battlefieldsInfoList);
                break;
            case INFO:
                jsonResponse = new Gson().toJson(manager.getAllyData(username));
                break;
            case CHOICE:
            if (manager.addAlliesToBattlefield(username, request.getParameter(CHOICE))) {
                manager.addUserUpdateUrl(username, ALLIES_BATTLEFIELD_URL);
                jsonResponse = new Gson().toJson(ALLIES_BATTLEFIELD_URL);
            }
            //else
            //todo Error battlefield not available
            break;
        }
        try (PrintWriter out = response.getWriter()) {
            out.println(jsonResponse);
            out.flush();
            return;
        }
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
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

