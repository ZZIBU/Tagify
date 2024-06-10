package zzibu.jeho.tagify.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.RefreshPolicy
import zzibu.jeho.tagify.entity.TagInfo
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FakeTagRepository : TagRepository{
    private val storage = ConcurrentHashMap<String, TagInfo>()
    private var idCounter = 0L;

    // ElasticsearchRepository가 java로 구현되어 TagInfo?가 아닌 Optional을 사용
    override fun findById(id: String): Optional<TagInfo> {
        return Optional.ofNullable(storage[id])
    }

    override fun <S : TagInfo?> save(entity: S & Any): S & Any {
        val id = entity.id ?: (++idCounter).toString()
        val tag = entity.copy(id = id, uploadDate = LocalDateTime.now())
        storage[id] = tag
        return tag as (S & Any)
    }

    // ---------------- Not yet implemented ----------------

    override fun findAll(sort: Sort): MutableIterable<TagInfo> {
        TODO("Not yet implemented")
    }

    override fun findAll(pageable: Pageable): Page<TagInfo> {
        TODO("Not yet implemented")
    }

    override fun findAll(): MutableIterable<TagInfo> {
        TODO("Not yet implemented")
    }

    override fun <S : TagInfo?> save(entity: S & Any, refreshPolicy: RefreshPolicy?): S & Any {
        TODO("Not yet implemented")
    }

    override fun <S : TagInfo?> saveAll(
        entities: MutableIterable<S>,
        refreshPolicy: RefreshPolicy?
    ): MutableIterable<S> {
        TODO("Not yet implemented")
    }

    override fun <S : TagInfo?> saveAll(entities: MutableIterable<S>): MutableIterable<S> {
        TODO("Not yet implemented")
    }

    override fun existsById(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun findAllById(ids: MutableIterable<String>): MutableIterable<TagInfo> {
        TODO("Not yet implemented")
    }

    override fun count(): Long {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: String, refreshPolicy: RefreshPolicy?) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: String) {
        TODO("Not yet implemented")
    }

    override fun delete(entity: TagInfo, refreshPolicy: RefreshPolicy?) {
        TODO("Not yet implemented")
    }

    override fun delete(entity: TagInfo) {
        TODO("Not yet implemented")
    }

    override fun deleteAllById(ids: MutableIterable<String>, refreshPolicy: RefreshPolicy?) {
        TODO("Not yet implemented")
    }

    override fun deleteAllById(ids: MutableIterable<String>) {
        TODO("Not yet implemented")
    }

    override fun deleteAll(entities: MutableIterable<TagInfo>, refreshPolicy: RefreshPolicy?) {
        TODO("Not yet implemented")
    }

    override fun deleteAll(refreshPolicy: RefreshPolicy?) {
        TODO("Not yet implemented")
    }

    override fun deleteAll(entities: MutableIterable<TagInfo>) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun searchSimilar(entity: TagInfo, fields: Array<out String>?, pageable: Pageable): Page<TagInfo> {
        TODO("Not yet implemented")
    }
}