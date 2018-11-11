package app.servlets;


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
import java.util.Map;

@WebServlet (name = "UserInfoServlet", urlPatterns = "/userinfo")
public class UserInfoServlet extends HttpServlet{
    public static final String GETNAME = "getname";
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String username = SessionUtils.getUsername(request);
        String jsonResponse = "";
        Map<String, String[]> paramMap = request.getParameterMap();
        switch (paramMap.entrySet().iterator().next().getKey()) {
            case GETNAME:
                jsonResponse = new Gson().toJson(username);
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
