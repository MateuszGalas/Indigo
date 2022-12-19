package indigo

import java.lang.Exception

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

data class Player(var score: Int, val player: String) {
    val cards: MutableList<Card> = mutableListOf()

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

class Indigo {
    private val allCards: MutableList<Card> = buildDeck()
    private val cardsOnTheTable: MutableList<Card> = mutableListOf()

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
        val player = Player(0, "Player")
        val computer = Player( 0, "Computer")
        allCards.shuffle()
        println("Indigo Card Game")

        var turn = turn()

        cardsOnTheTable.addAll(getCards(4))
        println("Initial cards on the table: ${cardsOnTheTable.joinToString(" ")}")
        println()

        while (true) {
            println("${cardsOnTheTable.size} cards on the table, and the top card is ${cardsOnTheTable.last()}")
            when {
                (cardsOnTheTable.size == 52) -> break
                (computer.cards.size + player.cards.size == 0) -> {
                    computer.cards.addAll(getCards(6))
                    player.cards.addAll(getCards(6))
                }
            }

            val playerActions = {
                print("Cards in hand: ")
                player.cards.forEach {
                    print("${player.cards.indexOf(it) + 1})$it ")
                }
                println()
                val cardToPlay: String = player.cardToPlay()

                cardsOnTheTable.add(player.cards[cardToPlay.toInt() - 1])
                player.cards.removeAt(cardToPlay.toInt() - 1)
                turn++
            }
            val computerActions = {
                println("Computer plays ${computer.cards[0]}")
                cardsOnTheTable.add(computer.cards[0])
                computer.cards.removeAt(0)
                turn--
            }

            val list = mutableListOf(playerActions, computerActions)
            try {
                list[turn].invoke()
            } catch (e: Exception) {
                break
            }
            println()
        }
        println("Game Over")
    }
}


fun main() {
    val game = Indigo()
    game.play()
}