package ca.parcplace.birdie

import org.scalatra._
import scalate.ScalateSupport
import servlet.{MultipartConfig, SizeConstraintExceededException, FileUploadSupport}
import xml.Node
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import scala.util.Random
import com.musicg.fingerprint.FingerprintManager
import com.musicg.wave.Wave
import java.io.InputStream
import com.musicg.fingerprint.FingerprintSimilarityComputer

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
      case Some(file) => {
        println("received file: " + file)
        Ok(BirdIdentService.identify(file.getInputStream))
      }

      case None =>
        BadRequest("Hey! You forgot to select a file.")
    }
  }
}

object BirdIdentService {
  
  var previousPrint: Array[Byte] = null
  
  def identify(inStream: InputStream): BirdIdentificationResponse = {
    val wave = new Wave(inStream)
    
    val fileManager = new FingerprintManager()
    val fingerPrint = fileManager.extractFingerprint(wave)
    
    if (previousPrint == null) previousPrint = fingerPrint
    
    val sim = new FingerprintSimilarityComputer(previousPrint, fingerPrint).getFingerprintsSimilarity()
    
    SuccessfulBirdIdentificationResponse("the", "match", "is", sim.getSimilarity().toString)
  }
}