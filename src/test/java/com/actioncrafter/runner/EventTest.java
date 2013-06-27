package com.actioncrafter.runner;

import static org.junit.Assert.*;

import org.junit.Test;

public class EventTest {

	@Test
	public void test() 
	{
		
		Event e = Event.build("cmd1 param1=value1|param2=value2");
		assertNotNull(e);
		assertEquals("cmd1", e.mName);
		assertEquals("value1", e.getParam("param1"));
		assertEquals("value2", e.getParam("param2"));
	}

}
