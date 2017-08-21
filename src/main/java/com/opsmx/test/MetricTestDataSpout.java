package com.opsmx.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

/**
 * Spout to feed data into Storm from an DatagramSocket (UDP).
 * <p>
 * The data is collected from Rt Plugin's port and passed onto respective bolts
 * for further processing. The collected data is emitted in the form of tuples
 * containing multiple fields, named "name", "timestamp", "tags" and "value" for
 * each packet received on the DatagramSocket.
 * </p>
 * 
 * @author Madhu
 */
public class MetricTestDataSpout extends BaseRichSpout implements IRichSpout {

	private static final long serialVersionUID = 4148449439835472941L;
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private SpoutOutputCollector collector;
	private static int currentNumber = 0;
	private Random rand;

	public MetricTestDataSpout() {
	}

	@Override
	public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector collector) {
		this.collector = collector;
		this.rand = new Random();
	}

	@Override
	public void nextTuple() {
		Utils.sleep(1000);
		HashMap<String, String> tags = new HashMap<>();
		tags.put("host", "host1");
		collector.emit("SlaStream", new Values("test.metric1", new Date().getTime(), tags, rand.nextDouble() * 100,
				new Integer(currentNumber++)));
	}

	public boolean isDistributed() {
		return false;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("SlaStream", new Fields("name", "timestamp", "tags", "value", "number"));
	}

	@Override
	public void ack(Object tuple) {
	}

	@Override
	public void fail(Object feedId) {
	}
}