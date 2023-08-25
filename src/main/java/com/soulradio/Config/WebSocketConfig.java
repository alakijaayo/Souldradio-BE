package com.soulradio.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws-message").setAllowedOriginPatterns("*").withSockJS(); // Configures SockJS endpoint, allowing fallback options for browsers that do not support WebSocket natively.
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic"); // Enables a simple memory-based broker to relay messages to the client on destinations prefixed with "/topic".
    config.setApplicationDestinationPrefixes("/app"); // Defines the prefix to use for client-side WebSocket destinations when a message is sent to the server.
  }

}
