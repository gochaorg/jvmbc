package xyz.cofe.jvmbc.io

import xyz.cofe.jvmbc.parse.desc.{ObjectType => JavaName}

class MyClassLoader( byteCodeOf:JavaName=>Option[Array[Byte]] ) extends ClassLoader("myClassloader",classOf[MyClassLoader].getClassLoader()):
  override def findClass(name: String):Class[?] =
    if name!=null then byteCodeOf(JavaName.java(name)) match
      case None => super.findClass(name)
      case Some(bytes) => defineClass(bytes,0,bytes.length)
    else
      super.findClass(name)

