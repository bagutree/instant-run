package com.zzy.instant.run.plugin

import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.JavaVersion
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option
import java.io.File
import java.nio.charset.Charset

abstract class InstantRunTask : DefaultTask() {

    init {
        description = "Just a sample template task"
        // Don't forget to set the group here.
        // group = BasePlugin.BUILD_GROUP
    }

    @get:Input
    @get:Option(option = "message", description = "A message to be printed in the output file")
    abstract val message: Property<String>

    @get:Input
    @get:Option(option = "tag", description = "A Tag to be used for debug and in the output file")
    @get:Optional
    abstract val tag: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun instantRunAction() {

        val prettyTag = tag.orNull?.let { "[$it]" } ?: ""

        logger.lifecycle("$prettyTag message is: ${message.orNull}")
        logger.lifecycle("$prettyTag tag is: ${tag.orNull}")
        logger.lifecycle("$prettyTag outputFile is: ${outputFile.orNull}")

        val log = FileUtils.readFileToString(
            File(project.rootDir.absolutePath.plus(File.separator).plus("FileUpdate.log")),
            Charset.forName("UTF-8")
        )

        outputFile.get().asFile.writeText("$prettyTag ${message.get()} $log")

        val fileStrArrayList = log.split("\n")

        val destJavaFileDirectory =
            File(project.rootDir.absolutePath.plus(File.separator).plus("/instant-run/java"))

        val destClassPath =
            File(project.rootDir.absolutePath.plus(File.separator).plus("/instant-run/patch.jar"))

        destClassPath.deleteOnExit()

        fileStrArrayList.filter {
            it.endsWith(".java")
        }.forEach {
//            logger.lifecycle("$prettyTag java update: $it")
            val javaPath = it.split(",")[1]
            try {
                FileUtils.copyFileToDirectory(File(javaPath), destJavaFileDirectory)
            } catch (e: Exception) {
            }
        }

        destJavaFileDirectory.listFiles { file ->
            file.endWith(".class").isTrue {
                file.delete()
            }.isFalse {
                file.absolutePath.print()
                file.compileClass()
            }
            true
        }

        destJavaFileDirectory.zipJar(destClassPath)

        fileStrArrayList.filter {
            it.endsWith(".kt")
        }.forEach {
//            logger.lifecycle("$prettyTag kt update: $it")
            val javaPath = it.split(",")[1]
            val destFile =
                File(project.rootDir.absolutePath.plus(File.separator).plus("/instant-run/kotlin"))
            try {
                FileUtils.copyFileToDirectory(File(javaPath), destFile)
            } catch (e: Exception) {
            }
        }
    }
}
