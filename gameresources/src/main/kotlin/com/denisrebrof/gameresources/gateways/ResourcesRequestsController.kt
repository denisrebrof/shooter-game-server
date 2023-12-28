package com.denisrebrof.gameresources.gateways

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@RestController
class ResourcesRequestsController @Autowired constructor(
    @Value("classpath:baseresources.data")
    private val baseResourcesPack: Resource
) {
    @RequestMapping(path = ["/basegameres"], method = [RequestMethod.GET])
    @Throws(IOException::class)
    fun getBaseRes(param: String?): ResponseEntity<Resource> {
        val header = HttpHeaders()
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=baseresources.data")
        header.add("Cache-Control", "no-cache, no-store, must-revalidate")
        header.add("Pragma", "no-cache")
        header.add("Expires", "0")
        header.add("Access-Control-Allow-Origin", "*")

        return ResponseEntity.ok()
            .headers(header)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(baseResourcesPack)
    }
}