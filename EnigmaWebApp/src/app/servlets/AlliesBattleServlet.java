package app.servlets;

import app.utils.ServletUtils;
import app.utils.SessionUtils;
import com.google.gson.Gson;
import engine.manager.Manager;
import engine.players.ally.Ally;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


@WebServlet(name = "AlliesBattleServlet", urlPatterns = {"/pages/alliesBattlefield/alliesBattle"})
public class AlliesBattleServlet extends HttpServlet {


    private static final String USER_READY = "ready";
    public static final String ALLIES_DATA_REQUEST = "alliesData";
    public static final String ROUND_PROGRESS_REQUEST = "roundProgress";
    public static final String MISSIONSIZE = "missionSize";
    public static final String INFO = "info";


    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String username = SessionUtils.getUsername(request);
        Manager manager = ServletUtils.getManager(getServletContext());
        String jsonResponse = "";
        Map<String, String[]> paramMap = request.getParameterMap();
        switch (paramMap.entrySet().iterator().next().getKey()) {
            case USER_READY:
                jsonResponse = new Gson().toJson(manager.allyConfirmed(username));
                manager.isBattleReady(username);

                break;
            case ALLIES_DATA_REQUEST:
                List<Ally.AllyData> alliesDataList = manager.getBattlefieldAlliesData(username);
                jsonResponse = new Gson().toJson(alliesDataList);
                break;
            case ROUND_PROGRESS_REQUEST:
                jsonResponse = new Gson().toJson(manager.getAllyBattlefieldProcessInfo(username));
                break;
            case MISSIONSIZE:
                manager.setAllyMissionSize(username,Integer.parseInt(request.getParameter(MISSIONSIZE)));
                //System.out.println("Mission size update");
                jsonResponse = new Gson().toJson("success");
                break;
            case INFO:
                jsonResponse = new Gson().toJson(manager.getAllyData(username));
                break;


        }

        try (PrintWriter out = response.getWriter()) {
            out.println(jsonResponse);
            out.flush();
        }
//            case Constants.GAME_OVER:
//            break;
//            case LOG_OUT:
//                response.sendRedirect(LOG_OUT_URL);
//            break;


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
