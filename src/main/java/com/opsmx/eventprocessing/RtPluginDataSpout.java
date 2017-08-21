package com.opsmx.eventprocessing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Map;

import org.apache.storm.shade.com.google.common.base.Preconditions;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opsmx.model.Metric;

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
public class RtPluginDataSpout extends BaseRichSpout implements IRichSpout {

	private static final long serialVersionUID = 4148449439835472941L;
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private SpoutOutputCollector collector;
	private int port;
	private MessagePack msgpack;
	private DatagramSocket datagramSocket;
	private DatagramPacket packet;
	private Metric metric;
	private byte[] buffer;
	private SocketAddress localport;
	private static int currentNumber = 0;

	public RtPluginDataSpout(int port) {
		this.port = port;
	}

	@Override
	public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector collector) {
		Preconditions.checkState(datagramSocket == null, "Spout already open on port " + port);
		this.collector = collector;
		buffer = new byte[1024];
		packet = new DatagramPacket(buffer, buffer.length);
		localport = new InetSocketAddress(port);
		try {
			datagramSocket = new DatagramSocket(null);
			datagramSocket.setReuseAddress(true);
			datagramSocket.bind(localport);
			logger.info("Opening Spout on port " + port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		msgpack = new MessagePack();
	}

	@Override
	public void close() {
		if (!datagramSocket.isClosed()) {
			datagramSocket.close();
			logger.info("Closing Sla Spout on port " + port);
		}
	}

	@Override
	public void nextTuple() {
		try {
			datagramSocket.receive(packet);
			//Value v=msgpack.read(buffer);			
			metric = msgpack.read(buffer, Metric.class);
			System.out.println("buffer= "+new String(buffer)+"--: "+metric);
		} catch (IOException e) {
			e.printStackTrace();
		}
		collector.emit("SlaStream", new Values(metric.getName(), metric.getTimestamp(), metric.getTags(),
				metric.getValue(), new Integer(currentNumber++)));
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