package fr.leandremru.rickandmortylocations.presentation.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Single source of truth for navigation requests.
 *
 * Stores, ViewModels and screens call [navigate] / [navigateBack] from
 * anywhere in the app. The events are collected by [AppNavHost], which
 * applies them to the active back stack.
 */
object AppNavigator {

    private val _events = MutableSharedFlow<NavEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<NavEvent> = _events.asSharedFlow()

    /** Push the given [route] onto the back stack. */
    fun navigate(route: AppRoute) {
        _events.tryEmit(NavEvent.GoTo(route))
    }

    /** Pop the current entry from the back stack. */
    fun navigateBack() {
        _events.tryEmit(NavEvent.Back)
    }
}

/** One-shot navigation events emitted by [AppNavigator] and consumed by [AppNavHost]. */
sealed interface NavEvent {
    data class GoTo(val route: AppRoute) : NavEvent
    data object Back : NavEvent
}
