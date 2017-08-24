package net.jtownson.swakka.routegen

import org.scalatest.{FlatSpec, Matchers}

class ProductExercise extends FlatSpec with Matchers {

  // here is a function that takes two Tuple2 parameters.
  // The first parameter is a Tuple2 of data such as ("A", 1)
  // The second parameter is a Tuple2 of functions (f1, f2).
  // Each function (f1, f2), maps from the corresponding data element in (a1, a2), to some other type (r1, r2).
  // The result of applying f1 and f2 is returned, also as a tuple2.
  // Here is the implementation...
  //  def tmap[A1,A2, R1, R2](t1: (A1, A2), t2: (A1=>R1, A2=>R2)): (R1, R2) =
  //    (t2._1(t1._1), t2._2(t1._2))

  // Problem: write a version of tmap that works for a tuple of any size, Tn (where n<=22)

  import shapeless._
  import ops.hlist._
  import syntax.std.tuple._

  // 1. Create a function that accepts any two tuples
  def tmap[P <: Product, F <: Product](p: P)(f: F): Int = 1

  "tmap" should "accept any tuple" in {
    tmap("a", 1)( (_: String) => "b", (i: Int) => i*2) shouldBe 1
  }

  // 2. Try with a slightly simpler syntax, exposing HList return type

  object fnApply extends Poly1 {
    implicit def caseFn1[T, R] = at[(T, (T) => R)]( {case (t, f) => f(t)})
  }

  def tmap2[P <: Product, PL <: HList, RL <: HList](p: P)
           (implicit pAux: Generic.Aux[P, PL],
            mapper: Mapper.Aux[fnApply.type, PL, RL]): RL = {
    val pl: PL = pAux.to(p)

    pl map fnApply
  }

  "tmap2" should "accept any tuple" in {
    tmap2(("a", (_: String) => "b"), (1, (i: Int) => i*2)) shouldBe "b" :: 2 :: HNil
  }

  // 3. Remove HList return type

  def tmap3[P <: Product, PL <: HList, RL <: HList, R <: Product](p: P)
           (implicit pAux: Generic.Aux[P, PL],
            mapper: Mapper.Aux[fnApply.type, PL, RL],
            tupler: Tupler[RL]): tupler.Out = {
    val pl: PL = pAux.to(p)

    val rl = pl map fnApply

    rl.tupled
  }

  "tmap3" should "accept any tuple" in {
    tmap3(("a", (_: String) => "b"), (1, (i: Int) => i*2)) shouldBe ("b", 2)
  }

  // 4. Separate data and function tuples
  def tmap4[P <: Product, PL <: HList, F <: Product, FL <: HList, RL <: HList, PLFL <: HList, R <: Product](p: P)(f: F)
           (implicit
            pAux: Generic.Aux[P, PL],
            fAux: Generic.Aux[F, FL],
            z: Zip.Aux[PL :: FL :: HNil, PLFL],
            mapper: Mapper.Aux[fnApply.type, PLFL, RL],
            tupler: Tupler[RL]): tupler.Out = {

    val pl: PL = pAux.to(p)
    val fl: FL = fAux.to(f)

    val plfl = pl zip fl

    val rl = plfl map fnApply

    rl.tupled
  }

  "tmap4" should "accept any tuple" in {
    tmap4("a", 1)((_: String) => "b", (i: Int) => i*2) shouldBe ("b", 2)
    tmap4("a", 1, 3)((_: String) => "b", (_: Int) * 2, (i: Int) => i*i) shouldBe ("b", 2, 9)
  }

}