package ca.parcplace.birdie

import org.scalatra._
import scalate.ScalateSupport
import servlet.{MultipartConfig, SizeConstraintExceededException, FileUploadSupport}
import xml.Node
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

@javax.servlet.annotation.MultipartConfig
class BirdIdentService 
	extends ScalatraServlet
	with FileUploadSupport
	with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }
  
  error {
    case e: SizeConstraintExceededException =>
      RequestEntityTooLarge("The file you uploaded exceeded the unlimited limit. Somehow.")
  }
  
  post("/") {
    fileParams.get("file") match {
      case Some(file) =>
        Ok(new SuccessfulBirdIdentificationResponse("Bluejay", file.getSize))

      case None =>
        BadRequest("Hey! You forgot to select a file. Dick.")
    }
  }
}
