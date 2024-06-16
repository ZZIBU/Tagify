package zzibu.jeho.tagify.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.messages.Media
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import org.springframework.web.multipart.MultipartFile
import zzibu.jeho.tagify.domain.TagInfo
import zzibu.jeho.tagify.repository.TagRepository
import java.io.IOException
import java.util.List

private val logger = KotlinLogging.logger{}

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val chatModel: ChatModel,
    private val assistantMessage: String,
    ) {
    fun generateTagByImage(image:MultipartFile,  name : String,url : String, owner : String): TagInfo {
        val vlmResponse = sendImageToVLM(image)
        val tags = jsonToList(vlmResponse)

        logger.info { tags }

        val tagInfo = TagInfo(
            id = null,
            url = url,
            name = name,
            owner = owner,
            tags = tags,
        )
        return tagRepository.save(tagInfo)
    }

    @Throws(IOException::class)
    fun sendImageToVLM(image : MultipartFile): String {
        val imageData = convertToInputStreamResource(image)

        val userMessage = UserMessage(
            assistantMessage,
            List.of<Media>(Media(MimeTypeUtils.ALL, imageData))
        )

        val response: ChatResponse = chatModel.call(
            Prompt(List.of<Message>(userMessage), OllamaOptions.create().withModel("llava"))
        )
        return response.result.output.content.trimIndent()
    }

    // 모델 반환 값을 json으로 설정하였으므로, 이를 리스트 형태로 변경
    private fun jsonToList(jsonString : String) : kotlin.collections.List<String> {
        val objectMapper = jacksonObjectMapper()
        val map: Map<String, String> = objectMapper.readValue(jsonString)
        val tagList = mutableListOf<String>()
        map.entries.forEach {
            tagList.add(it.value)
        }
        return tagList.toList()
    }

    // Spring AI 프레임워크의 Media 생성자 타입 Resource로 맞추기 위함.
    @Throws(IOException::class)
    private fun convertToInputStreamResource(file: MultipartFile): Resource {
        return object : InputStreamResource(file.inputStream) {
            override fun getFilename(): String? {
                return file.originalFilename
            }
        }
    }

    // not implemented yet
//    fun generateTagByUrl( name : String, url : String, owner : String): TagInfo {
//        val vlmResponse = sendImageToVLM(url)
//        val tags = jsonToList(vlmResponse)
//        val tagInfo = TagInfo(
//            id = null,
//            url = url,
//            name = name,
//            owner = owner,
//            tags = tags,
//        )
//        return tagRepository.save(tagInfo)
//    }
//    @Throws(IOException::class)
//    fun sendImageToVLM(url : String): String {
//        val url = URL(url)
//        val userMessage = UserMessage(
//            assistantMessage,
//            List.of<Media>(Media(MimeTypeUtils.ALL, url))
//        )
//        logger.info { assistantMessage }
//        val response: ChatResponse = chatModel.call(
//            Prompt(List.of<Message>(userMessage), OllamaOptions.create().withModel("llava"))
//        )
//        return response.result.output.content
//    }
}
