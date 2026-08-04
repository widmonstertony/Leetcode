from leettutor.mermaid import Segment, split_mermaid_blocks


def test_split_mermaid_blocks() -> None:
    text = "Before\n\n```mermaid\ngraph LR\nA --> B\n```\n\nAfter"
    assert split_mermaid_blocks(text) == [
        Segment("markdown", "Before"),
        Segment("mermaid", "graph LR\nA --> B"),
        Segment("markdown", "After"),
    ]


def test_plain_markdown_is_preserved() -> None:
    assert split_mermaid_blocks("hello") == [Segment("markdown", "hello")]
