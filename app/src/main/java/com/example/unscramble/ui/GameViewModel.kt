package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.unscramble.ui.GameUiState
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {
    // Game UI state
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var usedWords: MutableSet<String> = mutableSetOf()
    private lateinit var currentWord: String;

    var userGuess by mutableStateOf("")
        private set

    init {
        resetGame()
    }

    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        //var wordToReturn: String = ""
        return if(usedWords.contains(currentWord)){
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
            //wordToReturn = shuffleCurrentWord(currentWord)
        }
        //return  wordToReturn
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        //Scramble the word:
        tempWord.shuffle()
        while (String(tempWord) == word){
            tempWord.shuffle()
        }
        //return tempWord.toString
        return String(tempWord)
    }

    fun checkUserGuess(){
        if (userGuess.equals(currentWord, ignoreCase = true)){
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
            updateUserGuess("")
        } else{
            //User's guess is wrong
            _uiState.update {currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
    }

    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }

    private fun updateGameState(updatedScore: Int) {
        if(usedWords.size == MAX_NO_OF_WORDS){
            //Last Round of Game
            _uiState.update {currentState->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else {
            //Normal Game Round
            _uiState.update {currentState ->
                currentState.copy(
                    score = updatedScore,
                    isGuessedWordWrong = false,
                    currentScrambledWord =  pickRandomWordAndShuffle(),
                    currentWordCount = currentState.currentWordCount.inc()
                )
            }
        }

    }

    fun skipWord(){
        updateGameState(_uiState.value.score)
        //Reset User Guess:
        updateUserGuess("")
    }

    fun resetGame(){
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
        //_uiState.update {it ->
        //    it.copy(currentScrambledWord = pickRandomWordAndShuffle())
        //}
    }
}