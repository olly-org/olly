# Architecture Decision Records

This directory records significant architecture decisions for Olly.
It's a history, not current-state documentation.

For more information on ADRs, read [this blog post by Michael Nygard](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions).

## Creating an ADR

1. Copy `template.md` to `NNNN-short-title.md` using the next number.
2. Set `Status: Proposed` and include it in the PR that implements the decision.
3. Flip it to `Accepted` before merge.
4. Never rewrite an accepted ADR.
   To change a decision, write a new ADR and mark the old one `Superseded by NNNN`.

## Index

| ADR | Title | Status |
|-----|-------|--------|
| [0001](0001-record-architecture-decisions.md) | Record architecture decisions | Accepted |
