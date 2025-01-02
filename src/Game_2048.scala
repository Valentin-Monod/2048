import hevs.graphics.FunGraphics
import java.awt.Color
import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent}

object Game_2048 extends App {

  val gameWindow = new FunGraphics(600, 600, "2048")
  var offsetY = 0
  var offsetX = 0
  var stop = false

  gameWindow.setKeyManager(new KeyAdapter() {
    override def keyPressed(e: KeyEvent): Unit = {
      e.getKeyChar match {
        case 'a' => offsetX -= 10
        case 'd' => offsetX += 10
        case 'w' => offsetY -= 10
        case 's' => offsetY += 10
        case _ =>
      }
      e.getKeyCode match {
        case KeyEvent.VK_LEFT => offsetX -= 10
        case KeyEvent.VK_RIGHT => offsetX += 10
        case KeyEvent.VK_UP => offsetY -= 10
        case KeyEvent.VK_DOWN => offsetY += 10
        case _ =>
      }
    }
  })

  gameWindow.addMouseListener(new MouseAdapter() {
    override def mouseClicked(e: MouseEvent): Unit = {
      val event = e
      val posx = event.getX
      val posy = event.getY
      stop = true
    }
  })

  while (true) {
    if (stop)System.exit(0)
    else {
      gameWindow.clear(Color.blue)
      gameWindow.setColor(Color.red)
      gameWindow.drawRect(50 + offsetX, 50 + offsetY, 75, 75)
      gameWindow.syncGameLogic(60)
    }
  }
}