import hevs.graphics.FunGraphics
import hevs.graphics.utils.GraphicsBitmap
import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent}
import java.awt.Color

object Game_2048 extends App {
  val gameWindow = new FunGraphics(600, 600, "2048", true)
  val imageStop = new GraphicsBitmap("/res/stop.jpg")
  var pressedUp, pressedDown, pressedLeft, pressedRight = false
  var offsetY = 0
  var offsetX = 0
  var stop = false
  var factor = 20

  gameWindow.setKeyManager(new KeyAdapter() {

    override def keyPressed(e: KeyEvent): Unit = {
      e.getKeyChar match {
        case 'a' => pressedLeft = true
        case 'd' => pressedRight = true
        case 'w' => pressedUp = true
        case 's' => pressedDown = true
        case _ =>
      }
      e.getKeyCode match {
        case KeyEvent.VK_LEFT => pressedLeft = true
        case KeyEvent.VK_RIGHT => pressedRight = true
        case KeyEvent.VK_UP => pressedUp = true
        case KeyEvent.VK_DOWN => pressedDown = true
        case _ =>
      }
    }

    override def keyReleased(e: KeyEvent): Unit = {
      e.getKeyChar match {
        case 'a' => pressedLeft = false
        case 'd' => pressedRight = false
        case 'w' => pressedUp = false
        case 's' => pressedDown = false
        case _ =>
      }
      e.getKeyCode match {
        case KeyEvent.VK_LEFT => pressedLeft = false
        case KeyEvent.VK_RIGHT => pressedRight = false
        case KeyEvent.VK_UP => pressedUp = false
        case KeyEvent.VK_DOWN => pressedDown = false
        case _ =>
      }
    }

  })

  gameWindow.addMouseListener(new MouseAdapter() {
    override def mouseClicked(e: MouseEvent): Unit = {
      val event = e
      val posx = event.getX
      val posy = event.getY
      //      println("posx" + posx)
      //      println("posy" + posy)
      if (posy < 215 && posx < 200) stop = true
    }
  })

  while (true) {
    if (stop) System.exit(0)
    else {
      if (pressedDown) offsetY += 1
      if (pressedUp) offsetY -= 1
      if (pressedLeft) offsetX -= 1
      if (pressedRight) offsetX += 1
      gameWindow.frontBuffer.synchronized {
        gameWindow.clear(Color.blue)
        gameWindow.setColor(Color.red)
        gameWindow.drawRect(250 + offsetX * factor, 250 + offsetY * factor, 75, 75)
        gameWindow.drawTransformedPicture(100, 100, 0, 0.3, imageStop)
      }
      gameWindow.syncGameLogic(60)
    }
  }

}