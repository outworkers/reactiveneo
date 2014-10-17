# reactiveneo

Reactive typesafe Scala DSL for Neo4j

The library enforces strong type checks that imposes some restrictions on query format. Every node and relationship
used in the query needs to be defined and named.
E.g. this kind of query is not supported:
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
import com.websudos.reactiveneo.dsl._

class PersonNode extends Node[PersonNode, Person] {
  
  object name extends StringAttribute with Index
  
  object age extends IntegerAttribute
  
  def fromNode(data: QueryRecord): Person = {
    Person(name[String](data), age[Int](data))  
  }
  
}
```

## Relationships


## Indexes



# Querying

In this example all nodes of Person type are returned.
```
scala> val personNodes = matches[Person].return(p => p).execute
personNodes: Future[Seq[Person]]
```

You can also query for specific attributes of a node.
```
scala> val personNames = matches[Person].return(p => p.name).execute
personNames: Future[Seq[String]]
```
