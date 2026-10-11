"""Compare the real Build 6 service sources with this project."""
import difflib
from pathlib import Path
import sys

root = Path(__file__).resolve().parents[1]
if len(sys.argv) != 2:
    raise SystemExit("Usage: python scripts/compare_service.py BUILD6_PROJECT_DIRECTORY")
relative = Path("src/main/java/edu/riverbend/crs/service")
old = Path(sys.argv[1]) / relative
new = root / relative
if not old.is_dir():
    raise SystemExit("Build 6 service directory does not exist")
files = sorted({p.relative_to(old) for p in old.rglob("*.java")} |
               {p.relative_to(new) for p in new.rglob("*.java")})
parts = []
for name in files:
    before = (old / name).read_text().splitlines(keepends=True) if (old / name).exists() else []
    after = (new / name).read_text().splitlines(keepends=True) if (new / name).exists() else []
    parts.extend(difflib.unified_diff(before, after, fromfile=f"build6/{name}", tofile=f"current/{name}"))
output = root / "docs/service-build6.diff"
output.write_text("".join(parts))
print(f"Wrote {output}; {'differences found' if parts else 'no differences'}")
raise SystemExit(1 if parts else 0)
