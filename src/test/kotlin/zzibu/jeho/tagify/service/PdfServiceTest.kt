package zzibu.jeho.tagify.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartFile
import zzibu.jeho.tagify.exception.InvalidFileTypeException
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files

class PdfServiceTest : BehaviorSpec({
    val chatModel = StubChatModel()
    val assistantMessage = """
                Look at the image and list the words that come to mind in an array 
                    format : [1, 2, 3, 4, 5]
            """.trimIndent()
    val maxFileSize: Long = 10 * 1024 * 1024
    val pdfService = PdfService(chatModel, assistantMessage, maxFileSize)

    Given("PdfService가 주어졌을 때") {
        When("generateTagByPDF가 호출되면") {
            Then("PDF를 이미지로 변환하고 태그를 생성한다") {
                val pdfPath = "src/test/resources/test.pdf" // 테스트용 PDF 파일 경로
                val pdfFile = File(pdfPath)
                val pdfBytes = Files.readAllBytes(pdfFile.toPath())

                val multipartFile = MockMultipartFile(
                    "file",
                    "test.pdf",
                    "application/pdf",
                    pdfBytes
                )

                val tags = listOf("tag1", "tag2", "tag3")

                val tagInfo = pdfService.generateTagByPDF(multipartFile)

                tagInfo shouldBe tags
            }
        }

        When("sendImageToVLM이 호출되면") {
            Then("chatModel을 호출하고 응답을 반환해야 한다") {
                val bufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
                val response = pdfService.sendImageToVLM(bufferedImage)
                response shouldBe "{\"1\":\"tag1\",\"2\":\"tag2\",\"3\":\"tag3\"}"
            }
        }

        When("잘못된 파일 크기가 주어졌을 때") {
            Then("MaxUploadSizeExceededException 예외를 던져야 한다") {
                val multipartFile = object : MultipartFile {
                    override fun getName() = "file"
                    override fun getOriginalFilename() = "large_test.pdf"
                    override fun getContentType() = "application/pdf"
                    override fun isEmpty() = false
                    override fun getSize() = maxFileSize + 1 // 파일 크기를 최대 크기보다 크게 설정
                    override fun getBytes() = ByteArray((maxFileSize + 1).toInt())
                    override fun getInputStream() = ByteArrayInputStream(getBytes())
                    override fun transferTo(dest: java.io.File) {
                        dest.writeBytes(getBytes())
                    }
                }

                shouldThrow<MaxUploadSizeExceededException> {
                    pdfService.generateTagByPDF(multipartFile)
                }
            }
        }

        When("잘못된 파일 포맷이 주어졌을 때") {
            Then("InvalidFileTypeException 예외를 던져야 한다") {
                val multipartFile = object : MultipartFile {
                    override fun getName() = "file"
                    override fun getOriginalFilename() = "test.txt"
                    override fun getContentType() = "text/plain" // 잘못된 파일 포맷
                    override fun isEmpty() = false
                    override fun getSize() = 10L
                    override fun getBytes() = "test".toByteArray()
                    override fun getInputStream() = ByteArrayInputStream(getBytes())
                    override fun transferTo(dest: java.io.File) {
                        dest.writeBytes(getBytes())
                    }
                }

                shouldThrow<InvalidFileTypeException> {
                    pdfService.generateTagByPDF(multipartFile)
                }
            }
        }
    }
})