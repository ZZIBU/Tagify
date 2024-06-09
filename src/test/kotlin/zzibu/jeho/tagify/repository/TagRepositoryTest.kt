package zzibu.jeho.tagify.repository

import io.kotest.core.spec.style.BehaviorSpec
import java.time.LocalDateTime


class TagRepositoryTest : BehaviorSpec({
    Given("태그 저장 테스트") { // id, uploadDate는 ES에서 자동 생성
        val tagInfo = TagInfo(
            name = "test",
            url = "http://example.com/test",
            owner = "owner",
            updateDate = null,
        )
        When("태그 정보를 입력하면") {
            val savedTagInfo = tagRepository.save(tagInfo)
            Then("저장된 태그 정보를 반환받는다."){
                savedTagInfo.id shouldNotBe null
                savedTagInfo.copy(id = null, updateDate = tagInfo.updateDate) shouldBe tagInfo
            }
        }
    }
})