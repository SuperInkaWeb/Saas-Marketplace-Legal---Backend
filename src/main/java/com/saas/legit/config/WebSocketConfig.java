package com.saas.legit.config;

import com.saas.legit.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
                    
                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        String jwt = authorizationHeader.substring(7);

                        jwtService.validateAndExtractClaims(jwt).ifPresent(claims -> {
                            String userIdStr = claims.getSubject();
                            String email = claims.get("email", String.class);
                            String role = claims.get("role", String.class);
                            Long tenantId = claims.get("tenant", Long.class);

                            if (userIdStr != null) {
                                Long userId = Long.parseLong(userIdStr);
                                java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities = 
                                        new java.util.ArrayList<>();
                                if (role != null) {
                                    authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
                                }

                                com.saas.legit.security.CustomUserDetailsService.CustomUserDetails userDetails =
                                        new com.saas.legit.security.CustomUserDetailsService.CustomUserDetails(
                                                userId, email, null, authorities, tenantId);

                                UsernamePasswordAuthenticationToken authToken = 
                                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                                
                                accessor.setUser(authToken);
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                            }
                        });
                    }
                }
                return message;
            }
        });
    }
}
