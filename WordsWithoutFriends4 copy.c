#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <sys/stat.h>
#include <ctype.h>
#include <stdbool.h>

#define PORT "6969"
#define BACKLOG 10
#define BUFFER_SIZE 1024

char *root_dir;  // Root directory for serving files

// Game-related variables
char scrambledWord[30];  // The scrambled word
char foundWords[1024];   // Found words (for simplicity, a string for now)
bool gameInProgress = false;  // Whether the game is currently in progress

// Function to shuffle a word (to create the scrambled word)
void shuffleWord(const char *word) {
    int len = strlen(word);
    strncpy(scrambledWord, word, len);
    scrambledWord[len] = '\0';

    // Shuffle the scrambled word randomly
    for (int i = 0; i < len; i++) {
        int j = rand() % len;
        char temp = scrambledWord[i];
        scrambledWord[i] = scrambledWord[j];
        scrambledWord[j] = temp;
    }
}

// Function to handle each client's HTTP request
void *handle_client(void *arg) {
    int client_fd = *(int *)arg;
    free(arg);

    char buffer[BUFFER_SIZE];
    ssize_t bytes_read = read(client_fd, buffer, BUFFER_SIZE - 1);
    if (bytes_read <= 0) {
        close(client_fd);
        return NULL;
    }
    buffer[bytes_read] = '\0';

    // Parse HTTP GET request
    char *method = strtok(buffer, " ");
    char *path = strtok(NULL, " ");
    
    if (!method || !path || strcmp(method, "GET") != 0) {
        // Send 400 Bad Request if method is not GET
        dprintf(client_fd, "HTTP/1.1 400 Bad Request\r\n\r\n");
        close(client_fd);
        return NULL;
    }

    // Parse the query string for game-related parameters
    char *moveParam = strstr(path, "move=");
    if (moveParam != NULL && gameInProgress) {
        moveParam += 5;  // Skip "move=" part

        // Get the guess from the URL
        char guess[30];
        int i = 0;
        while (moveParam[i] != '\0' && moveParam[i] != '&' && i < sizeof(guess) - 1) {
            guess[i] = moveParam[i];
            i++;
        }
        guess[i] = '\0';  // Null terminate the guess

        printf("Player guess: %s\n", guess);

        // Check if the guess is correct (this is simplified; you can add logic to check against a word list)
        if (strcmp(guess, scrambledWord) == 0) {
            strcat(foundWords, guess);
            strcat(foundWords, ", ");
        }
    }

    // Build the HTML response to display the current game state
    char htmlResponse[2048];
    snprintf(htmlResponse, sizeof(htmlResponse),
         "HTTP/1.1 200 OK\r\n"
    "Content-Type: text/html; charset=UTF-8\r\n\r\n"
    "<html>"
    "<head>"
    "<title>Word Scramble Game</title>"
    "<style>"
    "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 20px; padding: 20px; }"
    "h2 { color: #333; }"
    "p { font-size: 16px; color: #555; }"
    "form { margin-top: 20px; }"
    "input[type=text] { padding: 10px; font-size: 16px; width: 200px; }"
    "input[type=submit] { padding: 10px 20px; font-size: 16px; cursor: pointer; background-color: #4CAF50; color: white; border: none; }"
    "</style>"
    "</head>"
    "<body>"
    "<h1>Welcome to the Word Scramble Game!</h1>"
    "<h2>Scrambled Word: %s</h2>"
    "<p>Found Words: %s</p>"
    "<p>Guess the word or type your guess and hit submit!</p>"
    "<form action=\"/words\" method=\"get\">"
    "<input type=\"text\" name=\"move\" autofocus>"
    "<input type=\"submit\" value=\"Submit\">"
    "</form>"
    "</body>"
    "</html>", scrambledWord, foundWords);

    // Send the response
    write(client_fd, htmlResponse, strlen(htmlResponse));

    close(client_fd);
    return NULL;
}

// Function to start the HTTP server
void startServer() {
    struct addrinfo hints, *res;
    int server_fd;

    // Set up the address info struct for the server socket
    memset(&hints, 0, sizeof(hints));
    hints.ai_family = AF_INET;         // Use IPv4
    hints.ai_socktype = SOCK_STREAM;   // Use TCP
    hints.ai_flags = AI_PASSIVE;       // Bind to the IP of the host

    // Resolve the address and port
    if (getaddrinfo(NULL, PORT, &hints, &res) != 0) {
        perror("getaddrinfo");
        exit(EXIT_FAILURE);
    }

    // Create and bind the socket, and start listening for connections
    server_fd = socket(res->ai_family, res->ai_socktype, res->ai_protocol);
    if (server_fd < 0) {
        perror("socket");
        exit(EXIT_FAILURE);
    }

    if (bind(server_fd, res->ai_addr, res->ai_addrlen) < 0) {
        perror("bind");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    freeaddrinfo(res);  // Free the address info struct

    if (listen(server_fd, BACKLOG) < 0) {
        perror("listen");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    printf("Server listening on port %s\n", PORT);

    // Main loop to accept and handle incoming connections
    while (1) {
        struct sockaddr_storage client_addr;
        socklen_t addr_size = sizeof(client_addr);
        int *client_fd = malloc(sizeof(int));
        if (!client_fd) {
            perror("malloc");
            continue;
        }

        *client_fd = accept(server_fd, (struct sockaddr *)&client_addr, &addr_size);
        if (*client_fd < 0) {
            perror("accept");
            free(client_fd);
            continue;
        }

        pthread_t thread;
        if (pthread_create(&thread, NULL, handle_client, client_fd) != 0) {
            perror("pthread_create");
            close(*client_fd);
            free(client_fd);
        } else {
            pthread_detach(thread);
        }
    }

    close(server_fd);
}

// Function to initialize the word game
void startGame() {
    // For simplicity, let's use a hardcoded word (you can replace this with a random word from your word list)
    const char *word = "SCRAMBLE";
    shuffleWord(word);
    strcpy(foundWords, "");  // Reset found words list
    gameInProgress = true;   // Start the game
}

int main() {
    // Start the game when the server starts
    startGame();

    // Start the HTTP server
    startServer();

    return 0;
}
