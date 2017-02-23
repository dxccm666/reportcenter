package edu.missouri.cf.projex4.email;

import java.util.HashMap;

import com.vaadin.data.Item;
import com.vaadin.server.VaadinService;
import com.x5.template.Chunk;
import com.x5.template.Theme;

import edu.missouri.cf.projex4.data.system.core.email.EmailTemplates;

public class ProjexEmailer extends SendMail {

	/* 
	final static String emailhead = "<!DOCTYPE html>" + "<html>" + "<head>"
			+ "<link href=\"//fonts.googleapis.com/css?family=Quattrocento Sans&subset=latin\" rel=\"stylesheet\" type=\"text/css\">"
			+ "</head>"
			+ "<body style=\"font-family: 'Quattrocento Sans', Candara, Tahoma, sans-serif; border:1px solid #cccccc; padding: 0px; margin:6px;\">"
			+ "<div id=\"header\" bgcolor=\"#000000\" style=\"background-color: black; height: 46px; margin-bottom: 48px; color: white; font-size: 2em; vertical-align: middle; padding-left: 6px\"><img src=\"cid:projexlogo\" align=\"middle\" alt=\"MU Logo\" style=\"padding-left: 4px\">Projex</div>"
			+ "<div id=\"content\" style=\"margin-left: 6px; margin-right: 6px;\">";

	final static String emailfoot = "</div>"
			+ "<div id=\"footer\" style=\"margin-top: 48px; padding-left: 6px; padding-top: 14px; padding-bottom: 14px; font-size: 1em;\">Copyright &copy; 2015 &mdash; Curators of the University of Missouri. All rights reserved.  An equal opportunity/access/affirmative action/pro-disabled and veteran employer.</div>"
			+ "</body>" + "</html>";
	*/
	
	final static String templateDirectory = "/home/projex4/templates";

	// TODO Change to system properties.
	protected String projexbg = "/VAADIN/themes/projex4/images/small-mosaic-black1.png";
	protected String projexlogo = "/VAADIN/themes/projex4/images/mulogotb.png";

	// protected ST template;
	protected Chunk head;
	protected Chunk template;
	protected Chunk footer;

	protected static Theme theme = new Theme(templateDirectory,"");

	public ProjexEmailer() {
		super();
		init();
	}
	
	public ProjexEmailer(java.sql.Connection conn) {
		super(conn);
		init();
	}

	public ProjexEmailer(String templateName) {

		super();
		init();
		setTemplate(templateName);

	}
	
	public void init() {
		
		head = theme.makeChunk("projexemail#header");
		footer = theme.makeChunk("projexemail#footer");
		
	}

	public void setTemplate(String templateName) {

		template = theme.makeChunk();
		template.append(EmailTemplates.getTemplate(templateName));

		// TODO THERE IS NO VAADINSERVICE ON A CRONTASK - SHOULD WE SPLIT THIS
		// CLASS INTO ONE FOR JOBS AND ONE OTHERWIST.
		
		if (VaadinService.getCurrent() != null) {
			String imageBasePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
			System.err.println("imageBasePath = " + imageBasePath);
			// String background = imageBasePath + projexbg;
			String logo = imageBasePath + projexlogo;
			System.err.println("logo = " + logo);
			HashMap<String, String> imageMap = new HashMap<String, String>();
			imageMap.put("projexlogo", logo);
			setImageMap(imageMap);
		}

	}

	public void setBody() {
		String body = template.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("Email Body = {}", body);
		}
		setBody(head.toString() + body + footer.toString());
	}

	public void set(String tag, String value) {
		template.set(tag, value);
	}
	
	public Chunk getTemplate() {
		return template;
	}

	public void set(Item item) {

		for (Object propertyId : item.getItemPropertyIds()) {
			if (item.getItemProperty(propertyId).getValue() != null) {
				set(propertyId.toString(), item.getItemProperty(propertyId).getValue().toString());
			}
		}

	}

}
