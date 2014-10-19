import org.scalatest.FlatSpec
import shapeless._
import shapeless.ops.hlist.Prepend
class MatchQueryTest extends FlatSpec {
  it should "compile" in {
    val step1 = new TestedClass(1 :: HNil)
    val step2 = step1.addElement { case node :: HNil => 3.0 }
    val step3 = step2.addElement { case node1 :: node2 => node2 }
  }

}

class TestedClass[HL <: HList](nodes: HL) {

  def addElement[T, OUT](clause: HL => T)(implicit prepend: Prepend.Aux[HL, T :: HNil, OUT]) = {
    new TestedClass(prepend(nodes, clause(nodes):: HNil))
  }

}
