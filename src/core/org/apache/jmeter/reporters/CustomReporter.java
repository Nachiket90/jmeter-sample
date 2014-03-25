package org.apache.jmeter.reporters;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jmeter.JMeter;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.samplers.Remoteable;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.math.StatCalculatorLong;
import org.apache.log.Logger;


/**
 * 
 * @author Nachiket
 * 
 * This is custom reporter class created for calculation of Test duration. 
 *
 */

public class CustomReporter extends AbstractTestElement implements SampleListener, Serializable, TestListener, Remoteable, NoThreadClone {

	
	/**
	 * This class contains sample data
	 * Label, elapsed time, latency and sample status 
	 */
	public class Samples {
		
		
		String sampleLabel;
		int sampleElapsedTime,sampleLatency;
		boolean sampleStatus;
	
		public Samples(String sampleLabel, int sampleElapsedTime, int sampleLatency, boolean sampleStatus)
		{
			this.sampleLabel=sampleLabel;
			this.sampleElapsedTime=sampleElapsedTime;
			this.sampleLatency=sampleLatency;
			this.sampleStatus=sampleStatus;
		}
	}
	
	private static final long serialVersionUID = 1L;
	private double sd;
	private double mean;
	private long errorCount=0;
	private long totalSampleCount=0; 
	private ArrayList<Samples> sampleList;
	private HashMap<String,StatCalculatorLong> sampleLabelStatscal=new HashMap<String,StatCalculatorLong>();; 
	private long startTime = 0;
	private long endTime = 0;
	private int stopTries = 0;
	
	
	private static final Logger log = LoggingManager.getLoggerForClass();
	
	public CustomReporter() {
		super();		
	}
	@Override
	public void sampleOccurred(SampleEvent e) {
		
		//get sample result into a variable 
		SampleResult s = e.getResult();
			
				//synchronized (this) {
		long now=System.currentTimeMillis();
		log.info("CustomLog : starttime :" + startTime + " endtime : " + endTime + " now :" + now);
		if (now < endTime){
			
				
				//Increasing total sample count by 1
				totalSampleCount+=1;
				
				//next thing is add error count per sample
				if (s.getErrorCount()==0) {
					errorCount+=1;
				}
				
				//map sample to hashmap
				if (sampleLabelStatscal.containsKey(s.getSampleLabel())) {
					StatCalculatorLong tmp=sampleLabelStatscal.get(s.getSampleLabel());
					(sampleLabelStatscal.get(s.getSampleLabel())).addValue(s.getTime(), s.getSampleCount());;
					log.info("values of stats cal is :" + tmp.toString()+ " count : " + tmp.getCount() + " sd :" + tmp.getStandardDeviation());
					log.info("CustomLog in if after adding: " + s.getSampleLabel()+  " cal : " + sampleLabelStatscal.toString());
				}
				else {
					StatCalculatorLong calc=new StatCalculatorLong();
					calc.addValue(s.getTime(), s.getSampleCount());
					//add label into hashmap as it is not present 
					sampleLabelStatscal.put(s.getSampleLabel(),calc);
					StatCalculatorLong tmp=sampleLabelStatscal.get(s.getSampleLabel());
					log.info("values of stats cal is :" + tmp.toString()+ " count : " + tmp.getCount() + " sd :" + tmp.getStandardDeviation());
					log.info("CustomLog in else after adding: " + s.getSampleLabel()+  " cal : " + sampleLabelStatscal.toString());
					
				}
				
				
		}
		else {
			printResults();
			stopTest();
		}
				
				//}
				
				
				/*
				 
				//calculate stats 
				StatCalculatorLong calculator = new StatCalculatorLong();
				
				//this block is synchronized as at a time all samples should not
				//update calculated stats.
				synchronized (calculator) {
					calculator.addValue(s.getTime(), s.getSampleCount());
		            calculator.addBytes(s.getBytes());
		          
		            sd=calculator.getStandardDeviation();
		            mean=calculator.getMean();
		            log.info("CustomLog : standard deviation:- " + sd + "mean:- " + mean + "total count of samples:- " + s.getSampleCount() + "sample label:- " + s.getSampleLabel());
					
				}
				
				 
				 * */
		
	}

	
	@Override
	public void sampleStarted(SampleEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sampleStopped(SampleEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void testStarted() {
		
		log.info("CustomLog : Test Started normal");
		startTime=System.currentTimeMillis();
		endTime=startTime+10000;
		log.info("CustomLog : starttime :" + startTime + "endtime : " + endTime);
	}
	@Override
	public void testStarted(String host) {
		
	}
	@Override
	public void testEnded() {
		
		log.info("CustomLog : Test ended normal");
		log.info("CustomLog : keyset" + sampleLabelStatscal.keySet().toString() + "length :" + sampleLabelStatscal.keySet().size());
		log.info("CustomLog : sampleLabelStatscal" + sampleLabelStatscal.toString());
		//printResults();
		
	}
	@Override
	public void testEnded(String host) {
		// TODO Auto-generated method stub
	}
	@Override
	public void testIterationStart(LoopIterationEvent event) {
		// TODO Auto-generated method stub
		log.info("CustomLog : Test iteration Started");
	}

	public void printResults(){
		if (!(sampleLabelStatscal.isEmpty())){
			for (String label : sampleLabelStatscal.keySet()) {	
				StatCalculatorLong tmp=sampleLabelStatscal.get(label);
				log.info("CustomLog : label : " + label + "standard deviation:- " + tmp.getStandardDeviation() + "mean:- " + tmp.getMean() + "total count of samples:- " + tmp.getCount());
			}
		}
		else 
			log.info("you cant access it here");
	}
	private void stopTest() {
		stopTries++;

		if (JMeter.isNonGUI()) {
		log.info("CustomLog : Stopping JMeter via UDP call");
		stopTestViaUDP("StopTestNow");
		} 
		else {
			if (stopTries> 5) {
				log.info("CustomLog :Tries more than 10, stop it NOW!");
				StandardJMeterEngine.stopEngineNow();
		    } 
			else if (stopTries> 2) {
				log.info("CustomLog :Tries more than 5, stop it!");
				StandardJMeterEngine.stopEngine();
		    } 
			else {
				log.info("CustomLog :stopping test normally!");
				JMeterContextService.getContext().getEngine().askThreadsToStop();
		    }
		}
		
		
	}
	private void stopTestViaUDP(String command) {
		try {
			int port = JMeterUtils.getPropDefault("jmeterengine.nongui.port", JMeter.UDP_PORT_DEFAULT);
			log.info("CustomLog : Sending " + command + " request to port " + port);
			DatagramSocket socket = new DatagramSocket();
			byte[] buf = command.getBytes("ASCII");
			InetAddress address = InetAddress.getByName("localhost");
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
			socket.send(packet);
			socket.close();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}	
	
}
