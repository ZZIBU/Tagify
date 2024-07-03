package zzibu.jeho.tagify.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO

object ConversionUtils {

    fun jsonToList(jsonString: String): List<String> {
        val objectMapper = jacksonObjectMapper()
        val map: Map<String, String> = objectMapper.readValue(jsonString)
        return map.values.toList()
    }

    @Throws(IOException::class)
    fun convertToInputStreamResource(image: BufferedImage): Resource {
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", baos)
        val inputStream = ByteArrayInputStream(baos.toByteArray())
        return object : InputStreamResource(inputStream) {
            override fun getFilename(): String? {
                return "image.jpg"
            }
        }
    }

    @Throws(IOException::class)
    fun convertToInputStreamResource(file: MultipartFile): Resource {
        return object : InputStreamResource(file.inputStream) {
            override fun getFilename(): String? {
                return file.originalFilename
            }
        }
    }

    fun convertFileToImages(file: MultipartFile): List<BufferedImage> {
        return if (file.contentType == "application/pdf") {
            convertPdfToImages(file)
        } else {
            listOf(convertToBufferedImage(file))
        }
    }

    private fun convertToBufferedImage(file: MultipartFile): BufferedImage {
        return ImageIO.read(file.inputStream)
    }

    private fun convertPdfToImages(file: MultipartFile): List<BufferedImage> {
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