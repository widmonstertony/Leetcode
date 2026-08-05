"""Small, deterministic Mermaid maps used by the visual learning surface."""

from __future__ import annotations


def algorithm_pattern_mermaid(topic: str, *, language: str = "zh") -> str:
    """Return a compact mental model without revealing a problem solution."""

    english = language == "en"
    diagrams = {
        "二分": (
            "flowchart LR\n"
            f'  A["{("Candidate interval" if english else "候选区间")} [left, right]"] --> B["mid"]\n'
            f'  B --> C{{"{("Compare / feasible?" if english else "比较 / 是否可行")}"}}\n'
            f'  C -->|"{("discard left" if english else "排除左侧")}"| D["left = mid + 1"]\n'
            f'  C -->|"{("keep mid" if english else "保留 mid")}"| E["right = mid"]\n'
            f'  D --> F{{"left < right"}}\n'
            f'  E --> F\n'
            f'  F -->|"{("yes" if english else "是")}"| B\n'
            f'  F -->|"{("no" if english else "否")}"| G["left = {("candidate" if english else "答案候选")}"]'
        ),
        "栈": (
            "flowchart LR\n"
            f'  A["{("next item" if english else "读入元素")}"] --> B{{"{("compare with top" if english else "与栈顶比较")}"}}\n'
            f'  B -->|"{("settled" if english else "可结算")}"| C["pop"]\n'
            f'  C --> D["{("record answer" if english else "记录答案")}"]\n'
            f'  D --> B\n'
            f'  B -->|"{("wait" if english else "继续等待")}"| E["push"]\n'
            f'  E --> F["{("stack invariant" if english else "保持栈内不变量")}"]'
        ),
        "优先队列": (
            "flowchart LR\n"
            f'  A["{("stream / candidates" if english else "数据流 / 候选")}"] --> B["push heap"]\n'
            f'  B --> C{{"size > k"}}\n'
            f'  C -->|"{("yes" if english else "是")}"| D["pop"]\n'
            f'  C -->|"{("no" if english else "否")}"| E["{("keep candidates" if english else "保留候选集合")}"]\n'
            f'  D --> E\n'
            f'  E --> F["{("heap top has one precise meaning" if english else "堆顶必须有明确含义")}"]'
        ),
        "DP": (
            "flowchart LR\n"
            f'  A["1. {("Define state" if english else "定义状态")}"] --> B["2. {("List choices" if english else "列出选择")}"]\n'
            f'  B --> C["3. {("Transition" if english else "写转移")}"]\n'
            f'  C --> D["4. {("Base cases" if english else "定初值")}"]\n'
            f'  D --> E["5. {("Traversal order" if english else "定遍历顺序")}"]\n'
            f'  E --> F["6. {("Read answer" if english else "读取答案")}"]'
        ),
    }
    return diagrams.get(
        topic,
        "flowchart LR\n"
        f'  A["{("Input" if english else "输入")}"] --> B["{("Invariant" if english else "核心不变量")}"]\n'
        f'  B --> C["{("State update" if english else "状态更新")}"]\n'
        f'  C --> D{{"{("Stop?" if english else "是否结束")}"}}\n'
        f'  D -->|"{("no" if english else "否")}"| C\n'
        f'  D -->|"{("yes" if english else "是")}"| E["{("Output" if english else "输出")}"]',
    )


def system_design_pattern_mermaid(track: str, *, language: str = "zh") -> str:
    """Return a mission-specific architecture skeleton for visual orientation."""

    english = language == "en"
    tracks: dict[str, tuple[str, ...]] = {
        "scaling": (
            "Client" if english else "客户端",
            "Edge / LB" if english else "边缘 / 负载均衡",
            "Service" if english else "服务层",
            "Cache" if english else "缓存",
            "Database" if english else "数据库",
            "Analytics" if english else "异步分析",
        ),
        "reliability": (
            "Client" if english else "客户端",
            "Gateway" if english else "网关",
            "Region A" if english else "区域 A",
            "Shared state" if english else "共享状态",
            "Region B" if english else "区域 B",
            "Fallback" if english else "降级路径",
        ),
        "realtime": (
            "Client" if english else "客户端",
            "Connection gateway" if english else "连接网关",
            "Session router" if english else "会话路由",
            "Message log" if english else "消息日志",
            "Delivery" if english else "投递服务",
            "History store" if english else "历史存储",
        ),
        "data": (
            "Producers" if english else "数据生产者",
            "Ingestion" if english else "接入层",
            "Buffer" if english else "消息缓冲",
            "Processing" if english else "流式处理",
            "Hot store" if english else "热存储",
            "Cold store" if english else "冷存储",
        ),
        "transactions": (
            "Client" if english else "客户端",
            "API" if english else "交易 API",
            "Idempotency" if english else "幂等层",
            "Transaction" if english else "事务服务",
            "Ledger" if english else "账本 / 真相源",
            "Events" if english else "事件与对账",
        ),
    }
    labels = tracks.get(
        track,
        (
            "Requirements" if english else "需求边界",
            "Scale" if english else "容量估算",
            "API / data" if english else "API / 数据模型",
            "Architecture" if english else "高层架构",
            "Reliability" if english else "可靠性",
            "Trade-offs" if english else "权衡",
        ),
    )
    nodes = [f'  N{i}["{label}"]' for i, label in enumerate(labels)]
    edges = [f"  N{i} --> N{i + 1}" for i in range(len(labels) - 1)]
    return "flowchart LR\n" + "\n".join([*nodes, *edges])
