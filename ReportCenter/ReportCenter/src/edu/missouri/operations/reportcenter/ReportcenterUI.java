package edu.missouri.operations.reportcenter;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.ServletSecurity.TransportGuarantee;

import org.eclipse.birt.report.engine.api.IReportEngine;
import org.quartz.Scheduler;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;

import edu.missouri.operations.data.OracleConverterFactory;
import edu.missouri.operations.reportcenter.ui.IconSet;
import edu.missouri.operations.reportcenter.ui.ReportCenterViewNavigator;
import edu.missouri.operations.reportcenter.ui.ReportCenterViewProvider;

import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import c10n.C10N;
import c10n.annotations.DefaultC10NAnnotations;

@SuppressWarnings("serial")
@Theme("reportcenter")
@Push
public class ReportcenterUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@ServletSecurity(@HttpConstraint(transportGuarantee = TransportGuarantee.CONFIDENTIAL))
	@VaadinServletConfiguration(productionMode = false, ui = ReportcenterUI.class)
	public static class Servlet extends VaadinServlet {
	}
	
	private ReportCenterViewNavigator navigator;
	private ReportCenterViewProvider viewProvider;
	
	public final static IconSet iconSet = new IconSet();
	
	public static IReportEngine getReportEngine() {
		return (IReportEngine) VaadinServlet.getCurrent().getServletContext().getAttribute("REPORTENGINE");
	}

	public static Scheduler getScheduler() {
		return (Scheduler) VaadinServlet.getCurrent().getServletContext().getAttribute("SCHEDULER");
	}

	@Override
	protected void init(VaadinRequest request) {
		
		getSession().setConverterFactory(new OracleConverterFactory());	
		
		C10N.configure(new DefaultC10NAnnotations());
		
		final VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		setContent(layout);
		
		viewProvider = new ReportCenterViewProvider();
		navigator = new ReportCenterViewNavigator(this, layout);
		navigator.addProvider(viewProvider);
		
		Page.getCurrent().setTitle("Operations ReportCenter");
		
	}
	
	public ReportCenterViewNavigator getViewNavigator() {
		return navigator;
	}
	
	public static ReportcenterUI get() {
		UI ui = UI.getCurrent();
		if (ui instanceof ReportcenterUI) {
			return (ReportcenterUI) ui;
		}
		return null;
	}
}