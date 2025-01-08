import hevs.graphics.FunGraphics
import hevs.graphics.utils.GraphicsBitmap
import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent, MouseMotionAdapter}
import java.awt.Color

object Game_2048 extends App {
  // ----------------------------------------------------------------------------------------------------------Variables
  val widthScreen = 600
  val menuScreen = 100
  val heightScreen = widthScreen + menuScreen
  val gridSize = 4
  val margin = 20
  val padding = 20
  val cellSize = (widthScreen - (2 * margin) - (gridSize * padding)) / gridSize
  val caseFactor = (cellSize.toDouble / 120.0)
  val gameWindow = new FunGraphics(widthScreen, heightScreen, "2048", true)

  val tabMax = 2048
  val tabLength = (Math.log(tabMax * 2) / Math.log(2)).toInt

  val tabImages: Array[GraphicsBitmap] = new Array(tabLength)
  for (i <- 0 until tabLength) {
    tabImages(i) = new GraphicsBitmap(s"/res/case${if (i == 0) 0 else Math.pow(2, i).toInt}.jpg")
  }

  val backColor = new Color(187, 173, 160)
  val caseColor = new Color(202, 192, 180)
  val titleColor = new Color(117, 110, 101)
  var stopColor = Color.darkGray

  var pressedUp, pressedDown, pressedLeft, pressedRight = false
  var direction = "none"

  var stop = false
  val stopSize = 50

  var tab: Array[Array[Int]] = Array.ofDim(gridSize, gridSize)
  var x = 3
  var y = 0
  tab(y)(x) = 2
  // -------------------------------------------------------------------------------------------------------------------


  // -------------------------------------------------------------------------------------------------Mouse and Keyboard
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
      val posx = e.getX
      val posy = e.getY
      if (posy < stopSize && posx > widthScreen - stopSize) stop = true
    }
  })
  gameWindow.addMouseMotionListener(new MouseMotionAdapter() {
    override def mouseMoved(e: MouseEvent): Unit = {
      val posx = e.getX
      val posy = e.getY
      if (posy < stopSize && posx > widthScreen - stopSize) stopColor = Color.red
      else stopColor = Color.darkGray
      //println(s"Mouse : (X:$posx, Y:$posy)")
    }
  })
  // -------------------------------------------------------------------------------------------------------------------


  // ----------------------------------------------------------------------------------------------------------Fonctions
  def drawBackground(): Unit = {
    // Background
    gameWindow.clear(backColor)
    // Title
    gameWindow.drawFancyString(215, 85, "2048", "Arial Rounded MT Bold", 1, 70, titleColor)
    // Stop BTN
    gameWindow.setColor(stopColor)
    gameWindow.drawFillRect(widthScreen - stopSize, 0, stopSize, stopSize)
    gameWindow.setColor(Color.white)
    gameWindow.drawLine(widthScreen - stopSize, 0, widthScreen, stopSize)
    gameWindow.drawLine(widthScreen - stopSize, stopSize, widthScreen, 0)
    // Game Cells
    gameWindow.setColor(caseColor)
    for (x <- 0 until gridSize; y <- 0 until gridSize) {
      gameWindow.drawFillRect(margin + padding + (x * cellSize) + (x * padding), menuScreen + margin + padding + (y * cellSize) + (y * padding), cellSize - padding, cellSize - padding)
    }
  }

  def getImage(caseValue: Int): GraphicsBitmap = {
    var i = if (caseValue == 0) 0 else (Math.log(caseValue) / Math.log(2)).toInt
    return tabImages(i)
  }

  def drawTab(tabValue: Array[Array[Int]]): Unit = {
    for (y <- tabValue.indices; x <- tabValue(y).indices) {
      gameWindow.drawTransformedPicture(margin + padding + (x * cellSize) + (x * padding) + ((cellSize - padding) / 2), menuScreen + margin + padding + (y * cellSize) + (y * padding) + ((cellSize - padding) / 2), 0, caseFactor, getImage(tabValue(y)(x)))
    }
  }

  def updateTab(tabValue: Array[Array[Int]]): Array[Array[Int]] = {
    var result: Array[Array[Int]] = Array.ofDim(gridSize, gridSize)
    result(y)(x) = 2
    return result
  }
  // -------------------------------------------------------------------------------------------------------------------


  // -----------------------------------------------------------------------------------------------------------GameLoop
  while (true) {
    if (stop) System.exit(0)
    else {
      if (pressedDown) direction = "down"
      if (pressedUp) direction = "up"
      if (pressedLeft) direction = "left"
      if (pressedRight) direction = "right"
      direction match {
        case "down" => if (y < gridSize - 1) y += 1
        case "up" => if (y > 0) y -= 1
        case "left" => if (x > 0) x -= 1
        case "right" => if (x < gridSize - 1) x += 1
        case _ =>
      }

      tab = updateTab(tab)

      gameWindow.frontBuffer.synchronized {
        drawBackground()
        drawTab(tab)
      }

      gameWindow.syncGameLogic(60)
    }
  }
  // -------------------------------------------------------------------------------------------------------------------
}