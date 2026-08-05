"""Curated system-design interview missions for JARVIS-led practice."""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class SystemDesignCase:
    id: str
    title: str
    title_cn: str
    difficulty: str
    track: str
    focus: str
    focus_cn: str
    requirement: str
    requirement_cn: str
    first_question: str
    first_question_cn: str

    def localized(self, field: str, language: str) -> str:
        suffix = "_cn" if language == "zh" else ""
        return str(getattr(self, field + suffix))


SYSTEM_DESIGN_TRACKS = (
    "scaling",
    "reliability",
    "realtime",
    "data",
    "transactions",
)


SYSTEM_DESIGN_CASES: tuple[SystemDesignCase, ...] = (
    SystemDesignCase(
        "SD-01",
        "Global URL Shortener",
        "全球短链接系统",
        "Easy",
        "scaling",
        "Turn traffic estimates into a read-heavy storage and cache design",
        "把流量估算转成读多写少的存储与缓存方案",
        "Design a global URL shortener with custom aliases, link expiration, analytics, and redirects under 100 ms p99.",
        "设计一个全球短链接系统：支持自定义短码、链接过期、访问统计，并让跳转延迟保持在 p99 100 ms 内。",
        "State your DAU, peak QPS, read/write ratio, and retention assumptions before choosing any component.",
        "先不要选组件：给出你对 DAU、峰值 QPS、读写比和保存年限的假设。",
    ),
    SystemDesignCase(
        "SD-02",
        "Distributed Rate Limiter",
        "分布式限流器",
        "Medium",
        "reliability",
        "Define consistency and failure behavior before choosing an algorithm",
        "先定义一致性与故障行为，再选择限流算法",
        "Design a multi-region API rate limiter that supports per-user and per-endpoint policies with burst allowance.",
        "设计一个多地域 API 限流器：支持按用户、按接口配置规则，并允许短时突发流量。",
        "What must happen when the shared counter store is slow or unavailable: fail open, fail closed, or degrade locally?",
        "共享计数存储变慢或不可用时，应该放行、拒绝，还是降级为本地限流？为什么？",
    ),
    SystemDesignCase(
        "SD-03",
        "Real-time Chat",
        "实时聊天系统",
        "Medium",
        "realtime",
        "Separate connection routing, message durability, and delivery semantics",
        "拆开连接路由、消息持久化和投递语义",
        "Design one-to-one and group chat with online presence, multi-device sync, offline delivery, and message history.",
        "设计支持单聊与群聊的系统：包含在线状态、多设备同步、离线投递和历史消息。",
        "Choose the delivery guarantee first. What does the user observe when a message is duplicated or arrives out of order?",
        "先选择投递语义：消息重复或乱序时，用户实际会看到什么？",
    ),
    SystemDesignCase(
        "SD-04",
        "Social News Feed",
        "社交动态 Feed",
        "Medium",
        "scaling",
        "Reason about fan-out on write versus fan-out on read",
        "权衡写扩散与读扩散",
        "Design a ranked home feed for hundreds of millions of users, including celebrities, pagination, and freshness.",
        "设计面向数亿用户的排序首页 Feed：需要处理大 V、分页和新鲜度。",
        "Which users should use fan-out on write, which should use fan-out on read, and where is the crossover?",
        "哪些用户适合写扩散，哪些适合读扩散？切换策略的分界点是什么？",
    ),
    SystemDesignCase(
        "SD-05",
        "Metrics and Alerting Platform",
        "指标与告警平台",
        "Medium",
        "data",
        "Control high-cardinality ingestion and time-series query cost",
        "控制高基数写入与时序查询成本",
        "Design a metrics platform that ingests billions of samples per minute and supports dashboards, retention tiers, and alerts.",
        "设计一个每分钟写入数十亿样本的指标平台，支持仪表盘、分层保留和告警。",
        "Estimate ingest bandwidth and identify which label dimensions can create a cardinality explosion.",
        "先估算写入带宽，并指出哪些标签维度会导致基数爆炸。",
    ),
    SystemDesignCase(
        "SD-06",
        "Flash-sale Ticketing",
        "秒杀票务系统",
        "Hard",
        "transactions",
        "Prevent overselling while keeping the purchase path available",
        "在保持购买链路可用的同时防止超卖",
        "Design ticket sales for a global concert launch with assigned seats, payment timeouts, refunds, and extreme bursts.",
        "设计全球演唱会开票系统：包含选座、支付超时、退款和极端突发流量。",
        "Where is the single source of truth for seat ownership, and how long can a temporary hold live?",
        "座位归属的唯一事实来源在哪里？临时锁座应该存活多久？",
    ),
    SystemDesignCase(
        "SD-07",
        "Cloud File Storage",
        "云文件存储",
        "Hard",
        "data",
        "Separate metadata consistency from immutable blob storage",
        "拆开元数据一致性与不可变文件块存储",
        "Design a Dropbox-like service with sync, sharing, version history, deduplication, and large uploads.",
        "设计类似 Dropbox 的文件系统：支持同步、分享、版本历史、去重和大文件上传。",
        "Which operations require strongly consistent metadata, and which data can be immutable and eventually replicated?",
        "哪些操作需要强一致元数据？哪些数据可以不可变并异步复制？",
    ),
    SystemDesignCase(
        "SD-08",
        "Global Payment Ledger",
        "全球支付账本",
        "Hard",
        "transactions",
        "Make idempotency, auditability, and reconciliation explicit",
        "显式设计幂等、审计与对账",
        "Design a payment platform with authorization, capture, refunds, webhooks, a double-entry ledger, and regional failures.",
        "设计支付平台：包含授权、扣款、退款、Webhook、复式记账和地域故障处理。",
        "Define the idempotency boundary and the invariant that must hold across every ledger entry.",
        "先定义幂等边界，以及每一笔账务分录都必须满足的不变量。",
    ),
)


def get_system_design_case(case_id: str) -> SystemDesignCase | None:
    return next((case for case in SYSTEM_DESIGN_CASES if case.id == case_id), None)


def choose_next_system_design_case(
    *,
    current_id: str = "",
    track: str = "auto",
    difficulty: str = "progressive",
) -> SystemDesignCase:
    candidates = list(SYSTEM_DESIGN_CASES)
    if track in SYSTEM_DESIGN_TRACKS:
        candidates = [case for case in candidates if case.track == track]
    if difficulty in {"Easy", "Medium", "Hard"}:
        candidates = [case for case in candidates if case.difficulty == difficulty]
    if not candidates:
        raise ValueError("No system-design mission matches the current filters.")
    if current_id:
        for index, case in enumerate(candidates):
            if case.id == current_id:
                return candidates[(index + 1) % len(candidates)]
    return candidates[0]
