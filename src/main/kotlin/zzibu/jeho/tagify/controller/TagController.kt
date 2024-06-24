package zzibu.jeho.tagify.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import zzibu.jeho.tagify.service.TagService

@RestController
@RequestMapping("/api/tags")
class TagController(@Autowired private val tagService: TagService) {

    @PostMapping("/generate/image")
    fun generateTags(
        @RequestParam("image") image: MultipartFile,

    ): List<String> {
        return tagService.generateTagByImage(image)
    }

//    @PostMapping("/generate/url")
//    fun tagsByUrl(
//        @RequestParam("name") name : String,
//        @RequestParam("url") url : String,
//        @RequestParam("owner") owner : String,
//    ): TagInfo {
//        return tagService.generateTagByUrl(name,url,owner)
//    }
}