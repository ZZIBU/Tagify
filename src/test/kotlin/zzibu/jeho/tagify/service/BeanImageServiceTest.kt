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
import java.io.File
import java.nio.file.Files

@SpringBootTest
class BeanImageServiceTest : BehaviorSpec() {

    @Autowired
    lateinit var imageService: ImageService

    @Autowired
    var maxFileSize : Long = 0L

    init {
        val imagePath = "src/test/resources/test.jpg" // 테스트용 이미지 파일 경로
        val imageFile = File(imagePath)
        val imageBytes = Files.readAllBytes(imageFile.toPath())

        val multipartFile = MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            imageBytes
        )

        Given("ImageService가 주어졌을 때") {
            When("generateTagByImage가 호출되면") {
                Then("태그를 생성하고 TagInfo를 저장해야 한다") {


                    val tagInfo = imageService.generateTagByImage(multipartFile)

                    tagInfo.size shouldNotBe 0
                }
            }

            When("sendImageToVLM이 호출되면") {
                Then("chatModel을 호출하고 응답을 반환해야 한다") {
                    val response = imageService.sendImageToVLM(multipartFile)

                    StringUtils.isNotEmpty(response) shouldBe  true
                }
            }
            When("잘못된 파일 크기가 주어졌을 때") {
                Then("MaxUploadSizeExceededException 예외를 던져야 한다") {
                    val largeImageFile = MockMultipartFile(
                        "file",
                        "large_test.jpg",
                        "image/jpeg",
                        ByteArray(maxFileSize.toInt() + 1)
                    )

                    shouldThrow<MaxUploadSizeExceededException> {
                        imageService.generateTagByImage(largeImageFile)
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
                        imageService.generateTagByImage(textFile)
                    }
                }
            }
        }
    }
    override fun extensions() = listOf(SpringExtension)
}