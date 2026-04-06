# Purina FeedRight — Product Idea Document

> **Prepared by:** rakesh barik  
> **Status:** Concept / Pre-PRD  
> **Date:** April 2026

---

## 1. The Problem

Purina field salespeople visit dozens of livestock farms every week. Their job is to recommend the right Purina feed product for each animal group on the farm — the correct SKU, the correct dosage, the correct rationale.

Today, that recommendation happens entirely from memory and printed catalogues.

This creates three compounding problems:

**Wrong recommendations.** A salesman who misremembers age-weight brackets recommends the wrong product. The farmer sees poor animal growth or health outcomes. Trust erodes. The account is at risk.

**Missed revenue.** Newer, better-fit products in the Purina catalogue go unmentioned because the salesman doesn't know they exist, or doesn't remember them under pressure. Every missed upsell is invisible — no one even knows it happened.

**No visit record.** The recommendation is verbal. There is no audit trail. When a farm account churns, the sales manager has no data to understand why. When a new salesman takes over a territory, they start from zero.

---

## 2. The Opportunity

A lightweight mobile tool that gives every Purina salesman — regardless of experience level — the ability to walk into any farm, enter basic animal group information, and instantly receive a data-backed, ranked product recommendation with dosage.

The junior salesman performs like a senior one. The senior salesman is faster and more confident. Every visit is recorded. Every recommendation is traceable.

---

## 3. Product Vision

**Purina FeedRight** is a field sales recommendation tool for Purina salespeople.

The salesman opens the app on his phone, selects the farm he is visiting, enters the animal group details — species, age, weight, health condition — and receives a ranked list of Purina products with confidence scores and daily dosage guidance. He shows the screen to the farmer. The recommendation is recorded automatically.

The sales manager, sitting at a desk, opens a web dashboard and sees every recommendation made by every salesman on the team — which farms were visited, which products were recommended, and how recently.

---

## 4. Who It Is For

### Primary User — Field Salesman

**Name:** Marco  
**Context:** Visits 5–10 farms per day across a rural territory. Spends most of his time in barns and fields with poor mobile signal. Carries a smartphone but is not technical. Under time pressure on every visit — farmers don't want a long consultation.

**Marco's pain today:** He relies on experience that took years to build and still gets it wrong sometimes. He has no way to look knowledgeable in front of a farmer when he encounters an unfamiliar species condition or a new product he hasn't memorised yet.

**What Marco needs:** A tool that gives him the right answer in under 30 seconds, works without signal, and makes him look competent in front of the farmer.

### Secondary User — Sales Manager

**Name:** Priya  
**Context:** Manages a team of 8–12 salesmen. Reviews performance weekly. Sits at a desk with a laptop. Is not present in the field.

**Priya's pain today:** She has no visibility into what recommendations her team is making. She finds out about bad recommendations only when a farmer complains — weeks later. She cannot tell which farms are being visited and which are being neglected.

**What Priya needs:** A dashboard that shows her what her team is doing in the field, without requiring her to travel or call each salesman.

---

## 5. Core User Journeys

### Journey 1 — Field Recommendation (Marco)

1. Marco arrives at Green Valley Farm and opens FeedRight.
2. He selects "Green Valley Farm" from his recent visits, or adds it as a new farm.
3. He enters the animal group: pigs, 120 animals, 8 weeks old, 12 kg average weight, healthy.
4. FeedRight returns: **Purina Pro Pig Grower 16% — 84% match — 700g/day**. Two alternative products are listed below with lower confidence scores.
5. Marco shows the screen to the farmer and walks him through the recommendation.
6. The visit is recorded. Marco drives to the next farm.

### Journey 2 — No Signal (Marco, offline)

1. Marco is in a barn with no mobile signal.
2. He opens FeedRight. The app loads from local data — no spinner, no error.
3. He enters the animal group and receives a recommendation based on the product catalogue cached on device.
4. When he returns to his truck and signal is restored, the visit syncs automatically. A small indicator shows "Synced."

### Journey 3 — Team Review (Priya)

1. Priya opens the FeedRight web dashboard on Monday morning.
2. She sees that 47 recommendations were made last week across 23 farms.
3. She filters by salesman "Marco" and sees his last 10 visits. She notices he recommended the Recovery Formula to a healthy pig group — a likely mismatch.
4. She calls Marco to discuss. She has the data she needs to coach him.

---

## 6. Key Features

### Mobile app (Android + iOS)

| Feature | Description |
|---|---|
| Farm selection | Select from previously visited farms or add a new one |
| Animal group entry | Species, count, age, weight, health condition — simple form, under 60 seconds |
| Ranked recommendation | Top 3 products with confidence score, dosage, and brief rationale |
| Offline-first | Full functionality without signal; local data storage on device |
| Auto-sync | Unsynced visits queue and upload automatically when signal returns |
| Visit history | Last 10 visits accessible on device for quick reference |

### Web dashboard (sales manager)

| Feature | Description |
|---|---|
| Recommendation feed | Chronological list of all recommendations across the team |
| Filters | Filter by salesman, farm name, date range |
| Summary metrics | Total visits, total recommendations, active farms in last 30 days |
| Visit detail | Full record of a specific farm visit — animal group data, products recommended, timestamp |

---

## 7. The Recommendation Logic

The engine that powers FeedRight is not a complex AI model. It is a rule-based scoring system grounded in Purina's own product specifications.

Each product in the Purina catalogue carries a profile: which animal type it serves, what age bracket it targets, what weight range it fits, and which health conditions it addresses.

When a salesman enters an animal group, FeedRight scores every eligible product against three criteria:

- **Age bracket match** — does the animal's age fall within the product's target range?
- **Weight range match** — does the animal's weight fall within the product's target range?
- **Health condition match** — does the product specifically address the animal's condition?

The top three scoring products are returned, ranked by confidence. The logic is transparent — Purina's product team can update product profiles in the catalogue and the recommendations adapt immediately, with no code change required.

---

## 8. What Makes This Defensible

**It is Purina's data, not generic data.** The recommendation engine runs entirely on Purina's own product catalogue. Competitors cannot replicate it without Purina's proprietary formulation data.

**It improves with scale.** Every recorded visit is a data point. Over time, Purina can identify which products are being recommended in which regions, detect patterns in mismatches, and improve the scoring rules. The data asset compounds.

**It raises the floor on salesman quality.** Purina's competitive advantage in the field is the quality of its salespeople's advice. FeedRight makes that advice consistent regardless of experience level. A salesman in their first month performs closer to one with five years of territory knowledge.

**It creates switching cost on the farmer side.** When a farmer's visit history, their farm profile, and their animal records are in FeedRight, they have a relationship with Purina's system — not just with an individual salesman.

---

## 9. What We Are Not Building (Yet)

To ship fast and learn, the following are explicitly out of scope for the first version:

- User login and authentication
- Integration with Purina's ERP or inventory systems
- Pricing, quoting, or order placement
- Veterinary diagnosis or clinical health monitoring
- GPS tracking of salesmen
- Push notifications or alerts
- Multi-language support

These are not rejected permanently — they are deferred until the core recommendation and visit-recording loop is validated in the field.

---

## 10. Success Metrics

| Metric | Target (6 months post-launch) |
|---|---|
| Weekly active salesmen | >80% of the pilot team using the app at least 3 days per week |
| Recommendation accuracy | >75% of recommendations accepted by the farmer (self-reported by salesman) |
| Offline sync reliability | >99% of offline visits successfully synced within 1 hour of signal restoration |
| Visit recording rate | >90% of farm visits have a FeedRight record (vs. zero today) |
| Manager dashboard adoption | >60% of sales managers checking the dashboard at least once per week |

---

## 11. Risks and Open Questions

| Risk | Mitigation |
|---|---|
| Salesmen don't adopt — they find it slower than memory | Invest heavily in speed of the input form; target under 45 seconds from open to recommendation |
| Product catalogue data is incomplete or inaccurate | Work with Purina's product team to validate and seed the catalogue before pilot launch |
| Offline sync conflicts — salesman edits the same farm on two devices | Lock farm records to the last-syncing device; surface conflicts clearly in the dashboard |
| Manager dashboard creates surveillance anxiety in the sales team | Frame the tool as a coaching aid, not a monitoring tool; give salesmen visibility into their own data first |

**Open questions for the discovery phase:**

- How frequently does the Purina product catalogue change? Who owns it?
- Do salesmen currently use any digital tool in the field, even informally?
- Is the recommendation today species-only, or do salesmen already factor in age and weight?
- What is the current average time spent on a product recommendation during a farm visit?

---

## 12. Suggested Pilot Plan

**Phase 1 — Closed pilot (weeks 1–8)**  
Deploy to a single sales team of 5–8 salesmen in one territory. Seed the product catalogue with pig products only — the highest-volume species. Collect weekly feedback. Measure recommendation acceptance rate and app usage.

**Phase 2 — Expanded pilot (weeks 9–16)**  
Expand to two additional territories. Add cattle products to the catalogue. Introduce the manager dashboard. Begin tracking visit recording rate as a KPI.

**Phase 3 — Regional rollout (weeks 17–24)**  
Full rollout to the region. Add remaining species (poultry, sheep, goat). Begin harvesting aggregate recommendation data for product team analysis.

---

*This document represents the product concept and is intended for internal alignment. It is not a product specification. A full PRD with acceptance criteria, data model, and API contracts will follow after stakeholder review.*
