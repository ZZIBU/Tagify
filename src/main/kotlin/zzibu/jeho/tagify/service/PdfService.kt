package zzibu.jeho.tagify.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.springframework.ai.chat.messages.Media
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartFile
import zzibu.jeho.tagify.exception.InvalidFileTypeException
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO


private val logger = KotlinLogging.logger{}

@Service
class PdfService(
    private val chatModel: ChatModel,
    private val assistantMessage: String,
    private val maxFileSize: Long
    ) {

    fun generateTagByPDF(file: MultipartFile): List<String> {
        logger.error { file }
        validateFile(file)
        val images = convertFileToImages(file)
        val tags = images.flatMap { image ->
            val vlmResponse = sendImageToVLM(image)
            jsonToList(vlmResponse)
        }
        return tags
    }
    fun sendImageToVLM(image: BufferedImage): String {
        val imageData = convertToInputStreamResource(image)
        val userMessage = UserMessage(
            assistantMessage,
            listOf<Media>(Media(MimeTypeUtils.ALL, imageData))
        )

        val response: ChatResponse = chatModel.call(
            Prompt(listOf(userMessage), OllamaOptions.create().withModel("llava"))
        )
        return response.result.output.content.trimIndent()
    }

    private fun jsonToList(jsonString: String): kotlin.collections.List<String> {
        val objectMapper = jacksonObjectMapper()
        val map: Map<String, String> = objectMapper.readValue(jsonString)
        return map.values.toList()
    }

    @Throws(IOException::class)
    private fun convertToInputStreamResource(image: BufferedImage): Resource {
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", baos)
        val inputStream = ByteArrayInputStream(baos.toByteArray())
        return object : InputStreamResource(inputStream) {
            override fun getFilename(): String {
                return "image.jpg"
            }
        }
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

    fun convertFileToImages(file: MultipartFile): MutableList<BufferedImage> {
        return if (file.contentType == "application/pdf") {
            convertPdfToImages(file)
        } else {
            mutableListOf(convertToBufferedImage(file))
        }
    }

    private fun convertToBufferedImage(file: MultipartFile): BufferedImage {
        return ImageIO.read(file.inputStream)
    }

    private fun convertPdfToImages(file: MultipartFile): MutableList<BufferedImage> {
        val document = PDDocument.load(file.inputStream)
        val pdfRenderer = PDFRenderer(document)
        val images = mutableListOf<BufferedImage>()

        for (pageIndex in 0 until document.numberOfPages) {
            val bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex, 300f) // Render at 300 DPI
            images.add(bufferedImage)
        }
        document.close()
        return images
    }
}
