package app.servlets;

import app.utils.MessagesUtils;
import app.utils.ServletUtils;
import app.utils.SessionUtils;
import com.google.gson.Gson;
import engine.manager.Manager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "LoginServlet", urlPatterns = {"/pages/signup/login"})
public class LoginServlet extends HttpServlet {

    private final String ENLISTMENT_URL = "/pages/enlistment/enlistment.html";
    private final String SIGN_UP_URL = "/pages/signup/signup.html";
    public static final String USERNAME = "username";
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

        String usernameFromSession = SessionUtils.getUsername(request);
        Manager manager = ServletUtils.getManager(getServletContext());

        if (usernameFromSession != null && manager.isUserExists(usernameFromSession)){
            String jsonResponse =new Gson().toJson(new MessagesUtils(manager.getUserLastUrl(usernameFromSession),true));
            try (PrintWriter out = response.getWriter()) {
                out.println(jsonResponse);
                out.flush();
                return;
            }
        }



        //System.out.println(request.getParameter(USERNAME));

        String username = request.getParameter(USERNAME);

        if (username == null) {
            String jsonResponse = new Gson().toJson(new MessagesUtils(SIGN_UP_URL, true));
            try (PrintWriter out = response.getWriter()) {
                out.println(jsonResponse);
                out.flush();
                return;
            }
        }
        else {
            username = username.trim();

            if (manager.isUserExists(username)) {
                username = manager.suggestAlternativeUsername(username);
                MessagesUtils message = new MessagesUtils(username, false);
                String jsonResponse = new Gson().toJson(message);
                try (PrintWriter out = response.getWriter()) {
                    out.println(jsonResponse);
                    out.flush();
                }
            }
            else {
                manager.addUserUpdateUrl(username,ENLISTMENT_URL);
                SessionUtils.setUsername(request, username);
                MessagesUtils message = new MessagesUtils(ENLISTMENT_URL, true);
                String jsonResponse = new Gson().toJson(message);
                try (PrintWriter out = response.getWriter()) {
                    out.println(jsonResponse);
                    out.flush();
                }

            }
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
