package com.clouway.http.servlets;

import com.clouway.FakeHttpServletRequest;
import com.clouway.FakeHttpServletResponse;
import com.clouway.core.Session;
import com.clouway.core.SessionsRepository;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.http.Cookie;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class LogoutServletTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private SessionsRepository sessionRepo = context.mock(SessionsRepository.class);

  private LogoutServlet servlet = new LogoutServlet(sessionRepo);
  private FakeHttpServletRequest request = new FakeHttpServletRequest();

  private FakeHttpServletResponse response = new FakeHttpServletResponse();

  @Test
  public void happyPath() throws Exception {
    Session session = new Session("id", "John", new Date());
    request.addCookie(new Cookie("SID", "id"));

    context.checking(new Expectations() {{
      oneOf(sessionRepo).findBySID("id");
      will(returnValue(Optional.of(session)));

      oneOf(sessionRepo).deleteByID("id");
    }});

    servlet.doGet(request, response);
    assertThat(response.getRedirect(), is("/login"));
  }
}