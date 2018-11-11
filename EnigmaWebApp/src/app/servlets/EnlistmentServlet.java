package app.servlets;

import app.utils.ServletUtils;
import app.utils.SessionUtils;
import engine.manager.Manager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "EnlistmentServlet", urlPatterns = {"/pages/enlistment/choice"})
public class EnlistmentServlet extends HttpServlet {

    private static final String CHOICE_PARAMETER_ROLE = "choice";
    private static final String ALLIES_LOBBY_URL = "/pages/alliesLobby/alliesLobby.html";
    private static final String FILE_UPLOAD_URL = "/pages/loadXML/fileUpload.html";
    public static final String UBOAT = "uboat";
    public static final String ALLIES = "allies";


    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String username = SessionUtils.getUsername(request);
        Manager manager = ServletUtils.getManager(getServletContext());
        String usersRoleChoice = request.getParameter(CHOICE_PARAMETER_ROLE);


        switch (usersRoleChoice) {
            case ALLIES:
                manager.addAllies(username);
                if(manager.initDMForAlly(username)) {
                    manager.addUserUpdateUrl(username, ALLIES_LOBBY_URL);
                    response.sendRedirect(ALLIES_LOBBY_URL);
                }
                break;
            case UBOAT:
                manager.addUboat(username);
                manager.addUserUpdateUrl(username,FILE_UPLOAD_URL);
                response.sendRedirect(FILE_UPLOAD_URL);
                break;
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

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
