package com.actioncrafter.core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ACEvent 
{
	
	protected String mName;

    protected Date mDate;

    protected String mSource;
	
	protected Map<String, String> mParams;
	
	private static Pattern sEventPattern = Pattern.compile("(.*?)(?:\\s+(.*))?");
	
	
	public ACEvent()
    {
        this(null);
    }

	public ACEvent(String name) {
		mName = name;
		mParams = new HashMap<String, String>();
	}

    public void setName(String name)
    {
        mName = name;
    }

    public String getName()
    {
        return mName;
    }

    public void setSource(String source)
    {
        mSource = source;
    }

    public String getSource()
    {
        return mSource;
    }

    public void setDate(Date date)
    {
        mDate = date;
    }

    public Date getDate()
    {
        return mDate;
    }

	public void setParam(String param, String value)
	{
		mParams.put(param, value);
	}
	
	public String getParam(String param)
	{
		return mParams.get(param);
	}

    public Set<String> getParamKeys()
    {
        return mParams.keySet();
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
        if (mSource != null)
        {
            sb.append("|source=");
            sb.append(mSource);
        }
		return sb.toString();
	}
	
	public String toUrlString()
	{
		try {
			
			StringBuilder sb = new StringBuilder();
			sb.append("name=");
			sb.append(URLEncoder.encode(mName, "UTF-8"));
			if (mParams.size() > 0)
			{
				sb.append("&");
			}
			int keysLeft = mParams.size();
			for (String param : mParams.keySet())
			{
				sb.append(URLEncoder.encode(param, "UTF-8"));
				sb.append("=");
				sb.append(URLEncoder.encode(mParams.get(param), "UTF-8"));
				if (keysLeft > 1)
				{
					sb.append("&");
				}
				keysLeft--;
			}
            if (mSource != null)
            {
                sb.append("&source=");
                sb.append(URLEncoder.encode(mSource,"UTF-8"));
            }
			return sb.toString();
		}
		catch (UnsupportedEncodingException e) 
		{
			System.err.println("Error encoding query: " + e.getMessage());
			return "";
		}
	}

	public static ACEvent build(String line) {
		Matcher m = sEventPattern.matcher(line);

		if (m.matches())
		{
			ACEvent e = new ACEvent(m.group(1));
			if (m.groupCount() > 1) {
				String args = m.group(2);
				if (args != null)
				{
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
			}
			return e;
		} 
		else
		{
			return new ACEvent(line);
		}
	}


    public static List<ACEvent> parseJson(Reader json)
    {
        List<ACEvent> events = new ArrayList<ACEvent>();


        JSONParser parser = new JSONParser();

        try
        {
            JSONObject o = (JSONObject)parser.parse(json);
            Boolean res = (Boolean)o.get("success");
            if (res)
            {
                JSONArray items = (JSONArray)o.get("items");
                System.out.println("Got " + items.size() + " events from server");
                for (int i = 0; i < items.size(); i++)
                {

                    ACEvent e = new ACEvent();

                    JSONObject item = (JSONObject)items.get(i);
                    for (Object k : item.keySet())
                    {
                        if (k.equals("name"))
                        {
                            e.setName((String)item.get(k));
                        }
                        else if (k.equals("_date"))
                        {
                            e.setDate(new Date((Long)item.get(k)*1000));
                        }
                        else
                        {
                            e.setParam((String)k, (String)item.get(k));
                        }
                        System.out.println("In item " + i + " - Found key " + k + " with value " + item.get(k));
                    }

                    events.add(e);
                }
            }
            else
            {
                System.out.println("Error in response.");
            }

        }
        catch (IOException e)
        {
            System.out.println(e);
        }
        catch(ParseException pe)
        {
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        }


        return events;
    }
}
