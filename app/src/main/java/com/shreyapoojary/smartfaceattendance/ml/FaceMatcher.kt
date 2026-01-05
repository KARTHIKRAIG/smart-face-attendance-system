package com.shreyapoojary.smartfaceattendance.ml

import kotlin.math.sqrt

object FaceMatcher {

    // Cosine similarity
    fun isMatch(
        embedding1: FloatArray,
        embedding2: FloatArray,
        threshold: Float = 0.75f
    ): Boolean {
        val similarity = cosineSimilarity(embedding1, embedding2)
        return similarity >= threshold
    }

    private fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        var dot = 0f
        var normA = 0f
        var normB = 0f

        for (i in a.indices) {
            dot += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }

        return dot / (sqrt(normA) * sqrt(normB))
    }
}
