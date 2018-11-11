package app.utils;
import engine.manager.Manager;

import javax.servlet.ServletContext;

public class ServletUtils {

	private static final String MANAGER_ATTRIBUTE_NAME = "Manager";
	public static Manager getManager(ServletContext servletContext) {
		if (servletContext.getAttribute(MANAGER_ATTRIBUTE_NAME) == null) {
			servletContext.setAttribute(MANAGER_ATTRIBUTE_NAME, new Manager());
		}
		return (Manager) servletContext.getAttribute(MANAGER_ATTRIBUTE_NAME);
	}
}
