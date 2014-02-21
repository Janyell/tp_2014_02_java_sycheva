package frontend;

import templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Frontend extends HttpServlet {
    private final static Map<String, String> USERS_DATA = new HashMap<String, String>() {{
        put("yell", "123");
        put("stiff", "12345");
    }};
    private AtomicLong userIdGenerator = new AtomicLong();

    public static String getTime() {
        DateFormat formatter = new SimpleDateFormat("HH.mm.ss");
        return formatter.format(new Date());
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, Object> pageVariables = new HashMap<>();
        if (request.getPathInfo().equals("/timer")) {
            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null)
                response.sendRedirect("/");
            pageVariables.put("refreshPeriod", "1000");
            pageVariables.put("serverTime", getTime());
            pageVariables.put("userId", userId);
            response.getWriter().println(PageGenerator.getPage("timer.tml", pageVariables));
        }
        else
            response.sendRedirect("/");
        return;
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        if (USERS_DATA.containsKey(login) && password.equals(USERS_DATA.get(login))) {
            HttpSession session = request.getSession();
            if (!session.isNew()) {
                session.invalidate();
                session = request.getSession();
            }
            Long userId = (Long) userIdGenerator.getAndIncrement();
            session.setAttribute("userId", userId);
            response.sendRedirect("/timer");
        }
        else
            response.sendRedirect("/");
        return;
    }
}
