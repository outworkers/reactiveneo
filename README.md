# reactiveneo

Reactive typesafe Scala DSL for Neo4j

The library enforces strong type checks that imposes some restrictions on query format. Every node and relationship
used in the query needs to be defined and named.
E.g. this kind of query will not be supported:
```
MATCH (wallstreet { title:'Wall Street' })<-[r:ACTED_IN]-(actor)
RETURN r
```
Instead you will need to use proper labels for nodes to produce the following query:
```
MATCH (wallstreet:Movie { title:'Wall Street' })<-[r:ACTED_IN]-(actor:Actor)
RETURN r
```

# Graph modelling

## Nodes

Domain model class
```
case class Person(name: String, age: Int)
```

Reactiveneo node definition
```
import com.websudos.neo._

class PersonNode extends Node[PersonNode, Person] {
  
  object name extends StringNode with Index
  
  object name extends IntNode
  
  def fromNode(node: Node[Person]): Person = {
    Person(name, age)  
  }
  
}
```

When no custom mapping required
```
class PersonNode extends DefaultNode[Person]
```

## Relationships

```
class MyRelationship extends Relationship {
  
}

```

## Indexes



# Querying
```
match(node[Person]).where { p =>
  p.name === "Samantha"
}.return(p)
```

Multi node query
```
match(node[Person], node[Person]).where { case (p1, p2) =>
  p1.age === p2.age
}.return(p1, p2)
```
