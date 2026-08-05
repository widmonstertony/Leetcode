# Intel MacBook Pro + AMD Radeon 本地模型指南

这份指南服务于仍在使用 **Intel MacBook Pro + 独立 AMD Radeon** 的 LeetTutor 用户，重点配置是项目持续实测的：

- 16-inch Intel MacBook Pro（本项目实测机器显示为 `MacBookPro16,4`）
- Intel x86_64
- AMD Radeon Pro 5600M，8 GB 独立显存
- macOS 原生系统，而不是 Boot Camp Windows

最省事的路径不是复制终端命令，而是双击 `run.command`，在 LeetTutor 侧栏选择 **AMD Metal（Intel Mac）**，展开“硬件检测与模型安装”，按编号补齐空心状态。App 可以安装 Ollama、下载模型、打开 Apple 编译工具安装器、下载并编译后端、启动服务和展示诊断日志。

> 这是社区实验后端，不是 Apple、Ollama 或 llama.cpp 官方承诺的 Intel Radeon 支持。项目把已验证版本固定下来，避免上游变化导致今天能跑、明天编译失败。

## 为什么官方 Ollama 显示 CPU-only

这类 Intel Mac 有两块 GPU：Intel UHD 集显和 Radeon Pro 独显。硬件支持 Metal，不代表每个推理程序都会使用它。官方 Ollama 的 macOS GPU 路径主要面向 Apple Silicon；在 Intel macOS 上看到 `Available devices: BLAS: Accelerate`，表示当前进程只加载了 CPU/Accelerate 后端，5600M 的 8 GB 显存并没有参与推理。

Boot Camp 也不会让 macOS Ollama 突然获得 GPU。Windows 是另一套驱动和 Vulkan 路径；它可以单独实验，但不是本项目验证过的 5600M Metal 方案。

LeetTutor 的实验 Provider 使用定制构建的 `llama-server`：

1. 固定下载官方 `ggml-org/llama.cpp` 的 `b10240`（提交 `0b14b87`）。
2. 应用仓库内的 Qwen 3.5 / Ollama GGUF 兼容补丁。
3. 使用 Apple Clang、Accelerate 和 Metal 编译 x86_64 后端。
4. 用 `MTL0`、private Metal buffer、4096 context、单并发加载 Qwen 3.5 9B。
5. 只在 `127.0.0.1:11435` 暴露 OpenAI 兼容接口。

后端不会修改显卡驱动、Ollama 程序或模型文件；它直接读取 Ollama 已下载的 GGUF blob，因此不会再复制一份约 6.6 GB 的模型。

## App 内一站式安装

### 1. 启动 LeetTutor

双击 `run.command`。首次运行会创建 `.venv` 并安装 Python 依赖，其中包含固定范围的 CMake。即使 AMD 后端尚未安装，LeetTutor 网页仍会正常打开。

### 2. 打开 AMD 安装中心

在侧栏设置：

- API Provider：`AMD Metal（Intel Mac）`
- Endpoint：`http://127.0.0.1:11435/v1`
- Model：`qwen3.5:9b`

展开“硬件检测与模型安装”。状态卡依次检查：

1. Intel macOS + 8 GB 独立 Radeon
2. Apple Command Line Tools
3. CMake
4. 定制 `llama-server`
5. `qwen3.5:9b`
6. 本地 GPU 服务

不要猜缺了什么；只处理仍为空心的项目。

### 3. 安装 Apple 编译工具

如果第 2 项为空，点击“打开 Apple 编译工具安装器”，完成 macOS 弹出的 Apple 签名安装流程，然后点“重新自检”。LeetTutor 不会绕过系统权限，也不会要求关闭 Gatekeeper。

### 4. 下载模型

如果第 5 项为空，App 会在同一面板显示 Ollama 安装、启动和模型下载控件。按顺序完成：

`安装 Ollama → 启动 Ollama → 下载 qwen3.5:9b`

模型下载完成后，AMD 后端和普通 Ollama 共用这一份文件。

### 5. 编译实验后端

点击“安装 AMD Metal 实验后端”。App 会把源码放在：

```text
.leettutor/llama.cpp-metal/
```

该目录、编译产物和日志均被 Git 忽略。安装器只运行固定的非交互命令，不执行网页或模型返回的代码。如果目录内容不完整，安装器会先把它重命名为带时间戳的 `backup`，而不是直接删除。

首次下载和编译通常需要数分钟，取决于网络和 CPU。编译输出会在 App 内实时显示；不要在编译阶段关闭终端窗口。

### 6. 启动并验证

点击“立即启动并验证 GPU 服务”。Qwen 3.5 9B 第一次进入 8 GB 显存可能需要几十秒。成功后，第 6 项会变成完成状态，JARVIS 可以直接使用。

以后只需双击 `run.command`：启动器会自动发现已编译后端、读取 Ollama 模型并启动服务；关闭 LeetTutor 的终端进程时，它也会停止由本次启动器创建的服务。

## 兼容范围

| 机器 | 建议 |
| --- | --- |
| Intel MacBook Pro + Radeon Pro 5600M 8 GB | 已验证，使用 Qwen 3.5 9B Q4 |
| Intel Mac + 其他 8 GB 独立 Radeon | 可以实验，速度、设备编号和稳定性可能不同 |
| Radeon Pro 5300M / 5500M 4 GB | 不建议加载 9B；改用 Ollama CPU + 4B 模型 |
| Apple Silicon | 不使用本指南；官方 Ollama 可用统一内存 Metal |
| Boot Camp Windows | 不使用本 Metal 后端；可单独实验 Ollama Vulkan |
| Intel Mac 无独立 Radeon | 使用 CPU 小模型 |

自动安装只对 Intel macOS + 至少 8 GB 独立 Radeon 开放，避免在明显装不下 9B 模型的设备上长时间编译后才失败。

## 参数为什么固定

8 GB 5600M 的余量不大。LeetTutor 使用保守参数：

```text
-dev MTL0
-ngl 999
-fit off
-lm none
-c 4096
-b 64
-ub 16
--parallel 1
--reasoning off
```

- `MTL0`：选择本项目实测机器上的 Radeon Metal 设备。
- `-lm none`：不使用 mmap，把模型放进独立显存 buffer。
- 4096 context / 单并发：控制 KV cache 和临时 buffer，给 8 GB 显存留余量。
- reasoning off：避免小模型把本轮输出额度全花在隐藏思考而没有最终回答。

不要先把 context 调到 8K 或并发调高来“优化”；对这块显卡，最常见结果是分配失败或系统换页，反而更慢。

## 如何确认真的用了 Radeon

App 显示“GPU 服务验证通过”只说明接口能完成模型加载。进一步检查时，展开“复制诊断信息”和最近日志：

- 设备列表应包含 `AMD Radeon Pro 5600M`。
- 模型 tensor/buffer 应分配到 Metal 设备，而不是只出现 `BLAS: Accelerate`。
- JARVIS 请求目标应是 `http://127.0.0.1:11435/v1`，不是 Ollama 的 `11434`。

本机 Q4_K_M 曾测得约 19.94 token/s，而 CPU 约 2.97 token/s（约 6.7 倍）。这只是同一台测试机、同一模型和参数下的参考，不是对所有 macOS 版本或 Radeon 型号的保证。

## 常见故障

### 一直显示 CPU-only

确认 Provider 是 **AMD Metal（Intel Mac）**。如果请求目标仍为 `11434`，你使用的还是 Ollama；AMD 服务的端口是 `11435`。

### “缺少 CMake”

退出应用，再双击 `run.command`。启动器会根据 `requirements.txt` 更新 `.venv`。如果曾使用 `--skip-install`，去掉该参数。

### “缺少 Apple Command Line Tools”

在 App 中点击 Apple 工具安装按钮。安装完成后重新自检；不需要安装完整 Xcode。

### 找不到 qwen3.5:9b

回到同一安装面板启动 Ollama 并下载模型。如果你的 Ollama 模型目录不在默认位置，启动 LeetTutor 前设置：

```bash
export OLLAMA_MODELS="/你的/Ollama/models"
```

如果你已经有单独的 GGUF，也可以显式指定：

```bash
export LEETTUTOR_METAL_MODEL_PATH="/绝对路径/model.gguf"
```

### 编译失败

先复制 App 的诊断信息和最后一段编译输出。常见原因是网络中断、Apple 工具尚未装完或磁盘空间不足。再次点击安装会复用正确的固定源码；不需要自己对上游仓库执行 `git pull`。

### 启动超时或模型载入失败

查看：

```text
.leettutor/amd-metal-server.log
```

先关闭其他占用 5600M 的重负载应用，保持默认 4096 context，然后重试。若日志显示端口占用，退出所有旧 LeetTutor 终端后重新启动。

### App 关闭了，但服务仍存在

正常通过 `run.command` 启动的服务会随启动器关闭。若你用命令行 `--start` 单独启动，回到那个终端按 `Ctrl+C`。

## 命令行备用入口

App 内按钮是推荐路径。需要远程诊断或无法打开网页时，可以运行：

```bash
python3 scripts/setup_intel_amd_metal.py
python3 scripts/setup_intel_amd_metal.py --install
python3 scripts/setup_intel_amd_metal.py --start
```

第一条只读检查，不改动机器；第二条安装/修复；第三条前台启动服务，按 `Ctrl+C` 停止。

## 清理与恢复

- 后端源码和编译产物只在 `.leettutor/llama.cpp-metal`。不再使用时，可退出 LeetTutor 后在 Finder 中把这个目录移到废纸篓。
- 安装器生成的 `backup-*` 目录也在 `.leettutor` 下，可确认新后端正常后再移到废纸篓。
- 删除后端不会删除 Ollama 模型；模型仍可供 CPU Ollama 使用。
- 仓库代码、刷题进度和 Python/Java 题解不会被安装器修改。

提交问题时，请附上 App 中“复制诊断信息”的内容、日志末尾和 macOS 版本；不要上传整个 Ollama 模型或任何 API Key。
