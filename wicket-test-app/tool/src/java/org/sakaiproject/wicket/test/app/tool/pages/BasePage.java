package org.sakaiproject.wicket.test.app.tool.pages;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.head.StringHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import org.sakaiproject.wicket.test.app.api.GradingsService;
import org.sakaiproject.wicket.test.app.api.ExportService;

public class BasePage extends WebPage implements IHeaderContributor {
    @SpringBean(name="org.sakaiproject.wicket.test.app.api.ExportService")
    protected ExportService exportService;

    @SpringBean(name="org.sakaiproject.wicket.test.app.api.GradingsService")
    protected GradingsService gradingsService;

    public BasePage() {

    }

    /**
     * This block adds the required wrapper markup to style it like a Sakai tool. 
     * Add to this any additional CSS or JS references that you need.
     */
    public void renderHead(IHeaderResponse response) {
        //get the Sakai skin header fragment from the request attribute
        HttpServletRequest request = (HttpServletRequest)getRequest().getContainerRequest();

        response.render(StringHeaderItem.forString((String)request.getAttribute("sakai.html.head")));
        response.render(OnLoadHeaderItem.forScript("setMainFrameHeight( window.name )"));

        //Tool additions (at end so we can override if required)
        response.render(StringHeaderItem.forString("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"));
        //response.renderCSSReference("css/my_tool_styles.css");
        //response.renderJavascriptReference("js/my_tool_javascript.js");
    }
}
