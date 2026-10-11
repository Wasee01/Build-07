# Build 6 comparison

No original Build 6 tree or Week 4 interfaces accompanied this request. Therefore no genuine historical diff can be generated, and an unchanged-service claim would be unsupported.

The new CatalogService depends exclusively on CourseRepository and SectionRepository. JDBC selection is achieved by repository component discovery, with student/enrollment beans in RepositoryConfiguration. There is no SQL, JdbcClient, HTTP, or concrete persistence adapter in the service.

When the original source is available, run:

```powershell
python scripts/compare_service.py C:\path\to\build6
```

This writes `docs/service-build6.diff` using the original and current service trees and exits 1 if they differ. Compare repository interfaces independently before claiming Week 4 compatibility. Do not submit a synthetic baseline as historical evidence.
