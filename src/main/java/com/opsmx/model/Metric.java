package com.opsmx.model;

import java.io.Serializable;
import java.util.Map;

import org.msgpack.annotation.Message;

import com.fasterxml.jackson.annotation.JsonProperty;

@Message
public class Metric implements Serializable {

	private static final long serialVersionUID = 1L;

	public Metric() {

	}

	@JsonProperty("name")
	private String name;
	@JsonProperty("timestamp")
	private long timestamp;
	@JsonProperty("value")
	private double value;
	@JsonProperty("tags")
	private Map<String, String> tags;

	public Metric(String name, long timestamp, double value, Map<String, String> tags) {
		this.name = name;
		this.timestamp = timestamp;
		this.value = value;
		this.tags = tags;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("timestamp")
	public long getTimestamp() {
		return timestamp;
	}

	@JsonProperty("timestamp")
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@JsonProperty("value")
	public double getValue() {
		return value;
	}

	@JsonProperty("value")
	public void setValue(double value) {
		this.value = value;
	}

	@JsonProperty("tags")
	public Map<String, String> getTags() {
		return tags;
	}

	@JsonProperty("tags")
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "Name:" + name + ",timestamp:" + timestamp + ",value:" + value+",tags:"+tags;
	}

}
