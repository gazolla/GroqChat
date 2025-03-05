package com.gazapps

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.thymeleaf.*
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import io.ktor.server.resources.*
import com.groq.api.client.GroqClientFactory
import com.groq.api.extensions.chatText
import io.ktor.server.request.receiveParameters
import org.slf4j.LoggerFactory
import org.thymeleaf.templatemode.TemplateMode
import java.io.File

fun main() {
    embeddedServer(Netty, port = 8080) {

        install(Thymeleaf) {
            setTemplateResolver(ClassLoaderTemplateResolver().apply {
                prefix = "/templates/"
                suffix = ".html"
                characterEncoding = "UTF-8"
                templateMode = TemplateMode.HTML
            })
        }

        install(ContentNegotiation) {
            jackson()
        }

        install(Resources)

        val logger = LoggerFactory.getLogger(Application::class.java)

        routing {

            val conversationHistory = mutableListOf<Map<String, String>>()

            staticFiles("/static", File("src/main/resources/static")) {
                default("index.html")
            }

            get("/") {
                call.respond(ThymeleafContent("index", emptyMap()))
            }

            post("/chat") {
                val params = call.receiveParameters()
                val message = params["message"] ?: ""
                logger.info("User Message: $message")
                if (message.isNotBlank()) {
                    conversationHistory.add(mapOf("role" to "user", "content" to message))
                    try {
                        val historyMsg:String =  conversationHistory.joinToString("\n") { messageMap ->
                            "${messageMap["role"]}: ${messageMap["content"]}"
                        }
                        val groqResponse = sendMessageToGroq(historyMsg)
                        conversationHistory.add(mapOf("role" to "assistant", "content" to groqResponse))

                        call.respond(ThymeleafContent("chat-response", mapOf("response" to groqResponse, "message" to message)))
                    } catch (e: Exception) {
                        logger.error("Error communicating to Groq", e)
                        call.respond(ThymeleafContent("chat-response", mapOf("response" to "Error processing message.", "message" to message)))
                    }
                } else {
                    call.respond(ThymeleafContent("chat-response", mapOf("response" to "Please, fill up the message.", "message" to message)))
                }
            }

            get("/clear-chat") {
                conversationHistory.clear()
                call.respond(ThymeleafContent("chat-response-clear", mapOf()))
            }
        }
    }.start(wait = true)
}


suspend fun sendMessageToGroq(message: String): String {
    val apiKey = System.getenv("GROQ_API_KEY") ?: "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"
    return try {
        GroqClientFactory.createClient(apiKey).use { client ->
            val response = client.chatText(
                model = "llama-3.3-70b-versatile",
                userMessage = message,
                systemMessage = "You are a helpful assistant."
            )
            response
        }
    } catch (e: Exception) {
        "Groq API error: ${e.message}"
    }
}
