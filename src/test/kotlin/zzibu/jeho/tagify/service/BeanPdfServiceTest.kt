package zzibu.jeho.tagify.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.micrometer.common.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MaxUploadSizeExceededException
import zzibu.jeho.tagify.exception.InvalidFileTypeException
import zzibu.jeho.tagify.util.ConversionUtils
import java.io.File
import java.nio.file.Files

@SpringBootTest
class BeanPdfServiceTest : BehaviorSpec() {

    @Autowired
    lateinit var pdfService: PdfService

    @Autowired
    var maxFileSize: Long = 0L

    init {
        val pdfPath = "src/test/resources/test.pdf" // 테스트용 PDF 파일 경로
        val pdfFile = File(pdfPath)
        val pdfBytes = Files.readAllBytes(pdfFile.toPath())

        val multipartFile = MockMultipartFile(
            "pdf",
            "test.pdf",
            "application/pdf",
            pdfBytes
        )

        Given("PdfService가 주어졌을 때") {
            When("generateTagByPDF가 호출되면") {
                Then("태그를 생성하고 TagInfo를 저장해야 한다") {
                    val tagInfo = pdfService.generateTagByPDF(multipartFile)

                    tagInfo.size shouldNotBe 0
                }
            }

            When("sendImageToVLM이 호출되면") {
                Then("chatModel을 호출하고 응답을 반환해야 한다") {
                    val images = ConversionUtils.convertFileToImages(multipartFile)
                    val response = pdfService.sendImageToVLM(images[0])

                    StringUtils.isNotEmpty(response) shouldBe true
                }
            }
            When("잘못된 파일 크기가 주어졌을 때") {
                Then("MaxUploadSizeExceededException 예외를 던져야 한다") {
                    val largePdfFile = MockMultipartFile(
                        "file",
                        "large_test.pdf",
                        "application/pdf",
                        ByteArray(maxFileSize.toInt() + 1)
                    )

                    shouldThrow<MaxUploadSizeExceededException> {
                        pdfService.generateTagByPDF(largePdfFile)
                    }
                }
            }

            When("잘못된 파일 포맷이 주어졌을 때") {
                Then("InvalidFileTypeException 예외를 던져야 한다") {
                    val textFile = MockMultipartFile(
                        "file",
                        "test.txt",
                        "text/plain",
                        "test".toByteArray()
                    )

                    shouldThrow<InvalidFileTypeException> {
                        pdfService.generateTagByPDF(textFile)
                    }
                }
            }
        }
    }

    override fun extensions() = listOf(SpringExtension)
}