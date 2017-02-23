package edu.missouri.cf.projex4.ui.desktop;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;

import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import edu.missouri.cf.data.OracleCurrency;
import edu.missouri.cf.data.OracleCurrencyRangeValidator;
import edu.missouri.cf.data.OracleString;
import edu.missouri.cf.data.OracleStringLengthValidator;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.AfterDateValidator;
import edu.missouri.cf.projex4.ui.common.status.StatusChangeButton;
import edu.missouri.cf.projex4.ui.common.system.FormEditControls;
import edu.missouri.cf.projex4.ui.common.system.StandardFormEditControls;
import edu.missouri.cf.projex4.ui.desktop.common.ObjectNotifier;

/**
 * 
 * 
 */
@SuppressWarnings("serial")
public abstract class StandardEditorView extends EditorView implements EditorComponent {

	protected Logger logger = Loggers.getLogger(this.getClass());

	/**
	 * Editing controls for the form. Already initialized.
	 */
	protected FormEditControls controls;

	public StandardEditorView() {
		super();
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	public void setStatusChangeAllowed(boolean allowed) {

		if (controls instanceof StandardFormEditControls) {
			((StandardFormEditControls) controls).getStatusChangeButton().setEnabled(allowed);
			((StandardFormEditControls) controls).getStatusChangeButton().setVisible(allowed);
		}

	}

	protected String objectName;
	protected ObjectNotifier notifier;

	protected class StandardEditorOracleStringLengthValidator extends OracleStringLengthValidator {

		public StandardEditorOracleStringLengthValidator(String errorMessage, Integer minLength, Integer maxLength,
				boolean allowNull) {
			super(errorMessage, minLength, maxLength, allowNull);
		}

		public StandardEditorOracleStringLengthValidator(String errorMessage) {
			super(errorMessage);
		}

		@Override
		protected boolean isValidValue(OracleString value) {

			if (getEditingState() == EditingState.READONLY) {
				return true;
			} else {
				return super.isValidValue(value);
			}

		}

	}

	protected class StandardEditorDateRangeValidator extends DateRangeValidator {

		public StandardEditorDateRangeValidator(String errorMessage, Date minValue, Date maxValue,
				Resolution resolution) {
			super(errorMessage, minValue, maxValue, resolution);
		}

		@Override
		protected boolean isValidValue(java.util.Date value) {
			if (getEditingState() == EditingState.READONLY) {
				return true;
			} else {
				return super.isValidValue(value);
			}
		}

	}

	protected class StandardEditorOracleCurrencyRangeValidator extends OracleCurrencyRangeValidator {

		public StandardEditorOracleCurrencyRangeValidator(String errorMessage, OracleCurrency minValue,
				OracleCurrency maxValue) {
			super(errorMessage, minValue, maxValue);
		}

		@Override
		protected boolean isValidValue(OracleCurrency value) {
			if (getEditingState() == EditingState.READONLY) {
				return true;
			} else {
				return super.isValidValue(value);
			}
		}

	}

	protected class StandardEditorAfterDateValidator extends AfterDateValidator {

		public StandardEditorAfterDateValidator(String mainFieldCaption, PopupDateField... dependentField) {
			super(mainFieldCaption, dependentField);
		}

		@Override
		public void validate(Object value) throws InvalidValueException {

			if (getEditingState() != EditingState.READONLY) {
				super.validate(value);
			}

		}

	}

	public void reload() {
		if (reloadFragment != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("***************************************************** reloadFragment is not null {}",
						reloadFragment);
			}
			setScreenData(reloadFragment);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("**************************************************** reloadFragemnet is null");
			}
			setScreenData(getObjectData().getUUID());
		}
	}

	private void init() {

		editingStateManipulator = new ExtendedEditingStateManipulator();
		controls = new StandardFormEditControls();
		notifier = new ObjectNotifier();

		addStatusChangeListener(new StatusChangeButton.StatusChangeListener() {
			@Override
			public void statusChanged(StatusChangeButton.StatusChangeEvent event) {
				if (logger.isDebugEnabled()) {
					logger.debug("StatusChangeListener fired.  beginning screen reload");
				}
				reload();
			}
		});

	}

	@Override
	public void setEditingState(EditingState state) {
		editingStateManipulator.setEditingState(state);
	}

	FieldGroup binder;

	public void bind(Item item) {

		if (item == null) {

			if (logger.isDebugEnabled()) {
				logger.debug("item to bind is null");
			}

			return;
		}

		try {

			if (logger.isDebugEnabled()) {
				logger.debug("binding item {}", item);
			}

			binder = new FieldGroup(item);
			binder.bindMemberFields(this);

		} catch (NullPointerException npe) {

			if (logger.isErrorEnabled()) {
				logger.error("Null Pointer Exception {}", npe.getCause(), npe);
			}

		}
	}

	protected ArrayList<Validator> validators = new ArrayList<Validator>();

	protected void addValidator(Validator validator) {
		validators.add(validator);
	}

	protected ArrayList<OracleContainer> sqlContainers = new ArrayList<OracleContainer>();

	protected void addOracleContainer(OracleContainer container) {
		sqlContainers.add(container);
	}

	protected void removeOracleContainer(OracleContainer container) {
		sqlContainers.remove(container);
	}

	protected boolean useThreading = true;

	protected void clearOracleContainers() {
		sqlContainers.clear();
	}

	public void validate() throws InvalidValueException {

		removeAllNotifications();

		if (logger.isDebugEnabled()) {
			logger.debug("StandardEditorView.validate");
		}

		for (Validator validator : validators) {
			validator.validate(null);
		}

		for (EditorComponent e : ((ExtendedEditingStateManipulator) editingStateManipulator)
				.getDependentEditorComponents()) {
			e.validate();
		}

	}

	String reloadFragment;

	public void setReloadFragment(String reloadFragment) {
		this.reloadFragment = reloadFragment;
	}

	/**
	 * Commits the binder and sqlcontainer. On success it sets the item and
	 * rebinds the item with its current data. Makes sure the non-editable
	 * components are read only. Disables editing. Extra steps should be handled
	 * in {@link #afterCommit()}, such as updating calculated fields not found
	 * in the OracleContainer.
	 */
	@Override
	public void commit() throws CommitException, SQLException {

		java.util.Date start = new java.util.Date();

		try {

			if (logger.isDebugEnabled()) {
				logger.debug("Attempting to commit standardeditorview");
			}

			if (binder != null) {

				if (logger.isDebugEnabled()) {
					logger.debug("committing binder");
				}

				binder.commit();

				if (logger.isTraceEnabled()) {
					logger.trace("Binder commit - {} ms", (new java.util.Date().getTime() - start.getTime()));
				}

			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("not committing binder - binder is null");
				}
			}

			if (!useThreading) {

				/*
				 * for(Validator validator : validators) {
				 * 
				 * try { validator.validate(null); } catch
				 * (InvalidValueException ive) { throw new
				 * CommitException(ive.getMessage()); } }
				 */

				if (sqlContainers.size() > 0) {

					if (logger.isDebugEnabled()) {
						logger.debug("committing {} sqlContainers", sqlContainers.size());
					}

					for (OracleContainer c : sqlContainers) {

						if (logger.isDebugEnabled()) {
							logger.debug("committing sqlContainer.");
						}

						c.commit();

						if (logger.isTraceEnabled()) {
							logger.trace("SQLContainer {} commit - {} ms", c.getQueryDelegate().getClass(),
									(new java.util.Date().getTime() - start.getTime()));
						}
					}

				} else {

					if (logger.isDebugEnabled()) {
						logger.debug("no sqlContainers to commit");
					}

				}

				/*
				 * if (((ExtendedEditingStateManipulator)
				 * editingStateManipulator).getEditableComponents().size() > 0)
				 * {
				 * 
				 * for (Component e : ((ExtendedEditingStateManipulator)
				 * editingStateManipulator).getEditableComponents()) { if (e
				 * instanceof Buffered) { ((Buffered) e).commit(); } }
				 * 
				 * } else {
				 * 
				 * if (logger.isDebugEnabled()) { logger.debug(
				 * "no editable components to commit."); }
				 * 
				 * }
				 */

				if (((ExtendedEditingStateManipulator) editingStateManipulator).getDependentEditorComponents()
						.size() > 0) {

					if (logger.isDebugEnabled()) {
						logger.debug("committing {} dependent projex editors",
								((ExtendedEditingStateManipulator) editingStateManipulator)
										.getDependentEditorComponents().size());
					}

					for (EditorComponent e : ((ExtendedEditingStateManipulator) editingStateManipulator)
							.getDependentEditorComponents()) {

						if (logger.isDebugEnabled()) {
							logger.debug("committing dependent EditorComponents");
						}

						e.commit();

						if (logger.isTraceEnabled()) {
							logger.trace("DependentEditor {} commit - {} ms", e.getClass().getCanonicalName(),
									(new java.util.Date().getTime() - start.getTime()));
						}
					}

				} else {

					if (logger.isDebugEnabled()) {
						logger.debug("no dependent projex editors");
					}

				}

				afterCommit();

				if (logger.isTraceEnabled()) {
					logger.trace("AfterCommit - {} ms ", (new java.util.Date().getTime() - start.getTime()));
				}

			} else {

				Collection<Callable<Boolean>> runningThreads = new ArrayList<Callable<Boolean>>();
				ExecutorService executor = Executors.newFixedThreadPool(50);

				if (sqlContainers.size() > 0) {

					if (logger.isDebugEnabled()) {
						logger.debug("committing {} sqlContainers", sqlContainers.size());
					}

					for (final OracleContainer c : sqlContainers) {

						if (c != null) {

							if (logger.isDebugEnabled()) {
								logger.debug("Starting thread for committing sqlContainer {}",
										c.getClass().getCanonicalName());
							}

							Callable<Boolean> callable = new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									try {

										java.util.Date start = new java.util.Date();
										c.commit();

										if (logger.isTraceEnabled()) {
											logger.trace("Commit for sqlContainer {} - {} ms",
													c.getClass().getCanonicalName(),
													new java.util.Date().getTime() - start.getTime());
										}
										return true;

									} catch (SQLException e) {

										String queryName = c.getQueryDelegate().getClass().getName();
										if (logger.isErrorEnabled()) {
											logger.error("Error committing {}", queryName, e);
										}

										return false;
									}

								}
							};

							runningThreads.add(callable);
						}
					}

				} else {

					if (logger.isDebugEnabled()) {
						logger.debug("no sqlContainers to commit");
					}

				}

				if (((ExtendedEditingStateManipulator) editingStateManipulator).getDependentEditorComponents()
						.size() > 0) {

					for (final EditorComponent e : ((ExtendedEditingStateManipulator) editingStateManipulator)
							.getDependentEditorComponents()) {

						if (e != null) {

							if (logger.isDebugEnabled()) {
								logger.debug("Starting thread for dependent editor {}",
										e.getClass().getCanonicalName());
							}

							Callable<Boolean> callable = new Callable<Boolean>() {

								@Override
								public Boolean call() throws Exception {

									java.util.Date start = new java.util.Date();

									if (logger.isDebugEnabled()) {
										logger.debug("committing {}", e.getClass().getCanonicalName());
									}

									e.validate();

									if (logger.isTraceEnabled()) {
										logger.trace("Validation of {} - {} ms", e.getClass().getCanonicalName(),
												new java.util.Date().getTime() - start.getTime());
									}

									e.commit();

									if (logger.isTraceEnabled()) {
										logger.trace("Commit of {} - {} ms", e.getClass().getCanonicalName(),
												new java.util.Date().getTime() - start.getTime());
									}

									return true;

								}

							};

							runningThreads.add(callable);

						}

					}

				} else {

					if (logger.isDebugEnabled()) {
						logger.debug("no dependent projex editors");
					}

				}

				List<Future<Boolean>> results = executor.invokeAll(runningThreads);
				executor.shutdown();

				if (logger.isTraceEnabled()) {
					logger.trace("All Threads have completed - {} ms",
							(new java.util.Date().getTime() - start.getTime()));
				}

				for (Future<Boolean> result : results) {
					try {
						result.get();
					} catch (ExecutionException ee) {
						if(logger.isErrorEnabled()) {
							logger.error("thread execution exception occurred",ee);
						}
						// throw new CommitException(ee.getCause().getMessage());
					}
				}

				afterCommit();

				if (logger.isTraceEnabled()) {
					logger.trace("AfterCommit - {} ms", (new java.util.Date().getTime() - start.getTime()));
				}

			}

			if (getObjectData() != null) {

				if (getObjectName() != null) {

					if (logger.isDebugEnabled()) {
						logger.debug("Should send notification for {}", getObjectName());
					}

					notifier.updatedNotification(getObjectData().getObjectId(), getObjectName());
				} else {

					if (logger.isDebugEnabled()) {
						logger.debug("Cannot send notification - no ObjectName is set");
					}

				}

				if (logger.isDebugEnabled()) {
					logger.debug("Should refresh screen for {}", getObjectData().getUUID());
				}

				reload();

			} else if (getObjectId() != null) {

				setScreenData(getObjectId());

			} else {

				if (logger.isDebugEnabled()) {
					logger.debug("Cannot refresh screen - no ObjectData is set");
				}

			}

			setEditingState(EditingState.READONLY);
			removeAllNotifications();
			Notification.show("Saved");

		} catch (SQLException | CommitException e) {

			if (logger.isErrorEnabled()) {
				logger.error("Error on commit", e);
			}

			throw e;

		} catch ( InterruptedException ie) {

			if (logger.isErrorEnabled()) {
				logger.error("Error on commit", ie);
			}

		}

	}

	public void afterCommit() {

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void rollback() {

		java.util.Date start = new java.util.Date();

		if (!useThreading) {

			if (((ExtendedEditingStateManipulator) editingStateManipulator).getDependentEditorComponents().size() > 0) {

				if (logger.isDebugEnabled()) {
					logger.debug("Rolling back {} dependent editor components",
							((ExtendedEditingStateManipulator) editingStateManipulator).getDependentEditorComponents()
									.size());
				}

				for (EditorComponent e : ((ExtendedEditingStateManipulator) editingStateManipulator)
						.getDependentEditorComponents()) {
					logger.debug("rolling back dependent EditorComponent");
					e.rollback();
				}

			} else {

				if (logger.isDebugEnabled()) {
					logger.debug("no dependent editor components to roll back");
				}

			}

			if (binder != null) {

				if (logger.isDebugEnabled()) {
					logger.debug("binder roll back");
				}

				binder.discard();

			} else {

				if (logger.isDebugEnabled()) {
					logger.debug("no binder to roll back");
				}

			}

		} else {

			Collection<Callable<Boolean>> runningThreads = new ArrayList<Callable<Boolean>>();
			ExecutorService executor = Executors.newFixedThreadPool(50);

			if (((ExtendedEditingStateManipulator) editingStateManipulator).getDependentEditorComponents().size() > 0) {

				if (logger.isDebugEnabled()) {
					logger.debug("Rolling back {} dependent editor components",
							((ExtendedEditingStateManipulator) editingStateManipulator).getDependentEditorComponents()
									.size());
				}

				for (final EditorComponent e : ((ExtendedEditingStateManipulator) editingStateManipulator)
						.getDependentEditorComponents()) {

					Callable<Boolean> callable = new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							logger.debug("rolling back dependent EditorComponent");
							e.rollback();
							return Boolean.TRUE;
						}

					};

					runningThreads.add(callable);

				}

			} else {

				if (logger.isDebugEnabled()) {
					logger.debug("no dependent editor components to roll back");
				}

			}

			if (binder != null) {

				Callable<Boolean> callable = new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {

						if (logger.isDebugEnabled()) {
							logger.debug("binder roll back");
						}

						binder.discard();
						return Boolean.TRUE;
					}

				};

				runningThreads.add(callable);

			} else {

				if (logger.isDebugEnabled()) {
					logger.debug("no binder to roll back");
				}

			}

			try {

				List<Future<Boolean>> results = executor.invokeAll(runningThreads);
				executor.shutdown();

			} catch (InterruptedException e) {
				e.printStackTrace();

			}

		}

		if (logger.isDebugEnabled()) {
			logger.debug("rollback completed - " + (new java.util.Date().getTime() - start.getTime()) + " ms.");
		}

		setEditingState(EditingState.READONLY);

	}

	public void clearComponents() {
		((ExtendedEditingStateManipulator) editingStateManipulator).clearComponents();
	}

	public void addEditableComponent(Component c) {
		if (c == null) {
			return;
		}
		((ExtendedEditingStateManipulator) editingStateManipulator).addEditableComponent(c);
	}

	public void addNonEditableComponent(Component c) {

		if (c == null) {
			return;
		}
		((ExtendedEditingStateManipulator) editingStateManipulator).addNonEditableComponent(c);

	}

	public void addDependentProjexEditor(EditorComponent e) {
		if (e == null) {
			return;
		}
		((ExtendedEditingStateManipulator) editingStateManipulator).addDependentEditorComponent(e);
	}

	public void setApplicationName(ProjexViewProvider.Views view) {
		setApplicationName(view.name());
	}

	@Override
	public void setApplicationName(String applicationName) {
		super.setApplicationName(applicationName);
		notifier.setApplicationName(applicationName);
		controls.setApplicationName(applicationName);
	}

	@Override
	public void setObjectData(ObjectData objectData) {
		super.setObjectData(objectData);
		controls.setObjectData(objectData);
		setObjectName(objectData.getTitle());
	}

	@Override
	public void setObjectId(String objectId) {
		super.setObjectId(objectId);
		controls.setObjectId(objectId);
	}

	/**
	 * @return the objectName
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName
	 *            the objectName to set
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
		controls.setObjectName(objectName);
	}

	public void addStatusChangeValidator(Validator validator) {

		if (controls instanceof StandardFormEditControls) {
			((StatusChangeButton) ((StandardFormEditControls) controls).getStatusChangeButton())
					.addValidator(validator);
		}

	}

	public void addStatusChangeValidator(String currentStatusId, String requestedStatusId, Validator validator) {

		if (controls instanceof StandardFormEditControls) {
			((StatusChangeButton) ((StandardFormEditControls) controls).getStatusChangeButton())
					.addValidator(currentStatusId, requestedStatusId, validator);
		}

	}

	public void addStatusChangeListener(StatusChangeButton.StatusChangeListener listener) {

		if (controls instanceof StandardFormEditControls) {
			((StatusChangeButton) ((StandardFormEditControls) controls).getStatusChangeButton())
					.addStatusChangeListener(listener);
		}

	}

}
