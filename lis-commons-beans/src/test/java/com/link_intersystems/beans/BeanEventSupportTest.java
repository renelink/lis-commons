package com.link_intersystems.beans;

import java.beans.IntrospectionException;

import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BeanEventSupportTest {

	private ChangeListener changeListener;
	private DefaultButtonModel defaultButtonModel;
	private BeanEventSupport<ButtonModel, ChangeListener> beanEventSupport;

	@Before
	public void setup() throws IntrospectionException {
		changeListener = Mockito.mock(ChangeListener.class);
		defaultButtonModel = new DefaultButtonModel();
		beanEventSupport = new BeanEventSupport<ButtonModel, ChangeListener>();
		beanEventSupport.setBean(defaultButtonModel);
	}

	@Test
	public void setListener() {
		beanEventSupport.setListener(changeListener);
		defaultButtonModel.setEnabled(!defaultButtonModel.isEnabled());
		Mockito.verify(changeListener, Mockito.times(1)).stateChanged(Mockito.any(ChangeEvent.class));
	}

	@Test
	public void setListenerNull() {
		beanEventSupport.setListener(changeListener);
		defaultButtonModel.setEnabled(!defaultButtonModel.isEnabled());
		Mockito.verify(changeListener, Mockito.times(1)).stateChanged(Mockito.any(ChangeEvent.class));

		Mockito.reset(changeListener);

		beanEventSupport.setListener(null);
		defaultButtonModel.setEnabled(!defaultButtonModel.isEnabled());
		Mockito.verify(changeListener, Mockito.never()).stateChanged(Mockito.any(ChangeEvent.class));
	}

	@Test
	public void setBeanNull() {
		beanEventSupport.setListener(changeListener);
		beanEventSupport.setBean(null);
		defaultButtonModel.setEnabled(!defaultButtonModel.isEnabled());
		Mockito.verify(changeListener, Mockito.never()).stateChanged(Mockito.any(ChangeEvent.class));
	}

	@Test
	public void disableEvents() {
		beanEventSupport.setListener(changeListener);
		beanEventSupport.setEventDisabled(true);

		defaultButtonModel.setEnabled(!defaultButtonModel.isEnabled());
		Mockito.verify(changeListener, Mockito.never()).stateChanged(Mockito.any(ChangeEvent.class));
	}

	@Test
	public void getBean() {
		ButtonModel buttonModel = beanEventSupport.getBean();
		Assert.assertSame(defaultButtonModel, buttonModel);
	}

	@Test
	public void getBeanNull() {
		beanEventSupport.setBean(null);
		ButtonModel buttonModel = beanEventSupport.getBean();
		Assert.assertNull(buttonModel);
	}

}
