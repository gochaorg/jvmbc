package xyz.cofe.jvmbc.io.json

import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

extension (ba:Array[Byte])
  def toHexString():String =
    ba.flatMap( byte => 
      val hi = (byte & 0xFF) >> 4
      val lo = (byte & 0x0F)
      List(hi.toHexString(), lo.toHexString())
    ).mkString

extension (ser:Serializable)
  def toHexString():String =
    val ba = new ByteArrayOutputStream()
    val objOut = new ObjectOutputStream(ba)
    try
      objOut.writeObject(ser)
      ba.toByteArray().toHexString()
    finally
      objOut.close()

extension (string:String)
  def fromHexString():Either[String,Array[Byte]] =
    string.grouped(2).flatMap( str => 
      List(
        if str.length()!=2 
        then Left("string length not even (mod 2)")
        else 
          val hi = str.charAt(0)
          val lo = str.charAt(1)
          if "abcdefABCDEF0123456789".indexOf(s"$hi") < 0
          then Left(s"$hi not hex digit")
          else if "abcdefABCDEF0123456789".indexOf(s"$lo") < 0
            then Left(s"$lo not hex digit")
            else 
              val hiN = Integer.parseInt(s"$hi",16)
              val loN = Integer.parseInt(s"$lo",16)
              Right(((hiN << 4) | loN).toByte)
      )
    ).foldLeft( Right(List.empty[Byte]):Either[String,List[Byte]] ){ case (res,bEt) => 
      res.flatMap( lst => 
        bEt.map( b => lst :+ b )
      )
    }.map( lst => lst.toArray )

  def deSerialize():Either[String,Serializable] =
    fromHexString().map{ arrBytes => 
      val ba = new ByteArrayInputStream(arrBytes)
      val objIn = new ObjectInputStream(ba)
      try
        val ser = objIn.readObject()
        ser.asInstanceOf[java.io.Serializable]
      finally
        objIn.close()
    }

