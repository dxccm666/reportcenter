package edu.missouri.cf.projex4.ui.desktop;

import java.util.ArrayList;

import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.data.collectioncontainer.CollectionContainer;
import edu.missouri.cf.projex4.ui.desktop.documents.PopupWindow;

public class ExcelImportErrorWindow extends PopupWindow {

	Label label;
	Table table;

	public ExcelImportErrorWindow() {
		
		setCaption("Import Errors");

		label = new Label("Excel File could not be imported due to the following reasons");
		table = new Table();

		setContent(new VerticalLayout() {
			{
				setSpacing(true);
				setMargin(true);
				label.setWidth("100%");
				addComponent(label);
				addComponent(table);
				table.setSizeFull();
			}
		});

	}

	public void setData(ArrayList<ExcelImportException> exceptions) {

		table.setContainerDataSource(CollectionContainer.fromPrimitives(exceptions, true));
		System.err.println(table.getVisibleColumns());
		
		table.setColumnHeaders("Error");

	}

}
