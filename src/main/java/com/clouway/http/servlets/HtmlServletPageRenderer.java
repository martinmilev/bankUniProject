package com.clouway.http.servlets;

import com.clouway.core.HtmlTemplate;
import com.clouway.core.ServletPageRenderer;
import com.google.common.io.ByteStreams;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class HtmlServletPageRenderer implements ServletPageRenderer {

  @Override
  public void renderPage(String pageName, Map<String, Object> params, HttpServletResponse response) throws IOException {
    try {
      HtmlTemplate template = new HtmlTemplate(loadTemplate(pageName));
      for (String each : params.keySet()) {
        template.put(each, params.get(each).toString());
      }
      renderTemplate(response, template.evaluate());
    } catch (IOException e) {
      throw new IllegalStateException(
              "Could not write response to the outputstream of servlet", e);
    }
  }

  private String loadTemplate(String pageName) throws IOException {
    return new String(ByteStreams.toByteArray(
            HtmlServletPageRenderer.class.getResourceAsStream(pageName)));
  }

  private void renderTemplate(HttpServletResponse response, String htmlContent) throws IOException {
    response.setContentType("text/html");
    response.setStatus(HttpURLConnection.HTTP_OK);
    PrintWriter writer = response.getWriter();
    writer.print(htmlContent);
    writer.flush();
  }
}