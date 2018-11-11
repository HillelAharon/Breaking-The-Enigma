package app.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {
    public static final String USERNAME = "username";
    public static String getUsername (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(USERNAME) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }
    public static void setUsername (HttpServletRequest request,String username){
        request.getSession(true).setAttribute(USERNAME, username);
    }
    public static void clearSession (HttpServletRequest request) {
        request.getSession().invalidate();
    }
}