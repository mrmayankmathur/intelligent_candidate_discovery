# Intelligent Candidate Discovery

A comprehensive, constraint-aware candidate ranking system built for the Redrob Hackathon. This project consists of two separate systems: an offline CPU-based ranking engine, and an interactive web sandbox for the judges.

## Deliverable 1: Ranking Engine (Python)

The core ranking engine is designed to run completely offline on a CPU within 5 minutes and under 16GB of RAM. It leverages pre-computed FAISS embeddings (BGE-small-en-v1.5) and a deduplicated BM25 index to perform Reciprocal Rank Fusion (RRF). Aggressive honeypot filtering (5 distinct heuristic checks) removes keyword stuffers and disqualifiers.

- **Directory**: `ranker/`
- **Performance**: Runs end-to-end in ~63 seconds.
- **Memory**: Peak footprint is ~2.8 GB.
- **Top 10 Quality**: Extensively manually reviewed to verify 0 traps and exclusively high-quality product company AI/ML engineers.

### Setup

```bash
git clone <repo-url> && cd intelligent_candidate_discovery
git lfs install && git lfs pull        # fetch the precomputed artifacts + baked model
pip install -r ranker/requirements.txt # only needed for local (non-Docker) runs
ls dataset/candidates.jsonl            # the 100K-profile input (~487 MB)
```

### Pre-computation (already done — artifacts are committed)

The embeddings, FAISS index, BM25 corpus and feature matrix in `ranker/artifacts/` are
checked into the repo via Git LFS, so **you do not need to re-run this**. To regenerate
from scratch (this step may use the network and has no time limit):

```bash
python -m ranker.precompute_all --candidates dataset/candidates.jsonl
```

### Reproduce the submission (single command, offline)

The judged ranking step runs CPU-only with **no network**. Build once, then run:

```bash
docker build -t icd-ranker -f ranker/Dockerfile .

mkdir -p output
docker run --rm \
  --memory=16g \
  --network=none \
  -v $(pwd)/dataset:/data:ro \
  -v $(pwd)/output:/output \
  icd-ranker

# Validate the result (must be exactly 100 rows)
python dataset/validate_submission.py output/submission.csv
```

The embedding model is baked into the image (`models/bge-small-en-v1.5/`, with
`HF_HUB_OFFLINE=1`), so the container never reaches huggingface.co under `--network=none`.

For local (non-Docker) runs and full flag reference, see [`ranker/README.md`](ranker/README.md).

## Deliverable 2: Web Sandbox & Demo (Java/Spring Boot + Kotlin/JS)

An interactive visual frontend to explore the ranking engine's output, view candidate profiles, read AI-generated reasoning, and examine the skill match scores.

- **Directory**: `webapp/`
- **Architecture**: Spring Boot 3.3 backend serving a compiled Kotlin/React SPA.
- **Live Ranking**: The UI features a "Re-run Ranking" console that streams the Python logs via Server-Sent Events (SSE).

**To run the sandbox locally** (recommended — single self-contained jar):

```bash
cd webapp
./gradlew :backend:bootRun
```
Then navigate to `http://localhost:8080`. The app reads the frozen engine's outputs
(`submission.csv`, `dataset/candidates.jsonl`, `ranker/artifacts/jd_intent.json`) and can
shell out to `python -m ranker.rank` for a live re-run streamed over SSE. A
`webapp/docker-compose.yml` is also provided. See [`webapp/README.md`](webapp/README.md).

## Directory Structure

- `ranker/`: The core Python ranking engine.
- `webapp/`: The Spring Boot + Kotlin/JS sandbox demo.
- `dataset/`: Hackathon data files (`candidates.jsonl`, `jd_intent.json`, etc.).
- `submission.csv`: The final generated submission output.
- `submission_metadata.yaml`: Team identity and metadata for the hackathon portal.
- `task.md` & `memory.md`: Internal agent status tracking and notes.
