package fr.leandremru.rickandmortylocations.core.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Base MVI store.
 *
 * Holds an immutable [State] in a [StateFlow], runs side effects on a
 * dedicated coroutine scope, and exposes the helpers actions use to mutate
 * the state from inside their `reduce()` extension. Concrete stores take
 * their domain dependencies (repositories, managers…) as constructor
 * parameters; actions reach them through the `this` reference inside `reduce()`.
 *
 * The store scope uses `Dispatchers.Main.immediate` so reducer blocks run
 * synchronously when dispatched from the main thread, and is cancelled by
 * [StoreViewModel] when the wrapping ViewModel is cleared.
 */
abstract class Store<State>(
    initialState: State,
    val storeScope: CloseableCoroutineScope = CloseableCoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate,
    ),
) {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> get() = _state

    /** Mutates the state immutably. Call from inside an action's `reduce()`. */
    fun Store<State>.updateState(block: State.() -> State) {
        _state.update { block(it) }
    }

    /**
     * Launches [source] on the store scope and forwards the wrapped [Result]
     * to [onResult] on the main thread, where state mutations should happen.
     */
    fun <T> Store<State>.fetchData(
        source: suspend () -> T,
        onResult: Result<T>.() -> Unit,
    ) {
        storeScope.launch(Dispatchers.Default) {
            try {
                val data = source()
                launch(Dispatchers.Main) { onResult(Result.success(data)) }
            } catch (e: Throwable) {
                launch(Dispatchers.Main) { onResult(Result.failure(e)) }
            }
        }
    }
}

/** A [CoroutineScope] that can be cancelled via [AutoCloseable.close]. */
class CloseableCoroutineScope(
    override val coroutineContext: CoroutineContext,
) : AutoCloseable, CoroutineScope {
    override fun close() = coroutineContext.cancel()
}
