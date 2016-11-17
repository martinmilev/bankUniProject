package com.clouway.http.filters;

import com.clouway.FakeHttpServletRequest;
import com.clouway.FakeHttpServletResponse;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class SecurityFilterTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private FilterChain chain = context.mock(FilterChain.class);

  private FakeHttpServletRequest request;
  private FakeHttpServletResponse response;

  @Before
  public void setUp() throws Exception {
    request = new FakeHttpServletRequest();
    response = new FakeHttpServletResponse();
  }

  @Test
  public void happyPath() throws Exception {
    SecurityFilter filter = new SecurityFilter();
    request.addCookie(new Cookie("SID", "123"));
    request.setRequestURI("/account");

    context.checking(new Expectations() {{
      oneOf(chain).doFilter(request, response);
    }});

    filter.doFilter(request, response, chain);
  }

  @Test
  public void redirectToAccount() throws Exception {
    SecurityFilter filter = new SecurityFilter();
    request.addCookie(new Cookie("SID", "123"));
    request.setRequestURI("/login");

    filter.doFilter(request, response, chain);

    assertThat(response.getRedirect(), is("account"));
  }

  @Test
  public void unauthorisedRequest() throws Exception {
    SecurityFilter filter = new SecurityFilter();
    filter.doFilter(request, response, chain);

    assertThat(response.getRedirect(), is("login"));
  }
}