package com.opsmx.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.opsmx.enums.Aggregator;
import com.opsmx.enums.ComparisonOperator;
import com.opsmx.utils.URLReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SLAData {
	private static ConcurrentHashMap<Integer, SLA> idVsSLAs;
	private static ConcurrentHashMap<String, ArrayList<SLA>> metricNameVsSLAs;

	public static ConcurrentHashMap<Integer, SLA> getIdVsSLAsMap() {
		return idVsSLAs;
	}

	public static ConcurrentHashMap<String, ArrayList<SLA>> getMetricNameVsSLAsMap() {
		return metricNameVsSLAs;
	}

	public static ArrayList<SLA> getSLAsForMetric(String metricName) {
		return metricNameVsSLAs.get(metricName);
	}

	public static SLA getSLA(int slaId) {
		return idVsSLAs.get(slaId);
	}

	/*public static void updateSLAs() {
		// TODO get SLAs from opsmx database and create SLA objects
		//SLA sla = new SLA(1, "test.metric1", Aggregator.AVG, ComparisonOperator.GT, 10, 5, 50, "host", null);
		SLA sla = new SLA(1, "googleglass.temperature", Aggregator.AVG, ComparisonOperator.GT, 30, 120, 20, "host", null);
		SLA sla2 = new SLA(2, "googleglass.batteryPercentage", Aggregator.AVG, ComparisonOperator.LT, 30, 120, 80, "host", null);
		idVsSLAs = new ConcurrentHashMap<>();
		metricNameVsSLAs = new ConcurrentHashMap<>();
		idVsSLAs.put(sla.getId(), sla);
		idVsSLAs.put(sla2.getId(), sla2);
		ArrayList<SLA> slas = new ArrayList<>();
		slas.add(sla);
		ArrayList<SLA> slas2 = new ArrayList<>();
		slas2.add(sla2);
		metricNameVsSLAs.put(sla.getMetricName(), slas);
		metricNameVsSLAs.put(sla2.getMetricName(), slas2);
	}*/
	
	public static void updateSLAs() {
		idVsSLAs = new ConcurrentHashMap<>();
		metricNameVsSLAs = new ConcurrentHashMap<>();
		/************Code starts*********************/
		try{
			String jsonitter;
	    	StringBuilder app = new StringBuilder();

	    	URL appsname = new URL(URLReader.getUrlFromConfigFile("SLAUrl"));

	    	URLConnection hj = appsname.openConnection();
	        BufferedReader ne = new BufferedReader(new InputStreamReader(hj.getInputStream()));
	        while ((jsonitter = ne.readLine()) != null) 
	        	app.append(jsonitter); 
	        	
	     	JSONObject jsonrules = new JSONObject(app.toString());
	     	JSONArray stagesjson = jsonrules.getJSONArray("slaRules");
	    	
	    	System.out.println("JSON Array : "+stagesjson+" : "+stagesjson.length());
	
	    	String stagetype = null; //= "";
	    	String servicetype = null; //= "";
	    	String googleservice = null; //= "";
	    	String desc = null; //= "";
	    	int id=0;
	    	for (int i = 0; i < stagesjson.length(); i++) {
	   			
	     		JSONObject stagenamejson = stagesjson.getJSONObject(i);
	    
	     		stagetype = stagenamejson.getString("metric");
	     		servicetype = stagenamejson.getString("service");
	     		id = stagenamejson.getInt("id");
	   			desc = stagenamejson.getString("description");
	
		 		System.out.println("Desc : "+desc);
		 		String[] sladesc = desc.split(",");
	 		
	 			SLA er1 = null;
	 				
				Aggregator agg1 = null ;
				
				ComparisonOperator com1 = null;
				FilterCondition fc = null;
				ArrayList<FilterCondition> aryfc=null;
	
				int aggtime1 = 0;
				int aggtime2 = 0;
				
				Double value=0.0;
				String groupby="host";
	 			
	 			for(int n = 0; n < sladesc.length;n++)
	 			{
	 				switch (n){
	 				case 0:
			 	 		if (sladesc[n].toLowerCase().contains("sum"))
			 	 		{
			 	 			agg1 = Aggregator.SUM;
			 	 		}else if (sladesc[n].toLowerCase().contains("avg"))
			 	 		{	
			 	 			agg1 = Aggregator.AVG;
			 	 		}else if (sladesc[n].toLowerCase().contains("max"))
			 	 		{
			 	 			agg1 = Aggregator.MAX;
			 	 		}else if (sladesc[n].toLowerCase().contains("min"))
			 	 		{
			 	 			agg1 = Aggregator.MIN;
			 	 		}else if (sladesc[n].toLowerCase().contains("count"))
			 	 		{
			 	 			agg1 = Aggregator.COUNT;
			 	 		}else{
			 	 			System.out.print("This aggregator is not handled : "+sladesc[n]);
			 	 		}
			 	 		break;
	 				case 1:
			 	 		if(sladesc[n].toLowerCase().trim().contains("sec"))
			 	 		{
			 	 			aggtime1 = Integer.parseInt(sladesc[n].toLowerCase().substring(0, sladesc[n].toLowerCase().indexOf("sec")));
			 	 			aggtime2 = Integer.parseInt(sladesc[n].toLowerCase().substring(0, sladesc[n].toLowerCase().indexOf("sec")));
			 	 		}else if(sladesc[n].toLowerCase().trim().contains("min"))
			 	 		{
			 	 			aggtime1 = Integer.parseInt(sladesc[n].toLowerCase().substring(0, sladesc[n].toLowerCase().indexOf("min")))*60;
			 	 			aggtime2 = Integer.parseInt(sladesc[n].toLowerCase().substring(0, sladesc[n].toLowerCase().indexOf("min")))*60;
			 	 		}
			 	 		break;
	 				case 2:
			 	 		if (sladesc[n].toLowerCase().trim().contains("greater than") || sladesc[n].toLowerCase().trim().contains(">"))
			 	 		{
			 	 			com1 = ComparisonOperator.GT;
			 	 		}else if (sladesc[n].toLowerCase().trim().contains("equal to") || sladesc[n].toLowerCase().trim().contains("="))
			 	 		{
			 	 			com1 = ComparisonOperator.EQ;
			 	 		}else if (sladesc[n].toLowerCase().trim().contains("less than") || sladesc[n].toLowerCase().trim().contains("<"))
			 	 		{
			 	 			com1 = ComparisonOperator.LT;
			 	 		}else {
			 	 			System.out.print("This comparison operator is not handled : "+sladesc[n]);
			 	 		}
			 	 		break;	 				
	 				case 3:
	 					value = Double.parseDouble(sladesc[n]);
	 					break;
	 				case 4:
	 					groupby = sladesc[n];
	 					break;
	 				case 5:
	 					if(sladesc[n].toLowerCase().trim().contains("=")){
	 						String [] s = sladesc[n].split("=");
	 						fc = new FilterCondition();
	 						fc.setField(s[0].trim());
	 						fc.setValue(s[1].trim());
	 						fc.setComparisonOperator(ComparisonOperator.EQ);
	 						aryfc = new ArrayList<>();
	 						aryfc.add(fc);
	 					}
	 				}
	 			}
	 			er1 =  new SLA(id, stagetype, agg1, com1, aggtime1, aggtime2,value ,groupby, aryfc);
	 		
	
		 		System.out.println(er1);
		 		idVsSLAs.put(er1.getId(), er1);

		 		try{
		 			if (metricNameVsSLAs.containsKey(er1.getMetricName())){
		 				metricNameVsSLAs.get(er1.getMetricName()).add(er1);
		 			}else {
						ArrayList<SLA> slas = new ArrayList<>();
						slas.add(er1);
						metricNameVsSLAs.put(er1.getMetricName(), slas);
					}		 					
		 		}catch(NullPointerException npe){
		 			npe.printStackTrace();
		 		}
		 		
	    	}
	    	/*
	    	//Hardcoding SLA for testing login and logout event, at BPO and Hospital for doctor
	 		ArrayList<FilterCondition> filter = new ArrayList<>();
	 		filter.add(new FilterCondition("event",ComparisonOperator.EQ,"logout"));
	 		
	 		SLA bpologout = new SLA(99,"doctor.event",Aggregator.SUM,ComparisonOperator.GT,30,30,2.0,"bpo",filter);
	 		//SLA hospitallogout = new SLA(98,"doctor.event",,Aggregator.SUM,ComparisonOperator.GT,300,300,10.0,"host",filter);
	 		
	 		System.out.println(bpologout);
	 		idVsSLAs.put(bpologout.getId(), bpologout);
	 		
	 		//Hardcoding SLA for testing login and logout event, at BPO and Hospital for doctor
 			ArrayList<SLA> doctorsla = new ArrayList<>();
 			doctorsla.add(bpologout);
 			metricNameVsSLAs.put("doctor.event", doctorsla);*/
		
		}catch(MalformedURLException mue){
			mue.printStackTrace();
		}catch(IOException ie){
			ie.printStackTrace();
		}catch(JSONException je){
			je.printStackTrace();
		}catch(NullPointerException npe){
			npe.printStackTrace();
		}
	}
}
