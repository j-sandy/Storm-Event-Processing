package com.opsmx.eventprocessing;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.esotericsoftware.yamlbeans.YamlReader;
import com.opsmx.model.Metric;
import com.opsmx.model.MetricData;
import com.opsmx.model.SLA;
import com.opsmx.model.SLAData;
//import com.opsmx.model.ActivityAlert;
//import com.opsmx.model.Alert;
import com.opsmx.utils.OpsmxUtils;


/**
 * Bolt for generating SLA (Service Level Agreement)alert.
 * <p>
 * This class will get data from the spout as well as from the SLA API. The data
 * is then compared to check whether there is any violation. If there is any
 * violation then a Alert will be generated.
 * </p>
 */
public class SlaBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private HashMap<Integer, MetricData> slaIdVsmetricData;

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		this.slaIdVsmetricData = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Tuple tuple) {
		if (tuple.getSourceStreamId().equals("EvaluateTriggerStream")) {
		//if (tuple.getSourceStreamId().equals("SlaStream")) {
			int slaId = tuple.getIntegerByField("slaId");
			//System.out.println("Inside SLABolt::EvaluateTriggerStream: "+Integer.toString(slaId));
			evaluateSLAAndCleanUp(slaId);
		} else {			
			Map<String, String> tags = (Map<String, String>) tuple.getValueByField("tags");
			//System.out.println("Inside SLABolt::SLAStream: "+tags.toString());
			long timestamp = tuple.getLongByField("timestamp");
			String metricName = (String) tuple.getValueByField("name");
			Double metricValue = tuple.getDoubleByField("value");
			System.out.println("Inside SLABolt::SLAStream: "+tags.toString()+ ":"+ timestamp+":"+metricName+":"+metricValue+":timestamp-now"+new Date().getTime());
			ArrayList<SLA> slalist = null;
			try {
				slalist = SLAData.getSLAsForMetric(metricName);
				for (SLA sla : slalist ) {
					System.out.println("Inside SLABolt::SLAStream::For loop "+sla+":--"+metricValue+metricName+":"+sla.getFilterConditions()+" --: tag= "+tags+":"+tags.keySet());
					MetricData metricData = slaIdVsmetricData.get(sla.getId());
					String groupByValue = tags.get(sla.getGroupBy());
					//if (sla.getFilterConditions()!=null){
						if (OpsmxUtils.evaluateFilterConditions(tags, sla.getFilterConditions())) {	
							System.out.println("Inside SLABolt::SLAStream::For loop::evalutefiltercondition: "+sla.getGroupBy());
							if (StringUtils.isBlank(groupByValue)) {
								continue;
							}
							if (metricData == null) {
								metricData = new MetricData();
								slaIdVsmetricData.put(sla.getId(), metricData);
							}
							metricData.pushMetric(groupByValue, new Metric(metricName, timestamp, metricValue, tags));
							System.out.println("MetricData: "+metricData.getData().get(groupByValue).getFirst());
						}
					//}else{
					//	metricData.pushMetric(groupByValue, new Metric(metricName, timestamp, metricValue, tags));
					//}			
				}
			} catch (NullPointerException npe){
				npe.printStackTrace();
				slalist = null;
			}
		}
		collector.ack(tuple);
	}

	private void evaluateSLAAndCleanUp(int slaId) {
		//System.out.println("inside evaluateSLAAndCleanUp(): "+Integer.toString(slaId));
		SLA sla = SLAData.getSLA(slaId);
		int aggregateDurationSecs = sla.getAggregateDurationSeconds();
		long currentTime = new Date().getTime();
		long startTimeToEvaluate = new Date(currentTime - aggregateDurationSecs * 1000).getTime();
		MetricData metricData = slaIdVsmetricData.get(slaId);
		System.out.println("inside evaluateSLAAndCleanUp(): starttimeto evaluate "+startTimeToEvaluate);
		if (metricData == null || metricData.getData().isEmpty()) {
			return;
		}
		for (Map.Entry<String, LinkedList<Metric>> groupMetricsEntry : metricData.getData().entrySet()) {
			String groupByValue = groupMetricsEntry.getKey();
			LinkedList<Metric> groupMetrics = groupMetricsEntry.getValue();
			Iterator<Metric> groupMetricsIterator = groupMetrics.iterator();
			Metric metric=null;
			long metrictimestamp;
			switch (sla.getAggregator()) {
			case AVG:
				double overallValue = 0;
				int count = 0;
				metric=null;
				while (groupMetricsIterator.hasNext()) {
					metric = groupMetricsIterator.next();
					//For converting timestamp without microsec (10 digit) to with microsec (13 digit) by multiplying 1000 ms, but loosing precision for microsec.
					metrictimestamp = Long.toString(metric.getTimestamp()).trim().length()== 10 ? metric.getTimestamp()*1000 : metric.getTimestamp() ;        
					if (metrictimestamp < startTimeToEvaluate) {
						System.out.println("inside evaluateSLAAndCleanUp(): AVG: cleanup "+metrictimestamp);
						groupMetricsIterator.remove();
						continue;
					}
					overallValue += metric.getValue();
					count++;
				}
				overallValue /= count;
				evaluateSLACondition(sla, overallValue, groupByValue,metric==null ? null :metric);
				break;
			case MIN:
				double minValue = Double.MAX_VALUE;
				metric = null;
				while (groupMetricsIterator.hasNext()) {
					metric = groupMetricsIterator.next();
					metrictimestamp = Long.toString(metric.getTimestamp()).trim().length()== 10 ? metric.getTimestamp()*1000 : metric.getTimestamp() ; 
					if (metrictimestamp < startTimeToEvaluate) {
						System.out.println("inside evaluateSLAAndCleanUp(): MIN: cleanup "+metrictimestamp);
						groupMetricsIterator.remove();
						continue;
					}
					minValue= Math.min(minValue, metric.getValue());
					//count++;
				}
				//overallValue /= count;
				evaluateSLACondition(sla, minValue==Double.MAX_VALUE ? Double.NaN : minValue, groupByValue,metric==null ? null :metric);
				break;
			case MAX:
				double maxValue = Double.MIN_VALUE;
				metric = null;
				while (groupMetricsIterator.hasNext()) {
					metric = groupMetricsIterator.next();
					metrictimestamp = Long.toString(metric.getTimestamp()).trim().length()== 10 ? metric.getTimestamp()*1000 : metric.getTimestamp() ;
					if (metrictimestamp < startTimeToEvaluate) {
						System.out.println("inside evaluateSLAAndCleanUp(): MAX: cleanup "+metrictimestamp);
						groupMetricsIterator.remove();
						continue;
					}
					maxValue= Math.max(maxValue, metric.getValue());
					//count++;
				}
				evaluateSLACondition(sla, maxValue==Double.MIN_VALUE ? Double.NaN : maxValue, groupByValue,metric==null ? null :metric);
				break;
			case SUM:
				double sumValue = 0;
				metric = null;
				while (groupMetricsIterator.hasNext()) {
					metric = groupMetricsIterator.next();
					metrictimestamp = Long.toString(metric.getTimestamp()).trim().length()== 10 ? metric.getTimestamp()*1000 : metric.getTimestamp() ;
					if (metrictimestamp < startTimeToEvaluate) {
						System.out.println("inside evaluateSLAAndCleanUp(): SUM: cleanup "+metrictimestamp);
						groupMetricsIterator.remove();
						continue;
					}
					sumValue+= metric.getValue() ;
					//count++;
				}
				evaluateSLACondition(sla, sumValue, groupByValue,metric==null ? null :metric);
				break;
			default:
				System.out.println("SLA Aggregator not handled:" + sla.getAggregator());
				break;
			}
		}
	}

	private void evaluateSLACondition(SLA sla, double evaluatedValue, String groupByValue, Metric metric) {
		System.out.println("inside evaluateSLACondition(): "+evaluatedValue+" : "+groupByValue+ " Metric : "+metric==null?"NULL":"Available");
		boolean slaEvaluationResult = OpsmxUtils.evaluateCondition(evaluatedValue, sla.getComparisonOperator(),
				sla.getValue());
		if (slaEvaluationResult) {
			System.out.println("SLA violated: conditionValue:" + sla.getValue() + " evaluatedValue:" + evaluatedValue
					+ ", groupBy:" + groupByValue + "SLA:" + sla);
			// TODO generate alert with sla.groupByName and groupByValue
			if (metric.getTags().get(sla.getGroupBy()).equalsIgnoreCase(groupByValue)){
				String entityUserUID=metric.getTags().get("providerUID")==null ? "" : metric.getTags().get("providerUID");
				String host=metric.getTags().get("host")==null ? "" : metric.getTags().get("host");
				System.out.println("Inside evaluateSLACondition(): SLA Violated : "+sla.getMetricName()+" : "+evaluatedValue+" : "+entityUserUID+" : "+host+" : "+new Date().getTime());
				collector.emit("ActivityAlert", new Values("alert","GOOGLE_GLASS",sla.getMetricName(),evaluatedValue,entityUserUID,host,new Date().getTime()));
			}
			//collector.emit("ActivityAlert", new Values("alert","signalLoss","device.rssi",85.0,"1124-1004","0WP1A1AA15250058",1501509541198L));
			//try{
				/*Alert testalert = new Alert(sla.getMetricName(),"Alert","Normal","Output",1,"host1",true,"testalert","sensorMetric","vendor","connectivitytype","location");
				testalert.addObjectToList();*/
			/*	ActivityAlert actalert = new ActivityAlert("alert","signalLoss","device.rssi",85.0,"1124-1004","0WP1A1AA15250058",1501509541198L);
				actalert.addObjectToList();
			} catch (IOException ioe){
				ioe.printStackTrace();
			}*/
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("ActivityAlert", new Fields("type","activity","metric","value","entityUserUID","host","timestamp"));
	}
}
