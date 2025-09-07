package com.example.treine_me.storage

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface Store<T : Any> {
    val items: StateFlow<List<T>>
    fun get(id: Any): T?
    suspend fun set(list: List<T>)
    suspend fun set(item: T)
    suspend fun remove(ids: List<Any>)
    suspend fun clear()
}

class InMemoryStore<T : Any>(
    private val idSelector: (T) -> Any
) : Store<T> {
    private val _items = MutableStateFlow<List<T>>(emptyList())
    private val map = LinkedHashMap<Any, T>()

    override val items: StateFlow<List<T>> = _items.asStateFlow()

    override fun get(id: Any): T? = map[id]

    override suspend fun set(list: List<T>) {
        map.clear()
        list.forEach { map[idSelector(it)] = it }
        _items.value = map.values.toList()
    }

    override suspend fun set(item: T) {
        map[idSelector(item)] = item
        _items.value = map.values.toList()
    }

    override suspend fun remove(ids: List<Any>) {
        ids.forEach { map.remove(it) }
        _items.value = map.values.toList()
    }

    override suspend fun clear() {
        map.clear()
        _items.value = emptyList()
    }
}


