package com.aiproxy.service;

import com.aiproxy.dto.GenerationRequest;
import com.aiproxy.dto.GenerationResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service("mockAIGenerationService")
public class MockAIGenerationService implements AIGenerationService {

    private static final long SIMULATED_LATENCY_MS = 1200;
    private static final String MODEL_NAME = "mock-gpt-1.0";

    private static final List<String> RESPONSES = List.of(
            "El patrón Proxy proporciona un sustituto o intermediario para otro objeto, permitiendo controlar el acceso a él. Es muy útil para aplicar reglas como autenticación, caché, registro o límites de uso sin modificar el objeto real.",
            "Una buena práctica al diseñar APIs es aplicar límites de uso por cliente. Así se protege la infraestructura, se garantiza un reparto justo de recursos y se pueden ofrecer distintos niveles de servicio según el plan contratado.",
            "La inteligencia artificial generativa produce contenido nuevo a partir de patrones aprendidos. En este entorno la respuesta es simulada, pero el flujo de control de cuotas, tokens y límites por minuto es el mismo que en un servicio real.",
            "Para resolver un problema complejo conviene dividirlo en partes pequeñas, definir entradas y salidas claras y validar cada paso. Esta estrategia reduce errores y facilita el mantenimiento del código a largo plazo.",
            "Spring Boot simplifica la creación de aplicaciones Java listas para producción gracias a su configuración automática, servidores embebidos y un ecosistema amplio de módulos para web, datos, seguridad y monitoreo.",
            "Los tokens son las unidades en que un modelo de lenguaje divide el texto. De forma aproximada, un token equivale a cuatro caracteres en inglés, por lo que el costo de una petición depende de la longitud del prompt y de la respuesta.",
            "Encadenar proxies permite separar responsabilidades: primero se valida la frecuencia de peticiones, luego el saldo de tokens disponible y finalmente se invoca el servicio real. Cada eslabón cumple una sola tarea y puede reutilizarse.",
            "Una cuota mensual ayuda a controlar costos y a planificar la capacidad. Cuando se agota, el usuario puede esperar al siguiente ciclo de facturación o mejorar su plan para obtener más tokens de forma inmediata."
    );

    private final TokenEstimator tokenEstimator;

    public MockAIGenerationService(TokenEstimator tokenEstimator) {
        this.tokenEstimator = tokenEstimator;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(SIMULATED_LATENCY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String text = RESPONSES.get(ThreadLocalRandom.current().nextInt(RESPONSES.size()));
        long promptTokens = tokenEstimator.estimate(request.prompt());
        long completionTokens = tokenEstimator.estimate(text);
        return new GenerationResponse(
                text,
                promptTokens,
                completionTokens,
                promptTokens + completionTokens,
                MODEL_NAME,
                System.currentTimeMillis() - start,
                Instant.now()
        );
    }
}
