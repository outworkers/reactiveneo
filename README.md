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

## Connection

Prerequisite to making Neo4j requests is REST endpoint definition. This is achived using RestConnection class.

```
scala> implicit val service = RestConnection("localhost", 7474)
service: RestConnection
```

## Making requests

In this example all nodes of Person type are returned.
```
scala> val personNodes = Person().returns(case p ~~ _ => p).execute
personNodes: Future[Seq[Person]]
```

The strange construct in the returns function is execution of extractor in the pattern. Pattern defines set of objects
that participate in the query. The objects are nodes and relationships.

You can also query for specific attributes of a node.
```
scala> val personNames = Person().returns(case p ~~ _ => p.name).execute
personNames: Future[Seq[String]]
```

A query that involves attributes matching.
```
scala> val personNodes = Person(_.name := "Tom").returns(case p ~~ _ => p).execute
personNodes: Future[Seq[Person]]
```

Query for a person that has a relationship to another person
```
scala> val personNodes = (Person() :->: Person()).returns(case p1 ~~ _ => p).execute
personNodes: Future[Seq[Person]]
```

Query for a person that has a relationship to another person with given name
```
scala> val personNodes = (Person() :->: Person(_.name := "James")).returns(case p ~~ _ => p).execute
personNodes: Future[Seq[Person]]
```


Query for a person that has a relationship to another person
```
scala> val personNodes = (Person() :<-: WorkRelationship() :->: Person()).returns(case p1 ~~ r ~~ p2 ~~ _ => p1).execute
personNodes: Future[Seq[Person]]
```


Query for a person that has a relationship to another person with given name
```
scala> val personNodes = (Person() :-: WorkRelationship(_.company := "ABC") :->: Person(_.name := "John"))
                         .returns(case p1 ~~ _ => p1).execute
personNodes: Future[Seq[Person]]
```

## An arbitrary Cypher query
Cypher is a rich language and whenever you need to use it directly escaping the abstraction layer it's still possible
with ReactiveNeo. Use the same REST connection object with an arbitrary Cypher query.
```
scala> val query = "MATCH (n:Person) RETURN n"
query: String
implicit val parser: Reads[Person] = __.read[JsObject].map(jsobj => Person((jsobj \ "name").as[String], (jsobj \ "age").as[Int]))
parser: Reads[Int]
val result = service.makeRequest[Int](query).execute
result: Future[Seq[Int]]
```