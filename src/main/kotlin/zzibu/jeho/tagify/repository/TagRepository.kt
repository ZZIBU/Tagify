package zzibu.jeho.tagify.repository

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import zzibu.jeho.tagify.domain.TagInfo
import java.util.*

interface TagRepository : ElasticsearchRepository<TagInfo,String> {
    override fun findById(id: String): Optional<TagInfo>
    override fun <S : TagInfo?> save(entity: S & Any): S & Any
}