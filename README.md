# LeetTutor-Local

本地互动式 LeetCode 与系统设计学习助手。导师会根据薄弱项和练习进度安排下一题；界面运行在浏览器中，可以直接导入题目、写 Python、跑自定义测试，并把当前代码现场交给本地 AI。代码仍保存在这个仓库里，也可以继续用 VS Code 编辑。AI 请求只发往本机 Ollama、LM Studio 或实验版 AMD Metal 端点。

## 最快启动

先安装 Python 3.10+。Ollama 本体和模型都可以随后在 LeetTutor 页面内安装。

- macOS：双击 `run.command`。
- Windows：双击 `run.bat`。
- 终端：`python3 scripts/launch.py`。
- 同时打开 VS Code：`python3 scripts/launch.py --vscode`。

首次运行会自动创建 `.venv`、安装依赖并打开浏览器。以后只有 `requirements.txt` 变化时才会重新安装。

应用使用 Streamlit 的 minimal 工具栏模式，避免本地开发模式把键盘 `C` 注册为 “Clear cache” 而干扰网页复制。

> 推荐组合：日常刷题直接使用浏览器里的刷题 IDE；需要断点调试、复杂工程导航或 Git 操作时再打开 VS Code。

## 两种训练模式

### Algorithm Mode

- 【导师给我下一题】会在二分、栈、优先队列和 DP 路线中自动补弱，并自动导入完整题面、Python 模板、方法名与样例参数；【开始导师引导】同样会补齐尚未导入的题目。
- 每道题说明本轮训练目标；第一轮诊断问题由本地课程引擎立即显示，不等待模型，再根据你的回答调用 AI 逐层提示。
- “已掌握 / 需要复习 / 练习次数”保存在本地 `study_progress.json`。
- 面试官先判断时间/空间复杂度，再用问题引导优化。
- 默认不交付完整答案；只有最新消息明确包含“求最优解代码”才会输出实现。
- 【代码 Review】会检查隐藏 Bug、最小失败用例和边界条件。
- 粘贴 `leetcode.com/problems/...` 或 `leetcode.cn/problems/...` 链接，可导入公开题面、Python 起始模板、方法名和样例参数。
- 浏览器内可以直接编辑 `class Solution`，使用 JSON 描述多个测试并查看实际输出、断言结果、打印和 traceback。
- Python 在受限的独立子进程中运行：默认 3 秒超时，阻止常见文件、网络和子进程操作；macOS/Linux 还会施加内存和 CPU 资源上限。它不是执行陌生代码的强安全沙箱，只应用于自己的题解。
- 【运行并让导师分析】【我卡住了】【根据现有代码继续引导】都会自动附带当前题面、完整编辑器代码、测试用例和最近运行结果。
- 普通聊天同样默认读取当前代码现场，不必反复复制粘贴。
- Algorithm Workspace 使用三栏布局：左侧完整题面、中间代码与测试、右侧常驻“👩🏻‍🏫 小沐导师”；写代码时不需要在题面、回复和输入框之间反复上下滚动。
- 导师回答只在右栏出现一次，回复完成后输入框仍在原位，可以直接继续追问；“我卡住了 / 下一步 / Review”都会读取此刻的题面、代码、测试和运行结果。
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

- 从 QPS、DAU、峰值和读写比开始容量估算。
- 对 SPOF、缓存问题和高并发进行至少三轮压力测试。
- 模型返回的 fenced `mermaid` 代码块会直接渲染成架构图。

## 本地模型设置

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

Ollama 的根地址会自动转换为 OpenAI 兼容的 `/v1` 地址。若服务未启动、超时、模型不存在或请求格式不兼容，界面会显示对应提示，不会直接崩溃。

刷题 Temperature 默认 `0.2`，系统设计默认 `0.5`；两者和 Top P、Prompt、Endpoint、模型名都可以在侧边栏修改并保存。

算法导师默认关闭长思考并限制为 768 个输出 token；系统设计默认低思考和 1536 token。侧边栏可手动开启低/中/高思考；界面会分别显示“加载模型”“正在思考”和“正在回答”。8 GB 显卡默认推荐 `qwen3.5:9b`；`qwen3.6:27b` 的 Q4 文件约 17 GB，只作为 28 GB 以上内存机器的慢速进阶选项。

### 这台 Intel Mac 的 AMD Metal 实验后端

`run.command` 会自动把 Ollama 已下载的 `qwen3.5:9b` 交给定制版 `llama.cpp`，以 `--no-mmap` 等价的私有 Metal buffer 完整装入 Radeon Pro 5600M 的 8 GB 显存，并在 `11435` 提供 OpenAI 兼容接口。关闭 LeetTutor 时，该服务也会一起退出；启动日志在 `.leettutor/amd-metal-server.log`。

本机 Q4_K_M 实测：生成约 `19.94 token/s`，CPU 约 `2.97 token/s`，约快 `6.7×`。当前固定为 4096 上下文、单并发并关闭深度思考，避免模型把输出额度全部消耗在隐藏推理中；这是文本对练后端，不启用同一 GGUF 中的视觉部分。若侧边栏显示启动失败，先确认 `qwen3.5:9b` 已下载，并查看上述日志。

## 配置

点击侧边栏“保存设置”会生成本地 `config.json`。它和 `.env` 都已被 Git 忽略。

如需环境变量覆盖：

```bash
cp .env.example .env
```

支持：`LEETTUTOR_PROVIDER`、`LEETTUTOR_MODEL`、`LEETTUTOR_OLLAMA_URL`、`LEETTUTOR_LM_STUDIO_URL`、`LEETTUTOR_AMD_METAL_URL` 和 `LEETTUTOR_API_KEY`。

## 项目结构

```text
Leetcode/
├── app.py                     # Streamlit 核心入口
├── leettutor/                 # 导师、LeetCode 导题、代码运行与模型适配层
├── scripts/launch.py          # 跨平台启动器
├── run.command / run.bat      # macOS / Windows 一键启动
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
