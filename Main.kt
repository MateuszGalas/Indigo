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

class Indigo {
    var allCards: MutableList<Card> = buildDeck()
    var cardsOnTheTable: MutableList<Card> = mutableListOf()
    var cardsInPlayerHand: MutableList<Card> = mutableListOf()
    var cardsInComputerHand: MutableList<Card> = mutableListOf()

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

    fun getCards(number: Int): MutableList<Card> {
        allCards.subList(0, number).apply {
            val cards = mutableListOf<Card>()
            cards.addAll(this)
            clear()
            return cards
        }
    }
}

fun main() {
    val game = Indigo()
    var player: Boolean?
    game.allCards.shuffle()
    println("Indigo Card Game")

    while (true) {
        println("Play first?")
        readln().apply {
            player = if (equals("yes", true)) {
                true
            } else if (equals("no", true)) {
                false
            } else {
                null
            }
        }
        if (player == null) continue
        break
    }
    game.cardsOnTheTable = game.getCards(4)
    println("Initial cards on the table: ${game.cardsOnTheTable.joinToString(" ")}")
    println()

    while (true) {
        println("${game.cardsOnTheTable.size} cards on the table, and the top card is ${game.cardsOnTheTable.last()}")

        if (game.allCards.size == 0 && game.cardsInPlayerHand.size == 0 && game.cardsInComputerHand.size == 0) break
        if (game.cardsInComputerHand.size == 0 && game.cardsInPlayerHand.size == 0) {
            game.cardsInComputerHand = game.getCards(6)
            game.cardsInPlayerHand = game.getCards(6)
        }

        if (player == true) {
            print("Cards in hand: ")
            game.cardsInPlayerHand.forEach {
                print("${game.cardsInPlayerHand.indexOf(it) + 1})$it ")
            }
            println()
            var cardToPlay: Int?
            while (true) {
                println("Choose a card to play (1-${game.cardsInPlayerHand.size}):")
                cardToPlay = readln().let {
                    if (it.matches("""\d+""".toRegex())) {
                        it.toInt()
                    } else if (it.equals("exit", true)) {
                        return println("Game Over")
                    } else {
                        null
                    }
                }
                if ((cardToPlay == null) || (cardToPlay !in (1..game.cardsInPlayerHand.size))) continue
                cardToPlay--
                break
            }
            game.cardsOnTheTable.add(game.cardsInPlayerHand[cardToPlay!!])
            game.cardsInPlayerHand.removeAt(cardToPlay)
            player = false
        } else {
            println("Computer plays ${game.cardsInComputerHand[0]}")
            game.cardsOnTheTable.add(game.cardsInComputerHand[0])
            game.cardsInComputerHand.removeAt(0)
            player = true
        }
        println()
    }
    println("Game Over")
}