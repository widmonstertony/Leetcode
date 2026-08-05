# LeetTutor-Local

**LeetTutor · Made by Tony** 是互动式 LeetCode 与系统设计学习助手。内置导师 **JARVIS** 会根据薄弱项和练习进度安排下一题；界面运行在浏览器中，可以直接导入题目、写 Python、跑自定义测试，并把当前代码现场交给 AI。模型既可完全留在本机，也可选择 OpenAI API 或 Gemini API；任意能运行本项目的电脑都可以开启局域网主机模式，让同一 Wi-Fi 内的手机或平板继续刷题。

## 最快启动

先安装 Python 3.10+。Ollama 本体和模型都可以随后在 LeetTutor 页面内安装。

- macOS：双击 `run.command`。
- Windows：双击 `run.bat`。
- 终端：`python3 scripts/launch.py`。
- 同时打开 VS Code：`python3 scripts/launch.py --vscode`。

首次运行会自动创建 `.venv`、安装依赖并打开浏览器。以后只有 `requirements.txt` 变化时才会重新安装。

应用使用 Streamlit 的 viewer 工具栏模式：右上角 `⋮` 可以直接选择 System / Light / Dark，同时不会显示开发模式的 “Clear cache”，因此不会再干扰网页复制。

产品顶栏可以切换训练模式、界面语言和 LeetCode 题面语言；右上角 `⋮` 可以选择 System / Light / Dark。中文题面读取 LeetCode 中国站的官方译文，英文题面读取国际站原文；切换题面语言只刷新标题和描述，不会覆盖编辑器代码。Ace 编辑器和悬浮导师会同步换色。

> 推荐组合：日常刷题直接使用浏览器里的刷题 IDE；需要断点调试、复杂工程导航或 Git 操作时再打开 VS Code。

## 局域网主机模式与手机续刷

启动局域网主机模式：

- macOS：双击 **`run-lan.command`**；
- Windows：双击 **`run-lan.bat`**；
- 终端：`python3 scripts/launch.py --lan`。

启动窗口会显示：

- 手机访问地址，例如 `http://192.168.1.25:8501`；
- 本次随机生成的 8 位访问码；
- 不含访问码的二维码，方便手机扫码。

手机和主机连到同一个可信 Wi-Fi 后，扫码并输入本次访问码。默认勾选“在此浏览器记住这台主机 30 天”，验证成功后会保存主机签发的随机凭证而不是访问码；之后刷新页面、关闭再打开浏览器或重启主机，都不需要重复输入。换浏览器、使用无痕模式、清除网站数据、主机地址变化、凭证到期或主机撤销信任后，才需要重新验证。

学习进度和已保存题解保留在主机上，因此换手机或电脑仍能接着练。编辑器里尚未保存的草稿和当前聊天属于各自浏览器会话，不会在两台设备间实时合并，请在换设备前保存题解。

手机界面针对 **iPhone 13 Pro（390 × 844 CSS px）** 和 **iPhone Air（420 × 912 CSS px）** 做了单独适配：顶部控制收成两行，不再横向溢出；算法模式底部固定“题目 / 代码 / JARVIS”，系统设计固定“任务 / 现场 / JARVIS”。默认直接进入代码页，题面和代码不会被挤成两条窄栏；JARVIS 在手机上以避开底部安全区和软键盘的全宽对话面板打开。侧栏首次进入会自动收起，点左上角 **LT** 随时重新打开。

第一次启动时，系统防火墙若询问是否允许 Python 接受传入连接，请选择允许。主机必须保持开机、联网且不进入会停止服务的深度睡眠。**只在私人网络使用，不要在路由器设置端口转发，也不要直接暴露到公网。** 当前主机模式是带访问码和可信设备凭证的 HTTP，能阻止同网段的随意访问，但不会像 HTTPS 一样加密无线网络中的传输。

API Key 只能在主机的本地模式中配置，或者写进主机 `.env`。局域网页面只显示“已配置”，不会读取、回显或修改密钥。完整步骤与故障排查见 [局域网主机模式指南](docs/HOST_MODE.md)。

## 两种训练模式

### Algorithm Mode

- 【JARVIS 给我下一题】会在二分、栈、优先队列和 DP 路线中自动补弱，并自动导入完整题面、Python 模板、方法名与样例参数；【开始导师引导】同样会补齐尚未导入的题目。
- 每道题说明本轮训练目标；第一轮诊断问题由本地课程引擎立即显示，不等待模型，再根据你的回答调用 AI 逐层提示。
- “已掌握 / 需要复习 / 练习次数”保存在本地 `study_progress.json`。
- 面试官先判断时间/空间复杂度，再用问题引导优化。
- 默认不交付完整答案；只有最新消息明确包含“求最优解代码”才会输出实现。
- 【代码 Review】会检查隐藏 Bug、最小失败用例和边界条件。
- 粘贴 `leetcode.com/problems/...` 或 `leetcode.cn/problems/...` 链接，可导入公开题面、Python 起始模板、方法名和样例参数。
- 浏览器内使用 Ace 编辑器直接编写 `class Solution`，支持 Tab/Shift+Tab 缩进、自动缩进、行号、语法高亮、括号匹配与代码查找。
- Python 在受限的独立子进程中运行：默认 3 秒超时，阻止常见文件、网络和子进程操作；macOS/Linux 还会施加内存和 CPU 资源上限。它不是执行陌生代码的强安全沙箱，只应用于自己的题解。
- 【运行并让导师分析】【我卡住了】【根据现有代码继续引导】都会自动附带当前题面、完整编辑器代码、测试用例和最近运行结果。
- 普通聊天同样默认读取当前代码现场，不必反复复制粘贴。
- Algorithm Workspace 默认是左侧完整题面、右侧代码与测试，JARVIS 以悬浮导师出现；写代码时不需要在题面、回复和输入框之间反复上下滚动。
- 手机端不会把桌面双栏机械堆叠成长页面：底部“题目 / 代码 / JARVIS”在三个完整工作面之间即时切换，代码页是默认入口。
- 题目和代码之间的分隔条可以左右拖动并自动记住比例；键盘方向键也能微调，双击分隔条恢复默认宽度。
- 题目、代码、导师都能单独收起；右上角“布局”可恢复任意面板或一键恢复默认布局，剩余面板会自动占满可用宽度。
- JARVIS 可以自由拖动，靠近任意四角时会磁吸回角落；也可以切换为右栏“停靠”，两种形态互斥并共享同一段导师历史。
- 普通导师回合强制采用“1 个短提示 + 1 个问题”的节奏，等你回答后再继续；只有明确输入“求最优解代码”才会展开完整实现。
- 导师回答只出现一次，回复完成后输入框仍在原位，可以直接继续追问；“我卡住了 / 下一步 / Review”都会读取此刻的题面、代码、测试和运行结果。
- 每轮只发送一份最新工作区快照，旧对话保留真实问答而不重复堆叠整份题面与代码，减少后续追问的上下文和等待时间。
- 可以载入、编辑并保存 `python/` 或 `java/` 中的题解；当前内置运行器先支持 Python，Java 可以继续保存和 AI Review。

### 测试用例格式

运行器会实例化 `Solution` 并调用“方法名”。如果不填写方法名，类中必须只有一个公开方法。测试区接受一个对象或对象数组：

```json
[
  {
    "args": [[-1, 0, 3, 5, 9, 12], 9],
    "expected": 4
  },
  {
    "args": [[-1, 0, 3, 5, 9, 12], 2],
    "expected": -1
  }
]
```

`args` 是位置参数，另可使用 `kwargs`；省略 `expected` 时只展示实际返回值。当前适合数字、字符串、数组、矩阵和普通 JSON 数据。树、链表等 LeetCode 特殊类型暂时仍建议在代码中自行构造。

“连接 LeetCode”目前指读取公开题目与跳转到官方提交页，不会收集或保存你的 LeetCode Cookie。点击【在 LeetCode 打开 / 提交】即可使用浏览器中已有的登录状态提交。

### System Design Mode

- 【JARVIS 分配任务】会从扩展性、可靠性、实时系统、数据平台与事务一致性路线轮换出题；可限定方向和难度，也可以填写自定义需求。
- 每个任务先显示任务简报、容量目标和第一个检查点；JARVIS 一次只追问一个架构判断，避免一次倾倒整套答案。
- 手机端使用“任务 / 现场 / JARVIS”底部导航；继续回答和“只提示下一步”固定在导航上方，不需要滑回页面顶部。
- 从 QPS、DAU、峰值和读写比开始容量估算。
- 一场完整训练会依次对 SPOF、缓存问题和高并发或一致性进行至少三轮压力测试。
- 模型返回的 fenced `mermaid` 代码块会直接渲染成架构图。

## 模型设置

侧边栏会读取系统类型、RAM、Apple Silicon / Intel、NVIDIA 和 Windows AMD VRAM，按保守内存预算给出平衡、快速和进阶模型。推荐仅是估算：上下文越长，额外内存越多。

- Ollama：如果尚未安装，页面会从官方地址下载安装器并交给 macOS/Windows 确认；安装后可自动启动服务，再一键下载推荐模型。
- LM Studio：显示对应搜索词、`lms get` 命令和官方下载安装入口。
- Intel Mac：普通 Ollama 仍会走 CPU；这台 Radeon Pro 5600M 机器已增加实验版 AMD Metal Provider，见下方说明。
- Windows AMD：应用会标记实验性 Vulkan 加速；由页面启动 Ollama 时自动注入 `OLLAMA_VULKAN=1`。已经在后台运行的 Ollama 需要先完全退出，再由页面重新启动。

首次使用 Ollama 的完整顺序是：`安装 Ollama → 完成系统安装向导 → 重新检测/启动 Ollama → 一键下载模型 → 刷新模型`。安装器必须经过系统界面确认，应用不会静默绕过 macOS Gatekeeper 或 Windows 签名检查。

下载完成后点击“检测服务并刷新模型”，也可以手动填写模型名。

| Provider | 默认地址 | 启动提示 |
| --- | --- | --- |
| Ollama | `http://localhost:11434` | 安装模型后运行 `ollama serve` |
| LM Studio | `http://localhost:1234/v1` | 在 Developer / Local Server 中加载模型并启动服务 |
| AMD Metal（Intel Mac） | `http://127.0.0.1:11435/v1` | 双击 `run.command` 自动启动和关闭 |
| OpenAI API | `https://api.openai.com/v1` | 在 OpenAI Platform 创建 API Key；默认 `gpt-5.6-terra` |
| Gemini API | `https://generativelanguage.googleapis.com/v1beta/openai` | 在 Google AI Studio 创建 API Key；默认 `gemini-3.6-flash` |

Ollama 的根地址会自动转换为 OpenAI 兼容的 `/v1` 地址。若服务未启动、超时、模型不存在或请求格式不兼容，界面会显示对应提示，不会直接崩溃。

刷题 Temperature 默认 `0.2`，系统设计默认 `0.5`；两者和 Top P、Prompt、Endpoint、模型名都可以在侧边栏修改并保存。

### OpenAI / Gemini 云端 API

侧栏选择对应 Provider，按“云端 API 设置”的官方入口创建 Key，粘贴后保存即可。密钥写入 Git 忽略的 `.leettutor/secrets.json`，文件权限设为 `0600`；`config.json` 不保存云端密钥。

需要特别区分：

- **ChatGPT Plus/Pro 不是 OpenAI Platform API 额度。** App 需要独立的 Platform API Key、API 模型权限和 API 账单。
- **Gemini 网页会员也不是 Gemini API Key。** App 使用 Google AI Studio 创建的 Key，API 免费/付费额度与账单独立。
- 选择云端 Provider 后，导师需要把当前题面、代码、运行结果和提问发送给该云端 API；Python 代码本身仍只在主机上点击运行后执行。

算法导师默认关闭长思考并限制为 768 个输出 token；系统设计默认低思考和 1536 token。侧边栏可手动开启低/中/高思考；界面会分别显示“加载模型”“正在思考”和“正在回答”。8 GB 显卡默认推荐 `qwen3.5:9b`；`qwen3.6:27b` 的 Q4 文件约 17 GB，只作为 28 GB 以上内存机器的慢速进阶选项。

### 这台 Intel Mac 的 AMD Metal 实验后端

这项功能现在不再依赖开发者电脑旁边预先存在的 `llama.cpp-metal` 仓库。侧栏选择 **AMD Metal（Intel Mac）** 后，内置安装中心会逐项检查 Intel/Radeon/显存、Apple 编译工具、CMake、模型、定制后端和本地服务，并可直接完成缺失步骤。

安装器固定下载官方 `llama.cpp b10240`，应用仓库内的 Qwen 3.5 / Ollama GGUF 兼容补丁并本地编译；源码和产物保存在 Git 忽略的 `.leettutor/llama.cpp-metal`。`run.command` 随后会自动把 Ollama 已下载的 `qwen3.5:9b` 以 private Metal buffer 完整装入 Radeon Pro 5600M 的 8 GB 显存，并在 `11435` 提供只监听本机的 OpenAI 兼容接口。关闭启动器时，其创建的服务也会一起退出；启动日志在 `.leettutor/amd-metal-server.log`。

本机 Q4_K_M 实测：生成约 `19.94 token/s`，CPU 约 `2.97 token/s`，约快 `6.7×`。当前固定为 4096 上下文、单并发并关闭深度思考，避免模型把输出额度全部消耗在隐藏推理中；这是文本对练后端，不启用同一 GGUF 中的视觉部分。若侧边栏显示启动失败，先确认 `qwen3.5:9b` 已下载，并查看上述日志。

持续验证的硬件是 16-inch Intel MacBook Pro（`MacBookPro16,4`）+ Radeon Pro 5600M 8 GB。其他 8 GB Intel Radeon 可以实验；4 GB Radeon、Apple Silicon 和 Boot Camp 不走这套 Metal 安装器。完整的 App 内操作、原理、验证方法和故障排查见 [Intel MacBook Pro + AMD Radeon 指南](docs/INTEL_AMD_MACBOOK.md)。

## 配置

点击侧边栏“保存设置”会生成本地 `config.json`。它和 `.env` 都已被 Git 忽略。

如需环境变量覆盖：

```bash
cp .env.example .env
```

支持：`LEETTUTOR_PROVIDER`、`LEETTUTOR_MODEL`、`LEETTUTOR_OLLAMA_URL`、`LEETTUTOR_LM_STUDIO_URL`、`LEETTUTOR_AMD_METAL_URL`、`LEETTUTOR_OPENAI_API_KEY`、`LEETTUTOR_GEMINI_API_KEY` 和 `LEETTUTOR_API_KEY`。标准的 `OPENAI_API_KEY` / `GEMINI_API_KEY` 也可读取。

## 项目结构

```text
Leetcode/
├── app.py                     # Streamlit 核心入口
├── assets/                    # JARVIS 原创全息 AI 视觉资源
├── leettutor/                 # 导师、LeetCode 导题、代码运行与模型适配层
├── scripts/launch.py          # 跨平台启动器
├── scripts/setup_intel_amd_metal.py # Intel Radeon 自检与安装备用入口
├── patches/                   # 固定版本的 AMD Metal / Qwen 兼容补丁
├── run.command / run.bat      # macOS / Windows 本机一键启动
├── run-lan.command / .bat     # 局域网主机模式手机入口
├── tests/                     # 不依赖本地模型的测试
├── python/                    # Python 重刷题解
├── java/                      # Java 题解，继续正常维护
├── sql/                       # SQL 题解
└── docs/                      # 背诵模板与旧笔记
```

## 刷题资料入口

- [Python 重刷手册](docs/PYTHON_PLAYBOOK.md)：二分、Stack、Heap、DP 模板与路线。
- [Python 题解](python/)：今后的主要重刷目录。
- [Java 题解](java/)：旧解和后续 Java 版本都保留在这里，不是 archive。
- [BST 笔记](docs/BST.md)
- [Intel MacBook Pro + AMD Radeon 指南](docs/INTEL_AMD_MACBOOK.md)：App 内安装、GPU 验证、兼容范围和故障排查。

二分的个人统一约定仍是：

```python
while left < right:
    mid = left + (right - left) // 2
    if mid_may_be_answer(mid):
        right = mid
    else:
        left = mid + 1
# 结束时只看 left；答案可能不存在时再验证
```

## 开发与测试

```bash
python3 -m venv .venv
.venv/bin/python -m pip install -r requirements-dev.txt
.venv/bin/python -m pytest
.venv/bin/python -m streamlit run app.py
```

Windows 把 `.venv/bin/python` 换成 `.venv\Scripts\python.exe`。
