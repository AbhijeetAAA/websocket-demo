package org.me;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.me.server.ChatEndpoint;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {

        try {
            // Create and start Jetty server
            Server server = new Server(new InetSocketAddress("localhost", 8080));

            // Create a servlet context handler to map to the WebSocket endpoint
            ServletContextHandler context = new ServletContextHandler();
            context.setContextPath("/");  // Root context
            server.setHandler(context);  // Set handler to context

            // Initialize the WebSocket container and add the ChatEndpoint
            ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);
            wscontainer.addEndpoint(ChatEndpoint.class);  // Map WebSocket endpoint

            // Start the server and wait for requests
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}