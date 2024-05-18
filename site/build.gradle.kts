import com.varabyte.kobweb.gradle.application.extensions.AppBlock.LegacyRouteRedirectStrategy
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link
import kotlinx.html.script

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
}

group = "dev.bitspittle.droidcon24"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            description.set("Kobweb Presentation for Droidcon SF 2024")
            head.add {
                script { src = routePrefix.prependTo("/reveal.js/dist/reveal.js") }
                link(rel = "stylesheet", href = routePrefix.prependTo("/reveal.js/dist/reveal.css"))
                link(rel = "stylesheet", href = routePrefix.prependTo("/reveal.js/dist/theme/white.css"))
            }
        }
        export {
            // Current, the Kobweb Markdown plugin creates one page per markdown file; but for this site, we only use
            // them as slides under the index page. So just export the index page. (In a future version of Kobweb, the
            // Markdown plugin will be updated to allow generating non-pages).
            // See also: https://github.com/varabyte/kobweb/issues/301
            filter.set { this.route == "/" }
        }

        // Only legacy sites need this set. Sites built after 0.16.0 should default to DISALLOW.
        // See https://github.com/varabyte/kobweb#legacy-routes for more information.
        legacyRouteRedirectStrategy.set(LegacyRouteRedirectStrategy.DISALLOW)
    }
    markdown {
        defaultRoot.set(".components.layouts.SectionLayout")
    }
}

kotlin {
    configAsKobwebApplication("droidcon24")

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
        }

        jsMain.dependencies {
            implementation(compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            implementation(libs.kobwebx.markdown)
            // We use devNpm to pull down relevant reveal.js files locally. See `head.add` block above.
            implementation(devNpm("reveal.js", "5.1.0"))
        }
    }
}
