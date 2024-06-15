package zzibu.jeho.tagify.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.web.multipart.MultipartFile
import zzibu.jeho.tagify.repository.FakeTagRepository
import java.io.ByteArrayInputStream

class TagServiceTest : BehaviorSpec({
    val tagRepository = FakeTagRepository()
    val chatModel = StubChatModel()
    val tagService = TagService(tagRepository, chatModel)

    val assistantMessage = """
                Look at the image and list the words that come to mind in an array 
                    format : [1, 2, 3, 4, 5]
            """.trimIndent()

    Given("TagService가 주어졌을 때") {
        When("generateTagByImage가 호출되면") {
            Then("태그를 생성하고 TagInfo를 저장해야 한다") {
                val multipartFile = object : MultipartFile { // construct가 없으므로 직접 재정의
                    override fun getName() = "file"
                    override fun getOriginalFilename() = "test.jpg"
                    override fun getContentType() = "image/jpeg"
                    override fun isEmpty() = false
                    override fun getSize() = 10L
                    override fun getBytes() = "test".toByteArray()
                    override fun getInputStream() = ByteArrayInputStream(getBytes())
                    override fun transferTo(dest: java.io.File) {
                        dest.writeBytes(getBytes())
                    }
                }
                val name = "testName"
                val url = "http://example.com/image.jpg"
                val owner = "testOwner"
                val vlmResponse = """{"1":"tag1","2":"tag2","3":"tag3"}"""
                val tags = listOf("tag1", "tag2", "tag3")

                val tagInfo = tagService.generateTagByImage(multipartFile, name, url, owner)

                tagInfo.name shouldBe name
                tagInfo.url shouldBe url
                tagInfo.owner shouldBe owner
                tagInfo.tags shouldBe tags
            }
        }

        When("sendImageToVLM이 호출되면") {
            Then("chatModel을 호출하고 응답을 반환해야 한다") {
                val multipartFile = object : MultipartFile {
                    override fun getName() = "file"
                    override fun getOriginalFilename() = "test.jpg"
                    override fun getContentType() = "image/jpeg"
                    override fun isEmpty() = false
                    override fun getSize() = 10L
                    override fun getBytes() = "test".toByteArray()
                    override fun getInputStream() = ByteArrayInputStream(getBytes())
                    override fun transferTo(dest: java.io.File) {
                        dest.writeBytes(getBytes())
                    }
                }
                val response = tagService.sendImageToVLM(multipartFile)
                response shouldBe "{\"1\":\"tag1\",\"2\":\"tag2\",\"3\":\"tag3\"}"
            }
        }
    }

})