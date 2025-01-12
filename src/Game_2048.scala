import java.awt.Color
import scala.util.Random
import hevs.graphics.FunGraphics
import hevs.graphics.utils.GraphicsBitmap
import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent, MouseMotionAdapter}

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

  // Game logic
  var gameState = "playing" // "playing", "menu", "win", "lose"
  var stop, isUpdating, win = false
  val stopSize = 50
  var incr = 0
  var tab: Array[Array[Case]] = Array.fill(gridSize, gridSize)(new Case)
  tab = getRandomCases(tab)
  tab = getRandomCases(tab)

  // To test the win easly
  //tab(0)(0).caseValue = 1024
  //tab(0)(3).caseValue = 1024

  // Images
  val tabMax = 2048
  val tabLength = (Math.log(tabMax * 2) / Math.log(2)).toInt
  val tabImages: Array[GraphicsBitmap] = new Array(tabLength)
  for (i <- 0 until tabLength) tabImages(i) = new GraphicsBitmap(s"/res/case${if (i == 0) 0 else Math.pow(2, i).toInt}.jpg")
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
    override def mousePressed(e: MouseEvent): Unit = {
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

    var check0 = false
    for (y <- tabValue.indices; x <- tabValue(y).indices) {
      if (tabValue(y)(x).caseValue == 0) check0 = true
    }
    if (!check0) check = true

    while (!check) {
      var randomX = Random.nextInt(gridSize) // Génère un nombre entre 0 et 3
      var randomY = Random.nextInt(gridSize) // Génère un nombre entre 0 et 3
      if (result(randomY)(randomX).caseValue == 0) {
        if (Random.nextInt(100) > 90) result(randomY)(randomX).caseValue = 4
        else result(randomY)(randomX).caseValue = 2
        check = true
      }
    }

    result
  }

  def checkGame(tabValue: Array[Array[Case]]): Int = {
    var result = 0 // lose(2), win(1) playing(0)

    var check2048, check0 = false
    for (y <- tabValue.indices; x <- tabValue(y).indices) {
      if (tabValue(y)(x).caseValue == 2048) check2048 = true
      if (tabValue(y)(x).caseValue == 0) check0 = true
    }

    if (check2048) result = 1
    else {
      if (!check0) {
        var fusionPossible = false
        for (y <- tabValue.indices; x <- tabValue(y).indices) {
          try {
            if (
              tabValue(y)(x).caseValue == tabValue(y + 1)(x).caseValue ||
                tabValue(y)(x).caseValue == tabValue(y)(x + 1).caseValue ||
                tabValue(y)(x).caseValue == tabValue(y - 1)(x).caseValue ||
                tabValue(y)(x).caseValue == tabValue(y)(x - 1).caseValue) {
              fusionPossible = true
            }
          }
          catch {
            case e: Exception =>
          }
        }
        if (!fusionPossible) result = 2
      }
    }
    result
  }

  def updateTab(tabValue: Array[Array[Case]]): Array[Array[Case]] = {
    var result: Array[Array[Case]] = tabValue.map(_.clone)
    if (incr <= 7) {
      direction match {
        case "down" =>
          if (incr <= 2 || incr > 3) {
            for (i <- 0 until gridSize; w <- (gridSize - 1) until 0 by -1) {
              if (result(w)(i).caseValue == 0) {
                result(w)(i).caseValue = result(w - 1)(i).caseValue
                result(w - 1)(i).caseValue = 0
              }
            }
          } else if (incr == 3) {
            for (i <- 0 until gridSize; w <- (gridSize - 1) until 0 by -1) {
              if (result(w)(i).caseValue == result(w - 1)(i).caseValue && !result(w)(i).hasFusionned && !result(w - 1)(i).hasFusionned) {
                result(w)(i).caseValue *= 2
                result(w)(i).hasFusionned = true
                result(w - 1)(i).caseValue = 0
              }
            }
          }

        case "up" =>
          if (incr <= 2 || incr > 3) {
            for (i <- 0 until gridSize; w <- 0 until (gridSize - 1)) {
              if (result(w)(i).caseValue == 0) {
                result(w)(i).caseValue = result(w + 1)(i).caseValue
                result(w + 1)(i).caseValue = 0
              }
            }
          } else if (incr == 3) {
            for (i <- 0 until gridSize; w <- 0 until (gridSize - 1)) {
              if (result(w)(i).caseValue == result(w + 1)(i).caseValue && !result(w)(i).hasFusionned && !result(w + 1)(i).hasFusionned) {
                result(w)(i).caseValue *= 2
                result(w)(i).hasFusionned = true
                result(w + 1)(i).caseValue = 0
              }
            }
          }

        case "left" =>
          if (incr <= 2 || incr > 3) {
            for (j <- 0 until gridSize; w <- 0 until (gridSize - 1)) {
              if (result(j)(w).caseValue == 0) {
                result(j)(w).caseValue = result(j)(w + 1).caseValue
                result(j)(w + 1).caseValue = 0
              }
            }
          } else if (incr == 3) {
            for (j <- 0 until gridSize; w <- 0 until (gridSize - 1)) {
              if (result(j)(w).caseValue == result(j)(w + 1).caseValue && !result(j)(w).hasFusionned && !result(j)(w + 1).hasFusionned) {
                result(j)(w).caseValue *= 2
                result(j)(w).hasFusionned = true
                result(j)(w + 1).caseValue = 0
              }
            }
          }

        case "right" =>
          if (incr <= 2 || incr > 3) {
            for (j <- 0 until gridSize; w <- (gridSize - 1) until 0 by -1) {
              if (result(j)(w).caseValue == 0) {
                result(j)(w).caseValue = result(j)(w - 1).caseValue
                result(j)(w - 1).caseValue = 0
              }
            }
          } else if (incr == 3) {
            for (j <- 0 until gridSize; w <- (gridSize - 1) until 0 by -1) {
              if (result(j)(w).caseValue == result(j)(w - 1).caseValue && !result(j)(w).hasFusionned && !result(j)(w - 1).hasFusionned) {
                result(j)(w).caseValue *= 2
                result(j)(w).hasFusionned = true
                result(j)(w - 1).caseValue = 0
              }
            }
          }

        case _ =>
      }
      incr += 1
    }
    else {
      incr = 0
      isUpdating = false
      direction = "none"
      for (y <- tabValue.indices; x <- tabValue(y).indices) result(y)(x).hasFusionned = false
      tab = getRandomCases(tab)
    }
    return result
  }

  def drawMenu(): Unit = {
    gameWindow.setColor(if (win) Color.green else Color.darkGray)
    gameWindow.drawFillRect(150, 150, 300, 300)
    val message = if (win) "You Win!" else "Game Over"
    gameWindow.drawFancyString(170, 300, message, "Arial", 1, 50, Color.white)
  }
  // -------------------------------------------------------------------------------------------------------------------


  // -----------------------------------------------------------------------------------------------------------GameLoop
  while (true) {
    if (stop) System.exit(0)
    else {
      gameWindow.frontBuffer.synchronized {
        gameState match {
          case "menu" =>
            drawBackground()
            drawTab(tab)
            drawMenu()

          case "playing" =>
            if (isUpdating) {
              tab = updateTab(tab)
              drawBackground()
              drawTab(tab)
            }
            else if (checkGame(tab) == 0) {
              if (pressedDown) direction = "down"
              if (pressedUp) direction = "up"
              if (pressedLeft) direction = "left"
              if (pressedRight) direction = "right"
              if (direction == "none") {
                drawBackground()
                drawTab(tab)
              } else isUpdating = true
            }
            else {
              gameState = if (checkGame(tab) == 2) "lose" else "win"
            }

          case "win" =>
            win = true
            gameState = "menu"

          case "lose" =>
            win = false
            gameState = "menu"
        }
      }
      gameWindow.syncGameLogic(60)
    }
  }
  // -------------------------------------------------------------------------------------------------------------------
}