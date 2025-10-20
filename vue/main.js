const { app, BrowserWindow, Menu } = require('electron')
const path = require('path')
const isDev = !app.isPackaged // 根据是否打包来判断开发模式

console.log('Environment info:')
console.log('NODE_ENV:', process.env.NODE_ENV)
console.log('isDev:', isDev)
console.log('isPackaged:', app.isPackaged)
console.log('__dirname:', __dirname)

// 保持对window对象的全局引用，避免被垃圾回收
let mainWindow

function createWindow() {
  // 创建浏览器窗口
  mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    minWidth: 800,
    minHeight: 600,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      enableRemoteModule: false,
      webSecurity: true
    },
    icon: path.join(__dirname, 'public/favicon.ico'),
    show: false, // 先不显示，等页面加载完成后再显示
    titleBarStyle: 'default'
  })

  // 加载应用
  if (isDev) {
    // 开发环境：加载开发服务器
    console.log('Loading development server...')
    mainWindow.loadURL('http://localhost:5173')
  } else {
    // 生产环境：加载打包后的文件
    // 在打包后的应用中，dist目录被包含在app.asar中
    const indexPath = path.join(__dirname, 'dist/index.html')
    console.log('Loading production file:', indexPath)
    console.log('__dirname:', __dirname)
    console.log('File exists check:', require('fs').existsSync(indexPath))
    mainWindow.loadFile(indexPath)
  }

  // 页面加载完成后显示窗口
  mainWindow.once('ready-to-show', () => {
    mainWindow.show()
    
    // 聚焦窗口
    if (isDev) {
      mainWindow.focus()
    }
  })

  // 当窗口被关闭时触发
  mainWindow.on('closed', () => {
    // 取消引用window对象
    mainWindow = null
  })

  // 处理窗口创建事件（用于安全性）
  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    // 在默认浏览器中打开外部链接
    require('electron').shell.openExternal(url)
    return { action: 'deny' }
  })
}

// Electron初始化完成，创建窗口
app.whenReady().then(() => {
  createWindow()

  // 在macOS上，当点击dock图标且没有其他窗口打开时，重新创建窗口
  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })

  // 设置应用菜单（可选）
  if (process.platform === 'darwin') {
    // macOS菜单
    const template = [
      {
        label: app.getName(),
        submenu: [
          { role: 'about' },
          { type: 'separator' },
          { role: 'services' },
          { type: 'separator' },
          { role: 'hide' },
          { role: 'hideothers' },
          { role: 'unhide' },
          { type: 'separator' },
          { role: 'quit' }
        ]
      },
      {
        label: 'Edit',
        submenu: [
          { role: 'undo' },
          { role: 'redo' },
          { type: 'separator' },
          { role: 'cut' },
          { role: 'copy' },
          { role: 'paste' }
        ]
      },
      {
        label: 'View',
        submenu: [
          { role: 'reload' },
          { role: 'forceReload' },
          { role: 'toggleDevTools' },
          { type: 'separator' },
          { role: 'resetZoom' },
          { role: 'zoomIn' },
          { role: 'zoomOut' },
          { type: 'separator' },
          { role: 'togglefullscreen' }
        ]
      },
      {
        label: 'Window',
        submenu: [
          { role: 'minimize' },
          { role: 'close' }
        ]
      }
    ]
    Menu.setApplicationMenu(Menu.buildFromTemplate(template))
  } else {
    // Windows/Linux - 移除菜单栏
    Menu.setApplicationMenu(null)
  }
})

// 当所有窗口都被关闭时退出应用
app.on('window-all-closed', () => {
  // 在macOS上，应用通常会保持活跃状态，即使没有打开的窗口
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

// 防止导航到外部URL（安全性）
app.on('web-contents-created', (event, contents) => {
  contents.on('will-navigate', (event, navigationUrl) => {
    const parsedUrl = new URL(navigationUrl)
    
    if (parsedUrl.origin !== 'http://localhost:5173' && parsedUrl.origin !== 'file://') {
      event.preventDefault()
    }
  })
})

// 处理证书错误（开发环境）
if (isDev) {
  app.on('certificate-error', (event, webContents, url, error, certificate, callback) => {
    // 在开发环境中忽略证书错误
    event.preventDefault()
    callback(true)
  })
}

// 应用安全设置
app.on('web-contents-created', (event, contents) => {
  // 禁用新窗口创建
  contents.on('new-window', (event, navigationUrl) => {
    event.preventDefault()
    require('electron').shell.openExternal(navigationUrl)
  })
})