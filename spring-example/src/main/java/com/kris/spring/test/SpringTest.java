package com.kris.spring.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(Bean.class);
		context.refresh();
		System.out.println(context.getBean(Bean.class));
	}

}
