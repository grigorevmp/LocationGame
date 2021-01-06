package com.mikhailgrigorev.game.core.fsm

class UndefinedStateException : Throwable()

class State <Arguments> (
    private var executer: () -> Unit
) {
    private class Action( private var action: ()-> Unit) {
        fun invoke() {
            action()
        }
    }

    private var transitions = ArrayList<Transition<Arguments>>()

    private var entryAction: Action? = null
    private var exitAction: Action? = null

    fun execute() {
        executer()
    }

    fun handle(arguments: Arguments): State<Arguments> {
        for (transition in transitions) {
            val newState = transition.handle(arguments)
            if (newState != null) {
                this.exit()
                newState.entry()
                return newState
            }
        }
        return this
    }

    fun addTransition(transition: Transition<Arguments>) {
        transitions.add(transition)
    }

    fun setEntryAction(action: () -> Unit) : State<Arguments> {
        entryAction = Action(action)
        return this
    }

    fun clearEntryAction() {
        entryAction = null
    }

    fun setExitAction(action: () -> Unit): State<Arguments> {
        exitAction = Action(action)
        return this
    }

    fun clearExitAction() {
        exitAction = null
    }

    fun entry(){
        entryAction?.invoke()
    }

    private fun exit() {
        exitAction?.invoke()
    }
}


class Transition <Arguments> (
    private var handler: (arguments: Arguments) -> State<Arguments>?
) {
    fun handle(arguments: Arguments): State<Arguments>? {
        return handler(arguments)
    }
}


class FSM <Arguments> {
    private var states =  ArrayList<State<Arguments>>()
    private var currentState: State<Arguments>? = null

    fun addState(state: State<Arguments>): State<Arguments> {
        states.add(state)
        return state
    }

    fun handle(arguments: Arguments) {
        currentState = currentState?.handle(arguments) ?: throw UndefinedStateException()
    }

    fun execute() {
        currentState?.execute() ?: throw UndefinedStateException()
    }

    fun setCurrentState(state: State<Arguments>) {
        currentState = state
        currentState?.entry()
    }
}