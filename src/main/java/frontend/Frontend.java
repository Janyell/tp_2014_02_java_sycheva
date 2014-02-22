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
    private AtomicLong userIdGenerator = new AtomicLong();
    private final static Map<String, String> UsersData = new HashMap<>();

    public Frontend() {
        UsersData.put("yell", "123");
        UsersData.put("stiff", "12345");
    }

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
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        if (UsersData.containsKey(login) && password.equals(UsersData.get(login))) {
            HttpSession session = request.getSession(true);
            Long userId = userIdGenerator.getAndIncrement();
            session.setAttribute("userId", userId);
            response.sendRedirect("/timer");
        }
        else
            response.sendRedirect("/");
    }
}
