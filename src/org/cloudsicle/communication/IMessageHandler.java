package org.cloudsicle.communication;

import org.cloudsicle.messages.IMessage;

public interface IMessageHandler {

	public void process(IMessage message);
}
