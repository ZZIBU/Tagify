package zzibu.jeho.tagify.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime

@Document(indexName = "tags")
data class TagInfo(
    @Id
    val id: String? = null,
    val name: String,
    val url: String,
    val owner: String,
    @CreatedDate
    @Field(type = FieldType.Date, format = [DateFormat.date_hour_minute_second])
    val uploadDate: LocalDateTime? = null,
)
