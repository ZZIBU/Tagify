package zzibu.jeho.tagify.service

import org.springframework.ai.chat.messages.Media
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartFile
import zzibu.jeho.tagify.exception.InvalidFileTypeException
import zzibu.jeho.tagify.util.ConversionUtils
import java.util.List

@Service
class ImageService(
    private val chatModel: ChatModel,
    private val assistantImageMessage: String,
    private val maxFileSize: Long
    ) {

    fun generateTagByImage(image: MultipartFile): kotlin.collections.List<String> {
        validateImage(image)
        val vlmResponse = sendImageToVLM(image)
        val tags = ConversionUtils.jsonToList(vlmResponse)

        return tags
    }

    fun sendImageToVLM(image : MultipartFile): String {
        val imageData = ConversionUtils.convertToInputStreamResource(image)

        val userMessage = UserMessage(
            assistantImageMessage,
            List.of<Media>(Media(MimeTypeUtils.ALL, imageData))
        )

        val response: ChatResponse = chatModel.call(
            Prompt(List.of<Message>(userMessage), OllamaOptions.create().withModel("llava"))
        )
        return response.result.output.content.trimIndent()
    }
    private fun validateImage(image : MultipartFile) : Unit {
        if(!isImage(image)) throw InvalidFileTypeException("파일 타입을 확인해주세요")
        if(image.size > maxFileSize) throw MaxUploadSizeExceededException(image.size)
    }
    private fun isImage(file : MultipartFile) : Boolean {
        val contentType = file.contentType
        return contentType != null && (contentType == MediaType.IMAGE_JPEG_VALUE ||
                        contentType == MediaType.IMAGE_PNG_VALUE ||
                        contentType == MediaType.IMAGE_GIF_VALUE
                )
    }
}