package zzibu.jeho.tagify.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import zzibu.jeho.tagify.service.ImageService
import zzibu.jeho.tagify.service.PdfService

@RestController
@RequestMapping("/api/v1/tag")
class TagController(private val pdfService: PdfService,
                    private val imageService: ImageService) {

    @PostMapping("/image")
    fun generateTagsFromImage(
        @RequestParam("image") image: MultipartFile,
    ): ResponseEntity<List<String>> {
        return ResponseEntity(
            imageService.generateTagByImage(image),
            HttpStatus.OK
        )
    }

    @PostMapping("/pdf")
    fun generateTagsFromPDF(
        @RequestParam("pdf") pdf: MultipartFile,
        ): ResponseEntity<List<String>> {
        return ResponseEntity(
            pdfService.generateTagByPDF(pdf),
            HttpStatus.OK
        )
    }

//    @PostMapping("/pdf/text")
//    fun generateTagsFromPDF2(
//        @RequestParam("pdf") pdf: MultipartFile,
//    ): ResponseEntity<List<String>> {
//        return ResponseEntity(
//            pdfService.generateTagFromPDFToText(pdf),
//            HttpStatus.OK
//        )
//    }
}