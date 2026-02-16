package com.akashgill3.springdatastarexamples;

import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TemplateRenderer {

  private final TemplateEngine templateEngine;

  public TemplateRenderer(TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  public String renderTemplate(String templateName, Map<String, Object> attributes) {
    StringOutput output = new StringOutput();
    templateEngine.render(templateName.endsWith(".jte") ? templateName : templateName + ".jte", attributes, output);
    return output.toString();
  }

  public String renderTemplate(String templateName) {
    StringOutput output = new StringOutput();
    templateEngine.render(templateName.endsWith(".jte") ? templateName : templateName + ".jte", null, output);
    return output.toString();
  }
}
