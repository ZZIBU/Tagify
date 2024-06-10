package zzibu.jeho.tagify

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing

@SpringBootApplication
@EnableElasticsearchAuditing
class TagifyApplication

fun main(args: Array<String>) {
	runApplication<TagifyApplication>(*args)
}
