package com.clouway.http.servlets;

import com.clouway.FakeHttpServletRequest;
import com.clouway.FakeHttpServletResponse;
import com.clouway.core.Account;
import com.clouway.core.AccountRepository;
import com.clouway.core.MyClock;
import com.clouway.core.Provider;
import com.clouway.core.ServletPageRenderer;
import com.clouway.core.Session;
import com.clouway.core.SessionsRepository;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class LoginPageServletTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private MyClock clock = context.mock(MyClock.class);
  private AccountRepository repo = context.mock(AccountRepository.class);
  private SessionsRepository sessions = context.mock(SessionsRepository.class);
  private ServletPageRenderer servletResponseWriter = context.mock(ServletPageRenderer.class);
  private Provider<String> uuid = context.mock(Provider.class);

  private LoginPageServlet servlet = new LoginPageServlet(repo, sessions, servletResponseWriter, clock, uuid);

  @Test
  public void happyPath() throws Exception {
    FakeHttpServletRequest request = createRequest(Collections.emptyMap());
    FakeHttpServletResponse response = createResponse();

    context.checking(new Expectations() {{
      oneOf(servletResponseWriter).renderPage("login.html", Collections.singletonMap("error", ""), response);
    }});

    servlet.doGet(request, response);
  }

  @Test
  public void login() throws Exception {
    final Date date = new Date();

    FakeHttpServletRequest request = createRequest(
            ImmutableMap.of(
                    "name", "John",
                    "password", "password"
            )
    );
    FakeHttpServletResponse response = createResponse();

    context.checking(new Expectations() {{

      oneOf(repo).getByName("John");
      will(returnValue(Optional.of(new Account("John", "password", 0.0))));

      oneOf(sessions).save(new Session("id", "John", date));

      oneOf(uuid).get();
      will(returnValue("id"));

      oneOf(clock).getDate();
      will(returnValue(date));

    }});

    servlet.doPost(request, response);
    assertThat(response.getRedirect(), is("/"));
  }

  @Test
  public void wrongUsername() throws Exception {
    FakeHttpServletRequest request = createRequest(
            ImmutableMap.of(
                    "name", "John",
                    "password", "pwd"
            )
    );
    final HttpServletResponse response = createResponse();

    context.checking(new Expectations() {{
      oneOf(repo).getByName("John");
      will(returnValue(Optional.empty()));

      oneOf(servletResponseWriter).renderPage("login.html", Collections.singletonMap("error", "Wrong username"), response);
    }});

    servlet.doPost(request, response);
  }

  @Test
  public void wrongPassword() throws Exception {
    FakeHttpServletRequest request = createRequest(
            ImmutableMap.of(
                    "name", "John",
                    "password", "pwd"
            )
    );
    HttpServletResponse response = createResponse();

    context.checking(new Expectations() {{
      oneOf(repo).getByName("John");
      will(returnValue(Optional.of(new Account("John", "wrong", 0.0))));

      oneOf(servletResponseWriter).renderPage("login.html", Collections.singletonMap("error", "Wrong password"), response);
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