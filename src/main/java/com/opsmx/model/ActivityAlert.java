package com.opsmx.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opsmx.utils.URLReader;

public class ActivityAlert implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("type")
	private String type;
	@JsonProperty("activity")
	private String activity;
	@JsonProperty("metric")
	private String metric;
	@JsonProperty("value")
	private double value;
	@JsonProperty("entityUserUID")
	private String entityUserUID;
	@JsonProperty("host")
	private String host;
	@JsonProperty("timestamp")
	private long timestamp;

	public ActivityAlert() {

	}

	public ActivityAlert(
			String type, 
			String activity, 
			String metric,
			double value,
			String entityUserUID, 
			String host,
			long timestamp
			)
	{
		this.type = type;
		this.activity = activity;
		this.metric = metric;
		this.value = value;
		this.entityUserUID = entityUserUID;
		this.host = host;
		this.timestamp = timestamp;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("activity")
	public String getActivity() {
		return activity;
	}

	@JsonProperty("activity")
	public void setActivity(String activity) {
		this.activity = activity;
	}

	@JsonProperty("metric")
	public String getMetric() {
		return metric;
	}

	@JsonProperty("metric")
	public void setMetric(String metric) {
		this.metric = metric;
	}
	
	@JsonProperty("value")
	public double getValue() {
		return value;
	}

	@JsonProperty("value")
	public void setValue(double value) {
		this.value = value;
	}

	@JsonProperty("entityUserUID")
	public String getEntityUserUID() {
		return entityUserUID;
	}

	@JsonProperty("entityUserUID")
	public void setEntityUserUID(String entityUserUID) {
		this.entityUserUID = entityUserUID;
	}

	@JsonProperty("host")
	public String getHost() {
		return host;
	}

	@JsonProperty("host")
	public void setHost(String host) {
		this.host = host;
	}

	@JsonProperty("timestamp")
	public void setTimestamp(long timestamp){
		this.timestamp = timestamp;
	}

	@JsonProperty("timestamp")
	public long getTimestamp(){
		return timestamp;
	}

	public String toString(){
		String str = "type "+ type +"\t"+ "activity  " + activity +"\t" + "metric  " + metric +"\t" + "value  " + value +"\t"+ "entityUserUID  " + entityUserUID +"\t" +"host  " + host +"\t" +"timestamp " + timestamp; 
		return str;
	}


	public void addObjectToList() throws IOException{
		parseAlertsToAddToREST(this);
	}

	public void parseAlertsToAddToREST(ActivityAlert alert) {
		HttpURLConnection httpURLConnection;
		BufferedWriter bufferedWriter = null;
		OutputStream outputStream = null;
		URL url = null;
		StringBuffer sb;
		BufferedReader br = null;
		
		try {
			url = new URL(URLReader.getUrlFromConfigFile("SaveAlertUrl"));
//			url = new URL("http://175.126.104.42:8161/opsmx-services/resources/sla/saveAlert");
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
			
			httpURLConnection.setInstanceFollowRedirects(false);
			
			outputStream = httpURLConnection.getOutputStream();


			bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

			ObjectMapper mapper = new ObjectMapper();

			mapper.writeValue(bufferedWriter, alert);
			mapper.writeValue(new File("activityalertjson.json"), alert);
			System.out.println("httpURLConnection.getResponseCode() ::"+httpURLConnection.getResponseCode());
			if(httpURLConnection.getResponseCode() == 500){
				System.out.println("httpURLConnection.getResponseMessage()" +httpURLConnection.getResponseMessage());
				System.out.println("GOT 500 ERROR");

			} else{
				System.out.println("httpURLConnection.getResponseMessage()" +httpURLConnection.getResponseMessage());
				br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
				sb = new StringBuffer();
				String output;
				while((output=br.readLine())!= null)
				{
					sb.append(output);
				}
				System.out.println(sb);
			}

			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {

			}
			outputStream.flush();
			outputStream.close();
			bufferedWriter.flush();
			bufferedWriter.close();
			br.close();

		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			System.out.println(mue.getMessage());
		} catch (ProtocolException pe) {
			System.out.println(pe.getMessage());
			pe.printStackTrace();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		finally{
			if(bufferedWriter != null) {
				try {
					//bufferedWriter.flush();
					bufferedWriter.close();
				} catch (IOException e) {
					System.out.println("Unable To Close bufferedWriter : "+e.getMessage());
				}
			}
			if(outputStream != null) {
				try {
					//outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					System.out.println("Unable To Close outputstream : "+e.getMessage());
				}
			}
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("Unable To Close outputstream : "+e.getMessage());
				}
			}
		}
	}

}
