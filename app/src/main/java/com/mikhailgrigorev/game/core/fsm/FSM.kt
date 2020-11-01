package com.mikhailgrigorev.game.core.fsm

class UndefinedStateException : Throwable()

class State <ArgumentsPack> (
    private var executer: () -> Unit
) {
    private var transitions = ArrayList<Transition<ArgumentsPack>>()

    fun execute() {
        executer()
    }

    fun handle(argumentsPack: ArgumentsPack): State<ArgumentsPack> {
        for (transition in transitions) {
            val newState = transition.handle(argumentsPack)
            if (newState != null) return newState
        }
        return this
    }

    fun addTransition(transition: Transition<ArgumentsPack>){
        transitions.add(transition)
    }
}


class Transition <ArgumentsPack> (
    private var states: ArrayList<State<ArgumentsPack>>,
    private var handler: (ArgumentsPack, ArrayList<State<ArgumentsPack>>) -> State<ArgumentsPack>?
) {
    fun handle(argumentsPack: ArgumentsPack): State<ArgumentsPack>? {
        return handler(argumentsPack, states)
    }
}


class FSM <ArgumentsPack> {
    private var states =  ArrayList<State<ArgumentsPack>>()
    private var currentState: State<ArgumentsPack>? = null

    fun addState(state: State<ArgumentsPack>): State<ArgumentsPack> {
        states.add(state)
        return state
    }

    fun handle(argumentsPack: ArgumentsPack) {
        currentState?.handle(argumentsPack) ?: throw UndefinedStateException()
    }

    fun execute() {
        currentState?.execute() ?: throw UndefinedStateException()
    }

    fun setCurrentState(state: State<ArgumentsPack>) {
        currentState = state
    }
}