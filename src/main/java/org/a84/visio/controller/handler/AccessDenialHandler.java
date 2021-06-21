package org.a84.visio.controller.handler;

import com.sun.istack.logging.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccessDenialHandler implements AccessDeniedHandler {
    /**
     * Logger.
     */
    public static final Logger LOG
            = Logger.getLogger(AccessDenialHandler.class);

    /**
     * Access denied redirect.
     * @param request - req
     * @param response - res
     * @param exc - exception
     * @throws IOException - except
     * @throws ServletException - except
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc) throws IOException, ServletException {

        Authentication auth
                = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            LOG.warning("User: " + auth.getName()
                    + " attempted to access the protected URL: "
                    + request.getRequestURI());
        }
        response.sendRedirect(request.getContextPath() + "/accessDenied");
    }

}
