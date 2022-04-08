package com.zzy.file.watch

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import kotlin.io.path.Path

@RestController
class FileWatchController {

    @PostMapping("/start")
    fun startFileWatch(@RequestBody path: Map<String, String>): String {
        if (FileWatchService.instance?.isWatching() == true) {
            FileWatchService.instance?.stopWatching()
        }
        FileWatchService.directoryToWatch = Path(path["path"].toString())
        FileWatchService.instance?.watch()
        return "start watch ".plus(path)
    }

    @GetMapping("/stop")
    fun stopFileWatch(): String {
        FileWatchService.instance?.stopWatching()
        return "stop success"
    }

    @GetMapping("/isWatching")
    fun isWatching(): String {
        return FileWatchService.instance?.isWatching().toString().plus(",")
            .plus(FileWatchService.directoryToWatch)
    }

    @GetMapping("/getUpdateFiles")
    fun getUpdateFiles(): String {
        return FileWatchService.instance?.getUpdateFiles() ?: ""
    }

}