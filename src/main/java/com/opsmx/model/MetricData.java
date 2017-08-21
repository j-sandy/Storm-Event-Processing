package com.opsmx.model;

import java.util.HashMap;
import java.util.LinkedList;

public class MetricData {

	private HashMap<String, LinkedList<Metric>> data;

	public MetricData() {
		this.data = new HashMap<>();
	}

	public HashMap<String, LinkedList<Metric>> getData() {
		return data;
	}

	public void pushMetric(String group, Metric metric) {
		LinkedList<Metric> metrics = data.get(group);
		if (metrics == null) {
			metrics = new LinkedList<>();
			data.put(group, metrics);
		}
		metrics.add(metric);
	}

}
