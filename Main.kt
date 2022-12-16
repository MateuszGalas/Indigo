package indigo

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

class Indigo() {
    var allCards: MutableList<Card> = buildDeck()

    fun buildDeck(): MutableList<Card> {
        val allCards = buildList {
            Suit.values().forEach { suit ->
                Rank.values().forEach { rank ->
                    add(Card(rank, suit))
                }
            }
        }.toMutableList()
        return allCards
    }

    fun getCards(number: String) {
        if (!number.matches("""\d+""".toRegex())) {
            println("Invalid number of cards.")
            return
        }
        val number = number.toInt()

        if (number !in 1..52) {
            println("Invalid number of cards.")
            return
        }

        if (allCards.size < number) {
            println("The remaining cards are insufficient to meet the request.")
            return
        } else {
            for (i in 1..number) {
                print("${allCards.removeAt(0)} ")
            }
            println()
        }
    }
}


fun main() {
    val game = Indigo()

    while (true) {
        println("Choose an action (reset, shuffle, get, exit):")
        val action = readln()
        when (action) {
            "reset" -> {
                println("Card deck is reset.")
                game.allCards = game.buildDeck()
            }

            "shuffle" -> {
                println("Card deck is shuffled.")
                game.allCards.shuffle()
            }

            "get" -> {
                println("Number of cards:")
                game.getCards(readln())
            }

            "exit" -> {
                println("Bye")
                break
            }

            else -> println("Wrong action.")
        }

    }
}