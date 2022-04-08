package com.zzy.instant.run.plugin

import org.apache.commons.io.FileUtils
import java.io.*
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import javax.tools.JavaFileObject
import javax.tools.ToolProvider


/**
 * 给String扩展 execute() 函数
 */
fun String.execute(): Process {
    val runtime = Runtime.getRuntime()
    return runtime.exec(this)
}

/**
 * 扩展Process扩展 text() 函数
 */
fun Process.text(): String {
    // 输出 Shell 执行结果
    val inputStream = this.inputStream
    val insReader = InputStreamReader(inputStream)
    val bufReader = BufferedReader(insReader)
    var output = ""
    var line: String? = ""
    while (null != line) {
        // 逐行读取shell输出，并保存到变量output
        line = bufReader.readLine()
        output += """$line"""
    }
    return output
}

fun String.print(tag: String = "instant-run") {
    println("$tag -> $this")
}

fun Boolean.isTrue(run: () -> Unit): Boolean {
    if (this)
        run()
    return this
}

fun Boolean.isFalse(run: () -> Unit): Boolean {
    if (!this)
        run()
    return this
}

fun String.fileMkdir(): File {
    val file = File(this)
    if (!file.exists()) {
        file.mkdir()
    }
    return file
}

fun File.endWith(with: String): Boolean {
    return this.name.endsWith(with)
}

fun File.compileClass() {
    if (this.absolutePath.endsWith("DS_Store")) return
    println("compileClass from -> ${this.absolutePath}")
    val systemJavaCompiler = ToolProvider.getSystemJavaCompiler()
    val manager = systemJavaCompiler.getStandardFileManager(null, null, null)
    val iterable: Iterable<*> = manager.getJavaFileObjects(this)
    val task = systemJavaCompiler.getTask(
        null, manager, null, null, null,
        iterable as MutableIterable<JavaFileObject>?
    )
    task.call()
    manager.close()
}

fun File.zipJar(destPath: File) {
    val jarOutputStream = JarOutputStream(FileOutputStream(destPath))
    this.listFiles()?.filter {
        it.endWith(".class")
    }?.forEach { file ->
        val entryName = file.absolutePath.substring(this.absolutePath.length + 1)
        entryName.print()
        jarOutputStream.putNextEntry(ZipEntry(entryName))
        if (!file.isDirectory) {
            FileUtils.copyFile(file, jarOutputStream)
        }
        true
    }
}