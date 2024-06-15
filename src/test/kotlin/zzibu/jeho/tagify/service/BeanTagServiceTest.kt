package zzibu.jeho.tagify.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.micrometer.common.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.mock.web.MockMultipartFile
import java.io.File
import java.nio.file.Files

@SpringBootTest
@ComponentScan(basePackages = ["zzibu.jeho.tagify"])
class BeanTagServiceTest : BehaviorSpec() {
    @Autowired
    lateinit var tagService: TagService

    init {
        extensions(SpringExtension)

        val imagePath = "src/test/resources/test.jpg" // 테스트용 이미지 파일 경로
        val imageFile = File(imagePath)
        val imageBytes = Files.readAllBytes(imageFile.toPath())

        val multipartFile = MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            imageBytes
        )

        Given("TagService가 주어졌을 때") {
            When("generateTagByImage가 호출되면") {
                Then("태그를 생성하고 TagInfo를 저장해야 한다") {

                    val name = "testName"
                    val url = "http://example.com/image.jpg"
                    val owner = "testOwner"
                    val tags = listOf("tag1", "tag2", "tag3")

                    val tagInfo = tagService.generateTagByImage(multipartFile, name, url, owner)

                    tagInfo.name shouldBe name
                    tagInfo.url shouldBe url
                    tagInfo.owner shouldBe owner
                    tagInfo.tags.size shouldBe 5
                }
            }

            When("sendImageToVLM이 호출되면") {
                Then("chatModel을 호출하고 응답을 반환해야 한다") {

                    val response = tagService.sendImageToVLM(multipartFile)

                    StringUtils.isNotEmpty(response) shouldBe  true
                }
            }
        }
    }
}