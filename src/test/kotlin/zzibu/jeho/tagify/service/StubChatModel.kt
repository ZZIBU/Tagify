package zzibu.jeho.tagify.service

import org.springframework.ai.chat.metadata.ChatResponseMetadata
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.Generation
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt

class StubChatModel : ChatModel {
    override fun call(prompt: Prompt): ChatResponse {
        // 간단한 스텁 응답을 반환합니다.
        val responseMessage = Generation("{\"1\":\"tag1\",\"2\":\"tag2\",\"3\":\"tag3\"}")
        return ChatResponse(listOf(responseMessage))
    }

    override fun getDefaultOptions(): ChatOptions {
        TODO("Not yet implemented")
    }
}