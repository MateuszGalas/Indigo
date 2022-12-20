package indigo

import java.lang.Exception

const val cardsGivePoints = "A♠A♥A♦A♣10♠10♥10♦10♣J♠J♦J♥J♣Q♠Q♥Q♦Q♣K♠K♥K♦K♣"

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

data class Player(var cardsWon: Int, var score: Int, val player: String) {
    val cards: MutableList<Card> = mutableListOf()
    var lastWon = false

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
        val player = Player(0, 0, "Player")
        val computer = Player(0, 0, "Computer")
        allCards.shuffle()
        println("Indigo Card Game")

        var turn = turn()
        if (turn == 0) player.lastWon = true

        cardsOnTheTable.addAll(getCards(4))
        println("Initial cards on the table: ${cardsOnTheTable.joinToString(" ")}")
        println()

        while (true) {
            println()
            if (!cardsOnTheTable.isEmpty()) {
                println("${cardsOnTheTable.size} cards on the table, and the top card is ${cardsOnTheTable.last()}")
            } else {
                println("No cards on the table")
            }
            when {
                (allCards.isEmpty() && computer.cards.size + player.cards.size == 0) -> {
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
                        println(
                            "${player.player} wins cards\n" +
                                    "Score: Player ${player.score} - Computer ${computer.score}\n" +
                                    "Cards: Player ${player.cardsWon} - Computer ${computer.cardsWon}"
                        )
                    } else if (cardsOnTheTable.isNotEmpty()) {
                        cardsOnTheTable.forEach { if (cardsGivePoints.contains(it.toString())) computer.score++ }
                        computer.cardsWon += cardsOnTheTable.size
                        cardsOnTheTable.clear()
                        println(
                            "${computer.player} wins cards\n" +
                                    "Score: Player ${player.score} - Computer ${computer.score}\n" +
                                    "Cards: Player ${player.cardsWon} - Computer ${computer.cardsWon}"
                        )
                    }
                    break
                }

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

                if (cardsOnTheTable.isNotEmpty()) {
                    if (player.cards[cardToPlay.toInt() - 1].rank == cardsOnTheTable.last().rank ||
                        player.cards[cardToPlay.toInt() - 1].suit == cardsOnTheTable.last().suit
                    ) {
                        cardsOnTheTable.add(player.cards[cardToPlay.toInt() - 1])
                        player.cards.removeAt(cardToPlay.toInt() - 1)
                        cardsOnTheTable.forEach { if (cardsGivePoints.contains(it.toString())) player.score++ }
                        player.cardsWon += cardsOnTheTable.size
                        cardsOnTheTable.clear()
                        println(
                            "${player.player} wins cards\n" +
                                    "Score: Player ${player.score} - Computer ${computer.score}\n" +
                                    "Cards: Player ${player.cardsWon} - Computer ${computer.cardsWon}"
                        )
                        player.lastWon = true
                    } else {
                        cardsOnTheTable.add(player.cards[cardToPlay.toInt() - 1])
                        player.cards.removeAt(cardToPlay.toInt() - 1)
                    }
                } else {
                    cardsOnTheTable.add(player.cards[cardToPlay.toInt() - 1])
                    player.cards.removeAt(cardToPlay.toInt() - 1)
                }
                turn++
            }
            val computerActions = {
                println("Computer plays ${computer.cards[0]}")

                if (cardsOnTheTable.isNotEmpty()) {

                    if (computer.cards[0].rank == cardsOnTheTable.last().rank ||
                        computer.cards[0].suit == cardsOnTheTable.last().suit
                    ) {
                        cardsOnTheTable.add(computer.cards[0])
                        cardsOnTheTable.forEach { if (cardsGivePoints.contains(it.toString())) computer.score++ }
                        computer.cardsWon += cardsOnTheTable.size
                        cardsOnTheTable.clear()
                        println(
                            "${computer.player} wins cards\n" +
                                    "Score: Player ${player.score} - Computer ${computer.score}\n" +
                                    "Cards: Player ${player.cardsWon} - Computer ${computer.cardsWon}"
                        )
                        player.lastWon = false
                    } else {
                        cardsOnTheTable.add(computer.cards[0])
                    }
                } else {
                    cardsOnTheTable.add(computer.cards[0])
                }
                computer.cards.removeAt(0)
                turn--
            }

            val list = mutableListOf(playerActions, computerActions)
            try {
                list[turn].invoke()
            } catch (e: Exception) {
                println(e.message)
                break
            }
        }
        println("Game Over")
    }
}


fun main() {
    val game = Indigo()
    game.play()
}