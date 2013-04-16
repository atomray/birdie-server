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
import java.io.File
import java.io.FileOutputStream
import com.musicg.wave.WaveFileManager

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
        Ok(MockBirdIdentService.identify(file.getInputStream))
      }

      case None =>
        BadRequest("Hey! You forgot to select a file.")
    }
  }
}

object MockBirdIdentService { 
  val birdies = List(new SuccessfulBirdIdentificationResponse("Phoebastria","immutabilis", "Laysan Albatross", "none"),
      new SuccessfulBirdIdentificationResponse("Setophaga","virens", "Black-throated Green Warbler", "none"),
      new SuccessfulBirdIdentificationResponse("Glaucidium","hardyi", "Amazonian Pygmy Owl", "none"))
  
  def identify(inStream: InputStream): BirdIdentificationResponse = {
    val index = Random.nextInt(birdies.length)
    birdies(index)
  }  
}

object BirdIdentService {
  
  var previousPrint: Array[Byte] = null
  
  def identify(inStream: InputStream): BirdIdentificationResponse = {
    val wave = new Wave(inStream)
    println("wave created - format: " + wave.getWaveHeader().getAudioFormat())
    println("wave created - bits per sample: " + wave.getWaveHeader().getBitsPerSample())
    println("wave created - sample rate: " + wave.getWaveHeader().getSampleRate())
    println("wave created - channels: " + wave.getWaveHeader().getChannels())
    println("wave created - block align: " + wave.getWaveHeader().getBlockAlign());
    println("wave created - byte rate: " + wave.getWaveHeader().getByteRate());

    new WaveFileManager(wave).saveWaveAsFile("/tmp/test.wav")
    
    val fileManager = new FingerprintManager()
    val fingerPrint = fileManager.extractFingerprint(wave)
    
    if (previousPrint == null) previousPrint = fingerPrint
    
    val sim = new FingerprintSimilarityComputer(previousPrint, fingerPrint).getFingerprintsSimilarity()
    
    SuccessfulBirdIdentificationResponse("the", "match", "is", sim.getSimilarity().toString)
  }
}