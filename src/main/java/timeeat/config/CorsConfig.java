package timeeat.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    public CorsConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
        validate(corsProperties.getOrigin());
    }

    private void validate(List<String> corsOriginList) {
        if (corsOriginList == null || corsOriginList.isEmpty()) {
            throw new RuntimeException("Initialization Error: CORS origin cannot be empty.");
        }
        for (String origin : corsOriginList) {
            if (origin == null || origin.isBlank()) {
                throw new RuntimeException("Initialization Error: CORS origin string cannot be blank.");
            }
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = corsProperties.getOrigin().toArray(new String[0]);

        registry.addMapping("/**")
                .allowedOriginPatterns(origins)
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.DELETE.name()
                )
                .allowCredentials(true)
                .allowedHeaders("*");
    }
}
