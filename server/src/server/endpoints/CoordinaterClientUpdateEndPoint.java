package server.endpoints;

import java.io.IOException;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import data.Message;
import data.MessageDecoder;
import data.MessageEncoder;
import server.manager.CoordinatorServerManager;
import util.Util;

/**
 * @author Sandeep
 *
 */
@ServerEndpoint(value = "/coordinatorclientupdate/{app}/{channel}/{deviceID}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class CoordinaterClientUpdateEndPoint {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	CoordinatorServerManager coordinatorManager;

	@OnOpen
	public void onOpen(Session session, @PathParam("app") final String app, @PathParam("channel") final String channel,
			@PathParam("deviceID") final String deviceID) {

		coordinatorManager = CoordinatorServerManager.getInstance();
		logger.info(" Opening connection for Application : " + app + " Channel :" + channel + "DeviceID : " + deviceID);

		if (null != deviceID) {
			session.getUserProperties().put("app", app);
			session.getUserProperties().put("channel", channel);
			session.getUserProperties().put("deviceID", deviceID);
			String key = Util.getAppSessionDeviceID(app, channel, deviceID);
			Message response = coordinatorManager.performClientOperation(app, channel, deviceID);
			

			try {
				logger.info(Util.MessageToJSONString(response));
				session.getBasicRemote().sendText(Util.MessageToJSONString(response));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("Connected ... " + session.getId());
	}

	@OnMessage
	public void onMessage(final Session session, Message message) {

	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
	}
}
