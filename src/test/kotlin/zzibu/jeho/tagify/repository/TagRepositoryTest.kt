package zzibu.jeho.tagify.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import zzibu.jeho.tagify.domain.TagInfo

private val logger = KotlinLogging.logger{}

class TagRepositoryTest : BehaviorSpec({

    val tagRepository : TagRepository = FakeTagRepository();

    Given("태그 저장 테스트") { // id, uploadDate는 ES에서 자동 생성
        val tagInfo = TagInfo(
            name = "test",
            url = "http://example.com/test",
            owner = "owner",
            tags = listOf()
        )
        When("태그 정보를 입력하면") {
            val savedTagInfo = tagRepository.save(tagInfo)
            logger.info { "TagRepository-save() : $savedTagInfo" }

            Then("저장된 태그 정보를 반환받는다."){
                savedTagInfo.id shouldNotBe null
                // id, uploadDate의 경우 ES에 의해 생성되므로 이를 제외하고 검증
                savedTagInfo.copy(id = null, uploadDate = null) shouldBe tagInfo
            }
        }
    }
    Given("태그 조회 테스트") {
        val tagInfo = TagInfo(
            name = "test",
            url = "http://example.com/test",
            owner = "owner",
            tags = listOf()
        )

        When("저장된 태그를 ID로 조회하면") {
            val savedTagInfo = tagRepository.save(tagInfo)
            val foundTagInfo = tagRepository.findById(savedTagInfo.id?: "1").get()
            logger.info { "TagRepository-findById() : $foundTagInfo" }

            Then("저장된 태그 정보를 반환받는다.") {
                foundTagInfo shouldBe TagInfo(
                    id = savedTagInfo.id,
                    name = savedTagInfo.name,
                    url = savedTagInfo.url,
                    owner = savedTagInfo.owner,
                    tags = listOf(),
                    uploadDate = foundTagInfo.uploadDate // uploadDate의 경우 auto generated 되므로 임의로 값을 일치시킴.
                )
            }
        }

        When("존재하지 않는 ID로 태그를 조회하면") {
            val nonExistentId = "non-existent-id"

            Then("빈 Optional을 반환한다.") {
                val result = tagRepository.findById(nonExistentId)
                result.isPresent shouldBe false
            }
        }
    }
})