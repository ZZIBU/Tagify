package zzibu.jeho.tagify.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.messages.Media
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
import java.awt.image.BufferedImage


private val logger = KotlinLogging.logger{}

@Service
class PdfService(
    private val chatModel: ChatModel,
    private val assistantPdfMessage: String,
    private val maxFileSize: Long
    ) {

    fun generateTagByPDF(file: MultipartFile): List<String> {
        validateFile(file)
        val images = ConversionUtils.convertFileToImages(file)
        val tags = images.flatMap { image ->
            val vlmResponse = sendImageToVLM(image)
            ConversionUtils.jsonToList(vlmResponse)
        }
        return tags
    }

    fun generateTagFromPDFToText(file: MultipartFile): List<String> {
        validateFile(file)
        val ocrTexts = ConversionUtils.convertFileToText(file)

        val vlmResponse = sendTextToVLM(ocrTexts)
        val tags = ConversionUtils.jsonToList(vlmResponse)

        return tags
    }
    fun sendImageToVLM(image: BufferedImage): String {
        val imageData = ConversionUtils.convertToInputStreamResource(image)
        val userMessage = UserMessage(
            assistantPdfMessage,
            listOf<Media>(Media(MimeTypeUtils.ALL, imageData))
        )

        val response: ChatResponse = chatModel.call(
            Prompt(listOf(userMessage), OllamaOptions.create().withModel("llava"))
        )
        return response.result.output.content.trimIndent()
    }

    fun sendTextToVLM(text: String): String {
        val userMessage = UserMessage(assistantPdfMessage)

        val response: ChatResponse = chatModel.call(
            Prompt(listOf(userMessage), OllamaOptions.create().withModel("llava"))
        )
        return response.result.output.content.trimIndent()
    }

    private fun validateFile(file: MultipartFile) {
        if (!isValidFile(file)) throw InvalidFileTypeException("파일 타입을 확인해주세요")
        if (file.size > maxFileSize) throw MaxUploadSizeExceededException(file.size)
    }

    private fun isValidFile(file: MultipartFile): Boolean {
        val contentType = file.contentType
        return contentType != null && (
                contentType == MediaType.IMAGE_JPEG_VALUE ||
                        contentType == MediaType.IMAGE_PNG_VALUE ||
                        contentType == MediaType.IMAGE_GIF_VALUE ||
                        contentType == "application/pdf"
                )
    }
}
