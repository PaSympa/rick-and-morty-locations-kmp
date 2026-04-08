package fr.leandremru.rickandmortylocations.core.presentation

/**
 * MVI action that knows how to mutate a [Store] of a given [State] type.
 *
 * Implementations are typically `data class`es declared next to their store.
 * They are dispatched through [StoreViewModel.handleAction], which calls
 * [execute] which in turn invokes the [reduce] extension on the store.
 */
interface StoreAction<State, S : Store<State>> {
    fun execute(from: S) = with(this) { from.reduce() }
    fun S.reduce()
}
