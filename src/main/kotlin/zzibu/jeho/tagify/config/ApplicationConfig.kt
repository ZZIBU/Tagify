package zzibu.jeho.tagify.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:application.yml")
@EnableConfigurationProperties(ApplicationProperties::class)
class ApplicationConfig(val applicationProperties: ApplicationProperties) {

    @Bean
    fun maxFileSize() : Long{
        return applicationProperties.maxFileSize
    }
}