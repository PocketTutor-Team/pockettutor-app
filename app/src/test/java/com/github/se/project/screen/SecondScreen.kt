package com.android.project.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.github.se.project.resources.C
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SecondScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SecondScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag(C.Tag.second_screen_container) }) {

  val simpleText: KNode = child { hasTestTag(C.Tag.greeting_robo) }
}
