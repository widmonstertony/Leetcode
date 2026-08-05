from leettutor.system_design_curriculum import (
    SYSTEM_DESIGN_CASES,
    choose_next_system_design_case,
    get_system_design_case,
)


def test_system_design_curriculum_has_bilingual_guided_missions() -> None:
    assert len(SYSTEM_DESIGN_CASES) >= 8
    first = SYSTEM_DESIGN_CASES[0]
    assert first.localized("title", "zh") == "全球短链接系统"
    assert first.localized("title", "en") == "Global URL Shortener"
    assert first.first_question and first.first_question_cn


def test_system_design_selector_rotates_and_filters() -> None:
    first = choose_next_system_design_case()
    second = choose_next_system_design_case(current_id=first.id)
    hard_transaction = choose_next_system_design_case(
        track="transactions", difficulty="Hard"
    )

    assert first.id != second.id
    assert hard_transaction.track == "transactions"
    assert hard_transaction.difficulty == "Hard"
    assert get_system_design_case(hard_transaction.id) == hard_transaction
