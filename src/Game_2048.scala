import hevs.graphics.FunGraphics
import hevs.graphics.utils.GraphicsBitmap
import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent}
import java.awt.Color

object Game_2048 extends App {

  val widthScreen = 600
  val menuScreen = 100
  val heightScreen = widthScreen + menuScreen
  val gridSize = 4
  val margin = 20
  val padding = 20
  val cellSize = (widthScreen - 2*margin) / gridSize
  val gameWindow = new FunGraphics(widthScreen, heightScreen, "2048", true)
  val imageStop = new GraphicsBitmap("/res/stop.jpg")

  var pressedUp, pressedDown, pressedLeft, pressedRight = false
  var direction = "none"
  var stop = false

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
      //println("posx" + posx); println("posy" + posy);
      if (posy < 100 && posx < 90) stop = true
    }
  })

  def background(): Unit = {
    gameWindow.clear(new Color(187, 173, 160)) // Fond en beige foncé
    gameWindow.drawTransformedPicture(300, 50, 0, 1, imageStop)
    gameWindow.setColor(new Color(205, 193, 180)) // Beige clair pour les cellules
    for (x <- 0 until gridSize; y <- 0 until gridSize) {
      gameWindow.drawFillRect(margin + (x * cellSize) + (padding / 2), menuScreen + margin + (y * cellSize) + (padding / 2), cellSize - padding, cellSize - padding)
    }
  }

  while (true) {
    if (stop) System.exit(0)
    else {
      if (pressedDown) direction = "down"
      if (pressedUp) direction = "up"
      if (pressedLeft) direction = "left"
      if (pressedRight) direction = "right"
      gameWindow.frontBuffer.synchronized {
        background()
      }
      gameWindow.syncGameLogic(60)
    }
  }
}