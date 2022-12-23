package indigo

import java.lang.Exception

const val cardsGivePoints = "A♠A♥A♦A♣10♠10♥10♦10♣J♠J♦J♥J♣Q♠Q♥Q♦Q♣K♠K♥K♦K♣"

open class Deck {
    val allCards: MutableList<Card> = buildDeck()
    val cardsOnTheTable: MutableList<Card> = mutableListOf()

    enum class Rank(val symbol: String) {
        ACE("A"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        TEN("10"),
        JACK("J"),
        QUEEN("Q"),
        KING("K");

        companion object {
            override fun toString(): String {
                return values().joinToString(" ") { it.symbol }
            }
        }
    }

    enum class Suit(val symbol: String) {
        SPADES("♠"),
        HEARTS("♥"),
        DIAMONDS("♦"),
        CLUBS("♣");

        companion object {
            override fun toString(): String {
                return values().joinToString(" ") { it.symbol }
            }
        }
    }

    data class Card(val rank: Rank, val suit: Suit) {
        override fun toString(): String {
            return "${rank.symbol}${suit.symbol}"
        }
    }


    data class Message(val playerWon: String = "", val player: Player, val secondPlayer: Player) {
        override fun toString(): String {
            if (playerWon == "") {
                return "Score: Player ${player.score} - Computer ${secondPlayer.score}\n" +
                        "Cards: Player ${player.cardsWon} - Computer ${secondPlayer.cardsWon}"
            }
            return "$playerWon wins cards\n" +
                    "Score: Player ${player.score} - Computer ${secondPlayer.score}\n" +
                    "Cards: Player ${player.cardsWon} - Computer ${secondPlayer.cardsWon}"
        }
    }

    private fun buildDeck(): MutableList<Card> {
        val allCards = buildList {
            Suit.values().forEach { suit ->
                Rank.values().forEach { rank ->
                    add(Card(rank, suit))
                }
            }
        }.toMutableList()
        return allCards
    }

    fun deckIsEmpty(player: Player, computer: Player): Boolean {
        return allCards.isEmpty() && computer.cards.size + player.cards.size == 0
    }
}

open class Player(var cardsWon: Int, var score: Int, val player: String) : Deck() {
    val cards: MutableList<Card> = mutableListOf()
    var lastWon = false

    fun throwCard(card: Int, cardsOnTheTable: MutableList<Card>) {
        cardsOnTheTable.add(cards[card])
        cards.removeAt(card)
    }
}

class Human : Player(cardsWon = 0, score = 0, player = "Player") {
    fun cardToPlay(): String {
        println("Choose a card to play (1-${cards.size}):")
        var cardToPlay = readln()
        when {
            cardToPlay.matches("""\d+""".toRegex()) && cardToPlay.toInt() in (1..cards.size) -> {
                return cardToPlay
            }

            cardToPlay.equals("exit", true) -> throw Exception("Game Over")
            else -> cardToPlay = cardToPlay()
        }
        return cardToPlay
    }

}

class Computer : Player(cardsWon = 0, score = 0, player = "Computer") {
    fun cardToPlay(cardsOnTheTable: MutableList<Card>): String {
        val isMatchRank = mutableListOf<Boolean>()
        val isMatchSuit = mutableListOf<Boolean>()

        println(cards.joinToString(" "))

        if (cardsOnTheTable.isNotEmpty()){
            cards.forEach {
                isMatchRank.add(
                    it.rank == cardsOnTheTable.last().rank
                )
                isMatchSuit.add(
                    it.suit == cardsOnTheTable.last().suit
                )
            }
        }

        val countRank: Int = isMatchRank.count { it }
        val countSuit: Int = isMatchSuit.count { it }

        if(cardsOnTheTable.isEmpty() || countSuit + countRank == 0) {
            val rank = cards.groupingBy { it.rank }.eachCount().filter { it.value > 1 }
            val suit = cards.groupingBy { it.suit }.eachCount().filter { it.value > 1 }

            return if (suit.isNotEmpty()) {
                cards.indexOf(cards.first { it.suit == suit.keys.first() }).toString()
            } else if (rank.isNotEmpty()){
                cards.indexOf(cards.first { it.rank == rank.keys.first() }).toString()
            } else {
                "0"
            }
        }

        return when {
            countSuit + countRank == 0 -> "0"
            countSuit < 2 && countRank > 0 -> isMatchRank.indexOf(true).toString()
            else -> isMatchSuit.indexOf(true).toString()
        }
    }
}

class Indigo : Deck() {
    private fun getCards(number: Int): MutableList<Card> {
        allCards.subList(0, number).apply {
            val cards = mutableListOf<Card>()
            cards.addAll(this)
            clear()
            return cards
        }
    }

    private fun turn(): Int {
        println("Play first?")
        val playersTurn = readln()

        val turn = when (playersTurn.lowercase()) {
            "yes" -> 0
            "no" -> 1
            else -> turn()
        }
        return turn
    }

    fun play() {
        val player = Human()
        val computer = Computer()
        val computerWon = Message(computer.player, player, computer)
        val playerWon = Message(player.player, player, computer)
        val neutral = Message("", player, computer)
        allCards.shuffle()
        println("Indigo Card Game")

        var turn = turn()
        if (turn == 0) player.lastWon = true

        cardsOnTheTable.addAll(getCards(4))
        println("Initial cards on the table: ${cardsOnTheTable.joinToString(" ")}")
        val playerActionsIfEmpty = {
            print("Cards in hand: ")
            player.cards.forEach { print("${player.cards.indexOf(it) + 1})$it ") }
            println()
            val cardToPlay: String = player.cardToPlay()
            player.throwCard(cardToPlay.toInt() - 1, cardsOnTheTable)
            turn++
        }
        val computerActionsIfEmpty = {
            val cardToPlay = computer.cardToPlay(cardsOnTheTable)
            println("Computer plays ${computer.cards[cardToPlay.toInt()]}")
            computer.throwCard(cardToPlay.toInt(), cardsOnTheTable)
            turn--
        }

        val playerActions = {
            print("Cards in hand: ")
            player.cards.forEach { print("${player.cards.indexOf(it) + 1})$it ") }
            println()
            val cardToPlay: String = player.cardToPlay()

            if (player.cards[cardToPlay.toInt() - 1].rank == cardsOnTheTable.last().rank ||
                player.cards[cardToPlay.toInt() - 1].suit == cardsOnTheTable.last().suit
            ) {
                cardsOnTheTable.add(player.cards[cardToPlay.toInt() - 1])
                player.cards.removeAt(cardToPlay.toInt() - 1)
                cardsOnTheTable.forEach { if (cardsGivePoints.contains(it.toString())) player.score++ }
                player.cardsWon += cardsOnTheTable.size
                cardsOnTheTable.clear()
                println(playerWon)
                player.lastWon = true
            } else {
                player.throwCard(cardToPlay.toInt() - 1, cardsOnTheTable)
            }

            turn++
        }
        val computerActions = {
            val cardToPlay = computer.cardToPlay(cardsOnTheTable)
            println("Computer plays ${computer.cards[cardToPlay.toInt()]}")

            if (computer.cards[cardToPlay.toInt()].rank == cardsOnTheTable.last().rank ||
                computer.cards[cardToPlay.toInt()].suit == cardsOnTheTable.last().suit
            ) {
                computer.throwCard(cardToPlay.toInt(), cardsOnTheTable)
                cardsOnTheTable.forEach { if (cardsGivePoints.contains(it.toString())) computer.score++ }
                computer.cardsWon += cardsOnTheTable.size
                cardsOnTheTable.clear()
                println(computerWon)
                player.lastWon = false
            } else {
                computer.throwCard(cardToPlay.toInt(), cardsOnTheTable)
            }
            turn--
        }
        val list =
            mutableListOf(playerActions, computerActions, playerActionsIfEmpty, computerActionsIfEmpty)

        while (true) {
            println()
            if (cardsOnTheTable.isNotEmpty()) {
                println("${cardsOnTheTable.size} cards on the table, and the top card is ${cardsOnTheTable.last()}")
            } else {
                println("No cards on the table")
            }

            when {
                (deckIsEmpty(player, computer)) -> break
                (computer.cards.size + player.cards.size == 0) -> {
                    computer.cards.addAll(getCards(6))
                    player.cards.addAll(getCards(6))
                }
            }

            try {
                if (cardsOnTheTable.isEmpty()) {
                    list[turn + 2].invoke()
                } else {
                    list[turn].invoke()
                }
            } catch (e: Exception) {
                println(e.message)
                println("Game Over")
                return
            }
        }

        if (player.cardsWon == computer.cardsWon && player.lastWon) {
            player.score += 3
        } else if (player.cardsWon == computer.cardsWon) {
            computer.score += 3
        } else if (player.cardsWon > computer.cardsWon) {
            player.score += 3
        } else {
            computer.score += 3
        }

        if (cardsOnTheTable.isNotEmpty() && player.lastWon) {
            cardsOnTheTable.forEach { if (cardsGivePoints.contains(it.toString())) player.score++ }
            player.cardsWon += cardsOnTheTable.size
            cardsOnTheTable.clear()
            println(neutral)
        } else if (cardsOnTheTable.isNotEmpty()) {
            cardsOnTheTable.forEach { if (cardsGivePoints.contains(it.toString())) computer.score++ }
            computer.cardsWon += cardsOnTheTable.size
            cardsOnTheTable.clear()
            println(neutral)
        } else if (player.lastWon) {
            println(neutral)
        } else {
            println(neutral)
        }

        println("Game Over")
    }
}

fun main() {
    val game = Indigo()
    game.play()
}