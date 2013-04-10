package ca.parcplace.birdie

abstract class BirdIdentificationResponse

case class SuccessfulBirdIdentificationResponse(genus: String, species: String, englishName: String, subspecies: String)
	extends BirdIdentificationResponse

case class UnsuccessfulBirdIdentificationResponse() extends BirdIdentificationResponse