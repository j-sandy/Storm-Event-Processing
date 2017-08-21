package com.opsmx.eventprocessing;

import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opsmx.model.SLAData;

/**
 * Bolt for generating SLA (Service Level Agreement)alert.
 * <p>
 * This class will get data from the spout as well as from the SLA API. The data
 * is then compared to check whether there is any violation. If there is any
 * violation then a Alert will be generated.
 * </p>
 */
public class SLARefreshBolt extends BaseRichBolt {

	private OutputCollector collector;
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// configure how often a tick tuple will be sent to our bolt
		Config conf = new Config();
		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 600);
		return conf;
	}

	@Override
	public void execute(Tuple tuple) {
		SLAData.updateSLAs();
		collector.ack(tuple);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}
