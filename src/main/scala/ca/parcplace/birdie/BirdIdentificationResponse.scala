package ca.parcplace.birdie

abstract class BirdIdentificationResponse

case class SuccessfulBirdIdentificationResponse(birdie: String, length: Long) extends BirdIdentificationResponse

case class UnsuccessfulBirdIdentificationResponse() extends BirdIdentificationResponse