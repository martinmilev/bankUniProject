package com.clouway.http.servlets;

import com.clouway.core.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
@Singleton
public class TransactionHistoryPageServlet extends HttpServlet {
  private final Integer limit = 20;
  private Integer offset = 0;
  private Boolean nextButton = true;
  private Boolean backButton = false;
  private HistoryRepository historyRepository;
  private TransferRepository transferRepository;
  private ServletPageRenderer renderer;
  private final SessionsRepository sessions;

  @Inject
  public TransactionHistoryPageServlet(HistoryRepository historyRepository, TransferRepository transferRepository, SessionsRepository sessions, ServletPageRenderer renderer) {
    this.historyRepository = historyRepository;
    this.transferRepository = transferRepository;
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
    String username = getName(request);
    String parameter = "Transactions";
    if (request.getParameter("table") != null) {
      parameter = request.getParameter("table");
    }
      String table = "No data available.";
      try {
        if(parameter.equals("Transactions")) {
          List<Transaction> history = historyRepository.getBalanceHistory(username, offset, limit);
          nextButton = history.size() >= 20;
          table = generateHtmlTable(history);
        }
        if(parameter.equals("Transfers")) {
          List<Transfer> history = transferRepository.getHistory(username, offset, limit);
          nextButton = history.size() >= 20;
          table = generateHtmlTable2(history);
        }
        values.put("username", username);
        values.put("transactions", table);
        values.put("next", (nextButton) ? "" : "disabled=\"disabled\"");
        values.put("back", (backButton) ? "" : "disabled=\"disabled\"");

        renderer.renderPage("history.html", values, response);
      } catch (Exception e) {
        e.printStackTrace();
      }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doGet(req,resp);
  }

  private void getTransactionsHistory(HttpServletResponse response, Map<String, Object> values, String username) {

  }

  private void getTransferssHistory(HttpServletResponse response, Map<String, Object> values, String username) {
    String transfer;
    try {
      List<Transfer> history = transferRepository.getHistory(username, offset, limit);
      nextButton = history.size() >= 20;
      transfer = generateHtmlTable2(history);

      values.put("username", username);
      values.put("transactions", transfer);
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

  private String generateHtmlTable2(List<Transfer> list) {
    String transactions = "<div class=\"table-responsive\">\n" +
            "        <table class=\"table\">\n" +
            "            <thead>\n" +
            "            <tr>\n" +
            "                <th>Date</th>\n" +
            "                <th>Sender</th>\n" +
            "                <th>Recipient</th>\n" +
            "                <th>Amount</th>\n" +
            "            </tr>\n" +
            "            </thead>\n" +
            "            <tbody>";

    for (int i = 0; i < list.size(); i++) {
      Transfer record = list.get(i);
      transactions = transactions + "<tr>";
      transactions = transactions + "<td>" + record.date + "</td>";
      transactions = transactions + "<td>" + record.from + "</td>";
      transactions = transactions + "<td>" + record.to + "</td>";
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