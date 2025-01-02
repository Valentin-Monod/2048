import hevs.graphics.FunGraphics
import hevs.graphics.utils.GraphicsBitmap
import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent}
import java.awt.Color

object Game_2048 extends App {
  val gameWindow = new FunGraphics(600, 600, "2048", true)
  val imageStop = new GraphicsBitmap("/res/stop.jpg")
  var offsetY = 0
  var offsetX = 0
  var stop = false
  var factor = 30

  gameWindow.setKeyManager(new KeyAdapter() {
    override def keyPressed(e: KeyEvent): Unit = {
      e.getKeyChar match {
        case 'a' => offsetX -= 1
        case 'd' => offsetX += 1
        case 'w' => offsetY -= 1
        case 's' => offsetY += 1
        case _ =>
      }
      e.getKeyCode match {
        case KeyEvent.VK_LEFT => offsetX -= 1
        case KeyEvent.VK_RIGHT => offsetX += 1
        case KeyEvent.VK_UP => offsetY -= 1
        case KeyEvent.VK_DOWN => offsetY += 1
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
      gameWindow.clear(Color.blue)
      gameWindow.setColor(Color.red)
      gameWindow.drawRect(250 + offsetX * factor, 250 + offsetY * factor, 75, 75)
      gameWindow.drawTransformedPicture(100, 100, 0, 0.3, imageStop)
      gameWindow.syncGameLogic(60)
    }
  }
}