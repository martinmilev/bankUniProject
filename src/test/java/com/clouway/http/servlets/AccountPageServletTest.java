package com.clouway.http.servlets;

import com.clouway.FakeHttpServletRequest;
import com.clouway.FakeHttpServletResponse;
import com.clouway.core.*;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.http.Cookie;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class AccountPageServletTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private AccountRepository repo = context.mock(AccountRepository.class);
  private SessionsRepository sessions = context.mock(SessionsRepository.class);
  private DailyActivityRepository activityRepository = context.mock(DailyActivityRepository.class);
  private ServletPageRenderer servletResponseWriter = context.mock(ServletPageRenderer.class);

  private AccountPageServlet servlet = new AccountPageServlet(repo, activityRepository, sessions, servletResponseWriter);

  @Test
  public void happyPath() throws Exception {
    FakeHttpServletRequest request = createRequest(Collections.emptyMap());
    FakeHttpServletResponse response = createResponse();
    request.addCookie(new Cookie("SID", "John"));

    Map<String, Object> params = new HashMap<String, Object>() {{
      put("name", "John");
      put("balance", 0.0);
      put("count", 1);
      put("deposits", 8);
      put("withdraws", 5);
    }};

    context.checking(new Expectations() {{
      oneOf(sessions).findBySID("John");
      will(returnValue(Optional.of(new Session("1", "John", new Date()))));

      oneOf(activityRepository).dailyActivity();
      will(returnValue(new HashMap<String, Integer>() {{
        put("Deposit", 8);
        put("Withdraw", 5);
      }}));


      oneOf(repo).getByName("John");
      will(returnValue(Optional.of(new Account("John", "", 0.0))));

      oneOf(sessions).countSessions();
      will(returnValue(1));

      oneOf(servletResponseWriter).renderPage("account.html", params, response);
    }});

    servlet.doGet(request, response);
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