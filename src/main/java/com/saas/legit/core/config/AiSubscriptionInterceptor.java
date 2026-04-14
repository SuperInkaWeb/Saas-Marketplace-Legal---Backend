package com.saas.legit.core.config;

import com.saas.legit.core.exception.ResourceNotFoundException;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.subscription.model.UserSubscription;
import com.saas.legit.module.subscription.repository.UserSubscriptionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AiSubscriptionInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final UserSubscriptionRepository subscriptionRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // Solo interceptar si ruta corresponde a IA
        if (request.getRequestURI().startsWith("/api/v1/ai")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Usuario no autenticado");
                return false;
            }

            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Usuario no encontrado");
                return false;
            }

            // Checar suscripción
            Optional<UserSubscription> activeSubOpt = subscriptionRepository.findActiveSubscriptionByUserId(user.getIdUser());
            if (activeSubOpt.isEmpty()) {
                response.sendError(HttpStatus.FORBIDDEN.value(), "Requiere subscripción Premium o Corporativa para acceder a la IA.");
                return false;
            }

            UserSubscription activeSub = activeSubOpt.get();
            String planName = activeSub.getPlan().getName();

            if (!planName.equals("PREMIUM") && !planName.equals("CORPORATE")) {
                response.sendError(HttpStatus.FORBIDDEN.value(), "Su plan actual (" + planName + ") no incluye acceso a la IA Legal.");
                return false;
            }
        }

        return true;
    }
}
