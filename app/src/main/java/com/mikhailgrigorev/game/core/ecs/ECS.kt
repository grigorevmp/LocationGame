@file:Suppress("UNCHECKED_CAST")

package com.mikhailgrigorev.game.core.ecs

abstract class Component {
    private var _entity : Entity? = null

    fun getEntity() : Entity? {
        return _entity
    }

    /**
    * Not for using
    */
    fun __setEntity(entity: Entity){
        this._entity = entity
    }

    /**
    * Use to add component to parent entity
    * @param Component object 
    * @return added Component
    */
    fun <_Component : Component> addComponent(component: _Component) : _Component {
        _entity?.addComponent(component)
        return component
    }

    open fun action() {}
    open fun update() {}
}

open class Entity {
    private var _components : HashMap<Any, Component>

    constructor(){
        _components = HashMap()
    }
    constructor(entity: Entity){
        this._components = entity._components
    }

    /**
    * Use to add component
    * @param ComponentClassName allocated Component object
    * @return added Component, can to not add a component if there is one, then the component has entity = null
    */
    fun <_Component : Component> addComponent(component: _Component) : _Component {
        if(_components[component::class.java] == null){
            _components[component::class.java] = component
            component.__setEntity(this)
        }
        return component
    }

    /**
    * Use to remove component
    * @param Class ComponentClassName::class.java
    * @return removed Component or null if missing
    */   
    fun <_Component : Component> removeComponent(componentClass : Class<_Component>) : _Component? {
        val removingComponent = _components[componentClass]
        if(removingComponent != null){
            _components.remove(componentClass)
        }
        return removingComponent as _Component?
    }

    /**
    * Use to find out that component is available
    * @param Class ComponentClassName::class.java
    * @return Availability of the component
    */    
    fun <_Component : Component> hasComponent(componentClass : Class<_Component>): Boolean {
        return _components[componentClass] != null
    }

    /**
    * Use to get component
    * @param ComponentClassName::class.java 
    * @return Component or null if missing
    */
    fun <_Component : Component> getComponent(componentClass : Class<_Component>): _Component? {
        return _components[componentClass] as _Component?
    }

    open fun action() {}
    open fun update() {}
}