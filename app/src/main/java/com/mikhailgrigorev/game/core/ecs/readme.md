# How to use
## Create some components
`Kotlin
	class HealthComponent(var healthValue: Int) : Component() {
		fun applyDamage(vlaue: Int) {...}
	}
`
## Inherit from Entity class
`
Kotlin
	class Player : Entity {...}
`
## Create instance and use
`Kotlin
	var player = Player()
	// adding component
	player.addComponent(HealthComponent(100))
	// check for a component
	if (player.hasComponent(HealthComponent::class.java)) {
		// get component
		player.getComponent(HealthComponent::class.java)?.applyDamage(50)
	}
	// remove component
	player.removeComponent(HealthComponent::class.java)
`