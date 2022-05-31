package xyz.cofe.jvmbc

trait Nested extends Product:
  self =>

  def nested:List[Product]=
    unpack(
      this.productIterator.zip(this.productElementNames)
        .filter { case(prod,name) => isNestedItem(name) }
    )

  def isNestedItem(name:String):Boolean

  def walk:Iterator[List[Product]] = new {
    private var ws = List[List[Product]](List(self))
    def hasNext:Boolean = ws.nonEmpty
    def next():List[Product] =
      val res = ws.head
      ws = ws.tail
      res.head match
        case nested:Nested =>
          val follows = nested.nested.map { n => 
            n :: res
          }
          ws = follows ::: ws
        case _ => 
      res
  }

  protected def unpack( seq:Iterator[(Any,String)] )=
    seq.filter { case(prod,name) => prod match 
        case None => false
        case _ => true
      }
      .map { case(prod,name) => prod match 
        case Some(prod) => (prod,name)
        case _ => (prod,name)
      }
      .filter { case(prod,name) => prod.isInstanceOf[Product] }
      .map { case(prod,name) => prod.asInstanceOf[Product] }
      .flatMap { prod => 
        prod match
          case seq@Seq(_*) =>
            seq.filter { _.isInstanceOf[Product] }.map { _.asInstanceOf[Product] }.toList
          case _ => List(prod)
      }
      .toList

trait NestedAll extends Product with Nested:
  self =>
  def isNestedItem(name:String):Boolean = true

trait NestedThey(they:String*) extends Product with Nested:
  self =>
  def isNestedItem(name:String):Boolean = they.exists(t=>t==name)

trait NestedExcl(they:String*) extends Product with Nested:
  self =>
  def isNestedItem(name:String):Boolean = !they.exists(t=>t==name)
