package xyz.cofe.jvmbc

trait Nested extends Product:
  self =>

  private def debug(any:AnyRef)=println(any)
  
  def nested:List[Product]

  def walk:Iterator[List[Product]] = new {
    private var ws = List[List[Product]](List(self))
    def hasNext:Boolean = ws.nonEmpty
    def next():List[Product] =
      val res = ws.head
      ws = ws.tail
      res.head match
        case Some(n:Nested) =>
          val follows = n.nested.map { n => 
            n :: res
          }
          ws = follows ::: ws
        case nested:Nested =>
          val follows = nested.nested.map { n => 
            //if n.isInstanceOf[Seq[_]] then 
            n :: res
          }
          ws = follows ::: ws
        case _ =>
          if res.head.isInstanceOf[Seq[_]] then
            var ins = List[List[Product]]()
            res.head.asInstanceOf[Seq[_]].filter(_!=null).foreach { n =>
              n match
                case nested:Nested =>
                  //val in0 = nested.nested.map { u => u :: res }
                  val in0 = List(nested :: res)
                  ins = ins ::: in0
                case _ =>
            }
            ws = ins ::: ws
      res
  }

trait NestedAll extends Product with Nested:
  self =>

  private def debug(any:AnyRef)=
    {}
    //println(any)

  def nested:List[Product] =
    debug("NestedAll")
    val r = this.productIterator.filter { _.isInstanceOf[Product] }.map { _.asInstanceOf[Product] }.toList
    debug(s"res = $r")
    r

trait NestedThey(they:String*) extends Product with Nested:
  self =>

  private def debug(any:AnyRef)=
    {}
    //println(any)

  def nested:List[Product] =
    debug(s"NestedThey($they) $self")
    val r = this.productIterator.zip(this.productElementNames)
      .map { case (prod,name) => (prod,name, they.exists(t => t==name) && prod.isInstanceOf[Product] ) }
      .filter { case(prod,name,matched) => 
        debug(s"name $name matched $matched")
        matched 
      }
      .map { case(prod,name,matched) => prod.asInstanceOf[Product] }
      .toList
    debug(s"res = $r")
    r

trait NestedExcl(they:String*) extends Product with Nested:
  self =>

  private def debug(any:AnyRef)=
    //println(any)
    {}

  def nested:List[Product] =
    debug(s"NestedExcl($they) $self")
    val r = this.productIterator.zip(this.productElementNames)
      .map { case (prod,name) => (prod,name, !they.exists(t => t==name ) && prod.isInstanceOf[Product] ) }
      .filter { case(prod,name,matched) => 
        debug(s"name $name matched $matched")
        matched 
      }
      .map { case(prod,name,matched) => prod.asInstanceOf[Product] }
      .toList
    debug(s"res = $r")
    r