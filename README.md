# reactiveneo

Reactive typesafe Scala DSL for Neo4j

* auto-gen TOC:
{:toc}

# Graph modelling

## Nodes

Domain model class
```
case class Person(name: String, age: Int)
```

Neo4j node definition
```
import com.websudos.reactiveneo._

class PersonNode extends Node[Person] {
  
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
