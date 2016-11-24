package com.clouway.http.servlets;

import com.clouway.core.HistoryRepository;
import com.clouway.core.ServletPageRenderer;
import com.clouway.core.Session;
import com.clouway.core.SessionsRepository;
import com.clouway.core.Transaction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
@Singleton
public class TransactionHistoryPageServlet extends HttpServlet {
    private final Integer limit = 20;
    private final HistoryRepository historyRepository;
    private final ServletPageRenderer renderer;
    private final SessionsRepository sessions;
    private Boolean backButton = false;
    private Boolean nextButton = true;
    private Integer offset = 0;

    @Inject
    public TransactionHistoryPageServlet(HistoryRepository historyRepository, SessionsRepository sessions, ServletPageRenderer renderer) {
        this.historyRepository = historyRepository;
        this.sessions =sessions;
        this.renderer = renderer;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> values = new HashMap<String, Object>();
        String button = request.getParameter("action");
        if ("Next".equals(button)) {
            offset += limit;
            backButton = true;
        }
        if ("Back".equals(button) && offset >= 20) {
            offset -= limit;
            nextButton = true;
            if (offset == 0) {
                backButton = false;
            }
        }
        String transaction;
        String username = getName(request);
        try {
            List<Transaction> history = historyRepository.getBalanceHistory(username, offset, limit);
            nextButton = history.size() >= 20;
            transaction = generateHtmlTable(history);

            values.put("username", username);
            values.put("transactions", transaction);
            values.put("next", (nextButton) ? "" : "disabled=\"disabled\"");
            values.put("back", (backButton) ? "" : "disabled=\"disabled\"");

            renderer.renderPage("history.html", values, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateHtmlTable(List<Transaction> list) {
        String transactions = "<div class=\"table-responsive\">\n" +
                "        <table class=\"table\">\n" +
                "            <thead>\n" +
                "            <tr>\n" +
                "                <th>Date</th>\n" +
                "                <th>Name</th>\n" +
                "                <th>Operation</th>\n" +
                "                <th>Amount</th>\n" +
                "            </tr>\n" +
                "            </thead>\n" +
                "            <tbody>";

        for (int i = 0; i < list.size(); i++) {
            Transaction record = list.get(i);
            transactions = transactions + "<tr>";
            transactions = transactions + "<td>" + record.operationDate + "</td>";
            transactions = transactions + "<td>" + record.customerName + "</td>";
            transactions = transactions + "<td>" + record.operationType + "</td>";
            transactions = transactions + "<td>" + record.amount + "</td>";
            transactions = transactions + "</tr>";
        }

        transactions = transactions + "</tbody>\n" +
                "        </table>\n" +
                "    </div>";

        return transactions;
    }

    private String getName(HttpServletRequest req) {
        Session session = null;

        for (Cookie cookie : req.getCookies()) {
            if ("SID".equals(cookie.getName())) {
                session = sessions.findBySID(cookie.getValue()).get();
            }
        }

        return session.username;
    }
}
