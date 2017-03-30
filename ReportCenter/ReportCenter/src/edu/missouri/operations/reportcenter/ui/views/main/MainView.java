package edu.missouri.operations.reportcenter.ui.views.main;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;

public class MainView extends TopBarView {

	private TabSheet tabs;
	private StandardTable reports;
	private StandardTable scheduledReports;
	private Button reportButton;
	private Button rescheduleReportButton;
	private Button deleteScheduledReportButton;

	public MainView() {
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	private void init() {

		reports = new StandardTable() {
			{

			}
		};

		scheduledReports = new StandardTable() {
			{

			}
		};

		reportButton = new Button() {
			{
				setCaption("run");
				setDescription("run selected report");
				addStyleName("borderless");
				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						// TODO Auto-generated method stub

					}
				});
			}
		};

		rescheduleReportButton = new Button() {
			{
				setCaption("reschedule");
				setDescription("adjust schedule of selected report");
				addStyleName("borderless");
				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						// TODO Auto-generated method stub
					}
				});
			}
		};

		deleteScheduledReportButton = new Button() {
			{
				setCaption("delete");
				setDescription("delete selected scheduled report.");
				addStyleName("borderless");
				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						// TODO Auto-generated method stub
					}
				});	
			}
		};

		VerticalLayout layout = new VerticalLayout() {
			{
				setSizeFull();
				
				addComponent(new Label("MU Operations ReportCenter Application", ContentMode.HTML) {
					{
						addStyleName("maintitle");
					}
				});
				addComponent(new Label("These are the reports you are authorized to run and schedule.", ContentMode.HTML) {
					{
						addStyleName("instructions");
					}
				});

				tabs = new TabSheet() {
					{
						addTab(new VerticalLayout() {
							{
								setCaption("Reports");
								setSizeFull();
								setMargin(true);
								addComponent(reportButton);
								addComponent(reports);
								setExpandRatio(reports, 1.0f);
							}
						});
						addTab(new VerticalLayout() {
							{
								setCaption("Scheduled Reports");
								setSizeFull();
								setMargin(true);
								addComponent(new HorizontalLayout() {
									{
										addComponent(rescheduleReportButton);
										addComponent(deleteScheduledReportButton);
									}
								});
								addComponent(scheduledReports);
								setExpandRatio(scheduledReports,1.0f);
							}
						});
					}
				};
				
				addComponent(tabs);
				setExpandRatio(tabs, 1.0f);
			}
		};
		addInnerComponent(layout);
		setExpandRatio(layout, 1.0f);

	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
