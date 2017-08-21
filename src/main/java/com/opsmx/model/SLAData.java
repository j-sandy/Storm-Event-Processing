package com.opsmx.model;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.opsmx.enums.Aggregator;
import com.opsmx.enums.ComparisonOperator;

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

	public static void updateSLAs() {
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
	}

}
