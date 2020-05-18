package com.joshualorett.querysuggestions

import io.reactivex.Observable
import java.util.concurrent.TimeUnit


/**
 * A mock repository to test searching.
 * Created by Joshua on 4/13/2019.
 */
interface MockRepository {
    fun search(query: String) : Observable<List<String>>
    fun getSuggestions(query: String) : Observable<List<String>>
}

class LocalMockRepository(private val maxNumberSuggestions: Int = 0,
private val searchDelay: Long = 0) : MockRepository {
    private val data = listOf(
        "aardvark",
        "albatross",
        "alligator",
        "alpaca",
        "ant",
        "anteater",
        "antelope",
        "ape",
        "armadillo",
        "baboon",
        "badger",
        "barracuda",
        "bat",
        "bear",
        "beaver",
        "bee",
        "bison",
        "boar",
        "buffalo",
        "butterfly",
        "camel",
        "capybara",
        "caribou",
        "cassowary",
        "cat",
        "caterpillar",
        "cattle",
        "chamois",
        "cheetah",
        "chicken",
        "chimpanzee",
        "chinchilla",
        "chough",
        "clam",
        "cobra",
        "cockroach",
        "cod",
        "cormorant",
        "coyote",
        "crab",
        "crane",
        "crocodile",
        "crow",
        "curlew",
        "deer",
        "dinosaur",
        "dog",
        "dogfish",
        "dolphin",
        "donkey",
        "dotterel",
        "dove",
        "dragonfly",
        "duck",
        "dugong",
        "dunlin",
        "eagle",
        "echidna",
        "eel",
        "eland",
        "elephant",
        "elephant-seal",
        "elk",
        "emu",
        "falcon",
        "ferret",
        "finch",
        "fish",
        "flamingo",
        "fly",
        "fox",
        "frog",
        "gaur",
        "gazelle",
        "gerbil",
        "giant-panda",
        "giraffe",
        "gnat",
        "gnu",
        "goat",
        "goose",
        "goldfinch",
        "goldfish",
        "gorilla",
        "goshawk",
        "grasshopper",
        "grouse",
        "guanaco",
        "guinea-fowl",
        "guinea-pig",
        "gull",
        "hamster",
        "hare",
        "hawk",
        "hedgehog",
        "heron",
        "herring",
        "hippopotamus",
        "hornet",
        "horse",
        "human",
        "hummingbird",
        "hyena",
        "ibex",
        "ibis",
        "jackal",
        "jaguar",
        "jay",
        "jellyfish",
        "kangaroo",
        "kingfisher",
        "koala",
        "komodo-dragon",
        "kookabura",
        "kouprey",
        "kudu",
        "lapwing",
        "lark",
        "lemur",
        "leopard",
        "lion",
        "llama",
        "lobster",
        "locust",
        "loris",
        "louse",
        "lyrebird",
        "magpie",
        "mallard",
        "manatee",
        "mandrill",
        "mantis",
        "marten",
        "meerkat",
        "mink",
        "mole",
        "mongoose",
        "monkey",
        "moose",
        "mouse",
        "mosquito",
        "mule",
        "narwhal",
        "newt",
        "nightingale",
        "octopus",
        "okapi",
        "opossum",
        "oryx",
        "ostrich",
        "otter",
        "owl",
        "ox",
        "oyster",
        "panther",
        "parrot",
        "partridge",
        "peafowl",
        "pelican",
        "penguin",
        "pheasant",
        "pig",
        "pigeon",
        "polar-bear",
        "pony",
        "porcupine",
        "porpoise",
        "prairie-dog",
        "quail",
        "quelea",
        "quetzal",
        "rabbit",
        "raccoon",
        "rail",
        "ram",
        "rat",
        "raven",
        "red-deer",
        "red-panda",
        "reindeer",
        "rhinoceros",
        "rook",
        "salamander",
        "salmon",
        "sand-dollar",
        "sandpiper",
        "sardine",
        "scorpion",
        "sea-lion",
        "sea-urchin",
        "seahorse",
        "seal",
        "shark",
        "sheep",
        "shrew",
        "skunk",
        "snail",
        "snake",
        "sparrow",
        "spider",
        "spoonbill",
        "squid",
        "squirrel",
        "starling",
        "stingray",
        "stinkbug",
        "stork",
        "swallow",
        "swan",
        "tapir",
        "tarsier",
        "termite",
        "tiger",
        "toad",
        "trout",
        "turkey",
        "turtle",
        "vicuña",
        "viper",
        "vulture",
        "wallaby",
        "walrus",
        "wasp",
        "water-buffalo",
        "weasel",
        "whale",
        "wolf",
        "wolverine",
        "wombat",
        "woodcock",
        "woodpecker",
        "worm",
        "wren",
        "yak",
        "zebra"
    )

    override fun getSuggestions(query: String): Observable<List<String>> {
        if(query.isEmpty()) {
            return Observable.fromCallable { emptyList<String>() }
        }
        return Observable.fromCallable { data.takeUntil(maxNumberSuggestions) { item -> item.contains(query) } }
    }

    override fun search(query: String) : Observable<List<String>> {
        if(query.isEmpty()) {
            return Observable.fromCallable { emptyList<String>() }
        }
        return Observable.fromCallable { data.filter { item -> item.contains(query) } }.delay(searchDelay, TimeUnit.SECONDS)
    }
}

/***
 * Returns the first [n] elements of a list containing only elements matching the given [predicate].
 * @throws IllegalArgumentException if [n] is less than zero.
 */
inline fun <T> Iterable<T>.takeUntil(n: Int, predicate: (T) -> Boolean) : List<T> {
    require(n >= 0) { "Requested element count $n is less than zero." }
    if (n == 0) return emptyList()
    val destination = mutableListOf<T>()
    for (element in this) {
        if (predicate(element)) {
            destination.add(element)
        }
        if(destination.size == n) {
            break
        }
    }
    return destination
}