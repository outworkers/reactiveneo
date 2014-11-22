# reactiveneo [![Build Status](https://travis-ci.org/websudos/reactiveneo.svg?branch=develop)](https://travis-ci.org/websudos/reactiveneo)

Reactive type-safe Scala DSL for Neo4j


# Table of contents

<ol>
  <li><a href="#graph-modelling">Graph modelling</a></li>
  <li><a href="#nodes">Nodes</a></li>
  <li><a href="#relationships">Relationships</a></li>
  <li><a href="#indexes">Indexes</a></li>
  <li><a href="#querying">Querying</a></li>
</ol>


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
<a href="#table-of-contents">Back to top</a>

## Nodes
<a href="#table-of-contents">Back to top</a>

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
<a href="#table-of-contents">Back to top</a>

Reactiveneo relationship definition
```
import com.websudos.reactiveneo.dsl._

class PersonRelation extends Relationship[PersonRelation, Person] {
  
  object name extends StringAttribute with Index
  
  object age extends IntegerAttribute
  
  def fromNode(data: QueryRecord): Person = {
    Person(name[String](data), age[Int](data))  
  }
  
}
```

## Indexes
<a href="#table-of-contents">Back to top</a>



# Querying
<a href="#table-of-contents">Back to top</a>

In this example all nodes of Person type are returned.
```
scala> val personNodes = Person.returns(p => p).execute
personNodes: Future[Seq[Person]]
```

You can also query for specific attributes of a node.
```
scala> val personNames = Person.returns(p => p.name).execute
personNames: Future[Seq[String]]
```

A query that involves attributes matching.
```
scala> val personNodes = Person( p => p.name := "Tom" ).returns(p => p).execute
personNodes: Future[Seq[Person]]
```

Query for a person that has a relationship to another person
```
scala> val personNodes = Person.relatedTo[Person].returns(p => p).execute
personNodes: Future[Seq[Person]]
```

Query for a person that has a relationship to another person with given name
```
scala> val personNodes = Person.relatedTo(Person(p => p.name := "James").returns(p => p).execute
personNodes: Future[Seq[Person]]
```


Query for a person that has a relationship to another person
```
scala> val personNodes = Person.relatedTo(WorkRelationship :-> Person).returns((p1,r,p2) => p1).execute
personNodes: Future[Seq[Person]]
```


Query for a person that has a relationship to another person with given name
```
scala> val personNodes = Person.relatedTo(WorkRelationship(r => r.company := "ABC") :-> Person(p => p.name := "John"))
                         returns((p1,r,p2) => p1).execute
personNodes: Future[Seq[Person]]
```
