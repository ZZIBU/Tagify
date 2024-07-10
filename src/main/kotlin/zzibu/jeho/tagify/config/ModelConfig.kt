package zzibu.jeho.tagify.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:model.properties")
@EnableConfigurationProperties(ModelProperties::class)
class ModelConfig(val modelProperties: ModelProperties) {

    @Bean
    fun assistantImageMessage() : String{
        return modelProperties.imageMessage
    }

    @Bean
    fun assistantPdfMessage() : String{
        return modelProperties.pdfMessage
    }
}