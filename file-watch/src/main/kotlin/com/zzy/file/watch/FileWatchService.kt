package com.zzy.file.watch

import io.methvin.watcher.DirectoryChangeEvent
import io.methvin.watcher.DirectoryWatcher
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap


class FileWatchService {

    private val fileWatchCache: ConcurrentHashMap<String, String> = ConcurrentHashMap()
    private var isWatching: Boolean = false
    private var watcher: DirectoryWatcher? = null
    private var fileUpdate: File? = null

    @Throws(IOException::class)
    fun stopWatching() {
        watcher?.close()
        isWatching = false
    }

    fun watch(): Boolean {
        // you can also use watcher.watch() to block the current thread
        if (isWatching) return false
        fileUpdate =
            File(
                directoryToWatch.toFile().absolutePath.plus(File.separator)
                    .plus(FILE_UPDATE_LOG_NAME)
            )
        if (fileUpdate?.exists() == false) {
            fileUpdate?.createNewFile()
        }
        watcher = DirectoryWatcher.builder()
            .path(directoryToWatch) // or use paths(directoriesToWatch)
            .listener { event: DirectoryChangeEvent ->
                if (event.endWith(FILE_UPDATE_LOG_NAME)) return@listener
                when (event.eventType()) {
                    DirectoryChangeEvent.EventType.CREATE -> {
                        val value = "CREATE,".plus(event.path().toString().plus(""))
                        fileWatchCache[event.path().toString()] = value
                        FileUtils.writeStringToFile(
                            fileUpdate,
                            value.plus("\n"),
                            Charset.forName("UTF-8"),
                            true
                        )
                        println("onChangeCreate:" + event.path())
                    }
                    DirectoryChangeEvent.EventType.MODIFY -> {
                        val value = "MODIFY,".plus(event.path().toString())
                        fileWatchCache[event.path().toString()] = value
                        FileUtils.writeStringToFile(
                            fileUpdate,
                            value.plus("\n"),
                            Charset.forName("UTF-8"),
                            true
                        )
                        println("onChangeModify:" + event.path())
                    }
                    DirectoryChangeEvent.EventType.DELETE -> {
                    }
                    else -> {
                    }
                }
            } // .fileHashing(false) // defaults to true
            // .logger(logger) // defaults to LoggerFactory.getLogger(DirectoryWatcher.class)
            // .watchService(watchService) // defaults based on OS to either JVM WatchService or the JNA macOS WatchService
            .build()
        watcher?.watchAsync()
        isWatching = true
        return isWatching
    }

    fun isWatching(): Boolean {
        return isWatching
    }

    fun getUpdateFiles(): String {
        val fileStr = fileWatchCache.values.joinToString(separator = "||")
        fileWatchCache.clear()
        return fileStr
    }

    companion object {
        private const val FILE_UPDATE_LOG_NAME = "FileUpdate.log"
        private var sInstance: FileWatchService? = null
        var directoryToWatch: Path = Paths.get(",")

        @get:Throws(IOException::class)
        val instance: FileWatchService?
            get() {
                if (sInstance == null) {
                    synchronized(FileWatchService::class.java) {
                        if (sInstance == null) {
                            sInstance = FileWatchService()
                        }
                    }
                }
                return sInstance
            }
    }
}