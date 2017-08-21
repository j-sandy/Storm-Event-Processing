package com.opsmx.utils;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.opsmx.enums.ComparisonOperator;
import com.opsmx.model.FilterCondition;

public class OpsmxUtils {

	public static boolean evaluateFilterConditions(Map<String, String> tags,
			ArrayList<FilterCondition> filterConditions) {
		if (filterConditions == null || filterConditions.isEmpty()) {
			return true;
		}
		boolean result = true;
		for (FilterCondition filterCondition : filterConditions) {
			boolean filterConditionResult = evaluateCondition(tags.get(filterCondition.getField()),
					filterCondition.getComparisonOperator(), filterCondition.getValue());
			result = result && filterConditionResult;
		}
		return result;

	}

	public static boolean evaluateCondition(String fieldValue, ComparisonOperator comparisonOperator,
			String conditionValue) {
		boolean result = false;
		try {
			switch (comparisonOperator) {
			case EQ:
				return StringUtils.equalsIgnoreCase(fieldValue, conditionValue);
			case GE:
				double fieldValueDouble = Double.parseDouble(fieldValue);
				double conditionValueDouble = Double.parseDouble(conditionValue);
				return fieldValueDouble >= conditionValueDouble;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}

	public static boolean evaluateCondition(double evaluatedValue, ComparisonOperator comparisonOperator,
			double conditionValue) {
		boolean result = false;
		try {
			switch (comparisonOperator) {
			case EQ:
				return evaluatedValue == conditionValue;
			case GE:
				return evaluatedValue >= conditionValue;
			case GT:
				return evaluatedValue > conditionValue;
			case LE:
				return evaluatedValue <= conditionValue;
			case LT:
				return evaluatedValue < conditionValue;
			case NE:
				return evaluatedValue != conditionValue;
			default:
				System.out.println(comparisonOperator + " not handled in evaluateCondition");
				break;

			}
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}

}
