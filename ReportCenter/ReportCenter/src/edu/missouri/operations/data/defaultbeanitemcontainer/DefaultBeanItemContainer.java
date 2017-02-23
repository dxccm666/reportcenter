package edu.missouri.operations.data.defaultbeanitemcontainer;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;

@SuppressWarnings("serial")
public class DefaultBeanItemContainer<BEANTYPE> extends BeanItemContainer<BEANTYPE> {
	
	private BeanItem<BEANTYPE> defaultBean;
	
	public DefaultBeanItemContainer(Class<? super BEANTYPE> type) {
		super(type);
	}
	
	public boolean hasDefaultBean() {
		return defaultBean != null;
	}

	public BeanItem<BEANTYPE> getDefaultBean() {
		return defaultBean;
	}

	public void setDefaultBeanItem(BeanItem<BEANTYPE> defaultBean) {
		this.defaultBean = defaultBean;
	}
	
	public void setDefaultBean(BEANTYPE defaultBean) {
		this.defaultBean = new BeanItem<BEANTYPE>(defaultBean);
	}
	

}
