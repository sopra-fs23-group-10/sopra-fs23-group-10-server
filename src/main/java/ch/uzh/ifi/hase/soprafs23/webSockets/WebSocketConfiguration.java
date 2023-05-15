package ch.uzh.ifi.hase.soprafs23.webSockets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/questions", "/invitations", "/topics", "/game/result", "/games")
      .setHeartbeatValue(new long[] {1000, 1000})
      .setTaskScheduler(heartBeatScheduler())
      .setHeartbeatValue(new long[] {60000, 120000});
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
    .setAllowedOrigins("*")
    .setHandshakeHandler(new DefaultHandshakeHandler())
    .addInterceptors(new HttpSessionHandshakeInterceptor());

    registry.addEndpoint("/wss")
      .setAllowedOrigins("*")
      .setHandshakeHandler(new DefaultHandshakeHandler())
      .addInterceptors(new HttpSessionHandshakeInterceptor());
  }

  @Bean
  public ServletServerContainerFactoryBean createWebSocketContainer() {
    ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
    container.setMaxSessionIdleTimeout(-1L);
    return container;
  }

  @Bean
  public TaskScheduler heartBeatScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(1);
    return scheduler;
  }
}
