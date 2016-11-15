package com.clouway.http.servlets;

import com.clouway.FakeHttpServletRequest;
import com.clouway.FakeHttpServletResponse;
import com.clouway.core.*;
import com.google.common.collect.Lists;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

import javax.servlet.http.Cookie;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
public class TransactionHistoryPageServletTest {
    private JUnit4Mockery context = new JUnit4Mockery();
    private HistoryRepository repository = context.mock(HistoryRepository.class);
    private ServletPageRenderer renderer = context.mock(ServletPageRenderer.class);
    private SessionsRepository sessions = context.mock(SessionsRepository.class);
    private TransactionHistoryPageServlet historyServlet = new TransactionHistoryPageServlet(repository, sessions, renderer);

    @Test
    public void happyPath() throws SQLException, IOException {
        Timestamp timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
        FakeHttpServletRequest request = createRequest(new HashMap<String, String>() {{
            put("action", null);
        }});
        FakeHttpServletResponse response = createResponse();
        request.addCookie(new Cookie("SID", "Jonathan"));

        context.checking(new Expectations() {{
            oneOf(sessions).findBySID("Jonathan");
            will(returnValue(Optional.of(new Session("1", "Jonathan", new Date()))));

            oneOf(repository).getBalanceHistory("Jonathan", 0, 20);
            will(returnValue(Lists.newArrayList(
                    new Transaction(timestamp, "Jonathan", "Deposit", 50.0),
                    new Transaction(timestamp, "Jonathan", "Deposit", 50.0)
            )));

            oneOf(renderer).renderPage("history.html", new HashMap<String, Object>() {{
                put("username", "Jonathan");
                put("transactions", fakeHtmlTable(new Transaction(timestamp, "Jonathan", "Deposit", 50.0), 2));
                put("next", "disabled=\"disabled\"");
                put("back", "disabled=\"disabled\"");
            }}, response);
        }});

        historyServlet.doGet(request,response);
    }

    private String fakeHtmlTable(Transaction transaction, Integer numberOfTransactions) {
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

        for (int i = 0; i < numberOfTransactions; i++) {
            transactions = transactions + "<tr>";
            transactions = transactions + "<td>" + transaction.operationDate + "</td>";
            transactions = transactions + "<td>" + transaction.customerName + "</td>";
            transactions = transactions + "<td>" + transaction.operationType + "</td>";
            transactions = transactions + "<td>" + transaction.amount + "</td>";
            transactions = transactions + "</tr>";
        }
        transactions = transactions + "</tbody>\n" +
                "        </table>\n" +
                "    </div>";

        return transactions;
    }

    private FakeHttpServletRequest createRequest(Map<String, String> params) {
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        for (String each : params.keySet()) {
            request.setParameter(each, params.get(each));
        }
        return request;
    }

    private FakeHttpServletResponse createResponse() {
        FakeHttpServletResponse response = new FakeHttpServletResponse();
        response.setWriter(new PrintWriter(new ByteArrayOutputStream()));
        return response;
    }
}
