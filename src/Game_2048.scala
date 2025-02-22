import hevs.graphics.FunGraphics
import hevs.graphics.utils.GraphicsBitmap

import java.awt.Color
import java.awt.event._
import scala.util.Random

object Game_2048 extends App {
  // ----------------------------------------------------------------------------------------------------------Variables
  // Window Settings
  val widthScreen = 600
  val menuScreen = 100
  val heightScreen = widthScreen + menuScreen
  val gameWindow = new FunGraphics(widthScreen, heightScreen, "2048", true)

  // Fixed colors
  val gameBackColor = new Color(187, 173, 160)
  val menuBackColor = new Color(187, 173, 160, 208)
  val caseColor = new Color(202, 192, 180)
  val titleColor = new Color(117, 110, 101)
  val neutralColor = new Color(117, 110, 101)
  val winColor = new Color(67, 125, 81)
  val newGameBtnColorNormal = new Color(242, 177, 121)
  val newGameBtnColorHover = new Color(199, 146, 99)
  val leaveBtnColorHover = new Color(217, 108, 108)
  val homeBtnColorNormal = new Color(242, 177, 121)
  val homeBtnColorHover = new Color(199, 146, 99)

  // Images
  val tabMax = 2048
  val tabLength = (Math.log(tabMax * 2) / Math.log(2)).toInt
  val tabImages: Array[GraphicsBitmap] = new Array(tabLength)
  for (i <- 0 until tabLength) tabImages(i) = new GraphicsBitmap(s"/res/case${if (i == 0) 0 else Math.pow(2, i).toInt}.jpg")

  // Game Settings
  var menuBtnSize = 50
  var margin = 20
  var gridSize = 4
  var winNumber = 2048

  // Cells
  var padding = 80 / gridSize
  var cellSize = (widthScreen - (2 * margin) - (gridSize * padding)) / gridSize
  var caseFactor = (cellSize.toDouble / 120.0)

  // Variables colors for the buttons
  var leaveBtnColor = caseColor
  var homeBtnColor = homeBtnColorNormal
  var newGameBtnColor = newGameBtnColorNormal
  var btnMenuColor = titleColor
  var grid4Color = newGameBtnColorNormal
  var grid8Color = caseColor
  var grid10Color = caseColor
  var grid16Color = caseColor
  var grid20Color = caseColor
  var goal128Color = caseColor
  var goal256Color = caseColor
  var goal512Color = caseColor
  var goal1024Color = caseColor
  var goal2048Color = newGameBtnColorNormal

  // Keyboard
  var pressedUp, pressedDown, pressedLeft, pressedRight = false

  // Game logic
  var gameState = "start" // "playing", "menu", "win", "lose", "start"
  var direction = "none" // direction pressed by the user on the keyboard
  var isUpdating, win, start = false // updating the tab or wait for a direction, win or lose the game, start or not the game
  var incr = 0 // increment use for updating the tab
  var tab: Array[Array[Case]] = _ // array of Case use for the grid logic
  // -------------------------------------------------------------------------------------------------------------------


  // -------------------------------------------------------------------------------------------------Mouse and Keyboard
  // Detect if the user press a key
  gameWindow.setKeyManager(new KeyAdapter() {
    override def keyPressed(e: KeyEvent): Unit = {
      // Detect A, S, D, W
      e.getKeyChar match {
        case 'a' => pressedLeft = true
        case 'd' => pressedRight = true
        case 'w' => pressedUp = true
        case 's' => pressedDown = true
        case _ =>
      }
      // Detect the arrows
      e.getKeyCode match {
        case KeyEvent.VK_LEFT => pressedLeft = true
        case KeyEvent.VK_RIGHT => pressedRight = true
        case KeyEvent.VK_UP => pressedUp = true
        case KeyEvent.VK_DOWN => pressedDown = true
        case _ =>
      }
    }

    override def keyReleased(e: KeyEvent): Unit = {
      // Detect A, S, D, W
      e.getKeyChar match {
        case 'a' => pressedLeft = false
        case 'd' => pressedRight = false
        case 'w' => pressedUp = false
        case 's' => pressedDown = false
        case _ =>
      }
      // Detect the arrows
      e.getKeyCode match {
        case KeyEvent.VK_LEFT => pressedLeft = false
        case KeyEvent.VK_RIGHT => pressedRight = false
        case KeyEvent.VK_UP => pressedUp = false
        case KeyEvent.VK_DOWN => pressedDown = false
        case _ =>
      }
    }
  })

  // Detect if the mouse is pressed
  gameWindow.addMouseListener(new MouseAdapter() {
    override def mousePressed(e: MouseEvent): Unit = {
      val posx = e.getX
      val posy = e.getY
      gameState match { // We look the position of the mouse according to the game state
        case "start" =>
          // New Game Button
          if (posy < 422 && posy > 305 && posx < 450 && posx > 150) {
            padding = 80 / gridSize
            cellSize = (widthScreen - (2 * margin) - (gridSize * padding)) / gridSize
            caseFactor = (cellSize.toDouble / 120.0)
            tab = Array.fill(gridSize, gridSize)(new Case)
            tab = getRandomCases(tab)
            tab = getRandomCases(tab)
            gameState = "playing"
            start = true
          }

          // Leave button
          if (posy < 570 && posy > 450 && posx < 450 && posx > 150) System.exit(0)

          // Goals buttons
          if (posy < 170 && posy > 130 && posx < 200 && posx > 160) {
            goal128Color = newGameBtnColorNormal
            goal256Color = caseColor
            goal512Color = caseColor
            goal1024Color = caseColor
            goal2048Color = caseColor
            winNumber = 128
          }
          if (posy < 170 && posy > 130 && posx < 260 && posx > 220) {
            goal128Color = caseColor
            goal256Color = newGameBtnColorNormal
            goal512Color = caseColor
            goal1024Color = caseColor
            goal2048Color = caseColor
            winNumber = 256
          }
          if (posy < 170 && posy > 130 && posx < 320 && posx > 280) {
            goal128Color = caseColor
            goal256Color = caseColor
            goal512Color = newGameBtnColorNormal
            goal1024Color = caseColor
            goal2048Color = caseColor
            winNumber = 512
          }
          if (posy < 170 && posy > 130 && posx < 380 && posx > 340) {
            goal128Color = caseColor
            goal256Color = caseColor
            goal512Color = caseColor
            goal1024Color = newGameBtnColorNormal
            goal2048Color = caseColor
            winNumber = 1024
          }
          if (posy < 170 && posy > 130 && posx < 440 && posx > 400) {
            goal128Color = caseColor
            goal256Color = caseColor
            goal512Color = caseColor
            goal1024Color = caseColor
            goal2048Color = newGameBtnColorNormal
            winNumber = 2048
          }

          // Grid buttons
          if (posy < 245 && posy > 205 && posx < 200 && posx > 160) {
            grid4Color = newGameBtnColorNormal
            grid8Color = caseColor
            grid10Color = caseColor
            grid16Color = caseColor
            grid20Color = caseColor
            gridSize = 4
          }
          if (posy < 245 && posy > 205 && posx < 260 && posx > 220) {
            grid4Color = caseColor
            grid8Color = newGameBtnColorNormal
            grid10Color = caseColor
            grid16Color = caseColor
            grid20Color = caseColor
            gridSize = 8
          }
          if (posy < 245 && posy > 205 && posx < 320 && posx > 280) {
            grid4Color = caseColor
            grid8Color = caseColor
            grid10Color = newGameBtnColorNormal
            grid16Color = caseColor
            grid20Color = caseColor
            gridSize = 10
          }
          if (posy < 245 && posy > 205 && posx < 380 && posx > 340) {
            grid4Color = caseColor
            grid8Color = caseColor
            grid10Color = caseColor
            grid16Color = newGameBtnColorNormal
            grid20Color = caseColor
            gridSize = 16
          }
          if (posy < 245 && posy > 205 && posx < 440 && posx > 400) {
            grid4Color = caseColor
            grid8Color = caseColor
            grid10Color = caseColor
            grid16Color = caseColor
            grid20Color = newGameBtnColorNormal
            gridSize = 20
          }

        case "menu" =>
          // Menu button
          if (posy < menuBtnSize && posx > widthScreen - menuBtnSize) gameState = "playing"
          // New game button
          if (posy < 422 && posy > 305 && posx < 450 && posx > 150) restart()
          // Home button
          if (posy < 570 && posy > 450 && posx < 450 && posx > 150) {
            restart()
            gameState = "start"
          }

        case "playing" =>
          // Menu button
          if (posy < menuBtnSize && posx > widthScreen - menuBtnSize) gameState = "menu"
      }
    }
  })

  // Detect the position of the mouse to change button colors
  gameWindow.addMouseMotionListener(m = new MouseMotionAdapter() {
    override def mouseMoved(e: MouseEvent): Unit = {
      val posx = e.getX
      val posy = e.getY
      gameState match { // We look the position of the mouse according to the game state
        case "start" =>
          // New Game Button
          if (posy < 422 && posy > 305 && posx < 450 && posx > 150) newGameBtnColor = newGameBtnColorHover
          else newGameBtnColor = newGameBtnColorNormal

          // Leave Button
          if (posy < 570 && posy > 450 && posx < 450 && posx > 150) leaveBtnColor = leaveBtnColorHover
          else leaveBtnColor = caseColor

          // Goal buttons
          if (goal128Color != newGameBtnColorNormal) {
            if (posy < 170 && posy > 130 && posx < 200 && posx > 160) goal128Color = Color.white
            else goal128Color = caseColor
          }
          if (goal256Color != newGameBtnColorNormal) {
            if (posy < 170 && posy > 130 && posx < 260 && posx > 220) goal256Color = Color.white
            else goal256Color = caseColor
          }
          if (goal512Color != newGameBtnColorNormal) {
            if (posy < 170 && posy > 130 && posx < 320 && posx > 280) goal512Color = Color.white
            else goal512Color = caseColor
          }
          if (goal1024Color != newGameBtnColorNormal) {
            if (posy < 170 && posy > 130 && posx < 380 && posx > 340) goal1024Color = Color.white
            else goal1024Color = caseColor
          }
          if (goal2048Color != newGameBtnColorNormal) {
            if (posy < 170 && posy > 130 && posx < 440 && posx > 400) goal2048Color = Color.white
            else goal2048Color = caseColor
          }

          // Grid buttons
          if (grid4Color != newGameBtnColorNormal) {
            if (posy < 245 && posy > 205 && posx < 200 && posx > 160) grid4Color = Color.white
            else grid4Color = caseColor
          }
          if (grid8Color != newGameBtnColorNormal) {
            if (posy < 245 && posy > 205 && posx < 260 && posx > 220) grid8Color = Color.white
            else grid8Color = caseColor
          }
          if (grid10Color != newGameBtnColorNormal) {
            if (posy < 245 && posy > 205 && posx < 320 && posx > 280) grid10Color = Color.white
            else grid10Color = caseColor
          }
          if (grid16Color != newGameBtnColorNormal) {
            if (posy < 245 && posy > 205 && posx < 380 && posx > 340) grid16Color = Color.white
            else grid16Color = caseColor
          }
          if (grid20Color != newGameBtnColorNormal) {
            if (posy < 245 && posy > 205 && posx < 440 && posx > 400) grid20Color = Color.white
            else grid20Color = caseColor
          }

        case "menu" =>
          // Menu button
          if (posy < menuBtnSize && posx > widthScreen - menuBtnSize) btnMenuColor = caseColor
          else btnMenuColor = titleColor

          // New game button
          if (posy < 422 && posy > 305 && posx < 450 && posx > 150) newGameBtnColor = newGameBtnColorHover
          else newGameBtnColor = newGameBtnColorNormal

          // Home button
          if (posy < 570 && posy > 450 && posx < 450 && posx > 150) homeBtnColor = homeBtnColorHover
          else homeBtnColor = homeBtnColorNormal

        case "playing" =>
          // Menu button
          if (posy < menuBtnSize && posx > widthScreen - menuBtnSize) btnMenuColor = caseColor
          else btnMenuColor = titleColor
      }
    }
  })
  // -------------------------------------------------------------------------------------------------------------------


  // ----------------------------------------------------------------------------------------------------------Fonctions
  // Draw the background when the game is running
  def drawBackground(): Unit = {
    // Background
    gameWindow.clear(gameBackColor)
    // Title
    gameWindow.drawFancyString(215, 85, "2048", "Arial Rounded MT Bold", 1, 70, titleColor)
    // Menu BTN
    gameWindow.setColor(btnMenuColor)
    gameWindow.drawFillRect(widthScreen - menuBtnSize, 0, menuBtnSize, menuBtnSize)
    gameWindow.setColor(Color.white)
    gameWindow.drawLine(widthScreen - menuBtnSize + menuBtnSize / 6, (menuBtnSize / 6) * 2, widthScreen - menuBtnSize / 6, (menuBtnSize / 6) * 2)
    gameWindow.drawLine(widthScreen - menuBtnSize + menuBtnSize / 6, (menuBtnSize / 6) * 3, widthScreen - menuBtnSize / 6, (menuBtnSize / 6) * 3)
    gameWindow.drawLine(widthScreen - menuBtnSize + menuBtnSize / 6, (menuBtnSize / 6) * 4, widthScreen - menuBtnSize / 6, (menuBtnSize / 6) * 4)
    // Game Cells
    gameWindow.setColor(caseColor)
    for (x <- 0 until gridSize; y <- 0 until gridSize) {
      gameWindow.drawFillRect(margin + padding + (x * cellSize) + (x * padding), menuScreen + margin + padding + (y * cellSize) + (y * padding), cellSize - padding, cellSize - padding)
    }
  }

  // Draw the tab of Case on the grid when the game is running
  def drawTab(tabValue: Array[Array[Case]]): Unit = {
    for (y <- tabValue.indices; x <- tabValue(y).indices) {
      gameWindow.drawTransformedPicture(margin + padding + (x * cellSize) + (x * padding) + ((cellSize - padding) / 2), menuScreen + margin + padding + (y * cellSize) + (y * padding) + ((cellSize - padding) / 2), 0, caseFactor, getImage(tabValue(y)(x).caseValue))
    }
  }

  // Returns the correct GraphicsBitmap (image) according to the value in the case
  def getImage(caseValue: Int): GraphicsBitmap = {
    var i = if (caseValue == 0) 0 else (Math.log(caseValue) / Math.log(2)).toInt // Convert the caseValue into the right index
    return tabImages(i)
  }

  // Check if the game can continue or if the user win or lose
  def checkGame(tabValue: Array[Array[Case]]): Int = {
    var result = 0 // lose(2), win(1) playing(0)

    // Check if there is a 0 or the winNumber in the game
    var checkWinNumber, check0 = false
    for (y <- tabValue.indices; x <- tabValue(y).indices) {
      if (tabValue(y)(x).caseValue == winNumber) checkWinNumber = true
      if (tabValue(y)(x).caseValue == 0) check0 = true
    }

    if (checkWinNumber) result = 1 // If we have a winNumber we win
    else {
      if (!check0) { // If there is no 0, we check if we can move or note
        var fusionPossible = false
        for (y <- tabValue.indices; x <- tabValue(y).indices) {
          try { // The try catch is here if we are a the positon 0 or gridSize to not be out of Bounds
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
    result // return 0 by default
  }

  // Return the tab updated to display
  def updateTab(tabValue: Array[Array[Case]]): Array[Array[Case]] = {
    var result: Array[Array[Case]] = tabValue.map(_.clone)
    /*
      We update the tab case per case with the increment to see the case animated and not just teleported to final state
      Step 1: Push all cells in the chosen direction as far as possible, i.e. free cells.
        For a 4 grid we need to do it 3 times so when the increment is under gridSize -1
      Step 2: Merge cells of the same value 1 time
        No matter the grid size, we only merge once, when the increment =3 so gridSize -1
      Step 3: Push back as in the first step
     */
    if (incr <= (gridSize * 2) - 1) { // Check if we finish to update the tab
      direction match {
        case "down" =>
          if (incr < gridSize - 1 || incr > gridSize - 1) { // Push to free case
            for (i <- 0 until gridSize; w <- (gridSize - 1) until 0 by -1) {
              if (result(w)(i).caseValue == 0) {
                result(w)(i).caseValue = result(w - 1)(i).caseValue
                result(w - 1)(i).caseValue = 0
              }
            }
          } else if (incr == gridSize - 1) { // Do the fusion
            for (i <- 0 until gridSize; w <- (gridSize - 1) until 0 by -1) {
              if (result(w)(i).caseValue == result(w - 1)(i).caseValue && !result(w)(i).hasFusionned && !result(w - 1)(i).hasFusionned) {
                result(w)(i).caseValue *= 2
                result(w)(i).hasFusionned = true
                result(w - 1)(i).caseValue = 0
              }
            }
          }

        case "up" =>
          if (incr < gridSize - 1 || incr > gridSize - 1) { // Push to free case
            for (i <- 0 until gridSize; w <- 0 until (gridSize - 1)) {
              if (result(w)(i).caseValue == 0) {
                result(w)(i).caseValue = result(w + 1)(i).caseValue
                result(w + 1)(i).caseValue = 0
              }
            }
          } else if (incr == gridSize - 1) { // Do the fusion
            for (i <- 0 until gridSize; w <- 0 until (gridSize - 1)) {
              if (result(w)(i).caseValue == result(w + 1)(i).caseValue && !result(w)(i).hasFusionned && !result(w + 1)(i).hasFusionned) {
                result(w)(i).caseValue *= 2
                result(w)(i).hasFusionned = true
                result(w + 1)(i).caseValue = 0
              }
            }
          }

        case "left" =>
          if (incr < gridSize - 1 || incr > gridSize - 1) { // Push to free case
            for (j <- 0 until gridSize; w <- 0 until (gridSize - 1)) {
              if (result(j)(w).caseValue == 0) {
                result(j)(w).caseValue = result(j)(w + 1).caseValue
                result(j)(w + 1).caseValue = 0
              }
            }
          } else if (incr == gridSize - 1) { // Do the fusion
            for (j <- 0 until gridSize; w <- 0 until (gridSize - 1)) {
              if (result(j)(w).caseValue == result(j)(w + 1).caseValue && !result(j)(w).hasFusionned && !result(j)(w + 1).hasFusionned) {
                result(j)(w).caseValue *= 2
                result(j)(w).hasFusionned = true
                result(j)(w + 1).caseValue = 0
              }
            }
          }

        case "right" =>
          if (incr < gridSize - 1 || incr > gridSize - 1) { // Push to free case
            for (j <- 0 until gridSize; w <- (gridSize - 1) until 0 by -1) {
              if (result(j)(w).caseValue == 0) {
                result(j)(w).caseValue = result(j)(w - 1).caseValue
                result(j)(w - 1).caseValue = 0
              }
            }
          } else if (incr == gridSize - 1) { // Do the fusion
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
    else { // We finish updating the tab so we reinitialize values
      incr = 0
      isUpdating = false
      direction = "none"
      for (y <- tabValue.indices; x <- tabValue(y).indices) result(y)(x).hasFusionned = false
      tab = getRandomCases(tab)
    }
    return result
  }

  // Draw the menu when we click on the menu icon when the game is running
  def drawMenu(): Unit = {
    // Background Menu
    gameWindow.setColor(menuBackColor)
    gameWindow.drawFillRect(margin * 4, menuScreen + margin * 4, widthScreen - (8 * margin), widthScreen - (8 * margin))
    // New Game Button
    gameWindow.setColor(newGameBtnColor)
    gameWindow.drawFillRect(widthScreen / 4, menuScreen + heightScreen / 4 + 30, widthScreen / 2, heightScreen / 6)
    gameWindow.drawFancyString(170, 380, "New Game", "Arial Rounded MT Bold", 1, 50, Color.white)
    // Home Button
    gameWindow.setColor(homeBtnColor)
    gameWindow.drawFillRect(widthScreen / 4, menuScreen + heightScreen / 2, widthScreen / 2, heightScreen / 6)
    gameWindow.drawFancyString(225, 525, "Home", "Arial Rounded MT Bold", 1, 50, Color.white)
    // Text win, lose, playing
    if (start) gameWindow.drawFancyString(110, 245, "Game running...", "Arial", 1, 50, neutralColor)
    else if (win) gameWindow.drawFancyString(190, 245, "You win !", "Arial", 1, 50, winColor)
    else gameWindow.drawFancyString(180, 245, "You lose !", "Arial", 1, 50, leaveBtnColorHover)
  }

  // Draw the home page
  def drawStart(): Unit = {
    // Background
    gameWindow.clear(gameBackColor)
    // Title
    gameWindow.drawFancyString(215, 85, "2048", "Arial Rounded MT Bold", 1, 70, titleColor)
    gameWindow.drawFancyString(80, 160, "Goal", "Arial Rounded MT Bold", 1, 30, titleColor)
    gameWindow.drawFancyString(85, 235, "Grid", "Arial Rounded MT Bold", 1, 30, titleColor)
    // Goal buttons
    gameWindow.setColor(goal128Color)
    gameWindow.drawFillRect(160, 130, 40, 40)
    gameWindow.drawFancyString(166, 155, "128", "Arial Rounded MT Bold", 1, 15, Color.black)
    gameWindow.setColor(goal256Color)
    gameWindow.drawFillRect(220, 130, 40, 40)
    gameWindow.drawFancyString(226, 155, "256", "Arial Rounded MT Bold", 1, 15, Color.black)
    gameWindow.setColor(goal512Color)
    gameWindow.drawFillRect(280, 130, 40, 40)
    gameWindow.drawFancyString(286, 155, "512", "Arial Rounded MT Bold", 1, 15, Color.black)
    gameWindow.setColor(goal1024Color)
    gameWindow.drawFillRect(340, 130, 40, 40)
    gameWindow.drawFancyString(342, 155, "1024", "Arial Rounded MT Bold", 1, 15, Color.black)
    gameWindow.setColor(goal2048Color)
    gameWindow.drawFillRect(400, 130, 40, 40)
    gameWindow.drawFancyString(402, 155, "2048", "Arial Rounded MT Bold", 1, 15, Color.black)
    // Grid buttons
    gameWindow.setColor(grid4Color)
    gameWindow.drawFillRect(160, 205, 40, 40)
    gameWindow.drawFancyString(175, 230, "4", "Arial Rounded MT Bold", 1, 15, Color.black)
    gameWindow.setColor(grid8Color)
    gameWindow.drawFillRect(220, 205, 40, 40)
    gameWindow.drawFancyString(235, 230, "8", "Arial Rounded MT Bold", 1, 15, Color.black)
    gameWindow.setColor(grid10Color)
    gameWindow.drawFillRect(280, 205, 40, 40)
    gameWindow.drawFancyString(290, 230, "10", "Arial Rounded MT Bold", 1, 15, Color.black)
    gameWindow.setColor(grid16Color)
    gameWindow.drawFillRect(340, 205, 40, 40)
    gameWindow.drawFancyString(350, 230, "16", "Arial Rounded MT Bold", 1, 15, Color.black)
    gameWindow.setColor(grid20Color)
    gameWindow.drawFillRect(400, 205, 40, 40)
    gameWindow.drawFancyString(410, 230, "20", "Arial Rounded MT Bold", 1, 15, Color.black)
    // New Game Button
    gameWindow.setColor(newGameBtnColor)
    gameWindow.drawFillRect(widthScreen / 4, menuScreen + heightScreen / 4 + 30, widthScreen / 2, heightScreen / 6)
    gameWindow.drawFancyString(170, 380, "New Game", "Arial Rounded MT Bold", 1, 50, Color.white)
    // Leave Button
    gameWindow.setColor(leaveBtnColor)
    gameWindow.drawFillRect(widthScreen / 4, menuScreen + heightScreen / 2, widthScreen / 2, heightScreen / 6)
    gameWindow.drawFancyString(225, 525, "Leave", "Arial Rounded MT Bold", 1, 50, Color.black)
  }

  // Reinitializes values when a game is restarted
  def restart(): Unit = {
    gameState = "playing"
    direction = "none"
    isUpdating = false
    start = true
    win = false
    incr = 0
    tab = Array.fill(gridSize, gridSize)(new Case)
    tab = getRandomCases(tab)
    tab = getRandomCases(tab)
  }

  // Return a new tab with a 2 or 4 on a empty case
  def getRandomCases(tabValue: Array[Array[Case]]): Array[Array[Case]] = {
    var result: Array[Array[Case]] = tabValue.map(_.clone)
    var check = false

    // If no case = 0, we just return the same tab
    var check0 = false
    for (y <- tabValue.indices; x <- tabValue(y).indices) {
      if (tabValue(y)(x).caseValue == 0) check0 = true
    }
    if (!check0) check = true

    // If a minimum of one case is free, we put a 2 or a 4 in it
    while (!check) {
      var randomX = Random.nextInt(gridSize) // Generate a random number between 0 and gridSize
      var randomY = Random.nextInt(gridSize) // Generate a random number between 0 and gridSize
      if (result(randomY)(randomX).caseValue == 0) {
        // 10% of chance to put a 4, 90% of chance to put a 2
        if (Random.nextInt(100) > 90) result(randomY)(randomX).caseValue = 4
        else result(randomY)(randomX).caseValue = 2
        check = true
      }
    }

    result
  }
  // -------------------------------------------------------------------------------------------------------------------


  // -----------------------------------------------------------------------------------------------------------GameLoop
  while (true) {
    gameWindow.frontBuffer.synchronized { // Drawing
      gameState match {
        case "start" =>
          drawStart()

        case "menu" =>
          drawBackground()
          drawTab(tab)
          drawMenu()

        case "playing" =>
          if (isUpdating) { // If we are updating the tab we don't take new direction
            tab = updateTab(tab)
            drawBackground()
            drawTab(tab)
          }
          else if (checkGame(tab) == 0) { // If we don't win or lose, we continue the game
            if (pressedDown) direction = "down"
            if (pressedUp) direction = "up"
            if (pressedLeft) direction = "left"
            if (pressedRight) direction = "right"
            if (direction == "none") { // We wait a new direction
              drawBackground()
              drawTab(tab)
            } else isUpdating = true // A direction was entered so we are going to update the tab
          }
          else {
            gameState = if (checkGame(tab) == 2) "lose" else "win"
          }

        case "win" =>
          win = true
          start = false
          gameState = "menu"

        case "lose" =>
          win = false
          start = false
          gameState = "menu"
      }
    }
    gameWindow.syncGameLogic(60) // FPS sync
  }
  // -------------------------------------------------------------------------------------------------------------------
}