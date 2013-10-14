package com.actioncrafter.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.actioncrafter.core.ACEvent;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

public class ACEventTest {

	@Test
	public void testParseEventString()
	{
		
		ACEvent e = ACEvent.build("cmd1 param1=value1|param2=value2");
		assertNotNull(e);
		assertEquals("cmd1", e.mName);
		assertEquals("value1", e.getParam("param1"));
		assertEquals("value2", e.getParam("param2"));
		
		e = ACEvent.build("cmd1");
		assertNotNull(e);
		assertEquals("cmd1", e.mName);
		
		e = ACEvent.build("cmd1 param1=value1");
		assertNotNull(e);
		assertEquals("cmd1", e.mName);
		assertEquals("value1", e.getParam("param1"));
		
		e = ACEvent.build("cmd1 param1=value1 with spaces");
		assertNotNull(e);
		assertEquals("cmd1", e.mName);
		assertEquals("value1 with spaces", e.getParam("param1"));
	}

    @Test
    public void testParseJSONResultIntoList()
    {

        String json = "{\"name\":\"bob\",\"arg1\":\"cool3\",\"_date\":1379045263}";

        ACEvent event = ACEvent.parseJson(json);

        assertEquals("bob", event.getName());
        assertEquals("cool3", event.getParam("arg1"));
    }

}
