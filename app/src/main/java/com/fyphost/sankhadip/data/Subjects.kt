package com.fyphost.sankhadip.data

/** The five fixed subject folders seeded on first launch. */
data class SubjectSeed(val name: String, val colorArgb: Long, val icon: String)

val SUBJECTS = listOf(
    SubjectSeed("Physics", 0xFF1E90FF, "\u269B\uFE0F"),
    SubjectSeed("Chemistry", 0xFF00D4A0, "\u2697\uFE0F"),
    SubjectSeed("Maths", 0xFFA26BFF, "\u03C0"),
    SubjectSeed("Biology", 0xFFFF5C8A, "\uD83E\uDDEC"),
    SubjectSeed("English", 0xFFFFB020, "\uD83D\uDCD6"),
)
