package com.zzy.file.watch

import io.methvin.watcher.DirectoryChangeEvent

fun String.print() {
    print(this)
}

fun DirectoryChangeEvent.endWith(suffix: String): Boolean {
    return this.path().toString().endsWith(suffix)
}