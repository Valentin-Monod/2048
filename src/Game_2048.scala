import hevs.graphics.FunGraphics
import hevs.graphics.utils.GraphicsBitmap
import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent, MouseMotionAdapter}
import java.awt.Color
import scala.util.Random

class Case() {
  var caseValue = 0
  var step = 0
  var nextValue = 0
}

object Game_2048 extends App {
  // ----------------------------------------------------------------------------------------------------------Variables
  // Screen
  val widthScreen = 600
  val menuScreen = 100
  val heightScreen = widthScreen + menuScreen
  val gameWindow = new FunGraphics(widthScreen, heightScreen, "2048", true)

  // Cells
  val gridSize = 4
  val margin = 20
  val padding = 20
  val cellSize = (widthScreen - (2 * margin) - (gridSize * padding)) / gridSize
  val caseFactor = (cellSize.toDouble / 120.0)

  // Colors
  val backColor = new Color(187, 173, 160)
  val caseColor = new Color(202, 192, 180)
  val titleColor = new Color(117, 110, 101)
  var stopColor = Color.darkGray

  // Keyboard - Directions
  var pressedUp, pressedDown, pressedLeft, pressedRight = false
  var direction = "none"
  var isUpdating = false

  // Game
  var stop = false
  val stopSize = 50
  val tabMax = 2048
  val tabLength = (Math.log(tabMax * 2) / Math.log(2)).toInt
  val tabImages: Array[GraphicsBitmap] = new Array(tabLength)
  for (i <- 0 until tabLength) tabImages(i) = new GraphicsBitmap(s"/res/case${if (i == 0) 0 else Math.pow(2, i).toInt}.jpg")
  var tab: Array[Array[Case]] = Array.fill(gridSize, gridSize)(new Case)
  //tab = getRandomCases(tab)
  //tab = getRandomCases(tab)
  var x = 3
  var y = 0
  tab(y)(x).caseValue = 2
  var incr = 0
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

  def drawTab(tabValue: Array[Array[Case]]): Unit = {
    for (y <- tabValue.indices; x <- tabValue(y).indices) {
      gameWindow.drawTransformedPicture(margin + padding + (x * cellSize) + (x * padding) + ((cellSize - padding) / 2), menuScreen + margin + padding + (y * cellSize) + (y * padding) + ((cellSize - padding) / 2), 0, caseFactor, getImage(tabValue(y)(x).caseValue))
    }
  }

  def getRandomCases(tabValue: Array[Array[Case]]): Array[Array[Case]] = {
    var result: Array[Array[Case]] = tabValue.map(_.clone)
    var check = false
    while (!check) {
      var randomX = Random.nextInt(gridSize - 1)
      var randomY = Random.nextInt(gridSize - 1)
      if (result(randomX)(randomY).caseValue == 0) {
        if (Random.nextInt(100) > 90) result(randomX)(randomY).caseValue = 4
        else result(randomX)(randomY).caseValue = 2
        check = true
      }
    }
    result
  }

  def checkGame(tabValue: Array[Array[Case]]): Int = {
    var result = 0 // Check si le jeu est perdu (2), gagné (1) ou si on continu (0)
    var check2048, check0 = false
    for (y <- tabValue.indices; x <- tabValue(y).indices) {
      if (tabValue(y)(x).caseValue == 2048) check2048 = true
      if (tabValue(y)(x).caseValue == 0) check0 = true
    }
    if (check2048) result = 1
    else {
      if(!check0){
        var fusionPossible = false
        for (y <- tabValue.indices; x <- tabValue(y).indices) {
          try {
            if (tabValue(y)(x).caseValue == tabValue(y+1)(x).caseValue ||
              tabValue(y)(x).caseValue == tabValue(y)(x+1).caseValue ||
              tabValue(y)(x).caseValue == tabValue(y-1)(x).caseValue ||
              tabValue(y)(x).caseValue == tabValue(y)(x-1).caseValue){
              fusionPossible = true
            }
          }
          catch { case e : Exception => }
        }
        if (!fusionPossible) result = 2
      }
    }
    result
  }
  
  def updateTab(tabValue: Array[Array[Case]]): Array[Array[Case]] = {
    var result: Array[Array[Case]] = Array.fill(gridSize, gridSize)(new Case)
    if (incr < gridSize) {
      // TODO :
      direction match {
        case "down" => if (y < gridSize - 1) y += 1
        case "up" => if (y > 0) y -= 1
        case "left" => if (x > 0) x -= 1
        case "right" => if (x < gridSize - 1) x += 1
        case _ =>
      }
      incr += 1
    }
    else {
      incr = 0
      isUpdating = false
      //tab = getRandomCases(tab)
    }
    result(y)(x).caseValue = 2
    return result
  }

  def calculateNewFinalTab (tabValue: Array[Array[Case]]): Array[Array[Case]] = {
    var result: Array[Array[Case]] = Array.fill(gridSize, gridSize)(new Case)
    // TODO : calculer les distances et les valeurs finales
    direction match {
      case "down" =>
        for (y <- tabValue.indices; x <- tabValue(y).indices){
          for (w <- 0 to gridSize-y-1) {
            if (tabValue(w)(x).caseValue != 0){
              if (tabValue(w)(x).caseValue == tabValue(y)(x).caseValue){
                tabValue(y)(x)
              }
            }
            else tabValue(y)(x).step += 1
          }
        }
      case "up" =>
      case "left" =>
      case "right" =>
      case _ =>
    }
    result
  }

  def drawMenu(stateOfGame: Int): Unit = {
    // TODO : faire une fonction draw menu qui dit si gagné ou perdu et si on veut rejouer
    // aussi réinitialiser les variables si on rejoue
  }
  // -------------------------------------------------------------------------------------------------------------------


  // -----------------------------------------------------------------------------------------------------------GameLoop
  while (true) {
    if (stop) System.exit(0)
    else {
      gameWindow.frontBuffer.synchronized {
        if (checkGame(tab) == 0 || isUpdating) {
          if (!isUpdating) {
            if (pressedDown) direction = "down";
            if (pressedUp) direction = "up"
            if (pressedLeft) direction = "left"
            if (pressedRight) direction = "right"
            isUpdating = true
            //calculateNewFinalTab(tab)
          }
          tab = updateTab(tab)
          drawBackground()
          drawTab(tab)
        }
        else {
          drawBackground()
          drawMenu(checkGame(tab))
        }
      }
      gameWindow.syncGameLogic(60)
    }
  }
  // -------------------------------------------------------------------------------------------------------------------
}