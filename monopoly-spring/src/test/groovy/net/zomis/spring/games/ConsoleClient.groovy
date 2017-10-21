package net.zomis.spring.games

import net.zomis.spring.test.RestTest

class ConsoleClient {

    final Scanner scanner
    final RestTest t
    String game
    String gameId
    String token

    ConsoleClient(Scanner scanner, RestTest t) {
        this.scanner = scanner
        this.t = t
    }

    String getPath() {
        return 'games2/' + game
    }
    String getGamePath() {
        return path + '/' + gameId
    }

    void run() {
        def gamesAvailable = t.get('games2')
        println gamesAvailable
        println "Choose game:"
        game = scanner.nextLine()
        if (!gamesAvailable.contains(game)) {
            println "Game does not exist"
            return
        }

        println t.get(path)

        println "Enter gameid if you have any:"
        gameId = scanner.nextLine()

        println "Enter token if you have any:"
        token = scanner.nextLine()

        def input = ''
        while (input != "q") {
            println "What to do? c) Create game. j) Join entered gameid. s) Start game. a) Actions in game."
            input = scanner.nextLine()
            if (input.startsWith('c')) {
                println "Enter your name"
                def name = scanner.nextLine()

                // Create new game
                def result = t.post(path, { playerName name })
                println result
            }
            if (input.startsWith('j')) {
                println "Enter your name"
                def name = scanner.nextLine()

                // Join game
                def result = t.post("$gamePath/join", { playerName name })
                println result
            }
            if (input.startsWith('s')) {
                // Start game
                def result = t.post("$gamePath/start", {})
                println result
            }
            if (input.startsWith('a')) {
                actions()
            }
        }
    }

    void actions() {
        println t.get("$gamePath/details")

        println "Enter action:"
        def type = scanner.nextLine()
        println "x"
        def vx = scanner.nextInt()
        println "y"
        def vy = scanner.nextInt()

        def result = t.post("$gamePath/actions/$type?token=$token", {
            x vx
            y vy
        })
    }

    static void main(String[] args) {
        Scanner scanner = new Scanner(System.in)
        def t = RestTest.localhost(8081)
        def cc = new ConsoleClient(scanner, t)
        cc.game = 'ttt'
        cc.gameId = '5y'
        // cc.token = '18f456f5-e319-3690-b29e-ae9b04df55d7' // Player 1
        cc.token = '39c10c18-a5e9-39ff-9a25-0c673972f97e' // Player 2
//        cc.run()
        cc.actions()


        scanner.close()
    }

}
