package com.kris.spring.test.util;

import com.kris.spring.test.dao.TMapper;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class MyFactoryBean implements FactoryBean {
	@Override
	public Object getObject() throws Exception {
		return new TMapper();
	}

	@Override
	public Class<?> getObjectType() {
		return TMapper.class;
	}
}
