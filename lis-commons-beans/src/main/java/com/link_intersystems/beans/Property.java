/**
 * Copyright 2011 Link Intersystems GmbH <rene.link@link-intersystems.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.link_intersystems.beans;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Formattable;
import java.util.Formatter;

import com.link_intersystems.beans.PropertyAccessException.PropertyAccessType;
import com.link_intersystems.lang.Assert;

/**
 * Encapsulates access to a java bean property.
 *
 * <pre>
 * public class SomeBean {
 *
 *    private String internalName;
 *
 *    public void setName(String name){
 *      this.internalName = name;
 *    }
 *
 *    public String getName(){
 *    	return internalName;
 *    }
 *
 * }
 *
 * ...
 * SomeBean someBean = new SomeBean();
 *
 * Bean<SomeBean> bean = new Bean<SomeBean>();
 * Property<String> nameProp = bean.getProperty("name");
 * nameProp.setValue("Hello");
 *
 * assertEquals("Hello", someBean.getName());
 * </pre>
 *
 *
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 *
 * @param <TYPE>
 *            the {@link Property}'s type.
 * @since 1.2.0.0
 */
public class Property<TYPE> implements Serializable, Formattable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6759158627808430975L;
	private Object bean;
	private PropertyDescriptor propertyDescriptor;

	private Class<TYPE> type;

	Property(Object bean, PropertyDescriptor propertyDescriptor) {
		Assert.notNull("bean", bean);
		Assert.notNull("propertyDescriptor", propertyDescriptor);
		this.bean = bean;
		this.propertyDescriptor = propertyDescriptor;
	}

	/**
	 * @return the bean object of this {@link Property}.
	 * @since 1.2.0.0
	 */
	protected final Object getBean() {
		return bean;
	}

	/**
	 * @return this {@link Property}'s getter method, if any.
	 * @since 1.2.0.0
	 */
	protected final Method getReadMethod() {
		return getPropertyDescriptor().getReadMethod();
	}

	/**
	 * @return this {@link Property}'s setter method, if any.
	 * @since 1.2.0.0
	 */
	protected final Method getWriteMethod() {
		return getPropertyDescriptor().getWriteMethod();
	}

	/**
	 * The {@link Property}'s type.
	 *
	 * @return the {@link Property}'s type.
	 * @since 1.2.0.0
	 */
	@SuppressWarnings("unchecked")
	public Class<TYPE> getType() {
		if (this.type == null) {
			Class<TYPE> type = null;
			Method readMethod = getReadMethod();
			if (readMethod != null) {
				type = (Class<TYPE>) readMethod.getReturnType();
			} else {
				Method writeMethod = getWriteMethod();
				Class<?>[] parameterTypes = writeMethod.getParameterTypes();
				type = (Class<TYPE>) parameterTypes[0];
			}
			this.type = type;
		}
		return type;
	}

	/**
	 * The name of this {@link Property}.
	 *
	 * @return name of this {@link Property}.
	 * @since 1.2.0.0
	 */
	public String getName() {
		return getPropertyDescriptor().getName();
	}

	/**
	 * Gets the value of this {@link Property}.
	 *
	 * @return the value of this property.
	 * @throws PropertyAccessException
	 *             if the property could not be accessed for any reason. If the
	 *             thrown {@link PropertyAccessException} has no cause this
	 *             property is not readable (has no property getter method).
	 * @since 1.2.0.0
	 */
	@SuppressWarnings("unchecked")
	public TYPE getValue() {
		Object target = getBean();
		Method readMethod = getReadMethod();
		if (readMethod == null) {
			throw new PropertyAccessException(this, PropertyAccessType.READ);
		}
		try {
			Object beanValue = invoke(readMethod, target);
			return (TYPE) beanValue;
		} catch (InvocationTargetException e) {
			throw new PropertyAccessException(this, PropertyAccessType.READ, e);
		} catch (IllegalAccessException e) {
			throw new PropertyAccessException(this, PropertyAccessType.READ, e);
		}
	}

	/**
	 * Encapsulation of a method invocation for better testing.
	 *
	 * @param method
	 * @param target
	 * @param args
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected Object invoke(Method method, Object target, Object... args)
			throws IllegalAccessException, InvocationTargetException {
		Object beanValue = method.invoke(target, args);
		return beanValue;
	}

	/**
	 * Sets the value of this {@link Property}.
	 *
	 * @param propertyValue
	 *            the value to set.
	 *
	 * @throws PropertyAccessException
	 *             if this {@link Property}'s value could not be set. If the
	 *             thrown {@link PropertyAccessException} has no cause this
	 *             property is not writable (has no property setter method).
	 * @since 1.2.0.0
	 */
	public void setValue(TYPE propertyValue) {
		Object target = getBean();
		Method writeMethod = getWriteMethod();
		if (writeMethod == null) {
			throw new PropertyAccessException(this, PropertyAccessType.WRITE);
		}
		try {
			invoke(writeMethod, target, propertyValue);
		} catch (InvocationTargetException e) {
			throw new PropertyAccessException(this, PropertyAccessType.WRITE, e);
		} catch (IllegalAccessException e) {
			throw new PropertyAccessException(this, PropertyAccessType.WRITE, e);
		}
	}

	/**
	 * @inherited
	 * @since 1.2.0.0
	 */
	public void formatTo(Formatter formatter, int flags, int width,
			int precision) {
		formatter.format("%s.%s", getBean().getClass().getCanonicalName(),
				getName());
	}

	protected final PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	/**
	 * Returns true if this property is readable (has a getter method).
	 *
	 * @return true if this property is readable (has a getter method).
	 */
	public boolean isReadable() {
		return getReadMethod() != null;
	}

	/**
	 * Returns if this property is writable (has a setter method).
	 *
	 * @return true if this property is writable (has a setter method).
	 */
	public boolean isWritable() {
		return getWriteMethod() != null;
	}

	/**
	 * The name of this property.
	 */
	@Override
	public String toString() {
		return getName();
	}
}