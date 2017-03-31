package edu.missouri.operations.ui.desktop.scheduler;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.hene.flexibleoptiongroup.FlexibleOptionGroup;
import org.vaadin.hene.flexibleoptiongroup.FlexibleOptionGroupItemComponent;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class SchedulerComponent extends VerticalLayout {

	protected Logger logger = LoggerFactory.getLogger(SchedulerComponent.class);

	public enum ScheduleType {
		NOW, ONETIME, MINUTES, HOUR, EACHDAY, DAYS, YEARS, EACHWEEK
	}

	ScheduleType[] enabledTypes;

	private static final ScheduleType[] defaultTypes = new ScheduleType[] { ScheduleType.NOW, ScheduleType.ONETIME, ScheduleType.MINUTES, ScheduleType.HOUR, ScheduleType.EACHDAY, ScheduleType.EACHWEEK, ScheduleType.DAYS,
			ScheduleType.YEARS };

	public void setEnabledTypes(ScheduleType... types) {
		this.enabledTypes = types;
	}

	private static final String CAPTION_PROPERTY = "caption";

	protected FlexibleOptionGroup flexibleOptionGroup;

	private Label headerLabel;

	private Minutes minutes;

	private Hours hours;

	private OneTime onetime;

	private Now now;

	private Days days;

	private Years years;

	private EachDay eachday;
	
	private boolean slowRunning;

	private EachWeek eachweek;

	public void setSlowRunning(boolean slowRunning) {
		this.slowRunning = slowRunning;
		if (slowRunning) {

		}
	}

	public SchedulerComponent() {

		setEnabledTypes(defaultTypes);
		init();
		layout();

	}

	public SchedulerComponent(boolean slowRunning) {
		this.slowRunning = slowRunning;

		if (slowRunning) {
			setEnabledTypes(ScheduleType.ONETIME, ScheduleType.EACHDAY, ScheduleType.EACHWEEK, ScheduleType.DAYS, ScheduleType.YEARS);
		} else {
			setEnabledTypes(ScheduleType.NOW, ScheduleType.ONETIME, ScheduleType.EACHDAY, ScheduleType.EACHWEEK, ScheduleType.DAYS, ScheduleType.YEARS);
		}

		init();
		layout();

	}

	public SchedulerComponent(ScheduleType... types) {

		setEnabledTypes(types);
		init();
		layout();

	}

	interface SchedulerSubComponent {

		String getCronExpression();

	}

	class SchedulerNumberField extends TextField {

		private final String numberWidth = "50px";

		public SchedulerNumberField() {
			setWidth(numberWidth);
			setConverter(Integer.class);
		}

		public void reset() {
			setValue(null);
		}

	}

	class SchedulerTimeField extends ComboBox {

		private final String width = "60px";

		public SchedulerTimeField() {

			setWidth(width);

			if (!slowRunning) {

				String h1 = "";
				int h = 0;
				while (h < 48) {
					if (h % 2 == 0) {
						h1 = h / 2 + ":00";
						addItem(h1);
					} else {
						h1 = (h - 1) / 2 + ":15";
						addItem(h1);
						h1 = (h - 1) / 2 + ":30";
						addItem(h1);
						h1 = (h - 1) / 2 + ":45";
						addItem(h1);
					}
					h = h + 1;
				}

			} else {

				String h1 = "";

				int h = 0;
				while (h < 24) {

					if ((h > 0 && h <= 7) || h > 17) {

						addItem(Integer.toString(h - 1) + ":15");
						addItem(Integer.toString(h - 1) + ":30");
						addItem(Integer.toString(h - 1) + ":45");
						addItem(Integer.toString(h) + ":00");

					}

					h = h + 1;
				}

			}

		}

		public Integer getHour() {
			String s = (String) getValue();

			if (logger.isDebugEnabled()) {
				logger.debug("value = {} {}", s, s.indexOf(":"));
			}

			if (s != null) {
				Integer hour = new Integer(s.substring(0, s.indexOf(":")));
				return hour;
			}
			return null;
		}

		public Integer getMinute() {
			String s = (String) getValue();
			if (s != null) {
				Integer minutes = new Integer(s.substring(s.indexOf(":") + 1));
				return minutes;
			}
			return null;
		}

		public void reset() {
			setValue(null);
		}

	}

	class SchedulerTimesPerHourField extends ComboBox {

		private final String width = "100px";

		public SchedulerTimesPerHourField() {

			setWidth(width);
			addItem("1");
			addItem("2");
			addItem("3");
			addItem("4");
			addItem("5");
			addItem("6");
			addItem("10");
			addItem("12");
			addItem("20");
			addItem("30");
			addItem("60");
		}

		public void reset() {
			setValue(null);
		}

	}

	class SchedulerTimesPerDayField extends ComboBox {

		private final String width = "100px";

		public SchedulerTimesPerDayField() {

			setWidth(width);
			addItem("1");
			addItem("2");
			addItem("3");
			addItem("4");
			addItem("6");
			addItem("8");
			addItem("12");
			addItem("24");

		}

		public void reset() {
			setValue(null);
		}

	}

	class SchedulerWeekdayField extends ComboBox {

		private final String width = "100px";

		public SchedulerWeekdayField() {
			setWidth(width);

			// TODO Convert to C10N
			addItem("Monday");
			addItem("Tuesday");
			addItem("Wednesday");
			addItem("Thursday");
			addItem("Friday");
			addItem("Saturday");
			addItem("Sunday");
		}

		public Integer getDay() {

			String s = (String) getValue();
			switch (s) {
			case "Monday":
				return new Integer(2);
			case "Tuesday":
				return new Integer(3);
			case "Wednesday":
				return new Integer(4);
			case "Thursday":
				return new Integer(5);
			case "Friday":
				return new Integer(6);
			case "Saturday":
				return new Integer(7);
			case "Sunday":
				return new Integer(1);
			default:
				return null;
			}
		}

		public void reset() {
			setValue(null);
		}

	}

	class SchedulerMonthField extends ComboBox {

		private final String width = "100px";

		public SchedulerMonthField() {

			setWidth(width);
			// TODO Convert to C10N
			addItem("January");
			addItem("February");
			addItem("March");
			addItem("April");
			addItem("May");
			addItem("June");
			addItem("July");
			addItem("August");
			addItem("September");
			addItem("October");
			addItem("November");
			addItem("December");
		}

		public Integer getCode() {

			String s = (String) getValue();

			switch (s) {

			case "January":
				return new Integer(1);
			case "February":
				return new Integer(2);
			case "March":
				return new Integer(3);
			case "April":
				return new Integer(4);
			case "May":
				return new Integer(5);
			case "June":
				return new Integer(6);
			case "July":
				return new Integer(7);
			case "August":
				return new Integer(8);
			case "September":
				return new Integer(9);
			case "October":
				return new Integer(10);
			case "November":
				return new Integer(11);
			case "December":
				return new Integer(12);
			default:
				return null;

			}

		}

		public void reset() {
			setValue(null);
		}

	}

	class SchedulerMonthOccurenceField extends ComboBox {

		private final String width = "100px";

		public SchedulerMonthOccurenceField() {
			setWidth(width);
			addItem("Every");
			addItem("First");
			addItem("Second");
			addItem("Third");
			addItem("Fourth");
			addItem("Last");
		}

		public String getCode() {

			String s = (String) getValue();

			switch (s) {

			case "Every":
				return "";

			case "First":
				return "#1";
			case "Second":
				return "#2";
			case "Third":
				return "#3";
			case "Fourth":
				return "#4";
			case "Last":
				return "L";

			default:
				return null;

			}

		}

		public void reset() {
			setValue(null);
		}

	}

	class Now extends HorizontalLayout implements SchedulerSubComponent {

		public Now() {
			setSpacing(true);
			Label otherIcon = new Label("Now");
			addComponent(otherIcon);
			setComponentAlignment(otherIcon, Alignment.MIDDLE_CENTER);
		}

		@Override
		public String getCronExpression() {
			return "now";
		}

		public void reset() {

		}

	}

	class OneTime extends HorizontalLayout implements SchedulerSubComponent {

		private SchedulerMonthField month;
		private SchedulerNumberField days;
		private SchedulerNumberField year;
		private SchedulerTimeField time;

		public OneTime() {

			month = new SchedulerMonthField();

			days = new SchedulerNumberField() {
				{
					setNullRepresentation("");
				}
			};

			year = new SchedulerNumberField() {
				{
					setNullRepresentation("");
				}
			};

			time = new SchedulerTimeField();

			setSpacing(true);
			Label otherIcon = new Label("Once on");
			addComponent(otherIcon);
			setComponentAlignment(otherIcon, Alignment.MIDDLE_CENTER);
			addComponent(month);
			addComponent(days);
			addComponent(year);
			Label l2 = new Label(" at ");
			addComponent(l2);
			addComponent(time);

		}

		@Override
		public String getCronExpression() {

			if (month.getValue() != null && days.getConvertedValue() != null && time.getValue() != null && year.getConvertedValue() != null) {

				String cronExpression = "0 " + time.getMinute() + " " + time.getHour() + " " + days.getConvertedValue().toString() + " " + month.getCode() + " ? " + year.getConvertedValue()
						.toString();

				return cronExpression;
			}

			return null;
		}

		@Override
		public void setEnabled(boolean enabled) {

			if (!enabled) {
				month.reset();
				days.reset();
				year.reset();
				time.reset();
			}

			month.setEnabled(enabled);
			days.setEnabled(enabled);
			year.setEnabled(enabled);
			time.setEnabled(enabled);

		}

	}

	class Minutes extends HorizontalLayout implements SchedulerSubComponent {

		private SchedulerTimesPerHourField minutes;

		public Minutes() {

			minutes = new SchedulerTimesPerHourField();

			setSpacing(true);
			Label otherIcon = new Label("Every ");
			addComponent(otherIcon);
			setComponentAlignment(otherIcon, Alignment.MIDDLE_CENTER);
			addComponent(minutes);
			Label label_1 = new Label("minute(s)");
			addComponent(label_1);

		}

		@Override
		public String getCronExpression() {

			/*
			 * Every xth minute of the hour starting at minute 0 asterix/10
			 * means on minute 0, 10, 20, 30, 40, 50
			 */
			if (minutes.getValue() != null) {

				int i = Integer.valueOf((String) minutes.getValue());
				if (i == 60) {
					return "0 0 * * * ?";
				} else {
					return "0 */" + i + " * * * ?";

				}
			}

			return null;

		}

		@Override
		public void setEnabled(boolean enabled) {

			if (!enabled) {
				minutes.reset();
			}

			minutes.setEnabled(enabled);
		}

	}

	class Hours extends HorizontalLayout implements SchedulerSubComponent {

		private SchedulerTimesPerDayField numbertField_hours;
		private SchedulerNumberField minuteField;

		public Hours() {

			numbertField_hours = new SchedulerTimesPerDayField();
			minuteField = new SchedulerNumberField() {
				{
					setNullRepresentation("00");
				}
			};

			setSpacing(true);
			Label otherIcon = new Label("Every ");
			addComponent(otherIcon);
			setComponentAlignment(otherIcon, Alignment.MIDDLE_CENTER);
			addComponent(numbertField_hours);
			Label label_1 = new Label("hour(s) on minute");
			addComponent(label_1);
			addComponent(minuteField);

		}

		@Override
		public String getCronExpression() {

			if (numbertField_hours.getValue() != null) {

				String s = (String) minuteField.getValue();
				if (s == null) {
					s = "0";
				}

				return "0 " + s + " 0/" + numbertField_hours.getValue() + "  * * ?";

			}

			return null;
		}

		@Override
		public void setEnabled(boolean enabled) {

			if (!enabled) {
				numbertField_hours.reset();
				minuteField.reset();
			}
			numbertField_hours.setEnabled(enabled);
			minuteField.setEnabled(enabled);
		}

	}

	class EachDay extends HorizontalLayout implements SchedulerSubComponent {

		private SchedulerTimeField timeField;

		public EachDay() {

			timeField = new SchedulerTimeField();

			setSpacing(true);
			Label l1 = new Label("Every day at ");
			addComponent(l1);
			setComponentAlignment(l1, Alignment.MIDDLE_CENTER);
			addComponent(timeField);

		}

		@Override
		public String getCronExpression() {

			if (timeField.getValue() != null) {
				Integer minute = timeField.getMinute();
				Integer hour = timeField.getHour();
				return "0 " + minute + " " + hour + " * * ?";
			}

			return null;
		}

		@Override
		public void setEnabled(boolean enabled) {
			if (!enabled) {
				timeField.reset();
			}
			timeField.setEnabled(enabled);
		}

	}
	
	class EachWeek extends HorizontalLayout implements SchedulerSubComponent {
		
		private SchedulerTimeField time;
		private SchedulerWeekdayField day;

		public EachWeek() {
			
			time = new SchedulerTimeField();
			day = new SchedulerWeekdayField();
			
			setSpacing(true);
			Label otherIcon = new Label("Every week on ");
			addComponent(otherIcon);
			setComponentAlignment(otherIcon, Alignment.MIDDLE_CENTER);
			addComponent(day);
			Label label_1 = new Label(" at ");
			addComponent(label_1);
			addComponent(time);
			
		}
		
		@Override
		public void setEnabled(boolean enabled) {

			if (!enabled) {
				day.reset();
				time.reset();
			}

			day.setEnabled(enabled);
			time.setEnabled(enabled);

		}
		
		@Override
		public String getCronExpression() {
			
			if (day.getValue() != null && time.getValue() != null) {
				Integer dayOfWeek = day.getDay();
				Integer minute = time.getMinute();
				Integer hour = time.getHour();
				String cronExpression = "0 " + minute + " " + hour + " ? * " + dayOfWeek;
				return cronExpression;
			}
			
			return null;
		}
		
		
	}

	class Days extends HorizontalLayout implements SchedulerSubComponent {

		private SchedulerWeekdayField day;
		private SchedulerTimeField time;
		private SchedulerMonthOccurenceField daySelectBox;

		public Days() {

			day = new SchedulerWeekdayField();
			time = new SchedulerTimeField();
			daySelectBox = new SchedulerMonthOccurenceField();

			setSpacing(true);
			Label otherIcon = new Label("Every month on ");
			addComponent(otherIcon);
			setComponentAlignment(otherIcon, Alignment.MIDDLE_CENTER);
			addComponent(daySelectBox);
			addComponent(day);
			Label label_1 = new Label(" at ");
			addComponent(label_1);
			addComponent(time);

		}

		@Override
		public String getCronExpression() {

			if (day.getValue() != null && daySelectBox.getValue() != null && time.getValue() != null) {
				String daySelector = (String) daySelectBox.getCode();
				Integer dayOfWeek = day.getDay();
				Integer minute = time.getMinute();
				Integer hour = time.getHour();
				String cronExpression = "0 " + minute + " " + hour + " ? * " + dayOfWeek + daySelector;
				return cronExpression;
			}

			return null;
		}

		@Override
		public void setEnabled(boolean enabled) {

			if (!enabled) {
				day.reset();
				daySelectBox.reset();
				time.reset();
			}

			day.setEnabled(enabled);
			daySelectBox.setEnabled(enabled);
			time.setEnabled(enabled);

		}

	}

	class Years extends HorizontalLayout implements SchedulerSubComponent {

		private SchedulerMonthField month;
		private SchedulerNumberField days;
		private SchedulerTimeField time;

		public Years() {

			month = new SchedulerMonthField();
			days = new SchedulerNumberField() {
				{
					setNullRepresentation("");
				}
			};
			time = new SchedulerTimeField();

			setSpacing(true);

			Label label1 = new Label("Every year on");
			Label label2 = new Label("at ");

			addComponent(label1);
			setComponentAlignment(label1, Alignment.MIDDLE_CENTER);

			addComponent(label1);
			addComponent(month);
			addComponent(days);
			addComponent(label2);
			addComponent(time);
		}

		@Override
		public String getCronExpression() {

			if (month.getValue() != null && days.getValue() != null && time.getValue() != null) {
				String cronExpression = "0 " + time.getMinute() + " " + time.getHour() + " " + days.getValue() + " " + month.getCode() + " ?";
				return cronExpression;
			}

			return null;
		}

		@Override
		public void setEnabled(boolean enabled) {

			if (!enabled) {
				month.reset();
				days.reset();
				time.reset();
			}
			month.setEnabled(enabled);
			days.setEnabled(enabled);
			time.setEnabled(enabled);
		}

	}

	public FlexibleOptionGroup getSelector() {
		return flexibleOptionGroup;
	}

	private void init() {

		headerLabel = new Label("Report Schedule");

		now = new Now();
		onetime = new OneTime();
		minutes = new Minutes();
		hours = new Hours();
		eachday = new EachDay();
		eachweek = new EachWeek();
		days = new Days();
		years = new Years();

		flexibleOptionGroup = new FlexibleOptionGroup(createContainer());
		flexibleOptionGroup.setItemCaptionPropertyId(CAPTION_PROPERTY);
		enableOptions("now");

	}

	private void enableOptions(String option) {

		onetime.setEnabled(false);
		minutes.setEnabled(false);
		hours.setEnabled(false);
		eachday.setEnabled(false);
		eachweek.setEnabled(false);
		days.setEnabled(false);
		years.setEnabled(false);

		switch (option) {

		case "onetime":
			onetime.setEnabled(true);
			break;
		case "minutes":
			minutes.setEnabled(true);
			break;
		case "hour":
			hours.setEnabled(true);
			break;
		case "eachweek" :
			eachweek.setEnabled(true);
			break;
		case "eachday":
			eachday.setEnabled(true);
			break;
		case "days":
			days.setEnabled(true);
			break;
		case "years":
			years.setEnabled(true);
			break;

		case "now":
		default:
			break;

		}

	}

	public void layout() {

		VerticalLayout mainLayout = new VerticalLayout() {
			{
				setSizeFull();
				setMargin(true);
				addComponent(headerLabel);
				addComponent(layoutTab());
			}
		};

		addComponent(mainLayout);

	}

	public ScheduleType getChosenValue() {
		return ScheduleType.valueOf(flexibleOptionGroup.getValue().toString().toUpperCase());
	}

	public String getValue() {

		String cronExpression = null;

		switch (ScheduleType.valueOf(flexibleOptionGroup.getValue().toString().toUpperCase())) {

		case NOW:
			cronExpression = now.getCronExpression();
			break;

		case ONETIME:
			cronExpression = onetime.getCronExpression();
			break;

		case MINUTES:
			cronExpression = minutes.getCronExpression();
			break;

		case HOUR:
			cronExpression = hours.getCronExpression();
			break;

		case EACHDAY:
			cronExpression = eachday.getCronExpression();
			break;
			
		case EACHWEEK:
			cronExpression = eachweek.getCronExpression();
			break;

		case DAYS:
			cronExpression = days.getCronExpression();
			break;

		case YEARS:
			cronExpression = years.getCronExpression();
			break;

		}

		if (logger.isDebugEnabled()) {
			logger.debug("CronExpression is = {} ", cronExpression);
		}

		return cronExpression;

	}

	@SuppressWarnings("unchecked")
	private Container createContainer() {

		IndexedContainer cont = new IndexedContainer();
		cont.addContainerProperty(CAPTION_PROPERTY, String.class, null);

		for (int i = 0; i < enabledTypes.length; i++) {
			String name = enabledTypes[i].name().toLowerCase();
			Item item = cont.addItem(name);
			item.getItemProperty(CAPTION_PROPERTY).setValue(name);
		}
		return cont;

	}

	public static Label createCaptionLabel(FlexibleOptionGroupItemComponent fog) {
		Label captionLabel = new Label();
		captionLabel.setData(fog);
		captionLabel.setIcon(fog.getIcon());
		captionLabel.setCaption(fog.getCaption());
		captionLabel.setWidth(null);
		return captionLabel;
	}

	protected LayoutClickListener layoutClickListener = new LayoutClickListener() {

		public void layoutClick(LayoutClickEvent event) {

			FlexibleOptionGroupItemComponent c = null;
			boolean allowUnselection = flexibleOptionGroup.isMultiSelect();

			if (event.getChildComponent() instanceof FlexibleOptionGroupItemComponent) {
				c = (FlexibleOptionGroupItemComponent) event.getChildComponent();

			} else if (event.getChildComponent() instanceof AbstractComponent) {
				Object data = ((AbstractComponent) event.getChildComponent()).getData();

				if (data instanceof FlexibleOptionGroupItemComponent) {

					c = (FlexibleOptionGroupItemComponent) data;

				}
				if (event.getChildComponent() instanceof HorizontalLayout) {
					allowUnselection = false;
				}
			}
			if (c != null) {
				Object itemId = c.getItemId();

				if (logger.isDebugEnabled()) {
					logger.debug("Selected itemId = {}", itemId);
				}

				if (flexibleOptionGroup.isSelected(itemId) && allowUnselection) {
					flexibleOptionGroup.unselect(itemId);
				} else {

					flexibleOptionGroup.select(itemId);
					enableOptions(itemId.toString());

				}
			}
		}
	};

	public VerticalLayout layoutTab() {

		return new VerticalLayout() {
			{
				setMargin(true);
				addComponent(new GridLayout(2, 1) {
					{
						setSizeFull();
						setColumnExpandRatio(1, 1);
						addLayoutClickListener(layoutClickListener);
						boolean hasNow = false;

						for (Iterator<FlexibleOptionGroupItemComponent> iter = flexibleOptionGroup.getItemComponentIterator(); iter.hasNext();) {

							FlexibleOptionGroupItemComponent c = iter.next();

							addComponent(c);
							setComponentAlignment(c, Alignment.MIDDLE_CENTER);

							switch ((String) c.getItemId()) {

							case "now":
								addComponent(now);
								hasNow = true;
								break;

							case "onetime":
								addComponent(onetime);
								break;

							case "minutes":
								addComponent(minutes);
								break;

							case "hour":
								addComponent(hours);
								break;

							case "eachday":
								addComponent(eachday);
								break;
								
							case "eachweek":
								addComponent(eachweek);
								break;

							case "days":
								addComponent(days);
								break;

							case "years":
								addComponent(years);
								break;

							}

							newLine();
						}
						
						if (hasNow) {
							flexibleOptionGroup.select("now");
						} else {
							flexibleOptionGroup.select("onetime");
						}
					}
				});

			}
		};
	}
}
