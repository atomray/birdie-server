package ca.parcplace.birdie

import org.scalatra._
import scalate.ScalateSupport

class BirdIdentService extends BirdSongIdentificationServiceStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate Adam</a>.
      </body>
    </html>
  }
  
}
