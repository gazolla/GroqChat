# GroqChat

A simple web-based chat interface for the Groq API built with Kotlin, Ktor, and HTMX.

![GroqChat Screenshot](https://via.placeholder.com/800x450.png?text=GroqChat+Screenshot)

## Features

- Clean, responsive UI built with Tailwind CSS
- Real-time chat interactions using HTMX
- Conversation history tracking
- Built with Kotlin and Ktor for a robust backend
- Integration with Groq's LLaMA-3 model (70B versatile)
- Conversation clearing functionality

## Prerequisites

- JDK 17 or later
- Kotlin 1.9.22 or later
- Groq API key ([Get one here](https://console.groq.com/keys))

## Getting Started

### Configuration

1. Clone the repository
   ```bash
   git clone https://github.com/yourusername/groqchat.git
   cd groqchat
   ```

2. Set up your Groq API Key
   ```bash
   # Linux/macOS
   export GROQ_API_KEY="your-groq-api-key"
   
   # Windows
   set GROQ_API_KEY=your-groq-api-key
   ```

3. Build the project
   ```bash
   ./gradlew build
   ```

4. Run the application
   ```bash
   ./gradlew run
   ```

5. Open your browser and navigate to [http://localhost:8080](http://localhost:8080)

## Project Structure

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/
│   │       └── gazapps/
│   │           └── Application.kt  # Main application file
│   └── resources/
│       ├── static/                 # Static assets
│       │   └── css/
│       │       └── styles.css
│       └── templates/              # Thymeleaf templates
│           ├── index.html
│           ├── chat-response.html
│           └── chat-response-clear.html
```

## How It Works

1. The application sets up a Ktor server with Thymeleaf templating and HTMX for interactivity
2. When a user submits a message, it's sent to the `/chat` endpoint
3. The server maintains conversation history and forwards the message to the Groq API
4. Groq's response is returned and displayed in the chat interface
5. Users can clear the conversation history with the "Clear" button

## Dependencies

- [Ktor](https://ktor.io/) - Asynchronous web framework for Kotlin
- [Thymeleaf](https://www.thymeleaf.org/) - Modern server-side Java template engine
- [HTMX](https://htmx.org/) - High power tools for HTML
- [Tailwind CSS](https://tailwindcss.com/) - Utility-first CSS framework
- [Groq API Kotlin Client](https://github.com/yourusername/groq-api-kotlin) - Kotlin client for the Groq API

## Customization

### Changing the AI Model

To use a different Groq model, modify the `sendMessageToGroq` function in `Application.kt`:

```kotlin
suspend fun sendMessageToGroq(message: String): String {
    // ...
    val response = client.chatText(
        model = "llama-3.3-8b-instruct", // Change model here
        userMessage = message,
        systemMessage = "You are a helpful assistant."
    )
    // ...
}
```

### Customizing System Instructions

To change how the AI responds, modify the `systemMessage` parameter:

```kotlin
suspend fun sendMessageToGroq(message: String): String {
    // ...
    val response = client.chatText(
        model = "llama-3.3-70b-versatile",
        userMessage = message,
        systemMessage = "You are a friendly and concise assistant who responds with humor." // Change system message here
    )
    // ...
}
```

## Building for Production

To create a standalone JAR file:

```bash
./gradlew shadowJar
```

The resulting JAR can be found in `build/libs/` and run with:

```bash
java -jar build/libs/groqchat-0.0.1-all.jar
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgements

- [Groq](https://groq.com/) for their powerful LLM API
- [Ktor](https://ktor.io/) community
- [HTMX](https://htmx.org/) for simplifying web interactions