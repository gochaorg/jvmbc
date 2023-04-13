package xyz.cofe.jvmbc.io.json.bin

import xyz.cofe.jvmbc.io.json.given
import java.io.File
import xyz.cofe.jvmbc.io.ByteCodeIO
import xyz.cofe.json4s3.stream.ast.FormattingJson
import xyz.cofe.json4s3.derv.*
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.FileInputStream
import java.io.InputStreamReader
import xyz.cofe.jvmbc.cls.CBegin
import xyz.cofe.jvmbc.io.toBytes

object JsonIOMain:
  enum Output:
    case StdOut
    case ToFile(fileName:String)

  case class Args(
    actions: List[Args=>Unit] = List.empty,
    output: Output = Output.StdOut,
  )

  def main(argsArr: Array[String]):Unit =
    System.err.println("jclass-json")

    val (_,args) = argsArr.foldLeft(
      ("", Args())
    ){ case ((state,args), arg) => 
      state match {
        case "" => arg match {
          case "-o" => 
            ("-o", args)            
          case _ => 
            val file = File(arg)
            if file.isFile()
            then
              if file.getName().toLowerCase().endsWith(".class") then
                ("", args.copy( actions = args.actions :+ parseJavaClass(file) ))
              else if file.getName().toLowerCase().endsWith(".json") then
                ("", args.copy( actions = args.actions :+ compileJson(file) ))
              else
                System.err.println(s"undefined action for file ${file}")
                ("", args)
            else
              System.err.println(s"undefined arg ${arg}")
              ("", args)
        }
        case "-o" =>
          ("", args.copy(output = Output.ToFile(arg)))
      }
    }

    args.actions.foreach( action => action(args) )

  def parseJavaClass( file:File )( args:Args ):Unit = {  
    args.output match
      case Output.StdOut => 
        parseJavaClass(file, System.out)
      case Output.ToFile(fileName) =>
        val fileOut = new FileOutputStream(fileName)
        val textOut = new OutputStreamWriter(fileOut, StandardCharsets.UTF_8)
        parseJavaClass(file, textOut)
        textOut.flush()
        textOut.close()
  }

  def parseJavaClass( file:File, out:Appendable ):Unit = {
    ByteCodeIO.parse(file) match
      case Left(err) => 
        System.err.println(s"parse error: $err")
      case Right(cbegin) =>
        implicit val fmt = FormattingJson.pretty(true)
        val json = cbegin.json
        out.append(json)
  }

  def compileJson( file:File )( args:Args ):Unit = {
    args.output match
      case Output.StdOut => 
      case Output.ToFile(fileName) =>
        val fout = new FileOutputStream(fileName)
        compileJson(file, fout)
        fout.flush()
        fout.close()
  }

  def compileJson( file:File, out:OutputStream ):Unit = {
    val fileStream = new FileInputStream(file)
    val textStream = new InputStreamReader(fileStream, StandardCharsets.UTF_8)
    val sb = new java.lang.StringBuilder()
    val buff = new Array[Char](1024*8)
    while {
      val reads = textStream.read(buff)
      if reads>0 then sb.append(buff,0,reads)
      reads > 0
    } do {
    }
    textStream.close()
    compileJson( sb.toString(), out )
  }

  def compileJson( json:String, out:OutputStream ):Unit = {
    json.jsonAs[CBegin] match
      case Left(err) => System.err.println(s"parse json err: $err")
      case Right(cb) =>
        out.write(cb.toBytes)
  }
