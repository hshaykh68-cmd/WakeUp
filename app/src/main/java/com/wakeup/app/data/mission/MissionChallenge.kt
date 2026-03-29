package com.wakeup.app.data.mission

import com.wakeup.app.domain.model.MissionDifficulty
import com.wakeup.app.domain.model.MissionType
import kotlin.random.Random

sealed class MissionChallenge {
    abstract val type: MissionType
    abstract val difficulty: MissionDifficulty
    abstract fun getDescription(): String
    abstract fun generate(): MissionData
    abstract fun validate(userInput: String, missionData: MissionData): Boolean
}

data class MissionData(
    val question: String,
    val answer: String,
    val options: List<String> = emptyList(),
    val pattern: List<Int> = emptyList(),
    val type: MissionType
)

class MathMission(private val diff: MissionDifficulty) : MissionChallenge() {
    override val type = MissionType.MATH
    override val difficulty = diff

    override fun getDescription(): String = when (diff) {
        MissionDifficulty.EASY -> "Solve this simple equation"
        MissionDifficulty.MEDIUM -> "Solve this equation"
        MissionDifficulty.HARD -> "Solve this challenging equation"
    }

    override fun generate(): MissionData {
        val (question, answer) = when (diff) {
            MissionDifficulty.EASY -> {
                val a = Random.nextInt(1, 20)
                val b = Random.nextInt(1, 20)
                val op = listOf("+", "-").random()
                val q = "$a $op $b = ?"
                val ans = when (op) {
                    "+" -> a + b
                    "-" -> a - b
                    else -> 0
                }
                q to ans.toString()
            }
            MissionDifficulty.MEDIUM -> {
                val a = Random.nextInt(5, 50)
                val b = Random.nextInt(3, 20)
                val c = Random.nextInt(1, 10)
                val q = "($a + $b) × $c = ?"
                val ans = (a + b) * c
                q to ans.toString()
            }
            MissionDifficulty.HARD -> {
                val a = Random.nextInt(10, 99)
                val b = Random.nextInt(5, 50)
                val c = Random.nextInt(2, 9)
                val d = Random.nextInt(2, 5)
                val q = "($a - $b) × $c + $d² = ?"
                val ans = (a - b) * c + (d * d)
                q to ans.toString()
            }
        }
        return MissionData(question, answer, type = type)
    }

    override fun validate(userInput: String, missionData: MissionData): Boolean {
        return userInput.trim() == missionData.answer
    }
}

class MemoryMission(private val diff: MissionDifficulty) : MissionChallenge() {
    override val type = MissionType.MEMORY
    override val difficulty = diff

    override fun getDescription(): String = "Memorize and repeat the pattern"

    override fun generate(): MissionData {
        val length = when (diff) {
            MissionDifficulty.EASY -> 4
            MissionDifficulty.MEDIUM -> 6
            MissionDifficulty.HARD -> 8
        }
        val pattern = List(length) { Random.nextInt(1, 5) }
        val display = pattern.joinToString(" - ")
        return MissionData(
            question = "Remember: $display",
            answer = pattern.joinToString(","),
            pattern = pattern,
            type = type
        )
    }

    override fun validate(userInput: String, missionData: MissionData): Boolean {
        val inputPattern = userInput.split(",").mapNotNull { it.trim().toIntOrNull() }
        return inputPattern == missionData.pattern
    }
}

class TypingMission(private val diff: MissionDifficulty) : MissionChallenge() {
    override val type = MissionType.TYPING
    override val difficulty = diff

    private val phrases = listOf(
        "Time to wake up and seize the day",
        "Rise and shine, it's a beautiful morning",
        "Success begins with getting out of bed",
        "Early to bed, early to rise",
        "Wake up with determination, go to bed with satisfaction",
        "The early bird catches the worm",
        "Every morning is a fresh start",
        "Discipline is choosing between what you want now and what you want most",
        "Your future is created by what you do today",
        "Small daily improvements lead to stunning results"
    )

    override fun getDescription(): String = "Type the exact phrase to dismiss"

    override fun generate(): MissionData {
        val phrase = when (diff) {
            MissionDifficulty.EASY -> phrases.take(3).random()
            MissionDifficulty.MEDIUM -> phrases.take(6).random()
            MissionDifficulty.HARD -> phrases.random()
        }
        return MissionData(
            question = "Type exactly:",
            answer = phrase,
            type = type
        )
    }

    override fun validate(userInput: String, missionData: MissionData): Boolean {
        return userInput.trim().lowercase() == missionData.answer.trim().lowercase()
    }
}

object MissionFactory {
    fun createMission(type: MissionType, difficulty: MissionDifficulty): MissionChallenge {
        return when (type) {
            MissionType.MATH -> MathMission(difficulty)
            MissionType.MEMORY -> MemoryMission(difficulty)
            MissionType.TYPING -> TypingMission(difficulty)
            else -> MathMission(difficulty) // Default fallback
        }
    }
}
