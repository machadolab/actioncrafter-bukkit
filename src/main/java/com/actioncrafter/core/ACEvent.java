package com.actioncrafter.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ACEvent 
{
	
	protected String mName;
	
	protected Map<String, String> mParams;
	
	private static Pattern sEventPattern = Pattern.compile("(.*?)(?:\\s+(.*))?");
	
	
	
	public ACEvent(String name) {
		mName = name;
		mParams = new HashMap<String, String>();
	}
	
	public void setParam(String param, String value)
	{
		mParams.put(param, value);
	}
	
	public String getParam(String param)
	{
		return mParams.get(param);
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(mName);
		sb.append(" ");
		int keysLeft = mParams.size();
		for (String param : mParams.keySet())
		{
			sb.append(param);
			sb.append("=");
			sb.append(mParams.get(param));
			if (keysLeft > 1)
			{
				sb.append("|");
			}
			keysLeft--;
		}
		return sb.toString();
	}
	
	public String toUrlString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("name=");
		sb.append(mName);
		if (mParams.size() > 0)
		{
			sb.append("&");
		}
		int keysLeft = mParams.size();
		for (String param : mParams.keySet())
		{
			sb.append(param);
			sb.append("=");
			sb.append(mParams.get(param));
			if (keysLeft > 1)
			{
				sb.append("&");
			}
			keysLeft--;
		}
		return sb.toString();
	}

	public static ACEvent build(String line) {
		Matcher m = sEventPattern.matcher(line);

		if (m.matches())
		{
			ACEvent e = new ACEvent(m.group(1));
			if (m.groupCount() > 1) {
				String[] pairs = m.group(2).split("\\|");
				for (String p : pairs)
				{
					String[] nv = p.split("=");
					if (nv != null && nv.length == 2)
					{
						e.setParam(nv[0].trim(), nv[1].trim());
					}
				}
			}
			return e;
		} 
		else
		{
			return null;
		}
	}
}
