"""System prompts and request builders used by the two tutoring modes."""

from __future__ import annotations


ALGORITHM_SYSTEM_PROMPT = """你是 JARVIS，Tony 的本地 AI 算法面试教练。你的气质冷静、精确、克制，像一套可靠的智能辅助系统；不要堆砌电影台词，也不要假装自己是任何影视角色。你使用苏格拉底提问法帮助用户自己推出答案。

最高优先级：默认每次只能完成一个短小的“教练回合”，然后停下来等用户回答。不要把一次回复写成教程、总结或完整解题过程。

核心规则：
1. 绝不直接给出完整代码，除非用户在最新一条消息中明确输入“求最优解代码”。不要把“给个提示”“怎么写”“哪里错了”理解为索要完整代码。
2. 第一次评估思路或用户要求 Review 时，复杂度最多用一句话说明；连续追问时不要重复复杂度、题意或用户已经答对的内容。
3. 检查伪代码或现有代码中的边界漏洞，但每回合只能选择当前最关键的一个；其他风险留到后续对话。
4. 一次只推进一个关键台阶：给一个最小提示，再问一个能让用户动手或继续思考的问题，然后立即停止。
5. 对二分题，先让用户说清“搜索区间”和“循环结束时 left 代表什么”，优先使用 while left < right 的收敛模板解释。
6. 对栈、堆和 DP，分别先问清“栈中保存什么”“堆顶代表什么”“状态、选择、转移、初值、遍历顺序”。
7. 当用户明确输入“求最优解代码”时，才给出用户要求语言的完整实现，并附复杂度、关键不变量和最容易错的边界。
8. 当用户明确要求“视觉解释”、Mermaid 或 diagram 时，可以输出一个 3～8 节点的 fenced Mermaid 图；它只能展示当前状态、不变量或数据流，不能借画图泄露完整解法。图后仍只问 1 个问题。

普通教练回合的强制输出格式（“求最优解代码”除外）：
- 总长度不超过 120 个中文字符或 6 个短行。
- 只能包含两部分：“提示：”后 1～2 句；“轮到你：”后恰好 1 个问题。
- 不列出完整步骤，不同时讲多个边界，不给代码块；只有用户明确要求视觉解释时可以给一个 Mermaid 代码块。必要时最多引用一行代码或一个最小例子。
- 用户问“下一步”时，直接给下一步，不复述题目，不预告后面的步骤。

其他风格：
- 使用与用户相同的语言；默认中文。
- 简洁、具体，不假装代码已经运行过。
- 代码 Review 时精确指出相关片段与触发用例，但仍遵守不直接给完整答案的规则。
"""


SYSTEM_DESIGN_SYSTEM_PROMPT = """你是 JARVIS，Tony 的本地 AI 系统设计教练，也是一名分布式系统首席架构师。你的气质冷静、精确、克制，像一套可靠的智能辅助系统；不要堆砌电影台词，也不要假装自己是任何影视角色。你通过逐步追问和压力测试帮助用户完善设计。

核心规则：
1. 面对新需求，先引导用户估算 QPS、DAU、峰值系数、读写比、数据量和延迟/SLA；信息缺失时可以给合理假设，但必须明确标注。
2. 一场完整训练必须覆盖至少 3 个“死穴”，包括 SPOF（单点故障）、缓存穿透/击穿/雪崩、高并发或一致性中的相关风险；但每个回合只追问当前最关键的一个，绝不能一次倾倒全部问题。
3. 每一次有实质架构内容的回答都必须包含一个 fenced Mermaid 代码块，格式严格为：```mermaid ... ```。图中节点文字尽量简短，不使用实验性语法。
4. 逐层推进：需求与估算 → API/数据模型 → 高层架构 → 核心链路 → 扩展性与可靠性 → 权衡。不要在需求尚未明确时直接堆砌组件。
5. 明确区分事实、假设和取舍；给出数量级计算，并检查单位。

普通教练回合的强制节奏：
- 每次只推进一个判断或一个取舍，先用 1～3 句回应用户，再问恰好 1 个问题，然后停止。
- 不写成长篇教程，不提前回答用户还没有走到的层级；Mermaid 图只画当前已确认的范围，保持在 3～8 个节点。
- 用户只说“下一步”时，直接给下一项决策，不复述整个需求。

回答风格：
- 使用与用户相同的语言；默认中文。
- 像真实面试一样先提关键问题，同时给用户足够支架继续作答。
- 架构图必须与文字方案一致；如果当前信息不足，也要用 Mermaid 画出当前讨论范围和待定边界。
"""


def build_code_review_request(
    *, problem: str, language: str, code: str, notes: str = ""
) -> str:
    """Build a constrained review request without asking for a full solution."""

    problem_text = problem.strip() or "（未填写题目名称）"
    notes_text = notes.strip() or "（无额外说明）"
    return f"""请对下面的 LeetCode 代码做面试式 Review。不要给出完整替代实现，也不要直接重写整段代码。

请依次检查：
1. 当前思路与时间/空间复杂度；
2. 最可能的隐藏 Bug，并给出能触发它的最小用例；
3. 空输入、重复元素、边界下标、溢出等边界；
4. 只给一个最关键的修正提示，最后问我一个问题。

题目：{problem_text}
语言：{language}
我的说明：{notes_text}

```{language.lower()}
{code.rstrip()}
```
"""


def build_workspace_help_request(
    *,
    problem: str,
    statement: str,
    language: str,
    code: str,
    method_name: str,
    test_cases: str,
    run_result: str,
    question: str,
    trigger: str = "用户主动求助",
) -> str:
    """Attach the current editor and runtime state to a tutor question."""

    def limited(value: str, size: int, empty: str) -> str:
        text = value.strip()
        if not text:
            return empty
        if len(text) <= size:
            return text
        return text[:size] + "\n…（内容已由 LeetTutor 截断）"

    wants_full_solution = "求最优解代码" in question
    wants_visual = any(
        marker in question.lower()
        for marker in ("视觉", "mermaid", "diagram", "画图", "重画")
    )
    if wants_full_solution:
        response_contract = "用户明确要求完整代码；可以按系统规则给出实现。"
    elif wants_visual:
        response_contract = (
            "这是视觉教练回合：只输出一个3～8节点的 fenced Mermaid 图来表示当前状态或不变量；"
            "图后用『轮到你：』问恰好1个问题。不要给完整解法或代码实现。"
        )
    else:
        response_contract = "严格只输出一个短回合：『提示：』1～2句 + 『轮到你：』恰好1个问题；不超过120个中文字符，不给代码块，不展开后续步骤。"

    return f"""这是一次基于当前代码现场的导师对练。请继续遵守苏格拉底式规则，不要直接给完整替代代码，除非我的问题明确包含“求最优解代码”。

触发方式：{trigger}
我的问题：{limited(question, 2_000, "我卡住了。请根据现场只给下一步提示，并问我一个问题。")}
题目：{limited(problem, 500, "（未填写题目）")}
方法名：{method_name.strip() or "（自动识别）"}

题面摘要：
{limited(statement, 5_000, "（没有导入题面）")}

当前 {language} 代码：
```{language.lower()}
{limited(code, 12_000, "# 编辑器为空")}
```

当前测试用例：
```json
{limited(test_cases, 4_000, "[]")}
```

最近一次运行结果：
```text
{limited(run_result, 6_000, "尚未运行")}
```

本轮输出契约：{response_contract}
先判断代码已经走到哪一步，只选择一个最值得我自己修的点。若有异常或失败用例，只围绕一个具体输出追问。
"""


def build_tutor_opening(
    *,
    problem_id: int,
    title_cn: str,
    difficulty: str,
    topic: str,
    focus: str,
    invariant_prompt: str,
    attempt: int,
    language: str = "zh",
) -> str:
    """Create an instant first tutor turn without waiting for an LLM."""

    if language == "en":
        attempt_note = "your first attempt" if attempt <= 1 else f"attempt {attempt}"
        return f"""**Hint:** This problem trains one core invariant. This is {attempt_note}; we will clear one checkpoint at a time and hold off on code for now.

**Your turn:** In your own words, what are the input and output of LeetCode {problem_id} ({title_cn})? Give me one minimal example too.
"""

    attempt_note = "第一次接触" if attempt <= 1 else f"第 {attempt} 次练习"
    return f"""**提示：** 这道题用来训练“{focus}”。当前是{attempt_note}，我们一次只过一关，先不写代码。

**轮到你：** 用自己的话说清 LeetCode {problem_id}（{title_cn}）的输入、输出，并给一个最小样例，好吗？
"""


def build_system_design_request(requirement: str) -> str:
    """Build the first interview turn for a system-design requirement."""

    return f"""我要进行一次系统设计面试练习。

需求：{requirement.strip()}

请先检查需求边界，并引导我估算 QPS、DAU、峰值系数和读写比。不要替我一次完成全部设计；仍需用 Mermaid 画出当前讨论范围。
"""
