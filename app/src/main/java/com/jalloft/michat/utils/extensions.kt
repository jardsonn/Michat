package com.jalloft.michat.utils

import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.ModelType


fun String.splitMessage(): List<String> {
    val registry = Encodings.newDefaultEncodingRegistry()
    val secondEnc = registry.getEncodingForModel(ModelType.TEXT_DAVINCI_003)
    val tokens = secondEnc.countTokens(this)
    if (tokens > 400){
        val paragraphs = this.split(Regex("\\.(?=\\s+[^.]*\\.\\s+[^\n]*$)"), 2).toMutableList()
        if (paragraphs.size > 1) {
            paragraphs[1] = paragraphs[0].substringAfterLast(".") + paragraphs[1]
            paragraphs[0] = paragraphs[0].substringBeforeLast(".") + "."
        }

        return paragraphs
    }
    return arrayListOf(this)
}