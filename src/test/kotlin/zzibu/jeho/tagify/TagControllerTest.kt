package zzibu.jeho.tagify

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import zzibu.jeho.tagify.controller.TagController
import zzibu.jeho.tagify.exception.ErrorCode
import zzibu.jeho.tagify.exception.ErrorResponse
import zzibu.jeho.tagify.exception.GlobalExceptionHandler
import zzibu.jeho.tagify.service.TagService
import java.nio.file.Files
import java.nio.file.Paths


@SpringBootTest
@AutoConfigureMockMvc
class TagControllerTest : BehaviorSpec() {
    @Autowired
    protected lateinit var mockMvc : MockMvc

    @Autowired
    lateinit var tagService: TagService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    init {
        beforeSpec {
            mockMvc = MockMvcBuilders
                .standaloneSetup(TagController(tagService))
                .setControllerAdvice(GlobalExceptionHandler())
                .build()
            objectMapper = ObjectMapper()
        }

        given("이미지에 대한 태그 생성이 필요할 때") {
            val path = Paths.get("src/test/resources/test.jpg")
            val imageContent = Files.readAllBytes(path)
            val image = MockMultipartFile("image", "test.jpg", "image/jpeg", imageContent)

            `when`("클라이언트가 이미지를 업로드하면") {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.multipart("/api/tags/generate/image")
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()

                then("이미지 식별을 통해 생성된 태그 목록을 반환한다.") {
                    val response = objectMapper.readValue(result.response.contentAsString, List::class.java)

                    response.size shouldBe 5
                }
            }
            `when`("클라이언트가 지원되지 않는 이미지 포맷을 업로드하면") {
                val unsupportedImageContent = Files.readAllBytes(Paths.get("src/test/resources/unsupported-image.txt"))
                val unsupportedImage = MockMultipartFile("image", "unsupported-image.txt", "text/plain", unsupportedImageContent)

                val result = mockMvc.perform(
                    MockMvcRequestBuilders.multipart("/api/tags/generate/image")
                        .file(unsupportedImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest)
                    .andReturn()

                then("응답은 에러 메시지를 포함해야 한다") {
                    val response = objectMapper.readValue(result.response.contentAsString, ErrorResponse::class.java)
                    response.code shouldBe ErrorCode.INVALID_FILE_TYPE
                }
            }
        }
    }
    override fun extensions() = listOf(SpringExtension)
}