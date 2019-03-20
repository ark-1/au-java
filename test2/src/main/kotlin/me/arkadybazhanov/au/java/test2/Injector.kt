package me.arkadybazhanov.au.java.test2

import me.arkadybazhanov.au.java.test2.Injector.InjectorException.*

object Injector {

    fun initialize(rootClass: Class<*>, vararg implementations: Class<*>): Any {
        val rootClassName = rootClass.canonicalName
        val dependencies = mutableSetOf(rootClass)
        for (implementation in implementations) {
            dependencies += implementation.constructor.parameterTypes
        }

        val dependencyImplementations = dependencies.associate { dependencyType ->
            val dependency = dependencyType.canonicalName!!
            val suitable = implementations.filter { dependencyType.isAssignableFrom(it) }

            if (suitable.isEmpty()) {
                throw ImplementationNotFoundException(dependency)
            }

            dependency to (
                suitable.singleOrNull() ?: throw AmbiguousImplementationException(dependencyType.canonicalName)
            )
        }


        val instanceByImplementation = mutableMapOf<String, Any>()

        return inject(rootClassName, dependencyImplementations, instanceByImplementation, mutableSetOf())
    }

    private val Class<*>.constructor
        get() = constructors.singleOrNull()
            ?: throw NoSingleConstructorImplementationException(canonicalName)

    private fun inject(
        dependency: String,
        dependencyImplementations: Map<String, Class<*>>,
        instanceByImplementation: MutableMap<String, Any>,
        marked: MutableSet<String>
    ): Any {
        if (dependency in marked) {
            throw InjectionCycleException()
        }
        marked += dependency

        val implementation = dependencyImplementations.getValue(dependency)
        instanceByImplementation[implementation.canonicalName]?.let { return it }

        val constructor = implementation.constructor

        val parameterTypes = constructor.parameterTypes
        val arguments = arrayOfNulls<Any>(parameterTypes.size)

        for ((i, child) in parameterTypes.withIndex()) {
            arguments[i] = inject(
                child.canonicalName,
                dependencyImplementations,
                instanceByImplementation,
                marked
            )
        }

        return constructor.newInstance(*arguments).also {
            instanceByImplementation[implementation.canonicalName] = it
            marked -= dependency
        }
    }

    sealed class InjectorException(message: String) : IllegalArgumentException(message) {
        class AmbiguousImplementationException(dependency: String) : InjectorException(
            "Multiple implementations found for dependency '$dependency'"
        )

        class ImplementationNotFoundException(dependency: String) : InjectorException(
            "No implementations found for dependency '$dependency'"
        )

        class NoSingleConstructorImplementationException(implementation: String) : InjectorException(
            "Implementation '$implementation' should have a single public constructor"
        )

        class InjectionCycleException : InjectorException("Cyclic dependencies found")
    }
}
