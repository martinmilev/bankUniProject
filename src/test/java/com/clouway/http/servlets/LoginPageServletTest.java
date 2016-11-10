package com.clouway.http.servlets;

import com.clouway.FakeHttpServletRequest;
import com.clouway.FakeHttpServletResponse;
import com.clouway.core.Account;
import com.clouway.core.AccountRepository;
import com.clouway.core.ServletPageRenderer;
import com.google.common.collect.ImmutableMap;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
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

  private AccountRepository repo = context.mock(AccountRepository.class);
  private ServletPageRenderer servletResponseWriter = context.mock(ServletPageRenderer.class);

  private LoginPageServlet servlet = new LoginPageServlet(repo, servletResponseWriter);

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
    FakeHttpServletRequest request = createRequest(
            ImmutableMap.of(
                    "name", "John",
                    "password", "password"
            )
    );
    FakeHttpServletResponse response = createResponse();

    context.checking(new Expectations() {{
      oneOf(repo).getByName("John");
      will(returnValue(Optional.of(new Account("John", "password", 0))));
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
      will(returnValue(Optional.of(new Account("John", "wrong", 0))));

      oneOf(servletResponseWriter).renderPage("login.html", Collections.singletonMap("error", "Wrong password"), response);
    }});

    servlet.doPost(request, response);
  }

  @Test
  public void anotherWrongPassword() throws Exception {
    FakeHttpServletRequest request = createRequest(
            ImmutableMap.of(
                    "name", "John",
                    "password", "123123"
            )
    );
    FakeHttpServletResponse response = createResponse();

    context.checking(new Expectations() {{
      oneOf(repo).getByName("John");
      will(returnValue(Optional.of(new Account("John", "123123", 0))));

    }});

    servlet.doPost(request, response);
    assertThat(response.getRedirect(), is("/"));
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