package xyz.cofe.jvmbc
package io

import cls.CBegin

trait ByteCodeIO[ERR] {
  def parse(bytes:Array[Byte]):Either[ERR,CBegin]
}

object ByteCodeIO {
  
}
