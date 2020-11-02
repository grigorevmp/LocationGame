# How to use
## Create some class with handling fsm arguments
```Kotlin
	class Args(var flag: Int, ...) {}
```
## Create FSM
```Kotlin
	var fsm = FSM<Args>()
```
## Create states
```Kotlin
	var state1 = fsm.addState(State{ println("state 1")})
    	var state2 = fsm.addState(State{ println("state 2")})
```
## Add transitions for the states
```Kotlin
	state1.addTransition(Transition { arguments ->
        	if (arguments.flag == 2) return@Transition state2
        	else return@Transition null
    	})

    state2.addTransition(Transition { arguments ->
        	if (arguments.flag == 1) return@Transition state1
        	else return@Transition null
    	})
```
## Set current state of the FSM
```Kotlin
	fsm.setCurrentState(state1)
```
## Use it
```Kotlin
	fsm.handle(Args(2))
    	fsm.execute()
    	fsm.handle(Args(1))
    	fsm.execute()
	...
```
