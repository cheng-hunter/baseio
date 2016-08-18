package com.gifisan.nio.extend.plugin.jms.server;

import java.io.IOException;
import java.util.List;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.gifisan.nio.extend.plugin.jms.Message;

public class P2PProductLine extends AbstractProductLine implements MessageQueue, Runnable {

	private Logger logger = LoggerFactory.getLogger(P2PProductLine.class);
	
	public P2PProductLine(MQContext context) {
		super(context);
	}
	
	protected ConsumerQueue createConsumerQueue() {
		return new P2PConsumerQueue();
	}

	//FIXME 完善消息匹配机制
	public void run() {

		for (; running;) {

			Message message = storage.poll(16);

			if (message == null) {
				continue;
			}

			String queueName = message.getQueueName();

			ConsumerQueue consumerQueue = getConsumerQueue(queueName);

			List<Consumer> consumers = consumerQueue.getSnapshot();

			if (consumers.size() == 0) {

				filterUseless(message);

				continue;
			}

			for(Consumer consumer:consumers){
				try {
					consumer.push(message);
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
					this.offerMessage(message);
				}
			}

			context.consumerMessage(message);
		}
	}
	
	public int messageSize(){
		return storage.size();
	}
}
