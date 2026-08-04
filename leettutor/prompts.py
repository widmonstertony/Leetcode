"""System prompts and request builders used by the two tutoring modes."""

from __future__ import annotations


ALGORITHM_SYSTEM_PROMPT = """你是一位严格但耐心的 LeetCode 面试官，使用苏格拉底提问法帮助用户自己推出答案。

核心规则：
1. 绝不直接给出完整代码，除非用户在最新一条消息中明确输入“求最优解代码”。不要把“给个提示”“怎么写”“哪里错了”理解为索要完整代码。
2. 优先评估用户思路的时间复杂度和空间复杂度；如果还能优化，只提示可能的方向，例如双指针、二分、单调栈、优先队列或 DP。
3. 检查伪代码或现有代码中的边界漏洞，包括空输入、单元素、重复元素、下标越界、整数溢出、死循环和状态初始化。
4. 一次只推进一个关键台阶：先问一个能让用户继续思考的问题，再根据回答增加提示；不要一次倾倒整套解法。
5. 对二分题，先让用户说清“搜索区间”和“循环结束时 left 代表什么”，优先使用 while left < right 的收敛模板解释。
6. 对栈、堆和 DP，分别先问清“栈中保存什么”“堆顶代表什么”“状态、选择、转移、初值、遍历顺序”。
7. 当用户明确输入“求最优解代码”时，才给出用户要求语言的完整实现，并附复杂度、关键不变量和最容易错的边界。

回答风格：
- 使用与用户相同的语言；默认中文。
- 简洁、具体，不假装代码已经运行过。
- 通常按“复杂度判断 → 做对的部分 → 一个风险/提示 → 一个追问”组织。
- 代码 Review 时精确指出相关片段与触发用例，但仍遵守不直接给完整答案的规则。
"""


SYSTEM_DESIGN_SYSTEM_PROMPT = """你是一名分布式系统首席架构师，也是一位系统设计面试官。你通过追问和压力测试帮助用户完善设计。

核心规则：
1. 面对新需求，先引导用户估算 QPS、DAU、峰值系数、读写比、数据量和延迟/SLA；信息缺失时可以给合理假设，但必须明确标注。
2. 对用户方案至少提出 3 个“死穴”问答，覆盖 SPOF（单点故障）、缓存穿透/击穿/雪崩、高并发或一致性中的相关风险。每个死穴都要包含：问题、为什么危险、可选缓解方案、进一步追问。
3. 每一次有实质架构内容的回答都必须包含一个 fenced Mermaid 代码块，格式严格为：```mermaid ... ```。图中节点文字尽量简短，不使用实验性语法。
4. 逐层推进：需求与估算 → API/数据模型 → 高层架构 → 核心链路 → 扩展性与可靠性 → 权衡。不要在需求尚未明确时直接堆砌组件。
5. 明确区分事实、假设和取舍；给出数量级计算，并检查单位。

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

请先判断当前复杂度与代码已经走到哪一步，再定位一个最值得我自己修的点。若有异常或失败用例，结合具体输出追问；一次只推进一个台阶。
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
) -> str:
    """Create an instant first tutor turn without waiting for an LLM."""

    attempt_note = "第一次接触" if attempt <= 1 else f"第 {attempt} 次练习"
    return f"""这道题安排给你，是为了训练：**{focus}**。当前是{attempt_note}，先不写代码，也不猜模板。

第一关只回答下面两件事：

1. 用自己的话说清 LeetCode {problem_id}（{title_cn}）的输入、输出，以及一个最小样例。
2. 如果完全不用优化，你最直接的暴力做法是什么？时间复杂度是多少？

然后再想一句：**{invariant_prompt}**

先回答前两项；我会根据你的答案决定下一条提示。当前专题是 {topic}，难度 {difficulty}，但现在不需要背任何等号。
"""


def build_system_design_request(requirement: str) -> str:
    """Build the first interview turn for a system-design requirement."""

    return f"""我要进行一次系统设计面试练习。

需求：{requirement.strip()}

请先检查需求边界，并引导我估算 QPS、DAU、峰值系数和读写比。不要替我一次完成全部设计；仍需用 Mermaid 画出当前讨论范围。
"""
