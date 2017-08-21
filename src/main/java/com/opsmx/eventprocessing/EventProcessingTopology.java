package com.opsmx.eventprocessing;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import com.opsmx.model.SLAData;
import com.opsmx.test.MetricTestDataSpout;

/**
 * <h1>Event Processing Storm Topology</h1>
 * 
 * This is the main class for storm which will create a topology with spouts and
 * bolts. Then the created topology is submitted to a cluster to run the storm.
 *
 * @author Madhu
 */
public class EventProcessingTopology {
	public static void main(String[] args) {
		//System.out.println("Inside EventProcessingTopology::");
		SLAData.updateSLAs();
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("RtPluginDataSpout", new RtPluginDataSpout(2025));
		//builder.setSpout("RtPluginDataSpout", new MetricTestDataSpout());
		builder.setSpout("EvaluateTriggerSpout", new EvaluateTriggerSpout());
		builder.setBolt("SlaProcessingBolt", new SlaBolt())
				.fieldsGrouping("RtPluginDataSpout", "SlaStream", new Fields("name"))
				.fieldsGrouping("EvaluateTriggerSpout", "EvaluateTriggerStream", new Fields("name"));
		builder.setBolt("AlertProcessing", new AlertBolt())
				.fieldsGrouping("SlaProcessingBolt", "ActivityAlert",new Fields("type"));
		Config conf = new Config();
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("EventProcessing", conf, builder.createTopology());

		// To Kill The Storm Topology Uncomment The Following Code.

		/*
		 * cluster.killTopology("EventProcessing"); cluster.shutdown();
		 */
	}
}
