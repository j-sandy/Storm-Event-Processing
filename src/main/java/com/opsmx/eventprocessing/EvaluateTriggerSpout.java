package com.opsmx.eventprocessing;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opsmx.model.SLA;
import com.opsmx.model.SLAData;

/**
 * Spout to feed data into Storm from an DatagramSocket (UDP).
 * <p>
 * The data is collected from Rt Plugin's port and passed onto respective bolts
 * for further processing. The collected data is emitted in the form of tuples
 * containing multiple fields, named "name", "timestamp", "tags" and "value" for
 * each packet received on the DatagramSocket.
 * </p>
 * 
 * @author Harsha
 */
public class EvaluateTriggerSpout extends BaseRichSpout implements IRichSpout {
	private static final long serialVersionUID = 4148449439835472942L;
	private static ConcurrentHashMap<Integer,Long> eval = new ConcurrentHashMap<>();

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private SpoutOutputCollector collector;

	public EvaluateTriggerSpout() {
	}

	@Override
	public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void nextTuple() {
		// TODO emit evaluation tuples for each SLA at required intervals
//		Utils.sleep(10000);
//		int slaId = 2;
//		SLA sla = SLAData.getSLA(slaId);
//		collector.emit("EvaluateTriggerStream", new Values(sla.getMetricName(), slaId));
		SLAData.updateSLAs();
		long timetosleep=Long.MAX_VALUE;
		//System.out.print(i);
		for(Map.Entry<Integer,SLA> slalist : SLAData.getIdVsSLAsMap().entrySet()){
			if(eval.containsKey(slalist.getKey())){
				if((eval.get(slalist.getKey())+(slalist.getValue().getEvaluateDurationSeconds()*1000))< new Date().getTime()){
					eval.put(slalist.getKey(),new Date().getTime());
					System.out.println( eval.get(slalist.getKey())+ " : "+slalist.getValue());
					collector.emit("EvaluateTriggerStream", new Values(slalist.getValue().getMetricName(), slalist.getKey()));
				}
				long temp=(eval.get(slalist.getKey())+(slalist.getValue().getEvaluateDurationSeconds()*1000))- new Date().getTime();
				timetosleep = Math.min(timetosleep, temp<0 ? timetosleep : temp );
			}else{
				eval.put(slalist.getKey(), new Date().getTime());
				System.out.println(eval.get(slalist.getKey())+ " : "+slalist.getValue());
				collector.emit("EvaluateTriggerStream", new Values(slalist.getValue().getMetricName(), slalist.getKey()));
				timetosleep=Math.min(timetosleep, (slalist.getValue().getEvaluateDurationSeconds()*1000));
				
			}
		}
		//System.out.println(timetosleep);
			Utils.sleep(timetosleep);
		
	}

	public boolean isDistributed() {
		return false;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("EvaluateTriggerStream", new Fields("name", "slaId"));
	}

	@Override
	public void ack(Object tuple) {
	}

	@Override
	public void fail(Object feedId) {
	}
}