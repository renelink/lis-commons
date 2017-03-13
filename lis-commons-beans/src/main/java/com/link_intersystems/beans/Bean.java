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

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import com.link_intersystems.lang.Assert;

/**
 * A {@link Bean} is a wrapper for any java object that fulfills the <a href=
 * "http://download.oracle.com/otndocs/jcp/7224-javabeans-1.01-fr-spec-oth-JSpec/"
 * target="_blank">java bean specification</a> to access the bean's properties
 * in a convenience way.
 *
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @param <T>
 *            the type of the bean.
 * @since 1.2.0.0
 */
public class Bean<T> {

	private T bean;

	private BeanClass<T> beanClass;

	private Map<String, Property<?>> properties = new HashMap<String, Property<?>>();

	private Map<String, IndexedProperty<?>> indexedProperties = new HashMap<String, IndexedProperty<?>>();

	/**
	 * Constructs a new {@link Bean} for the given bean object.
	 *
	 * @param bean
	 *            the bean object.
	 * @since 1.2.0.0
	 */
	@SuppressWarnings("unchecked")
	public Bean(T bean) {
		Assert.notNull("bean", bean);
		this.bean = bean;
		beanClass = (BeanClass<T>) BeanClass.get(bean.getClass());
	}

	/**
	 * Constructs a new {@link Bean} based on the beanClass. The bean class must
	 * fulfill the java bean specification. The bean that this {@link Bean}
	 * represents is lazy initialized by instantiating a bean using the default
	 * constructor of the beanClass.
	 *
	 * @param beanClass
	 *            the type of the bean that this {@link Bean} represents.
	 * @since 1.2.0.0
	 */
	public Bean(Class<T> beanClass) {
		Assert.notNull("beanClass", beanClass);
		this.beanClass = (BeanClass<T>) BeanClass.get(beanClass);
	}

	/**
	 * Get the {@link IndexedProperty} of this bean with the property name.
	 *
	 * @param propertyName
	 *            the name of the indexed property.
	 * @return the indexed property, if any.
	 * @throws NoSuchPropertyException
	 *             if the property does not exist.
	 * @throws PropertyAccessException
	 *             if the property could not be accessed.
	 * @since 1.2.0.0
	 */
	public <PT> IndexedProperty<PT> getIndexedProperty(String propertyName) {
		IndexedProperty<PT> indexedProperty = getIndexedPropertyInternal(propertyName);
		if (indexedProperty == null) {
			throw new NoSuchPropertyException(bean.getClass(), propertyName);
		}
		return indexedProperty;
	}

	@SuppressWarnings("unchecked")
	private <PT> IndexedProperty<PT> getIndexedPropertyInternal(
			String propertyName) {
		IndexedProperty<PT> indexedProperty = (IndexedProperty<PT>) indexedProperties
				.get(propertyName);
		if (indexedProperty == null) {
			PropertyDescriptor propertyDescriptor = beanClass
					.getPropertyDescriptor(propertyName);
			if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
				IndexedPropertyDescriptor indexedPropertyDescriptor = (IndexedPropertyDescriptor) propertyDescriptor;
				T target = getTarget();
				indexedProperty = new IndexedProperty<PT>(target,
						indexedPropertyDescriptor);
				indexedProperties.put(propertyName, indexedProperty);
				properties.put(propertyName, indexedProperty);
			}
		}
		return indexedProperty;
	}

	/**
	 * Get the {@link Property} of this bean with the property name.
	 *
	 * @param propertyName
	 *            the name of the indexed property.
	 * @return the property, if any.
	 * @throws NoSuchPropertyException
	 *             if the property does not exist.
	 * @throws PropertyAccessException
	 *             if the property could not be accessed.
	 * @since 1.2.0.0
	 */
	public <PT> Property<PT> getProperty(String propertyName) {
		Property<PT> property = getPropertyInternal(propertyName);
		if (property == null) {
			throw new NoSuchPropertyException(bean.getClass(), propertyName);
		}
		return property;
	}

	/**
	 *
	 * @param propertyName
	 * @return true if either a simple property or an indexed property with the
	 *         given name exists.
	 */
	public boolean hasAnyProperty(String propertyName) {
		return hasProperty(propertyName) || hasIndexedProperty(propertyName);
	}

	/**
	 *
	 * @param propertyName
	 * @return true if a property with the given name exists.
	 */
	public boolean hasProperty(String propertyName) {
		return getPropertyInternal(propertyName) != null;
	}

	/**
	 *
	 * @param propertyName
	 * @return true if an indexed property with the given name exists.
	 */
	public boolean hasIndexedProperty(String propertyName) {
		return getIndexedPropertyInternal(propertyName) != null;
	}

	@SuppressWarnings("unchecked")
	private <PT> Property<PT> getPropertyInternal(String propertyName) {
		Property<PT> property = (Property<PT>) properties.get(propertyName);
		if (property == null) {
			PropertyDescriptor propertyDescriptor = beanClass
					.getPropertyDescriptorInternal(propertyName);
			if (propertyDescriptor != null) {
				T target = getTarget();
				property = new Property<PT>(target, propertyDescriptor);
				properties.put(propertyName, property);
			}
		}
		return property;
	}

	T getTarget() {
		if (bean == null) {
			Bean<T> newBeanInstance = beanClass.newBeanInstance();
			bean = newBeanInstance.getTarget();
		}
		return bean;
	}

	/**
	 *
	 * @return the {@link BeanClass} of this {@link Bean}.
	 */
	public BeanClass<T> getBeanClass() {
		return beanClass;
	}

}