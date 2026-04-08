package fr.leandremru.rickandmortylocations.core.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Generic Compose ViewModel wrapping an MVI [Store].
 *
 * Concrete screens declare a `class XxxViewModel(store: XxxStore) :
 * StoreViewModel<XxxUiState, XxxStore, XxxAction>(store)`. The store is
 * provided by Koin and its coroutine scope is automatically cancelled when
 * the ViewModel is cleared.
 */
abstract class StoreViewModel<State, S : Store<State>, A : StoreAction<State, S>>(
    protected val store: S,
) : ViewModel() {

    init {
        addCloseable(store.storeScope)
    }

    val state: StateFlow<State> get() = store.state

    fun handleAction(action: A): Unit = action.execute(from = store)
}
