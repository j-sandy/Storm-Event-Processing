package com.opsmx.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
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

public class Alert implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("representMetric")
	private String representMetric;
	@JsonProperty("status")
	private String status;
	@JsonProperty("priority")
	private String priority;
	@JsonProperty("output")
	private String output;
	@JsonProperty("generatedNumberOfTimes")
	private Integer generatedNumberOfTimes;
	@JsonProperty("host")
	private String host;
	@JsonProperty("sentEmail")
	private Boolean sentEmail;
	@JsonProperty("alertType")
	private String alertType;  
	@JsonProperty("sensorMetric")
	private String sensorMetric;
	@JsonProperty("vendor")
	private String vendor;
	@JsonProperty("connectivityType")
	private String connectivityType;
	@JsonProperty("location")
	private String location;

	//int x;
	public Alert() {

	}

	public Alert(
			String representMetric, 
			String status, 
			String priority,
			String output, 
			Integer generatedNumberOfTimes,
			String host,
			Boolean sentEmail,
			String alertType,
			String sensorMetric,
			String vendor,
			String connectivityType,
			String location
			)
	{
		this.representMetric = representMetric;
		this.status = status;
		this.priority = priority;
		this.output = output;
		this.generatedNumberOfTimes = generatedNumberOfTimes;
		this.host = host;
		this.sentEmail = sentEmail;
		this.alertType = alertType;
		this.sensorMetric = sensorMetric;
		this.vendor = vendor;
		this.connectivityType = connectivityType;
		this.location = location;
		//this.x=0;
	}

	@JsonProperty("representMetric")
	public String getRepresentMetric() {
		return representMetric;
	}

	@JsonProperty("representMetric")
	public void setRepresentMetric(String representMetric) {
		this.representMetric = representMetric;
	}

	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("priority")
	public String getPriority() {
		return priority;
	}

	@JsonProperty("priority")
	public void setPriority(String priority) {
		this.priority = priority;
	}

	@JsonProperty("output")
	public String getOutput() {
		return output;
	}

	@JsonProperty("output")
	public void setOutput(String output) {
		this.output = output;
	}

	@JsonProperty("generatedNumberOfTimes")
	public Integer getGeneratedNumberOfTimes() {
		return generatedNumberOfTimes;
	}

	@JsonProperty("generatedNumberOfTimes")
	public void setGeneratedNumberOfTimes(Integer generatedNumberOfTimes) {
		this.generatedNumberOfTimes = generatedNumberOfTimes;
	}

	@JsonProperty("host")
	public String getHost() {
		return host;
	}

	@JsonProperty("host")
	public void setHost(String host) {
		this.host = host;
	}

	@JsonProperty("sentEmail")
	public Boolean isSentEmail() {
		return sentEmail;
	}

	@JsonProperty("sentEmail")
	public void setSentEmail(Boolean sentEmail) {
		this.sentEmail = sentEmail;
	}

	@JsonProperty("alertType")
	public void setAlertType(String alertType){
		this.alertType = alertType;
	}

	@JsonProperty("alertType")
	public String getAlertType(){
		return alertType;
	}

	@JsonProperty("sensorMetric")
	public void setSensorMetric(String sensorMetric){
		this.sensorMetric = sensorMetric;
	}

	@JsonProperty("sensorMetric")
	public String getSensorMetric(){
		return sensorMetric;
	}

	@JsonProperty("vendor")
	public void setVendor(String vendor){
		this.vendor = vendor;
	}

	@JsonProperty("vendor")
	public String getVendor(){
		return vendor;
	}

	@JsonProperty("connectivityType")
	public void setConnectivityType(String connectivityType){
		this.connectivityType = connectivityType;
	}

	@JsonProperty("connectivityType")
	public String getConnectivityType(){
		return connectivityType;
	}

	@JsonProperty("location")
	public void setLocation(String location){
		this.location = location;
	}

	@JsonProperty("location")
	public String getLocation(){
		return location;
	}

	public String toString(){
		String str = "description "+ representMetric +"\t"+ "status  " + status +"\t" + "priority  " + priority +"\t"+ "output  " + output +"\t" + "generatedNumberOfTimes " + generatedNumberOfTimes +"\t" +"host  " + host +"\t" +"sentEmail"+ sentEmail+ "\t" +"sensorName :" + sensorMetric; 
		// To enable printing the alert object 
		return str;
	}


	public void addObjectToList() throws IOException{
		parseAlertsToAddToREST(this);
	}

	public void parseAlertsToAddToREST(Alert alert) {
		HttpURLConnection httpURLConnection;
		BufferedWriter bufferedWriter = null;
		OutputStream outputStream = null;
		URL url = null;
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

			/*
                        if(this.status.equalsIgnoreCase("OK")){
                            mapper.writeValue(new File("ok.json"), alert);
                        }
                        else {
                            mapper.writeValue(new File("user.json"), alert);
                        }*/

			mapper.writeValue(new File("alertjson.json"), alert);


			System.out.println("httpURLConnection.getResponseCode() ::"+httpURLConnection.getResponseCode());
			/*
                        if(httpURLConnection.getResponseCode() == 500 && (x==0)){
                            System.out.println("Calling alert again");
                            x++;
                            this.addObjectToList();
                        }*/
			if(httpURLConnection.getResponseCode() == 500){
				System.out.println("httpURLConnection.getResponseMessage()" +httpURLConnection.getResponseMessage());
				System.out.println("GOT 500 ERROR");

				//				url = new URL(URLReader.getUrlFromConfigFile("SaveAlertUrl"));
				//				httpURLConnection = (HttpURLConnection) url.openConnection();
				//				httpURLConnection.setRequestMethod("POST");
				//				httpURLConnection.setDoOutput(true);
				//				httpURLConnection.setRequestProperty("Content-Type", "application/json");
				//				outputStream = httpURLConnection.getOutputStream();
				//
				//
				//				bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
				//				mapper = new ObjectMapper();
				//				mapper.writeValue(bufferedWriter, alert);
				//				mapper.writeValue(new File("retriedalertjson.json"), alert);

			} else{
				System.out.println("not500");
			}

			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				/* System.out.println("Failed : HTTP error code : "
			     + httpURLConnection.getResponseCode());
			    System.out.println("Failed : HTTP error code : "
			      + httpURLConnection.getResponseMessage()); */

			}


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
					bufferedWriter.flush();
					bufferedWriter.close();
				} catch (IOException e) {
					System.out.println("Unable To Close bufferedWriter : "+e.getMessage());
				}
			}
			if(outputStream != null) {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					System.out.println("Unable To Close outputstream : "+e.getMessage());
				}
			}
		}
	}

}
