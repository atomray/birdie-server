package ca.parcplace.birdie

import org.scalatra._
import scalate.ScalateSupport
import servlet.{MultipartConfig, SizeConstraintExceededException, FileUploadSupport}
import xml.Node

@javax.servlet.annotation.MultipartConfig
class BirdIdentService 
	extends ScalatraServlet
	with FileUploadSupport
	with FlashMapSupport {

  configureMultipartHandling(MultipartConfig(maxFileSize = Some(30*1024*1024)))

  def displayPage(content: Seq[Node]) = content

  error {
    case e: SizeConstraintExceededException =>
      RequestEntityTooLarge(displayPage(
        <p>The file you uploaded exceeded the 30 MB limit.</p>))
  }
  
  post("/") {
    fileParams.get("file") match {
      case Some(file) =>
        println(s"Got a file! ${file.size} bytes")
//        Ok(file.get(), Map(
//          "Content-Type"        -> (file.contentType.getOrElse("application/octet-stream")),
//          "Content-Disposition" -> ("attachment; filename=\"" + file.name + "\"")
//        ))
        Ok(<p>Thanks, come again dude</p>)

      case None =>
        BadRequest(displayPage(
          <p>
            Hey! You forgot to select a file. Dick.
          </p>))
    }
  }
}
