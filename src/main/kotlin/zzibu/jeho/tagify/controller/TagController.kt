package zzibu.jeho.tagify.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import zzibu.jeho.tagify.service.TagService

@RestController
@RequestMapping("/api/v1/tag")
class TagController(private val tagService: TagService) {

    @PostMapping("/image")
    fun generateTags(
        @RequestParam("image") image: MultipartFile,

    ): ResponseEntity<List<String>> {
        return ResponseEntity(
            tagService.generateTagByImage(image),
            HttpStatus.OK
        )
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