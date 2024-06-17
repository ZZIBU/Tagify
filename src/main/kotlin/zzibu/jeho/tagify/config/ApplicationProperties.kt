package zzibu.jeho.tagify.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix="app.config")
data class ApplicationProperties (val maxFileSize:Long)
{}