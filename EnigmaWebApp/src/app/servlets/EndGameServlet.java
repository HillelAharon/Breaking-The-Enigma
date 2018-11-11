package app.servlets;


import app.utils.MessagesUtils;
import app.utils.ServletUtils;
import app.utils.SessionUtils;
import com.google.gson.Gson;
import engine.battlefield.BattlefieldSummary;
import engine.manager.Manager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet(name = "EndGameServlet", urlPatterns = {"/endgame"})
public class EndGameServlet extends HttpServlet {
    public static final String ROUND = "round";
    public static final String LOGOUT = "logout";
    public static final String LOBBY = "lobby";
    public static final String UBOAT = "uboat";
    public static final String ALLIES = "ally";
    public static final String CONTINUE = "continue";
    private static final String ALLIES_LOBBY_URL = "/Enigma/pages/alliesLobby/alliesLobby.html";
    private static final String FILE_UPLOAD_URL = "/Enigma/pages/loadXML/fileUpload.html";
    private final String SIGN_UP_URL = "/Enigma/pages/signup/signup.html";
    private static final String ALLIES_BATTLEFIELD_URL = "/Enigma/pages/alliesBattlefield/alliesBattlefield.html";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String username = SessionUtils.getUsername(request);
        Manager manager = ServletUtils.getManager(getServletContext());
        String jsonResponse = "";
        Map<String, String[]> paramMap = request.getParameterMap();
        switch (paramMap.entrySet().iterator().next().getKey()) {

            case CONTINUE:
                switch (manager.getUserRole(username)) {
                    case UBOAT:
                        jsonResponse = new Gson().toJson(new MessagesUtils(FILE_UPLOAD_URL, true));
                        manager.addUserUpdateUrl(username, FILE_UPLOAD_URL);
                        break;
                    case ALLIES:
                        jsonResponse = new Gson().toJson(new MessagesUtils(ALLIES_LOBBY_URL, true));
                        manager.addUserUpdateUrl(username, ALLIES_LOBBY_URL);
                        break;
                }
                break;

            case LOGOUT:
                manager.logOut(username);
                jsonResponse = new Gson().toJson(new MessagesUtils(SIGN_UP_URL, true));
                break;

            case ROUND:
                BattlefieldSummary summary = manager.isGameOver(username);
                if (summary.getStatus().compareTo("round") == 0)
                    switch (manager.getUserRole(username)) {
                        case UBOAT:
                            jsonResponse = new Gson().toJson(new MessagesUtils(summary, true));
                            manager.addUserUpdateUrl(username, FILE_UPLOAD_URL);
                            break;
                        case ALLIES:
                            jsonResponse = new Gson().toJson(new MessagesUtils(summary, true));
                            break;
                    }
                else {
                    if (manager.getUserRole(username).compareTo(ALLIES) == 0)
                        manager.removeAllyFromBattlefield(username);
                    jsonResponse = new Gson().toJson(new MessagesUtils(summary, false));
                }
                break;
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
