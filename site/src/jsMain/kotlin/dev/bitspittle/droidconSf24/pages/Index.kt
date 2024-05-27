package dev.bitspittle.droidconSf24.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.browser.util.invokeLater
import com.varabyte.kobweb.compose.dom.disposableRef
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.Page
import dev.bitspittle.droidconSf24.bindings.revealjs.Reveal
import dev.bitspittle.droidconSf24.bindings.revealjs.RevealHighlight
import dev.bitspittle.droidconSf24.bindings.revealjs.RevealNotes
import dev.bitspittle.droidconSf24.components.sections.*
import dev.bitspittle.droidconSf24.rememberLastSlide
import dev.bitspittle.droidconSf24.showSlideNumbers
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Div
import kotlin.js.Json
import kotlin.js.json

private const val REVEALJS_LAST_SLIDE_KEY = "revealjs-last-slide"

@Page
@Composable
fun HomePage() {
    Box(Modifier.fillMaxSize().classNames("reveal"),
        contentAlignment = Alignment.Center,
        ref = disposableRef { element ->
            println("Initializing reveal.js...")

            val deck = Reveal(
                element,
                json(
                    "embedded" to false,
                    "slideNumber" to AppGlobals.showSlideNumbers,
                    "plugins" to arrayOf(RevealHighlight, RevealNotes) // imported in build.gradle.kts
                )
            ).apply {
                initialize()
            }

            println("... initialized!")

            if (AppGlobals.rememberLastSlide) {
                localStorage.getItem(REVEALJS_LAST_SLIDE_KEY)?.let { lastSlideValue ->
                    val slideData = JSON.parse<Json>(lastSlideValue)
                    window.invokeLater {
                        try {
                            deck.slide(slideData["h"] as Int, slideData["v"] as? Int ?: 0)
                            console.info("Jumped to slide $lastSlideValue because `slides.remember.last=true` in gradle.properties")
                        } catch (ex: Throwable) {
                            localStorage.removeItem(REVEALJS_LAST_SLIDE_KEY)
                            console.warn("Aborted jumping to $lastSlideValue due to an exception", ex)
                        }
                    }
                }
                deck.on("slidechanged") {
                    localStorage.setItem(REVEALJS_LAST_SLIDE_KEY, JSON.stringify(deck.getIndices()))
                }
            } else {
                localStorage.removeItem(REVEALJS_LAST_SLIDE_KEY)
            }

            onDispose {
                println("Cleaning up reveal.js...")
                deck.destroy()
                println("... cleaned up!")
            }
        }
    ) {
        Div(Modifier.classNames("slides").toAttrs()) {
            Slides()
        }
    }
}
