package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

import org.tepi.filtertable.FilterFieldGenerator.IFilterTable;

public abstract class AbstractFilterFieldHandler implements FilterFieldHandler {
	protected IFilterTable owner;
	public void setOwner(IFilterTable owner) {
		this.owner = owner;
	}
}