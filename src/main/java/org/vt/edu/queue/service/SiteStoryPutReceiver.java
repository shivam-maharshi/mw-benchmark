package org.vt.edu.queue.service;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * A simple Receiver class that consumes messages from queue in a round robin
 * fashion by default.
 * 
 * @author shivam.maharshi
 */
public class SiteStoryPutReceiver {

	private final static String QUEUE_NAME = "hello";

	public static void main(String[] argv) throws java.io.IOException, java.lang.InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		Consumer consumer = new SimpleConsumer(channel);
		channel.basicConsume(QUEUE_NAME, true, consumer);
	}

}

class SimpleConsumer extends DefaultConsumer {

	public SimpleConsumer(Channel channel) {
		super(channel);
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {
		String message = new String(body, "UTF-8");
		System.out.println(" [x] Received '" + message + "'");
	}

}
