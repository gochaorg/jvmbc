package xyz.cofe.jvmbc.io

object FirstErr {
  def firstErr[E,T](source:Seq[Either[E,T]]):Either[E,List[T]] =
    source.foldLeft( Right(List()):Either[E,List[T]] )( (a,b) => { 
    a match 
      case Left(err) => a
      case Right(lst) =>
        b match
          case Left(err) => Left(err)
          case Right(ca) => a.map { lst => 
            ca +: lst
          }
  })
}
