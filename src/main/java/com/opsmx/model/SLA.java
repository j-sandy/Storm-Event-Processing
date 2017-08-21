package com.opsmx.model;

import java.util.ArrayList;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.opsmx.enums.Aggregator;
import com.opsmx.enums.ComparisonOperator;

public class SLA {
	private int id;
	private String metricName;
	private Aggregator aggregator;
	private ComparisonOperator comparisonOperator;
	private int aggregateDurationSeconds;
	private int evaluateDurationSeconds;
	private double value;
	private String groupBy;
	private ArrayList<FilterCondition> filterConditions;

	public SLA(int id, String metricName, Aggregator aggregator, ComparisonOperator comparisonOperator,
			int aggregateDurationSeconds, int evaluateDurationSeconds, double value, String groupBy,
			ArrayList<FilterCondition> filterConditions) {
		super();
		this.id = id;
		this.metricName = metricName;
		this.aggregator = aggregator;
		this.comparisonOperator = comparisonOperator;
		this.aggregateDurationSeconds = aggregateDurationSeconds;
		this.setEvaluateDurationSeconds(evaluateDurationSeconds);
		this.value = value;
		this.groupBy = groupBy;
		this.filterConditions = filterConditions;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public Aggregator getAggregator() {
		return aggregator;
	}

	public void setAggregator(Aggregator aggregator) {
		this.aggregator = aggregator;
	}

	public ComparisonOperator getComparisonOperator() {
		return comparisonOperator;
	}

	public void setComparisonOperator(ComparisonOperator comparisonOperator) {
		this.comparisonOperator = comparisonOperator;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public ArrayList<FilterCondition> getFilterConditions() {
		return filterConditions;
	}

	public void setFilterConditions(ArrayList<FilterCondition> filterConditions) {
		this.filterConditions = filterConditions;
	}

	public int getAggregateDurationSeconds() {
		return aggregateDurationSeconds;
	}

	public void setAggregateDurationSeconds(int aggregateDurationSeconds) {
		this.aggregateDurationSeconds = aggregateDurationSeconds;
	}

	public int getEvaluateDurationSeconds() {
		return evaluateDurationSeconds;
	}

	public void setEvaluateDurationSeconds(int evaluateDurationSeconds) {
		this.evaluateDurationSeconds = evaluateDurationSeconds;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
