# 导入Win32 API
Add-Type @"
    using System;
    using System.Runtime.InteropServices;

    public class Clicker {
        [DllImport("user32.dll")]
        public static extern bool SetCursorPos(int X, int Y);

        [DllImport("user32.dll")]
        public static extern void mouse_event(uint dwFlags, uint dx, uint dy, uint dwData, int dwExtraInfo);

        [DllImport("user32.dll")]
        public static extern IntPtr FindWindow(string lpClassName, string lpWindowName);

        [DllImport("user32.dll")]
        public static extern bool SetForegroundWindow(IntPtr hWnd);

        public const uint MOUSEEVENTF_LEFTDOWN = 0x02;
        public const uint MOUSEEVENTF_LEFTUP = 0x04;
    }
"@


# 循环拍照
while($true) {
    # 检查相机应用程序是否已启动
    $cameraProcess = Get-Process -Name "WindowsCamera" -ErrorAction SilentlyContinue
    # 若相机应用程序未启动，则启动它
    if ($cameraProcess -eq $null) {
        # 打开相机应用程序
        $cameraProcess = Start-Process "shell:AppsFolder\Microsoft.WindowsCamera_8wekyb3d8bbwe!App"
        # 等待相机应用程序加载完成
        Start-Sleep -Seconds 20
    }
    # 将相机应用程序窗口置顶显示
    #$windowHandle = [Clicker]::FindWindow($null, "Camera")
    #[Clicker]::SetForegroundWindow($windowHandle)
	# 使用窗口类名来查找相机应用程序的窗口句柄
	#$windowHandle = [WindowHelper]::FindWindow("Windows.UI.Core.CoreWindow", $null)
	# 打印窗口句柄的值
	#Write-Host "窗口句柄: $windowHandle"
	# 将相机应用程序的窗口置顶
	#[Clicker]::SetForegroundWindow($windowHandle)

    # 计算相机窗口中拍照按钮的坐标
    $x = 1870
    $y = 535

    # 移动鼠标到拍照按钮上并模拟鼠标左键按下和抬起
    [Clicker]::SetCursorPos($x, $y)
    [Clicker]::mouse_event([Clicker]::MOUSEEVENTF_LEFTDOWN, $x, $y, 0, 0)
    [Clicker]::mouse_event([Clicker]::MOUSEEVENTF_LEFTUP, $x, $y, 0, 0)
    # 拍照后关闭相机应用程序
    #Stop-Process $cameraProcess -Force
    #Start-Sleep -Seconds 5
    #Stop-Process -Name "WindowsCamera" -Force
    # 等待一分钟后再次拍照
    Start-Sleep -Seconds 280
}


