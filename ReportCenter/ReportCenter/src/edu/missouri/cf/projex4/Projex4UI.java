package edu.missouri.cf.projex4;

import java.sql.SQLException;

import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity.TransportGuarantee;
import javax.servlet.annotation.WebServlet;

import org.eclipse.birt.report.engine.api.IReportEngine;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c10n.C10N;
import c10n.annotations.DefaultC10NAnnotations;
import edu.missouri.operations.data.AtomicLong;
import edu.missouri.operations.data.OracleConverterFactory;
import edu.missouri.operations.data.ThreadPool;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Main UI class
 */

@SuppressWarnings({ "serial" })
@Theme("projex4")
@Widgetset(value="edu.missouri.cf.projex4.widgetset.Projex4_75on8Widgetset")
@Push
public class Projex4UI extends UI {
	
	@WebServlet(value = "/*", asyncSupported = true)
	@ServletSecurity(@HttpConstraint(transportGuarantee = TransportGuarantee.CONFIDENTIAL))
	@VaadinServletConfiguration(productionMode = true, ui = Projex4UI.class, heartbeatInterval = 300)
	public static class Servlet extends VaadinServlet {
	}

	protected final static transient Logger logger = LoggerFactory.getLogger(Projex4UI.class);

	private ProjexViewNavigator navigator;
	private ProjexViewProvider viewProvider;

	/* These are static, should they be threadLocal? */
	public static ThreadPool threadPool = new ThreadPool();

	public static IReportEngine getReportEngine() {
		return (IReportEngine) VaadinServlet.getCurrent().getServletContext().getAttribute("REPORTENGINE");
	}

	public static Scheduler getScheduler() {
		return (Scheduler) VaadinServlet.getCurrent().getServletContext().getAttribute("SCHEDULER");
	}

	static AtomicLong atomicLong = new AtomicLong(0);

	public static AtomicLong getAtomicLong() {
		return atomicLong;
	}

	@Override
	protected void init(VaadinRequest request) {

		getSession().setConverterFactory(new OracleConverterFactory());
		
		// Initialize Cosmopolitan Internationalization Library
		C10N.configure(new DefaultC10NAnnotations());

		setStyleName("v-app");

		VerticalLayout mainLayout = new VerticalLayout();
		
		mainLayout.setSizeFull();
		setContent(mainLayout);
		

		viewProvider = new ProjexViewProvider();
		navigator = new ProjexViewNavigator(this, mainLayout);
		navigator.addProvider(viewProvider);
		
		/*

		try {

			SystemProperties.put("server.basepath", VaadinService.getCurrent().getBaseDirectory().getAbsolutePath());

		} catch (SQLException e) {

			if (logger.isErrorEnabled()) {
				logger.error("Could not save basepath property", e);
			}

		} */
		
		Page.getCurrent().setTitle("Projex4");

	}

	public ProjexViewNavigator getProjexViewNavigator() {
		return navigator;
	}

	public static Projex4UI get() {
		UI ui = UI.getCurrent();
		if (ui instanceof Projex4UI) {
			return (Projex4UI) ui;
		}
		return null;
	}

}
