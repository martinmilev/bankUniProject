package com.clouway.http.servlets;

import com.clouway.FakeHttpServletRequest;
import com.clouway.FakeHttpServletResponse;
import com.clouway.core.*;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
public class TransactionPageServletTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private AccountRepository repository = context.mock(AccountRepository.class);
  private ServletPageRenderer servletResponseWriter = context.mock(ServletPageRenderer.class);
  private SessionsRepository sessions = context.mock(SessionsRepository.class);
  private TransactionPageServlet servlet = new TransactionPageServlet(repository, sessions, servletResponseWriter);

  @Test
  public void happyPath() throws IOException, ServletException {
    FakeHttpServletRequest request = createRequest(Collections.emptyMap());
    FakeHttpServletResponse response = createResponse();
    request.addCookie(new Cookie("SID", "John"));

    context.checking(new Expectations() {{
      oneOf(sessions).findBySID("John");
      will(returnValue(Optional.of(new Session("1", "John", new Date()))));

      oneOf(repository).getByName("John");
      will(returnValue(Optional.of(new Account("John", "password", 3500.0))));

      oneOf(servletResponseWriter).renderPage("transaction.html", new HashMap<String, Object>() {{
        put("name", "John");
        put("balance", 3500.0);
        put("error", "");
      }}, response);
    }});

    servlet.doGet(request, response);
  }

  @Test
  public void deposit() throws IOException, ServletException {
    FakeHttpServletRequest request = createRequest(new HashMap<String, String>() {{
      put("transaction", "deposit");
      put("amount", "500.0");
    }});
    FakeHttpServletResponse response = createResponse();
    request.addCookie(new Cookie("SID", "John"));

    context.checking(new Expectations() {{
      oneOf(sessions).findBySID("John");
      will(returnValue(Optional.of(new Session("1", "John", new Date()))));

      oneOf(repository).getByName("John");
      will(returnValue(Optional.of(new Account("John", "password", 3500.0))));

      oneOf(repository).deposit("John", 500.0);

      oneOf(servletResponseWriter).renderPage("transaction.html", new HashMap<String, Object>() {{
        put("name", "John");
        put("balance", 4000.0);
        put("error", "");
      }}, response);
    }});

    servlet.doPost(request, response);
  }

  @Test
  public void withdraw() throws IOException, ServletException {
    FakeHttpServletRequest request = createRequest(new HashMap<String, String>() {{
      put("transaction", "withdraw");
      put("amount", "500.0");
    }});
    FakeHttpServletResponse response = createResponse();
    request.addCookie(new Cookie("SID", "John"));

    context.checking(new Expectations() {{
      oneOf(sessions).findBySID("John");
      will(returnValue(Optional.of(new Session("1", "John", new Date()))));

      oneOf(repository).getByName("John");
      will(returnValue(Optional.of(new Account("John", "password", 3500.0))));

      oneOf(repository).withdraw("John", 500.0);

      oneOf(servletResponseWriter).renderPage("transaction.html", new HashMap<String, Object>() {{
        put("name", "John");
        put("balance", 3000.0);
        put("error", "");
      }}, response);
    }});

    servlet.doPost(request, response);
  }

  @Test
  public void withdrawBigAmount() throws IOException, ServletException {
    FakeHttpServletRequest request = createRequest(new HashMap<String, String>() {{
      put("transaction", "withdraw");
      put("amount", "5000.0");
    }});
    FakeHttpServletResponse response = createResponse();
    request.addCookie(new Cookie("SID", "John"));

    context.checking(new Expectations() {{
      oneOf(sessions).findBySID("John");
      will(returnValue(Optional.of(new Session("1", "John", new Date()))));

      oneOf(repository).getByName("John");
      will(returnValue(Optional.of(new Account("John", "password", 3500.0))));

      oneOf(servletResponseWriter).renderPage("transaction.html", new HashMap<String, Object>() {{
        put("name", "John");
        put("balance", 3500.0);
        put("error", "Insufficient funds");
      }}, response);
    }});

    servlet.doPost(request, response);
  }

  @Test
  public void unknownUserTransact() throws IOException, ServletException {
    FakeHttpServletRequest request = createRequest(new HashMap<String, String>() {{
      put("transaction", "deposit");
      put("amount", "500.0");
    }});
    FakeHttpServletResponse response = createResponse();
    request.addCookie(new Cookie("SID", "John"));

    context.checking(new Expectations() {{
      oneOf(sessions).findBySID("John");
      will(returnValue(Optional.of(new Session("1", "John", new Date()))));

      oneOf(repository).getByName("John");
      will(returnValue(Optional.empty()));
    }});

    servlet.doPost(request, response);
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
