package edu.missouri.cf.projex4.ui.desktop;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import c10n.C10N;

import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.data.system.core.objects.Objects;
import edu.missouri.cf.projex4.data.system.core.securitygroups.SecurityGroupUsers;
import edu.missouri.cf.projex4.data.system.core.workflows.WorkflowApprovals;
import edu.missouri.cf.projex4.ui.c10n.workflow.WorkflowText;
import edu.missouri.cf.projex4.ui.common.status.StatusChangeButton;
import edu.missouri.cf.projex4.ui.common.system.StandardFormEditControls;
import edu.missouri.cf.projex4.ui.common.workflow.WorkflowEnabled;
import edu.missouri.cf.projex4.ui.common.workflow.WorkflowHistoryComponent;
import edu.missouri.cf.projex4.ui.common.workflow.WorkflowManipulator;

/**
 * All common screen related work flow methods and variables should go here to
 * simplify implementation of work flow.
 * 
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class WorkflowEnabledEditorView extends StandardEditorView implements WorkflowEnabled {

	@PropertyId("APPROVALNOTES")
	private TextArea approvalComments;

	protected Button disapproveButton;
	protected Button approveButton;
	protected Button abortButton;
	protected Button holdButton;
	protected Button reviseButton;

	private Button firstRevisorButton;

	protected Button skipButton;

	private boolean workflowAllowed = true;

	private boolean disapproveAllowed = false;
	private boolean approveAllowed = true;
	private boolean abortAllowed = true;
	private boolean holdAllowed = true;
	private boolean reviseAllowed = true;

	private boolean skipAllowed = false;
	private boolean firstRevisorAllowed = false;

	private Label label_1;
	private VerticalLayout workflow;
	protected WorkflowHistoryComponent workflowHistory;
	private ObjectData workflowObject;
	protected WorkflowManipulator workflowManipulator;
	private Item approvalItem;
	private WorkflowComponentState currentState = WorkflowComponentState.HIDDEN;
	private WorkflowText st;
	private OracleContainer approvalContainer;

	private String workflowApplicationName;

	protected ArrayList<Validator> approvalValidators = new ArrayList<Validator>();

	private HorizontalLayout skippedPrevious;

	private boolean previousSkipped;

	private boolean approvalCommentsChanged;

	protected void addApprovalValidator(Validator validator) {
		approvalValidators.add(validator);
	}

	public void setWorkflowApplicationName(ProjexViewProvider.Views view) {
		workflowApplicationName = view.name();
		workflowManipulator.setApplicationName(workflowApplicationName);
	}

	public WorkflowEnabledEditorView() {
	}

	@Override
	public void attach() {

		super.attach();

		if (User.getUser() != null) {
			st = C10N.get(WorkflowText.class, User.getUser().getUserLocale());
		} else {
			st = C10N.get(WorkflowText.class, Locale.ENGLISH);
		}

		label_1 = new Label() {
			{
				setContentMode(ContentMode.HTML);
			}
		};
		
		approvalCommentsChanged = false;
		
		approvalComments = new TextArea() {
			{
				setCaption(st.comments());
				setDescription(st.comments_help());
				setImmediate(false);
				setWidth("100%");
				setNullRepresentation("");
				
				addValueChangeListener(new ValueChangeListener() {

					@Override
					public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
						
						if(!approvalComments.isReadOnly()) {
							approvalCommentsChanged = true;
						}
						
					}});
				
			}
		};

		approveButton = new Button() {
			{
				setCaption(st.approveButton());
				setImmediate(true);
				setStyleName("workflowbutton");
				setIcon(new ThemeResource("icons/workflow/Green_Normal_24x24.png"));
				setDescription(st.approveButton_help());

				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

						try {

							java.util.Date start = new java.util.Date();

							for (Validator validator : approvalValidators) {

								if (logger.isDebugEnabled()) {
									logger.debug("Calling validator");
								}

								validator.validate(null);
								System.err.println("Validator " + validator.getClass().getCanonicalName() + "took " + (new java.util.Date().getTime() - start.getTime()) + "ms");

							}

							approvalComments.commit();
							approvalContainer.commit();
							System.err.println("Workflow commits took " + (new java.util.Date().getTime() - start.getTime()) + "ms");

							workflowManipulator.workflowApprove();
							System.err.println("WorkflowApprove took " + (new java.util.Date().getTime() - start.getTime()) + "ms");

							resetScreen();

						} catch (UnsupportedOperationException e) {
							logger.error("Could not save approval", e);
						} catch (SQLException e) {
							logger.error("Could not save approval", e);
						} catch (InvalidValueException ive) {

							if (logger.isErrorEnabled()) {
								logger.error("Invalid Value Exception", ive);
							}

						}

					}

				});
			}
		};

		disapproveButton = new Button() {
			{
				setCaption(st.disapproveButton());
				setImmediate(true);
				setStyleName("workflowbutton");
				setIcon(new ThemeResource("icons/workflow/Red_Normal_24x24.png"));
				setDescription(st.disapproveButton_help());

				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						logger.debug("Workflow Disapprove Clicked");
						if (approvalComments.getValue() == null || "".equals(approvalComments.getValue()) || !approvalCommentsChanged ) {
							Notification.show(st.comments_error());
						} else {

							try {

								approvalComments.commit();
								approvalContainer.commit();

							} catch (UnsupportedOperationException e) {
								logger.error("Could not save rejection", e);
							} catch (SQLException e) {
								logger.error("Could not save rejection", e);
							}

							workflowManipulator.workflowReject();
							resetScreen();
						}
					}

				});
			}
		};

		holdButton = new Button() {
			{
				setCaption(st.holdButton());
				setImmediate(true);
				setStyleName("workflowbutton");
				setIcon(new ThemeResource("icons/workflow/Cyan_Normal_24x24.png"));
				setDescription(st.holdButton_help());

				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						logger.debug("Workflow Hold Clicked");
						if (approvalComments.getValue() == null || "".equals(approvalComments.getValue()) || !approvalCommentsChanged) {
							Notification.show(st.comments_error());
						} else {

							try {

								approvalComments.commit();
								approvalContainer.commit();

							} catch (UnsupportedOperationException e) {
								logger.error("Could not save hold", e);
							} catch (SQLException e) {
								logger.error("Could not save hold", e);
							}

							workflowManipulator.workflowHold();
							resetScreen();
						}
					}
				});
			}
		};

		reviseButton = new Button() {
			{
				setCaption(st.reviseButton());
				setDescription(st.reviseButton_help());
				setStyleName("workflowbutton");
				setIcon(new ThemeResource("icons/workflow/Cyan_Normal_24x24.png"));
				setImmediate(true);

				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						workflowManipulator.workflowRevise();
						approvalComments.setEnabled(false);
						approvalCommentsChanged = false;
						resetScreen();
						controls.setEnabled(true);
					}
				});
			}
		};

		abortButton = new Button() {
			{

				setCaption(st.abortButton());
				setImmediate(true);
				setStyleName("workflowbutton");
				setIcon(new ThemeResource("icons/workflow/Grey_Normal_24x24.png"));
				setDescription(st.abortButton_help());

				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

						logger.debug("Workflow abort clicked {}", currentState);

						try {

							switch (currentState) {
							case REVISOR:
							case APPROVALNEEDED:

								if (logger.isDebugEnabled()) {
									logger.debug("REVISOR/APPROVER ABORT");
								}

								approvalComments.commit();

								if (approvalComments.getValue() == null || "".equals(approvalComments.getValue()) || !approvalCommentsChanged) {

									Notification.show(st.comments_error());
									break;

								} else {

									approvalContainer.commit();
									workflowManipulator.workflowAbort();
									resetScreen();

								}

								break;

							case INITIATOR:

								if (logger.isDebugEnabled()) {
									logger.debug("INITIATOR ABORT");
								}

								workflowManipulator.workflowAbort();
								resetScreen();
								break;

							case HIDDEN:
								if (logger.isDebugEnabled()) {
									logger.debug("HIDDEN ABORT");
								}
								workflowManipulator.workflowAbort();
								resetScreen();
								break;

							default:

								if (logger.isDebugEnabled()) {
									logger.debug("DEFAULT ABORT");
								}
								break;

							}

						} catch (UnsupportedOperationException e) {
							logger.error("Could not save approval", e);
						} catch (SQLException e) {
							logger.error("Could not save approval", e);
						}

					}
				});
			}
		};

		skipButton = new Button() {
			{
				setCaption(st.skipButton());
				setImmediate(true);
				setStyleName("workflowbutton");
				setIcon(new ThemeResource("icons/workflow/Cyan_Normal_24x24.png"));
				setDescription(st.skipButton_help());

				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

						logger.debug("Workflow Skip Clicked");
						if (approvalComments.getValue() == null || "".equals(approvalComments.getValue()) || !approvalCommentsChanged ) {
							Notification.show(st.comments_error());
						} else {

							try {

								approvalComments.commit();
								approvalContainer.commit();

							} catch (UnsupportedOperationException e) {
								logger.error("Could not save skip", e);
							} catch (SQLException e) {
								logger.error("Could not save skip", e);
							}

							workflowManipulator.workflowSkip();
							resetScreen();
						}

					}
				});
			}
		};

		// TODO 4.1 Enhancement
		// This should be a popup button that lists previous approvers.

		firstRevisorButton = new Button() {
			{
				setCaption(st.previousButton());
				setDescription(st.previousButton_help());
				setImmediate(true);
				setStyleName("workflowbutton");
				setIcon(new ThemeResource("icons/workflow/Cyan_Normal_24x24.png"));

				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(Button.ClickEvent event) {

						logger.debug("Workflow First Revisor Clicked");
						if (approvalComments.getValue() == null || "".equals(approvalComments.getValue()) || !approvalCommentsChanged ) {
							Notification.show(st.comments_error());
						} else {

							try {

								approvalComments.commit();
								approvalContainer.commit();
								workflowManipulator.workflowFirstRevisor(getObjectData().getObjectId());

							} catch (UnsupportedOperationException e) {
								logger.error("Could execute First Revisor", e);
							} catch (SQLException e) {
								logger.error("Could not execute First Revisor", e);

							}

							resetScreen();

						}
					}

				});

			}
		};

		workflowHistory = new WorkflowHistoryComponent();
		workflowHistory.setHeight("200px");
		workflowManipulator = new WorkflowManipulator();

		skippedPrevious = new HorizontalLayout() {
			{
				addComponent(new Label("Previous approver was skipped due to response time-out."));
				addStyleName("skippedapprover");
				setWidth("100%");
			}
		};

		workflow = new VerticalLayout() {
			{

				setStyleName("workflow");
				addComponent(new HorizontalLayout() {
					{
						setMargin(false);
						setSpacing(true);
						addComponent(label_1);
						addComponent(approveButton);
						addComponent(abortButton);
						addComponent(reviseButton);
						addComponent(holdButton);
						addComponent(skipButton);
						addComponent(firstRevisorButton);
						addComponent(disapproveButton);
					}
				});
				addComponent(approvalComments);
			}
		};

		addComponent(skippedPrevious, getComponentIndex(notificationLayout));
		addComponent(workflow, getComponentIndex(notificationLayout));

	}

	@Override
	public void setScreenData(String parameters) {

		String uuid = parameters;

		logger.debug("WorkflowEnabledEditorView.setScreenData({})", parameters);

		workflowObject = Objects.getObjectDataFromUUID(uuid);

		if (workflowObject == null) {

			logger.debug("workflowObject not found");

		} else if (workflowObject.isInWorkFlow()) {

			logger.debug("Screen is in Workflow");

			String userId = User.getUser().getUserId();
			

			try {

				WorkflowApprovals workflowApprovals = new WorkflowApprovals();
				workflowApprovals.removeMandatoryFilters();

				String currentApprovalId = WorkflowApprovals.getCurrentApproval(workflowManipulator.getWorkflowName(), workflowObject.getObjectId());
				String previousApprovalId = WorkflowApprovals.getPreviousApproval(workflowManipulator.getWorkflowName(), workflowObject.getObjectId());

				previousSkipped = "SKIP".equals(WorkflowApprovals.getApprovalStatus(previousApprovalId));

				logger.error("current approvalId = {}", currentApprovalId);

				workflowApprovals.setMandatoryFilters(new Compare.Equal("ID", currentApprovalId));
				approvalContainer = new OracleContainer(workflowApprovals);
				approvalItem = approvalContainer.getItemByProperty("ID", currentApprovalId);

				logger.error("Approval Item = {}", approvalItem);
				if (approvalItem != null) {

					if (userId.equals(approvalItem.getItemProperty("USERID").getValue().toString())) {

						bind(approvalItem);

						if ("APPROVER".equals(approvalItem.getItemProperty("APPROVALTYPE").getValue().toString())) {

							logger.debug("User is next Approver");
							setWorkflowComponentState(WorkflowComponentState.APPROVALNEEDED);

						} else if ("REVISOR".equals(approvalItem.getItemProperty("APPROVALTYPE").getValue().toString())) {
							logger.debug("User is next Revisor");
							setWorkflowComponentState(WorkflowComponentState.REVISOR);
						}

					} else if (userId.equals(WorkflowApprovals.getInitiator(workflowManipulator.getWorkflowName(), workflowObject.getObjectId()))) {

						logger.debug("User is initiator");
						setWorkflowComponentState(WorkflowComponentState.INITIATOR);

					} else {

						logger.debug("User is not next Approver or initiator ");
						setWorkflowComponentState(WorkflowComponentState.INWORKFLOW);

					}

				} else {

					// This should not happen.
					if (logger.isDebugEnabled()) {
						logger.debug("Approval Item is null - Workflow needs to be set.");
					}
					setWorkflowComponentState(WorkflowComponentState.INWORKFLOW);

				}
				
				approvalCommentsChanged = false;

				workflowHistory.setData(workflowObject);
				workflowManipulator.setObjectData(workflowObject);
				workflowManipulator.setObjectTitle(workflowObject.getTitle());

			} catch (SQLException e) {
				logger.error("Could Not retrieve approval records");
			}

		} else {

			logger.debug("Screen is not in Workflow");

			if (User.getUser().getUserId().equals(WorkflowApprovals.getInitiator(workflowManipulator.getWorkflowName(), workflowObject.getObjectId()))) {
				logger.debug("User is initiator");

				try {

					WorkflowApprovals workflowApprovals = new WorkflowApprovals();
					workflowApprovals.removeMandatoryFilters();

					String currentApprovalId = WorkflowApprovals.getFinalApproval(workflowManipulator.getWorkflowName(), workflowObject.getObjectId());

					logger.error("current approvalId = {}", currentApprovalId);

					workflowApprovals.setMandatoryFilters(new Compare.Equal("ID", currentApprovalId));
					approvalContainer = new OracleContainer(workflowApprovals);
					approvalItem = approvalContainer.getItemByProperty("ID", currentApprovalId);

					if (approvalItem != null) {

						bind(approvalItem);

						if ("APPROVED".equals(approvalItem.getItemProperty("APPROVALSTATUS").getValue().toString())) {
							setWorkflowComponentState(WorkflowComponentState.INITIATORAPPROVED);
						} else {
							setWorkflowComponentState(WorkflowComponentState.INITIATORAFTERWORKFLOW);
						}

					} else {
						setWorkflowComponentState(WorkflowComponentState.HIDDEN);

					}

				} catch (SQLException e) {

					if (logger.isErrorEnabled()) {
						logger.error("Could not retrieve final approval", e);
					}
					setWorkflowComponentState(WorkflowComponentState.HIDDEN);

				}

			} else {
				setWorkflowComponentState(WorkflowComponentState.HIDDEN);
			}

			workflowHistory.setData(workflowObject);
			workflowManipulator.setObjectData(workflowObject);
			workflowManipulator.setObjectTitle(workflowObject.getTitle());

		}

	}

	@Override
	public WorkflowComponentState getWorkflowComponentState() {
		return currentState;
	}

	@Override
	public void setWorkflowComponentState(WorkflowComponentState state) {

		// Control state of edit/save and change status buttons.

		if (logger.isDebugEnabled()) {
			logger.debug("Current WorkflowComponentState = {}", state);
		}

		currentState = state;

		switch (state) {

		case APPROVALNEEDED:

			skippedPrevious.setVisible(previousSkipped);
			workflow.setVisible(true);

			if (workflowAllowed) {

				approvalComments.setEnabled(true);
				approvalComments.setVisible(true);

				disapproveButton.setEnabled(disapproveAllowed);
				disapproveButton.setVisible(disapproveAllowed);

				approveButton.setEnabled(approveAllowed);
				approveButton.setVisible(approveAllowed);

				abortButton.setEnabled(abortAllowed);
				abortButton.setVisible(abortAllowed);

				holdButton.setEnabled(holdAllowed);
				holdButton.setVisible(holdAllowed);

				skipButton.setVisible(false);
				skipButton.setEnabled(false);

				reviseButton.setVisible(false);
				reviseButton.setEnabled(false);

				firstRevisorButton.setVisible(firstRevisorAllowed);
				firstRevisorButton.setEnabled(firstRevisorAllowed);

				label_1.setValue("<b>" + st.approvalNeededInstructions() + "</b>");
				label_1.setVisible(true);

				controls.setEnabled(false);

			} else {

				approvalComments.setEnabled(false);
				approvalComments.setVisible(false);

				disapproveButton.setEnabled(false);
				disapproveButton.setVisible(false);

				approveButton.setEnabled(false);
				approveButton.setVisible(false);

				abortButton.setEnabled(false);
				abortButton.setVisible(false);

				holdButton.setEnabled(false);
				holdButton.setVisible(false);

				skipButton.setVisible(false);
				skipButton.setEnabled(false);

				reviseButton.setVisible(false);
				reviseButton.setEnabled(false);

				firstRevisorButton.setVisible(false);
				firstRevisorButton.setEnabled(false);

				label_1.setValue("<b>" + st.approvalNeededInstructionsOtherScreen() + "</b>");
				label_1.setVisible(true);

				controls.setEnabled(false);

			}

			break;

		case REVISOR:

			skippedPrevious.setVisible(previousSkipped);
			workflow.setVisible(true);

			if (workflowAllowed) {

				approvalComments.setEnabled(true);
				approvalComments.setVisible(true);

				disapproveButton.setEnabled(disapproveAllowed);
				disapproveButton.setVisible(disapproveAllowed);

				approveButton.setEnabled(approveAllowed);
				approveButton.setVisible(approveAllowed);

				abortButton.setEnabled(abortAllowed);
				abortButton.setVisible(abortAllowed);

				reviseButton.setVisible(reviseAllowed);
				reviseButton.setEnabled(reviseAllowed);

				holdButton.setEnabled(holdAllowed);
				holdButton.setVisible(holdAllowed);

				skipButton.setVisible(false);
				skipButton.setEnabled(false);

				firstRevisorButton.setVisible(firstRevisorAllowed);
				firstRevisorButton.setEnabled(firstRevisorAllowed);

				label_1.setValue("<b>" + st.revisorInstructions() + "</b>");
				label_1.setVisible(true);
				controls.setEnabled(false);

			} else {

				approvalComments.setEnabled(false);
				approvalComments.setVisible(false);

				disapproveButton.setEnabled(false);
				disapproveButton.setVisible(false);

				approveButton.setEnabled(false);
				approveButton.setVisible(false);

				abortButton.setEnabled(false);
				abortButton.setVisible(false);

				reviseButton.setVisible(false);
				reviseButton.setEnabled(false);

				holdButton.setEnabled(false);
				holdButton.setVisible(false);

				skipButton.setVisible(false);
				skipButton.setEnabled(false);

				firstRevisorButton.setVisible(false);
				firstRevisorButton.setEnabled(false);

				label_1.setValue("<b>" + st.revisorInstructionsOtherScreen() + "</b>");
				label_1.setVisible(true);
				controls.setEnabled(false);

			}

			break;

		default:
		case HIDDEN:

			skippedPrevious.setVisible(false);
			workflow.setVisible(false);
			approvalComments.setEnabled(false);
			approvalComments.setVisible(false);
			disapproveButton.setEnabled(false);
			disapproveButton.setVisible(false);
			approveButton.setEnabled(false);
			approveButton.setVisible(false);
			abortButton.setEnabled(false);
			abortButton.setVisible(false);
			reviseButton.setVisible(false);
			reviseButton.setEnabled(false);
			holdButton.setEnabled(false);
			holdButton.setVisible(false);
			skipButton.setVisible(false);
			skipButton.setEnabled(false);
			firstRevisorButton.setVisible(false);
			firstRevisorButton.setEnabled(false);
			label_1.setVisible(false);
			controls.setEnabled(true);
			break;

		case INITIATOR:

			skippedPrevious.setVisible(false);
			workflow.setVisible(true);
			approvalComments.setEnabled(false);

			if (approvalItem != null && approvalItem.getItemProperty("STATUS") != null && approvalItem.getItemProperty("STATUS").getValue() != null && "HOLD".equals(approvalItem.getItemProperty(
					"STATUS").getValue().toString())) {
				approvalComments.setReadOnly(true);
				approvalComments.setVisible(true);
			} else {
				approvalComments.setVisible(false);
			}

			disapproveButton.setEnabled(false);
			disapproveButton.setVisible(false);
			approveButton.setEnabled(false);
			approveButton.setVisible(false);
			abortButton.setEnabled(true);
			abortButton.setVisible(true);
			reviseButton.setVisible(false);
			reviseButton.setEnabled(false);
			holdButton.setEnabled(false);
			holdButton.setVisible(false);
			skipButton.setVisible(false);
			skipButton.setEnabled(false);
			firstRevisorButton.setVisible(false);
			firstRevisorButton.setEnabled(false);
			label_1.setVisible(true);
			label_1.setValue("<b>" + st.initiatorInstructions() + "</b>");
			controls.setEnabled(false);
			break;

		case INITIATORAFTERWORKFLOW:

			skippedPrevious.setVisible(false);
			workflow.setVisible(true);

			approvalComments.setReadOnly(true);
			approvalComments.setVisible(true);

			disapproveButton.setEnabled(false);
			disapproveButton.setVisible(false);
			approveButton.setEnabled(false);
			approveButton.setVisible(false);
			abortButton.setEnabled(false);
			abortButton.setVisible(false);
			reviseButton.setVisible(false);
			reviseButton.setEnabled(false);
			holdButton.setEnabled(false);
			holdButton.setVisible(false);
			skipButton.setVisible(false);
			skipButton.setEnabled(false);
			firstRevisorButton.setVisible(false);
			firstRevisorButton.setEnabled(false);
			label_1.setVisible(true);
			label_1.setValue("<b>" + st.initiatorAfterWorkflow() + "</b>");
			controls.setEnabled(true);
			break;

		case INITIATORAPPROVED:

			skippedPrevious.setVisible(false);
			workflow.setVisible(true);

			approvalComments.setReadOnly(true);
			approvalComments.setVisible(false);

			disapproveButton.setEnabled(false);
			disapproveButton.setVisible(false);
			approveButton.setEnabled(false);
			approveButton.setVisible(false);
			abortButton.setEnabled(false);
			abortButton.setVisible(false);
			reviseButton.setVisible(false);
			reviseButton.setEnabled(false);
			holdButton.setEnabled(false);
			holdButton.setVisible(false);
			skipButton.setVisible(false);
			skipButton.setEnabled(false);
			firstRevisorButton.setVisible(false);
			firstRevisorButton.setEnabled(false);
			label_1.setVisible(true);
			label_1.setValue("<b>" + st.initiatorApproved() + "</b>");
			controls.setEnabled(true);
			break;

		case INWORKFLOW:

			skippedPrevious.setVisible(false);
			workflow.setVisible(true);
			approvalComments.setVisible(true);

			// Use of deprecated method is required here.
			if (User.canDo(User.getUser().getUserId(), "WORKFLOWADMIN", null, "WORKFLOWADMIN.OVERRIDE") || SecurityGroupUsers.memberOf("ADMINISTRATORS", User.getUser().getUserId())
					|| SecurityGroupUsers.memberOf("WORKFLOWADMINS", User.getUser().getUserId())) {

				approvalComments.setEnabled(true);
				// approvalComments.setVisible(true);

				disapproveButton.setEnabled(disapproveAllowed);
				disapproveButton.setVisible(disapproveAllowed);

				approveButton.setEnabled(approveAllowed);
				approveButton.setVisible(approveAllowed);

				abortButton.setEnabled(abortAllowed);
				abortButton.setVisible(abortAllowed);

				reviseButton.setVisible(false);
				reviseButton.setEnabled(false);

				holdButton.setEnabled(false);
				holdButton.setVisible(false);

				skipButton.setVisible(skipAllowed);
				skipButton.setEnabled(skipAllowed);

				firstRevisorButton.setVisible(firstRevisorAllowed);
				firstRevisorButton.setEnabled(firstRevisorAllowed);

				label_1.setVisible(true);
				label_1.setValue("<b>" + st.administratorInstructions() + "</b>");

			} else {

				approvalComments.setEnabled(false);
				// approvalComments.setVisible(false);

				disapproveButton.setEnabled(false);
				disapproveButton.setVisible(false);

				approveButton.setEnabled(false);
				approveButton.setVisible(false);

				abortButton.setEnabled(false);
				abortButton.setVisible(false);

				reviseButton.setVisible(false);
				reviseButton.setEnabled(false);

				holdButton.setEnabled(false);
				holdButton.setVisible(false);

				skipButton.setVisible(false);
				skipButton.setEnabled(false);

				firstRevisorButton.setVisible(false);
				firstRevisorButton.setEnabled(false);

				label_1.setVisible(true);
				label_1.setValue("<b>" + st.inWorkFlowInstructions() + "</b>");

			}

			controls.setEnabled(false);
			break;

		}

	}

	@Override
	public void setApplicationName(String applicationName) {
		super.setApplicationName(applicationName);
		if (workflowApplicationName == null) {
			workflowManipulator.setApplicationName(applicationName);
		}
	}

	public void setApplicationName(ProjexViewProvider.Views view) {
		setApplicationName(view.name());
	}

	public void setWorkflowName(String workflowName) {
		workflowManipulator.setWorkflowName(workflowName);
	}

	public void setWorkflowDataItem(Item item, String propertyId) {
		workflowManipulator.setDataItem(item, propertyId);
	}

	public void setInitiatorStatus(String status) {

		if (controls instanceof StandardFormEditControls) {
			StatusChangeButton scb = (StatusChangeButton) ((StandardFormEditControls) controls).getStatusChangeButton();
			scb.setWorkflowManipulator(workflowManipulator, status);
		}

	}

	public void addInitiatorStatus(String status) {
		if (controls instanceof StandardFormEditControls) {
			StatusChangeButton scb = (StatusChangeButton) ((StandardFormEditControls) controls).getStatusChangeButton();
			scb.addInitiatorStatus(status);
		}
	}

	public void setInitiatorOnly(boolean initiatorOnly) {

		if (controls instanceof StandardFormEditControls) {
			StatusChangeButton scb = (StatusChangeButton) ((StandardFormEditControls) controls).getStatusChangeButton();
			scb.setInitiatorOnly(initiatorOnly);
		}

	}

	public void setObjectTitle(String objectTitle) {
		workflowManipulator.setObjectTitle(objectTitle);
	}

	@Override
	public void setObjectData(ObjectData objectData) {
		super.setObjectData(objectData);
		workflowManipulator.setObjectData(objectData);
	}

	public void initializeEngine() {
		logger.error("WorkflowEnabledEditorView.initializeEngine");
		workflowManipulator.initializeEngine();
	}

	/**
	 * @return the disapproveAllowed
	 */
	public boolean isDisapproveAllowed() {
		return disapproveAllowed;
	}

	/**
	 * @param disapproveAllowed
	 *            the disapproveAllowed to set
	 */
	public void setDisapproveAllowed(boolean disapproveAllowed) {
		this.disapproveAllowed = disapproveAllowed;
	}

	/**
	 * @return the approveAllowed
	 */
	public boolean isApproveAllowed() {
		return approveAllowed;
	}

	/**
	 * @param approveAllowed
	 *            the approveAllowed to set
	 */
	public void setApproveAllowed(boolean approveAllowed) {
		this.approveAllowed = approveAllowed;
	}

	/**
	 * @return the abortAllowed
	 */
	public boolean isAbortAllowed() {
		return abortAllowed;
	}

	/**
	 * @param abortAllowed
	 *            the abortAllowed to set
	 */
	public void setAbortAllowed(boolean abortAllowed) {
		this.abortAllowed = abortAllowed;
	}

	/**
	 * @return the holdAllowed
	 */
	public boolean isHoldAllowed() {
		return holdAllowed;
	}

	/**
	 * @param holdAllowed
	 *            the holdAllowed to set
	 */
	public void setHoldAllowed(boolean holdAllowed) {
		this.holdAllowed = holdAllowed;
	}

	/**
	 * @return the reviseAllowed
	 */
	public boolean isReviseAllowed() {
		return reviseAllowed;
	}

	/**
	 * @param reviseAllowed
	 *            the reviseAllowed to set
	 */
	public void setReviseAllowed(boolean reviseAllowed) {
		this.reviseAllowed = reviseAllowed;
	}

	/**
	 * @return the skipAllowed
	 */
	public boolean isSkipAllowed() {
		return skipAllowed;
	}

	/**
	 * @param skipAllowed
	 *            the skipAllowed to set
	 */
	public void setSkipAllowed(boolean skipAllowed) {
		this.skipAllowed = skipAllowed;
	}

	/**
	 * @return the workflowAllowed
	 */
	public boolean isWorkflowAllowed() {
		return workflowAllowed;
	}

	/**
	 * @param workflowAllowed
	 *            the workflowAllowed to set
	 */
	public void setWorkflowAllowed(boolean workflowAllowed) {
		this.workflowAllowed = workflowAllowed;
		setWorkflowComponentState(getWorkflowComponentState());
	}

	public boolean isFirstRevisorAllowed() {
		return firstRevisorAllowed;
	}

	public void setFirstRevisorAllowed(boolean firstRevisorAllowed) {
		this.firstRevisorAllowed = firstRevisorAllowed;
	}

}
