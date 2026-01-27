package com.akashgill3.springdatastarexamples.web;

import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ClocksController {

  private final TemplateEngine templateEngine;

  public ClocksController(TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  private String renderTemplate(String templateName, Map<String, Object> attributes) {
    StringOutput output = new StringOutput();
    templateEngine.render(templateName.endsWith(".jte") ? templateName : templateName + ".jte", attributes, output);
    return output.toString();
  }

  private String renderTemplate(String templateName) {
    StringOutput output = new StringOutput();
    templateEngine.render(templateName.endsWith(".jte") ? templateName : templateName + ".jte", null, output);
    return output.toString();
  }
}
