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
import com.websudos.reactiveneo.dsl._

class PersonNode extends Node[PersonNode, Person] {
  
  object name extends Attribute[String] with Index
  
  object age extends Attribute[Int]
  
  def fromRecord(record: NodeRecord): Person = {
    Person(record.value[name], record.value[age])  
  }
  
}
```


## Relationships

case class Studied

```
import com.websudos.reactiveneo.dsl._

class StudiedRelationship extends Relationship[StudiedRelationship, Studied] {
  
  object year extends Attribute[Int]

  def fromRecord(record: NodeRecord): Person = {
    Studied(record.value[year])  
  }
    
}

```

## Indexes



# Querying
```
matches[PersonNode](_.name := "Martin").
  inRelation(StudiedRelationship.any, PersonNode.criteria(name eq "Robert")).
  return( case(person1, rel, person2) => person1.name)
```
